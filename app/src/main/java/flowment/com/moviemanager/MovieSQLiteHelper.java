package flowment.com.moviemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Khaled Reguieg on 06.08.2015.
 */
public class MovieSQLiteHelper extends SQLiteOpenHelper {

    String createSQL = "CREATE TABLE movie (title TEXT, director TEXT, actors TEXT, date TEXT, city TEXT)";

    public MovieSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL(createSQL);
    }
}
