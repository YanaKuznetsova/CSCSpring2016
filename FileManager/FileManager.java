package family.kuziki.filemanager;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager extends ListActivity {
    private List<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = Environment.getExternalStorageDirectory();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //when application started
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set file_manager_main layout
        setContentView(R.layout.file_manager_main);
        //browse to root directory
//        browseTo(new File("/"));
//        Log.d("Clicker:", Boolean.toString(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())));
        browseTo(Environment.getExternalStorageDirectory());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //browse to parent directory
    private void upOneLevel() {
        if (currentDirectory.getParent() != null) {
            browseTo(currentDirectory.getParentFile());
        }
    }

    //browse to file or directory
    private void browseTo(final File aDirectory) {
        Log.d("CLICKER: ", aDirectory.getAbsolutePath());
        //if we want to browse directory
        if (aDirectory.isDirectory()) {
            //fill list with files from this directory
            currentDirectory = aDirectory;
            fill(aDirectory.listFiles());
            //set titleManager text
            TextView titleManager = (TextView) findViewById(R.id.titleManager);
        } else {
            Intent bookReaderIntent = new Intent(FileManager.this, BookReader.class);
            bookReaderIntent.putExtra("fileToOpen", aDirectory.getAbsolutePath());
            startActivity(bookReaderIntent);

//            //if we want to open file, show this dialog:
//            //listener when YES button clicked
//            DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
////                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file//" + aDirectory.getAbsolutePath()));
////                    startActivity(intent);
//                    Intent bookReaderIntent = new Intent(FileManager.this, BookReader.class);
//                    bookReaderIntent.putExtra("fileToOpen", aDirectory.getAbsolutePath());
//                    startActivity(bookReaderIntent);
//                }
//            };
//
//            //listener when NO button clicked
//            DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            };
//
//            //create dialog
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.dialog_title)
//                    .setMessage(R.string.dialog_mess + aDirectory.getName() + "?")
//                    .setPositiveButton(R.string.positiveButton, okButtonListener)
//                    .setNegativeButton(R.string.negativeButton, cancelButtonListener)
//                    .show();
        }
    }

    //fill list
    private void fill(File[] files) {
        //clear list
        directoryEntries.clear();
        if (currentDirectory.getParent() != null) {
            directoryEntries.add("..");
        }
        //add every file into list
        for (File file : files) {
            directoryEntries.add(file.getAbsolutePath());
        }

        //create array adapter to show everything
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, R.layout.file_manager_row, directoryEntries);
        setListAdapter(directoryList);
    }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        //get selected file name
        int selectionRowId = position;
        String selectedFileString = directoryEntries.get(selectionRowId);
        Log.d("CLICKER: ", selectedFileString);

        //if we select ".." then go upper
        if (selectedFileString.equals("..")) {
            upOneLevel();
        } else {
            //browse to clicked file or directory using browseTo()
            File chosenFile = null;
            chosenFile = new File(selectedFileString);
            if (chosenFile != null) {
                browseTo(chosenFile);
            }
        }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "FileManager Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://family.kuziki.filemanager/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "FileManager Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://family.kuziki.filemanager/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }
}
