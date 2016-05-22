package family.kuziki.yaBR;
/**
 * File manager allowing selection files on device
 */
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileManager extends ListActivity {
    TextView title;
    private List<DirectoryData> directoryEntries = new ArrayList<>();
    private File currentDirectory = Environment.getExternalStorageDirectory();

    private static class DirectoryData{
        String path;
        String name;
        DirectoryData(String filePath, String fileName){
            path = filePath;
            name = fileName;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    //when application started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("FileManager", "onCreate");
        super.onCreate(savedInstanceState);
        //set file_manager_main layout
        setContentView(R.layout.file_manager_main);
        title = (TextView) findViewById(R.id.titleManager);
        browseTo(Environment.getExternalStorageDirectory());
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
            title.setText(currentDirectory.getAbsolutePath());
        } else {
            Intent bookReaderIntent = new Intent(FileManager.this, BookReader.class);
            bookReaderIntent.putExtra("fileToOpen", aDirectory.getAbsolutePath());
            String fileName = aDirectory.getName();
            int pos = fileName.lastIndexOf(".");
            if (pos > 0) {
                fileName = fileName.substring(0, pos);
            }
            bookReaderIntent.putExtra("title", fileName);
            startActivity(bookReaderIntent);
        }
    }

    //fill list
    private void fill(File[] files) {
        //clear list
        directoryEntries.clear();
        if (currentDirectory.getParent() != null) {
            directoryEntries.add (new DirectoryData("..", "..")) ;
        }
        //add every file into list
        for (File file : files) {
            directoryEntries.add(new DirectoryData(file.getAbsolutePath(), file.getName()));
        }
        //create array adapter to show everything
        ArrayAdapter<DirectoryData> directoryList = new ArrayAdapter<DirectoryData>(this, R.layout.file_manager_row, directoryEntries);
        directoryList.sort(new Comparator<DirectoryData>() {
            @Override
            public int compare(DirectoryData a, DirectoryData b) {
                return a.name.compareTo(b.name);
            }
        });
        setListAdapter(directoryList);
    }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        //get selected file name
        int selectionRowId = position;
        String selectedFileString = directoryEntries.get(selectionRowId).path;
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
}
