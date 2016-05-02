package family.kuziki.yaBR;

import android.util.Log;

import ebook.EBook;

public class EBookWrapper {

    private int position;
    private String text;
    private EBook eBook;
    private static final int pageCapacity = 1500;

    public EBookWrapper(EBook eBook, String text) {
        this.eBook = eBook;
        this.text = text;
        this.position = 0;
    }

    public EBookWrapper(EBook eBook, String text, int position) {
        this(eBook, text);
        this.position = position;
    }

    public int getProgress(String text){
        return (int)(position/text.length()*100);
    }

    public int getPosition(){
        return position;
    }

    public String getPage(){
        return text.substring(position, position+pageCapacity);
    }

    public void nextPage(){
        position = Math.min(position + pageCapacity, text.length()-pageCapacity);
        Log.d("EBookWrapper", "next page " + String.valueOf(position));
        //int end = Math.min(beginning + pageCapacity, text.length());
        //Log.d("end", String.valueOf(end));
    }

    public void previousPage(){
        position = Math.max(position - pageCapacity, 0);
        Log.d("EBookWrapper", "previous page " + String.valueOf(position));
//        Log.d("beginning", String.valueOf(beginning));
//        int end = beginning + pageCapacity;
//        Log.d("end", String.valueOf(end));
    }
}
