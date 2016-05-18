package family.kuziki.yaBR;

import android.util.Log;

import ebook.EBook;

public class EBookWrapper {

    private int position;
    private String text;
    private String author;
    private String title;
    private String cover;
    private EBook eBook;

    public EBookWrapper(EBook eBook, String text) {
        this.eBook = eBook;
        this.text = text;
        this.position = 0;
    }

    public EBookWrapper(EBook eBook, String text, String title) {
        this.eBook = eBook;
        this.text = text;
        this.position = 0;
        this.title = title;
    }

    public int getProgress(String text){
        return (int)(position/text.length()*100);
    }

    public int getPosition(){
        return position;
    }

    public String getTitle() {
        return title;
    }

    public CharSequence getText() {
        return  text;
    }
}
