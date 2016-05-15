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
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    TextView mainTextView;
    ListView mainListView;
    LibraryAdapter libraryAdapter;
    Button mainButton;
    EditText mainEditText;
    public SharedPreferences sharedPreferences;
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
            loadBooks();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainListView.setAdapter(libraryAdapter);

    }

    private void loadBooks() throws JSONException {
        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        Map<String,?> keys = sharedPreferences.getAll();
        int numOfItems = 0;
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            numOfItems++;
        }
        String[][] books = new String[numOfItems][3];
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String[] item = entry.getKey().split("_");
            Log.d("map", entry.getKey());
            Log.d("map", item[0]);
            Log.d("map", item[1]);
            int id = Integer.parseInt(item[0]);
            String type = item[1];
            Log.d("map", String.valueOf(entry.getValue()));
            if (type.equals(TITLE)) {
                books[id][0] = (String) entry.getValue();
            }
            if (type.equals(AUTHOR)) {
                books[id][1] = (String) entry.getValue();
            }
            if (type.equals(COVER)) {
                books[id][2] = (String) entry.getValue();
            }
        }

        for (int i = 0; i < numOfItems; i++) {
            Library.getInstance().addLibraryItem(new LibraryItem(books[i][0], books[i][1], books[i][2], null));
        }
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
        bookReaderIntent.putExtra("fileName", item.getTitle());
        Log.d("MainActivity", item.getTitle());
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
