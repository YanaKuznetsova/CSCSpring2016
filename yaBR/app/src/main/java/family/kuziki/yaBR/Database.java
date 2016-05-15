package family.kuziki.yaBR;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper{

    public Database(Context context) {
        super(context, "myDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate");
        db.execSQL("create table mytable (id integer primary key autoincrement,"
                + "word text, translation text);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
