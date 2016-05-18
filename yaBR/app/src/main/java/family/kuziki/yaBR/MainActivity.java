package family.kuziki.yaBR;

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
import android.widget.EditText;
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
    EditText mainEditText;
    ShareActionProvider shareActionProvider;
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String COVER = "cover";
    private static final String PREFS = "prefs";
//    private static final String PREF_NAME = "name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);
        mainListView = (ListView) findViewById(R.id.main_listview);
        mainListView.setOnItemClickListener(this);
        //setShareIntent();
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

//    private void setShareIntent() {
//        if (shareActionProvider != null) {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());
//            shareActionProvider.setShareIntent(shareIntent);
//        }
//    }

    @Override
    public void onClick(View v) {
        Log.d("Main", "onClick");
        Intent fileManagerIntent = new Intent(this, FileManager.class);
        Log.d("Main", "fileManagerIntent");
        startActivity(fileManagerIntent);
    /*    try {
            queryBooks(mainEditText.getText().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

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

//    public void displayWelcome() {
//        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
//        String name = sharedPreferences.getString(PREF_NAME, "");
//        if (name.length() > 0) {
////            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
//        }  else {
////            // Grab the EditText's input
////            String inputName = input.getText().toString();
////            // Put it into memory (don't forget to commit!)
////            SharedPreferences.Editor e = sharedPreferences.edit();
////            e.putString(PREF_NAME, inputName);
////            e.commit();
//        }
//    }

}
