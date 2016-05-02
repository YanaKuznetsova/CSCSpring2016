package family.kuziki.yaBR;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

import ebook.*;
import ebook.parser.*;

public class BookReader extends Activity implements View.OnTouchListener, View.OnLongClickListener {
    public String fileToOpen;
    private TextView text;
    private EBookWrapper eBookWrapper;
    //   private int bufferSize = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_reader);
        this.fileToOpen = this.getIntent().getStringExtra("fileToOpen");
        if (fileToOpen != null) {
            text = (TextView) findViewById(R.id.openedFile);
            //text.setOnTouchListener(this);
            text.setOnLongClickListener(this);
            readFile();
            update();
        }
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
        eBookWrapper = new EBookWrapper(parseBook(), sb.toString());
    }


    public void update() {
        text = (TextView) findViewById(R.id.openedFile);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(eBookWrapper.getPage());
    }

    public EBook parseBook() {
        Parser parser = new InstantParser();
        EBook ebook = parser.parse(fileToOpen);
        if (ebook.isOk) {
            // do anything with ebook
        }
        return ebook;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float xScreen = event.getX();
        float yScreen = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие

                break;
            case MotionEvent.ACTION_SCROLL:
                goToNextPage();
                break;
            case MotionEvent.ACTION_MOVE: // движение
                if (xScreen > (text.getWidth() / 2)) {
                    Log.d("BookReader", "Next page");
                    goToNextPage();
                } else {
                    Log.d("BookReader", "Previous page");
                    goToPreviousPage();
                }
                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void goToPreviousPage() {
        Toast.makeText(this, "Previous page", Toast.LENGTH_LONG).show();
        eBookWrapper.previousPage();
        update();
    }

    private void goToNextPage() {
        Toast.makeText(this, "Next page", Toast.LENGTH_LONG).show();
        eBookWrapper.nextPage();
        update();
    }


    @Override

    public boolean onLongClick(View v) {
        try {
            Log.d("BookReader", "translate");
            Translator.getInstance().translate("Hello world!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}
