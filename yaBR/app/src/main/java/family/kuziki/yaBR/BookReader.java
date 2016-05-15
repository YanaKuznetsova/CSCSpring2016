package family.kuziki.yaBR;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import ebook.*;
import ebook.parser.*;

//public class BookReader extends Activity implements View.OnClickListener, View.OnLongClickListener {
public class BookReader extends Activity implements View.OnLongClickListener {

    public String fileToOpen;
    public String fileName;
    private EditText text;
    private TextView bookTitle;
    private EBookWrapper eBookWrapper;
    private static final String PREFS = "prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_reader);
        this.fileToOpen = this.getIntent().getStringExtra("fileToOpen");
        if (fileToOpen != null) {
            Library library = Library.getInstance();
            try {
                fileName = this.getIntent().getStringExtra("fileName");
                Log.d("BookReader", fileToOpen);
                Log.d("BookReader", fileName);
                LibraryItem newItem = library.getBookInfo(fileName);
                newItem.setFilepath(fileToOpen);
                library.addLibraryItem(newItem);
                saveBooks();
                text = (EditText) findViewById(R.id.openedFile);
                text.setMovementMethod(new ScrollingMovementMethod());
                bookTitle = (TextView) findViewById(R.id.bookTitle_textView);
                //text.setOnClickListener(this);
                text.setOnLongClickListener(this);
                readFile();
                update();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveBooks() {
        Log.d("BookReader", "saveBooks");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        Library library = Library.getInstance();
        library.saveBooks(sharedPreferences);
    }

    private void readFile() {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream stream = new FileInputStream(new File(fileToOpen))) {
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String str = "";
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
        } catch (FileNotFoundException e) {
            Log.d("BookReader: ", "file to open not found " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("BookReader: ", "problems with file reading" + e.toString());
            e.printStackTrace();
        }
        String title = convertTitle();
        eBookWrapper = new EBookWrapper(parseBook(), sb.toString(), title);
    }

    public String convertTitle() {
        String bookTitle = fileName.replace("_", " ");
        Pattern p = Pattern.compile("^*.\\w\\w\\w$");
        return (p.matcher(bookTitle).replaceAll(""));
    }

    public void update() {
        text = (EditText) findViewById(R.id.openedFile);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(eBookWrapper.getPage());
        bookTitle = (TextView) findViewById(R.id.bookTitle_textView);
        bookTitle.setText(eBookWrapper.getTitle());
    }

    public EBook parseBook() {
        Parser parser = new InstantParser();
        EBook ebook = parser.parse(fileToOpen);
        if (ebook.isOk) {
            // do anything with ebook
        }
        return ebook;
    }

//    public boolean onTouch(View v, MotionEvent event) {
//        float xScreen = event.getX();
//        float yScreen = event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: // нажатие
//
//                break;
//            case MotionEvent.ACTION_SCROLL:
//                goToNextPage();
//                break;
//            case MotionEvent.ACTION_MOVE: // движение
//                if (xScreen > (text.getWidth() / 2)) {
//                    Log.d("BookReader", "Next page");
//                    goToNextPage();
//                } else {
//                    Log.d("BookReader", "Previous page");
//                    goToPreviousPage();
//                }
//                break;
//            case MotionEvent.ACTION_UP: // отпускание
//            case MotionEvent.ACTION_CANCEL:
//                break;
//        }
//        return true;
//    }

    private void goToPreviousPage() {
        //Toast.makeText(this, "Previous page", Toast.LENGTH_LONG).show();
        eBookWrapper.previousPage();
        update();
    }

    private void goToNextPage() {
        //Toast.makeText(this, "Next page", Toast.LENGTH_LONG).show();
        eBookWrapper.nextPage();
        update();
    }


    @Override

    public boolean onLongClick(View view) {
        try {
            Log.d("BookReader", "translate");
            Translator.getInstance().translate("Hello world!");
            Log.d("BookReader", "translate2");
            Translator.getInstance().translate(getWord());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getWord() {
        int start = text.getSelectionStart();
        Log.d("getWord_start", String.valueOf(start));
        int end = text.getSelectionEnd();
        Log.d("getWord_end", String.valueOf(end));
        String copy = text.getText().toString().substring(start, end);
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        return copy;
    }

//    @Override
//    public void onClick(View v) {
//        float xScreen = v.getX();
//        if (xScreen > (text.getWidth() / 2)) {
//            Log.d("BookReader", "Next page");
//            goToNextPage();
//        } else {
//            Log.d("BookReader", "Previous page");
//            goToPreviousPage();
//        }
//    }


}
