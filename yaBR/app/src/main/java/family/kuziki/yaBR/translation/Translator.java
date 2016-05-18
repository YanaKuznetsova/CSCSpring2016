package family.kuziki.yaBR.translation;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import family.kuziki.yaBR.GetJSONTask;

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

    public String translate(String word, Database database) throws InterruptedException {
        SQLiteDatabase db = database.getWritableDatabase();
        // check the word
        Cursor cursor = db.query("mytable", null, null, null, null, null, null);
        String translation = null;
        if(cursor.moveToFirst()) {
            int wordColIndex = cursor.getColumnIndex("word");
            int translationColIndex = cursor.getColumnIndex("translation");
            do {
                String possibleWord = cursor.getString(wordColIndex);
                if (possibleWord.equals(word)) {
                    translation = cursor.getString(translationColIndex);
                    Log.d("Translator_Database", "word is found!");
                }
            } while (cursor.moveToNext());
        }
        if (translation != null) {
            db.close();
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
        long rowID = db.insert("mytable", null, contentValues);
        Log.d("Translator_Database", String.valueOf(rowID));
        db.close();
        return translation;
    }

    private String parseReceivedRequest(JSONObject response) {
        try {
            return response.getString("text").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Translation failed";
        }
    }

    private JSONObject sendRequest(String searchString) throws ExecutionException, InterruptedException, JSONException {
        // Prepare search string to be put in a URL
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String s = urlTranslate + urlString + apiKey;
        Log.d("Translator" , "URL :" +s);

        return new GetJSONTask().execute(s).get();
    }
}
