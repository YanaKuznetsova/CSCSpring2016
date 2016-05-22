package family.kuziki.yaBR.library;

/**
 * Class allowing to handle and save the data of the book (the author, title, filepath and cover) in the Library
 */

import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LibraryItem implements Serializable {
    private String cover;
    private String title;
    private String author;
    private String filepath;

    public LibraryItem(){
        cover = null;
        title = null;
        author = null;
        filepath = null;
    }

    public LibraryItem(String title, String author, String cover, String filepath) {
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.filepath = filepath;
    }

    public String getCover(){
        return cover;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthor(){
        return author;
    }

    public String getFilepath(){
        return filepath;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
        Log.d("LibraryItem", filepath);
    }

    public String serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(this);
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
