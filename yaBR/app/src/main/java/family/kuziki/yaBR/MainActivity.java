package family.kuziki.yaBR;
/**
 * Main activity: opens when application starts.
 * */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import java.io.IOException;
import family.kuziki.yaBR.library.Library;
import family.kuziki.yaBR.library.LibraryAdapter;
import family.kuziki.yaBR.library.LibraryItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    TextView mainTextView;
    ListView mainListView;
    LibraryAdapter libraryAdapter;
    Button mainButton;
    ShareActionProvider shareActionProvider;
    private static final String PREFS = "prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);
        mainListView = (ListView) findViewById(R.id.main_listview);
        mainListView.setOnItemClickListener(this);
        libraryAdapter = new LibraryAdapter(this, getLayoutInflater());
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
            Library.getInstance().loadBooks(this);
            libraryAdapter.updateData();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainListView.setAdapter(libraryAdapter);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        libraryAdapter.updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        if (shareItem != null) {
            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }
        //setShareIntent();
        return true;
    }

    // open file manager
    @Override
    public void onClick(View v) {
        Log.d("Main", "onClick");
        Intent fileManagerIntent = new Intent(this, FileManager.class);
        Log.d("Main", "fileManagerIntent");
        startActivity(fileManagerIntent);
    }

    // open selected book
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LibraryItem item = (LibraryItem) libraryAdapter.getItem(position);
        Intent bookReaderIntent = new Intent(MainActivity.this, BookReader.class);
        bookReaderIntent.putExtra("fileToOpen", item.getFilepath());
        Log.d("MainActivity", item.getFilepath());
        bookReaderIntent.putExtra("title", item.getTitle());
        Log.d("MainActivity", item.getTitle());
        bookReaderIntent.putExtra("fromLibrary", true);
        startActivity(bookReaderIntent);
    }

}
