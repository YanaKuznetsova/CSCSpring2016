package family.kuziki.filemanager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager extends ListActivity {
    private List<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = new File("/");
    //when application started
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set main layout
        setContentView(R.layout.main);
        //browse to root directory
        browseTo(new File("/"));
    }

    //browse to parent directory
    private void upOneLevel(){
        if(currentDirectory.getParent() != null) {
            browseTo(currentDirectory.getParentFile());
        }
    }

    //browse to file or directory
    private void browseTo(final File aDirectory){
        //if we want to browse directory
        if (aDirectory.isDirectory()){
            //fill list with files from this directory
            currentDirectory = aDirectory;
            fill(aDirectory.listFiles());
            //set titleManager text
            TextView titleManager = (TextView) findViewById(R.id.titleManager);
        } else {
            //if we want to open file, show this dialog:
            //listener when YES button clicked
            DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file//" + aDirectory.getAbsolutePath()));
                    startActivity(intent);
                }
            };

            //listener when NO button clicked
            DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };

            //create dialog
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_mess + aDirectory.getName() + "?")
                    .setPositiveButton(R.string.positiveButton, okButtonListener)
                    .setNegativeButton(R.string.negativeButton, cancelButtonListener)
                    .show();
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
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, R.layout.row, directoryEntries);
        setListAdapter(directoryList);
    }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id){
        //get selected file name
        int selectionRowId = position;
        String selectedFileString = directoryEntries.get(selectionRowId);

        //if we select ".." then go upper
        if (selectedFileString.equals("..")){
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
}
