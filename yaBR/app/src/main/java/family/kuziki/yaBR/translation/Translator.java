package family.kuziki.yaBR.translation;
/**
 * Class providing communication with database of translated words;
 * sending requests and receiving translation;
 * saving new words to database.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import family.kuziki.yaBR.GetJSON;

public class Translator {

    private static final String urlTranslate = "https://translate.yandex.net/api/v1.5/tr.json/translate?&lang=ru&format=plain&text=";
    private static final String apiKey = "&key=trnsl.1.1.20160425T151703Z.4adf02246a2895da.3ea89905d8057da0427aaef1a2eb984bb2cd5a6b";

    // Singleton
    private Translator(){
    }

    public static Translator getInstance(){
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        public static Translator instance = new Translator();
    }

    //check whether the word is in the database
    public String usedWord(String word, Database database) {
        try (SQLiteDatabase db = database.getWritableDatabase()) {
            // check the word
            String selection = "word = ?";
            Cursor cursor = db.query("mytable", null, selection, new String[]{word}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int wordColIndex = cursor.getColumnIndex("word");
                    int translationColIndex = cursor.getColumnIndex("translation");
                    String possibleWord = cursor.getString(wordColIndex);
                    if (possibleWord.equals(word)) {
                        Log.d("Translator_Database", "word is found!");
                        return cursor.getString(translationColIndex);
                    }
               }
            }
            return null;
        }
    }

    // translate the word not yet present in the database; add it to the database
    public String translate(String word, Database database) throws InterruptedException, MalformedURLException {
        String translation = usedWord(word, database);
        if (translation != null) {
            return translation;
        }
        // if DB doesn't contain the word, add it to the DB
        JSONObject response = null;
        try {
            response = sendRequest(word);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        translation = parseReceivedRequest(response);
        Log.d("Translator", translation);
        ContentValues contentValues = new ContentValues();
        contentValues.put("word", word);
        contentValues.put("translation", translation);
        SQLiteDatabase db = database.getWritableDatabase();
        long rowID = db.insert("mytable", null, contentValues);
        Log.d("Translator_Database", String.valueOf(rowID));
        db.close();
        return translation;
    }

    // parsing JSON response
    private String parseReceivedRequest(JSONObject response) {
        try {
            String textResponse = response.getJSONArray("text").getString(0);
            return textResponse;
        } catch (JSONException e) {
            e.printStackTrace();
            return "Translation failed";
        }
    }

    // sending JSON request
    private JSONObject sendRequest(String searchString) throws ExecutionException, InterruptedException, JSONException, MalformedURLException {
        // Prepare search string to be put in a URL
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String s = urlTranslate + urlString + apiKey;
        Log.d("Translator", "URL :" + s);

        return new GetJSON(s).getJson();
    }
}
