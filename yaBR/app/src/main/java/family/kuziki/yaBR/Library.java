package family.kuziki.yaBR;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Library {
    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String COVER = "cover";

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

    public LibraryItem getBookInfo(String fileName) throws ExecutionException, InterruptedException {
        String urlString = "";
        String bookTitle = fileName.replace("_", " ");
        Pattern p = Pattern.compile("^*.\\w\\w\\w$");
        bookTitle = p.matcher(bookTitle).replaceAll("");
        try {
            urlString = QUERY_URL + URLEncoder.encode(bookTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("query", "URL :" + urlString);
        RequestResponse response = sendRequest(urlString);
        JSONObject jsonObject = response.getResponse().optJSONArray("docs").optJSONObject(0);
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

    private RequestResponse sendRequest(String urlString) throws ExecutionException, InterruptedException {
        RequestResponse response = new RequestResponse();
        response.setResponse( new GetJSONTask().execute(urlString).get());
        response.setIsDone();
        return response;
    }

    public void saveBooks(SharedPreferences sharedPreferences) {
        Log.d("Library", "saveBooks");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        for (int i = 0; i < libraryItems.size(); i++){
            LibraryItem item = libraryItems.get(i);

            if (item.getCover() != null) {
                String imageID = item.getCover();
                String cover = i + "_" + COVER;
                editor.putString(cover, imageID);
            }
            String bookTitle = "";
            String authorName = "";
            if (item.getTitle() != null) {
                bookTitle = item.getTitle();
                String title = i + "_" + TITLE;
                editor.putString(title, bookTitle);
            }
            if (item.getAuthor()!= null) {
                authorName = item.getAuthor();
                String author = i + "_" + AUTHOR;
                editor.putString(author, authorName);
            }
        }
        editor.commit();
        Log.d("Library", "editorCommit");
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
