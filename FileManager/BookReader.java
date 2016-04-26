package family.kuziki.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import ebook.*;
import ebook.parser.*;

/**
 * Created by Admin on 016 16.04.16.
 */
public class BookReader extends Activity {
    public String fileToOpen;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
 //   private int bufferSize = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_reader);
        this.fileToOpen = this.getIntent().getStringExtra("fileToOpen");

        if (fileToOpen != null) {
            readFile();
        }
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void readFile() {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream stream = new FileInputStream (new File(fileToOpen))){
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
        update(sb.toString());
    }

//    private void readFileFromSD(){
//        StringBuilder sb = new StringBuilder();
//        try {
//            InputStreamReader in = new InputStreamReader(openFileInput(fileToOpen));
//            char[] inputBuffer = new char[bufferSize];
//            while (in.ready()) {
//                in.read(inputBuffer);
//                sb.append(inputBuffer);
//            }
//        } catch (FileNotFoundException e) {
//            Log.d("BookReader: ", "file to open not found " + e.toString());
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.d("BookReader: ", "problems with file reading" + e.toString());
//            e.printStackTrace();
//        }
//        update(sb.toString());
//    }

    public void update(String string) {
        TextView text = (TextView) findViewById(R.id.openedFile);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(string);
    }

    public void parseBook(){
        Parser parser = new InstantParser();
        EBook ebook = parser.parse(filename);
        if (ebook.isOk) {
            // do anything with ebook
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
//                "BookReader Page", // TODO: Define a title for the content shown.
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
//                "BookReader Page", // TODO: Define a title for the content shown.
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
