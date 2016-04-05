package family.kuziki.fileopen;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    public static String fileToOpen;
    private EditText filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePath = (EditText) findViewById(R.id.filePath);
        filePath.addTextChangedListener(filePathWatcher);

    }

    private TextWatcher filePathWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            try {
                fileToOpen = s.toString();
            } catch (Exception e) {
                fileToOpen = "";
            }

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };

    protected void onClick(View v) {
        switch (v.getId()) {
            case R.id.readFile:
                readFile();
                break;
            case R.id.readFromSD:
                readFileSD();
                break;
        }
    }

    public void readFile() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(openFileInput(fileToOpen)));
            String str = "";
            StringBuilder sb = new StringBuilder();
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            update(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFileSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/" + fileToOpen);
            File sdFile = new File(String.valueOf(sdPath));
            try {
                BufferedReader in = new BufferedReader(new FileReader(sdFile));
                String str = "";
                StringBuilder sb = new StringBuilder();
                while ((str = in.readLine()) != null) {
                    sb.append(str);
                }
                update(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void update (String string) {
        ScrollView view = (ScrollView) findViewById(R.id.openedFile);
        TextView text = (TextView) findViewById(R.id.filePath);
        text.setText(string);
        view.addView(text);
    }

}
