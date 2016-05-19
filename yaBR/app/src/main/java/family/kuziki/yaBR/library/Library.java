package family.kuziki.yaBR.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import family.kuziki.yaBR.GetJSON;

public class Library {
    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String COVER = "cover";
    private static final String PREFS = "prefs";

    ArrayList<LibraryItem> libraryItems;

    // Singleton
    private Library(){
        libraryItems = new ArrayList<>();
    }

    public static Library getInstance(){
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        public static Library instance = new Library();
    }

    public ArrayList<LibraryItem> getLibraryItems(){
        return libraryItems;
    }

    public boolean addLibraryItem(LibraryItem item) {
        for (LibraryItem li : libraryItems) {
            if ((li.getFilepath() != null) && (StringUtils.equals(li.getFilepath(), item.getFilepath()))) {
                return false;
            }
        }
        libraryItems.add(item);
        return true;
    }

    public LibraryItem getBookInfo(String fileName) throws ExecutionException, InterruptedException, MalformedURLException {
        String urlString = "";
        String bookTitle = fileName.replace("_", " ");
        bookTitle = bookTitle.replace("-", " ");
        try {
            urlString = QUERY_URL + URLEncoder.encode(bookTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("query", "URL :" + urlString);
        JSONObject response = new GetJSON(urlString).getJson();
        JSONObject jsonObject = response.optJSONArray("docs").optJSONObject(0);
        //Log.d("LibraryBookInfo_response", jsonObject.toString());

        String authorName = "";
        LibraryItem newItem = new LibraryItem();
        if (jsonObject != null) {
            if (jsonObject.has("title")) {
                bookTitle = jsonObject.optString("title");
            }
            Log.d("LibraryBookInfo", bookTitle);
            if (jsonObject.has("author_name")) {
                authorName = jsonObject.optJSONArray("author_name").optString(0);
                Log.d("LibraryBookInfo_author", authorName);
            }
            if (jsonObject.has("cover_i")) {
                String imageID = jsonObject.optString("cover_i");
                newItem.setCover(imageID);
                Log.d("LibraryBookInfo_cover", imageID);
            }
        }
        newItem.setTitle(bookTitle);
        newItem.setAuthor(authorName);
        newItem.setFilepath(fileName);
        return newItem;
    }

    public void saveBooks(Context context) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Log.d("Library", "saveBooks");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        for (int i = 0; i < libraryItems.size(); i++){
            LibraryItem item = libraryItems.get(i);
            editor.putString("Book_"+i, serialize(item));
        }
        editor.commit();
        Log.d("Library", "editorCommit");
    }

    public void loadBooks(Context context) throws JSONException, IOException, ClassNotFoundException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Map<String,?> keys = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String[] item = entry.getKey().split("_");
            LibraryItem li = Library.deserialize((String) entry.getValue());
            addLibraryItem(li);
        }
    }

    public static String serialize(LibraryItem li) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(li);
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    public static LibraryItem deserialize(String str) throws IOException, ClassNotFoundException {
        byte [] data = Base64.decode(str, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return (LibraryItem)o;
    }

}
