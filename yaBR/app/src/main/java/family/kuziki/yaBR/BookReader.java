package family.kuziki.yaBR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
    public String title;
    private TextView text;
    private TextView bookTitle;
    private EBookWrapper eBookWrapper;
    private Database database;
    private float pointX;
    private float pointY;
    private long startClickTime;
    private boolean isClick = false;
    private boolean fromLibrary;
    private int screenWidth;

    private Pagination pagination;

    private class BookLoader extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Library library = Library.getInstance();
            try {

                Log.d("BookReader", fileToOpen);
                Log.d("BookReader", title);

                Parser p = new InstantParser();
                EBook eBook = p.parse(fileToOpen);

                if (eBook.title != null) {
                    title = eBook.title;
                }
                Log.d("Parser", title);

                if (!fromLibrary) {
                    LibraryItem newItem = library.getBookInfo(title);;
                    newItem.setFilepath(fileToOpen);
                    library.addLibraryItem(newItem);
                    Library.getInstance().saveBooks(BookReader.this);
                }

                if (eBook.title == null) {
                    eBook.title = convertTitle();
                }
                eBookWrapper = new EBookWrapper(eBook);

                pagination = new Pagination(eBookWrapper.getText(), text);
                pagination.layout(new Pagination.Task() {
                    @Override
                    public void action(int num) {
                        publishProgress(num);
                    }
                });

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bookTitle.setText(eBookWrapper.getTitle());
            update();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            String loading = "\n\n Loading...\n\n" + progress[0] + "%";
            text.setText(loading);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.book_reader);
        this.fileToOpen = this.getIntent().getStringExtra("fileToOpen");
        fromLibrary = this.getIntent().getBooleanExtra("fromLibrary", false);
        if (fileToOpen != null) {
            title = this.getIntent().getStringExtra("title");
            text = (TextView) findViewById(R.id.openedFile);
            text.setMovementMethod(new ScrollingMovementMethod());

            bookTitle = (TextView) findViewById(R.id.bookTitle_textView);
            database = new Database(BookReader.this);

            text.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    text.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    String loading = "\n\n Loading...";
                    text.setText(loading);
                    new BookLoader().execute();
                }
            });

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
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 50);
        toast.show();
    }

    private CharSequence highlight(CharSequence text) {
        SpannableString result = new SpannableString(text);
        StringBuilder sb = new StringBuilder();
        int curPos = 0;
        for (int i = 0; i < result.length(); i++) {
            if (Character.isLetter(result.charAt(i))) {
                sb.append(result.charAt(i));
            }
            if (result.charAt(i) == ' ') {
                if (Translator.getInstance().usedWord(sb.toString(), database) != null) {
                    result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.userWord)), curPos, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                sb.setLength(0);
                curPos = i + 1;
            }
        }

        return result;
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

    private void processLongClickEvent(View view, MotionEvent event) throws InterruptedException, MalformedURLException {
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
        Log.d("BookReader_translation", word);
        String translation = Translator.getInstance().translate(word, database);
        showToast(word + " = " + translation);
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

    public String convertTitle() {
        if (fromLibrary) {
            return title;
        }
        String bookTitle = title.replace("_", " ");
        bookTitle = bookTitle.replace("-", " ");
        Pattern p = Pattern.compile("^*.\\w\\w\\w$");
        return (p.matcher(bookTitle).replaceAll(""));
    }

    public void update() {
        text = (TextView) findViewById(R.id.openedFile);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(highlight(pagination.getPage()));
    }

    public EBook parseBook() {
        Parser parser = new InstantParser();
        EBook ebook = parser.parse(fileToOpen);
        if (ebook.isOk) {
            // do anything with ebook
        }
        return ebook;
    }
    private void goToPreviousPage() {
        //Toast.makeText(this, "Previous page", Toast.LENGTH_LONG).show();
        pagination.previousPage();
        update();
    }

    private void goToNextPage() {
        //Toast.makeText(this, "Next page", Toast.LENGTH_LONG).show();
        pagination.nextPage();
        update();
    }

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
            try {
                processLongClickEvent(text, e);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
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
