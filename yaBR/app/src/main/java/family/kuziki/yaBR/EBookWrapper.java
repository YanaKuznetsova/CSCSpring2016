package family.kuziki.yaBR;

import android.util.Log;

import ebook.EBook;
import ebook.Person;

public class EBookWrapper {

    private int position;
    private String author;
    private String cover;
    private EBook eBook;

    public EBookWrapper(EBook eBook) {
        this.eBook = eBook;
        this.position = 0;
    }

    public int getPosition(){
        return position;
    }

    public String getTitle() {
        return eBook.title;
    }

    public CharSequence getText() {
        return eBook.body;
    }

    public String getAuthor() {
        return eBook.getAuthors();
    }
}
