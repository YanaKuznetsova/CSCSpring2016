package family.kuziki.yaBR;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import ebook.*;
import ebook.parser.*;
import family.kuziki.yaBR.library.Library;
import family.kuziki.yaBR.library.LibraryItem;
import family.kuziki.yaBR.translation.Database;
import family.kuziki.yaBR.translation.Translator;

//public class BookReader extends Activity implements View.OnClickListener, View.OnLongClickListener {
public class BookReader extends Activity {

    public String fileToOpen;
    public String fileName;
    private TextView text;
    private TextView bookTitle;
    private EBookWrapper eBookWrapper;
    public static Database database;

    private float pointX;
    private float pointY;
    private long startClickTime;
    private boolean isClick = false;

    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_reader);
        this.fileToOpen = this.getIntent().getStringExtra("fileToOpen");
        boolean fromLibrary = this.getIntent().getBooleanExtra("fromLibrary", false);
        if (fileToOpen != null) {
            Library library = Library.getInstance();
            try {
                fileName = this.getIntent().getStringExtra("fileName");
                Log.d("BookReader", fileToOpen);
                Log.d("BookReader", fileName);

                if (!fromLibrary) {
                    LibraryItem newItem = library.getBookInfo(fileName);
                    newItem.setFilepath(fileToOpen);
                    library.addLibraryItem(newItem);
                    Library.getInstance().saveBooks(this);
                }

                text = (TextView) findViewById(R.id.openedFile);
                text.setMovementMethod(new ScrollingMovementMethod());

                bookTitle = (TextView) findViewById(R.id.bookTitle_textView);
                //text.setOnClickListener(this);

                readFile();
                update();
                database = new Database(this);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        saveScreenParameters();
        text.setOnTouchListener(new OnSwipeTouchListener(this));
    }

    private void saveScreenParameters() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private String getWordOnPosition(int position, String string) {
        if (Character.isLetter(string.charAt(position))) {
            while (position >= 0 && Character.isLetter(string.charAt(position))) {
                position -= 1;
            }
            position += 1;
            int posEnd = position;
            while (posEnd < string.length() && Character.isLetter(string.charAt(posEnd))) {
                posEnd += 1;
            }
            return string.substring(position, posEnd);
        } else {
            showToast("Error");
            return null;
        }
    }

    private void processLongClickEvent(View view, MotionEvent event) {
        vibrate(100);

        int position = text.getOffsetForPosition(event.getX(), event.getY());
        if (position < 0 || position >= text.getText().length()) {
            showToast("Error");
            return;
        }

        String word = getWordOnPosition(position, text.getText().toString());
        if (word == null) {
            return;
        }

        showToast(word);
    }

    private void processClickEvent(MotionEvent event) {
        if (event.getX() > screenWidth / 1.3) {
            goToNextPage();
        } else if (event.getX() < screenWidth / 4.0) {
            goToPreviousPage();
        }
    }

    private void vibrate(int time) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(time);
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
        bookTitle = bookTitle.replace("-", " ");
        Pattern p = Pattern.compile("^*.\\w\\w\\w$");
        return (p.matcher(bookTitle).replaceAll(""));
    }

    public void update() {
        text = (TextView) findViewById(R.id.openedFile);
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


//    @Override
//
//    public boolean onLongClick(View view) {
//        try {
//            Log.d("BookReader", "translate");
//            Translator.getInstance().translate("Hello world!", database);
//            Log.d("BookReader", "translate2");
//            Translator.getInstance().translate(getWord(), database);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

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

    private class OnSwipeTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private static final float EPS = 10;
        private static final long MIN_CLICK_DURATION = 650;
        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pointX = event.getX();
                pointY = event.getY();
                startClickTime = Calendar.getInstance().getTimeInMillis();
                isClick = true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!isSamePoint(event.getX(), event.getY())) {
                    isClick = false;
                    return false;
                }
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration >= MIN_CLICK_DURATION && isClick) {
                    isClick = false;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isClick) {
                    processClickEvent(event);
                }
            }
            return true;
        }

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            processLongClickEvent(text, e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        public void onSwipeRight() {
            goToPreviousPage();
        }

        public void onSwipeLeft() {
            goToNextPage();
        }

        private boolean isSamePoint(float x, float y) {
            return Math.abs(x - pointX) < EPS && Math.abs(y - pointY) < EPS;
        }
    }
}
