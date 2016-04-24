package ru.chocco_crokko.musikant.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.chocco_crokko.musikant.utils.DBConstants;

/*
 * this class creates database. Method getWritableDatabase() returns the same database
 */
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, DBConstants.DB_NAME, null, DBConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder command = new StringBuilder()
                .append("CREATE TABLE ").append(DBConstants.TABLE_NAME)
                .append(" (")
                .append(DBConstants.ID).append(" INTEGER PRIMARY KEY, ")
                .append(DBConstants.NAME).append(" TEXT NOT NULL, ")
                .append(DBConstants.GENRES).append(" TEXT, ")
                .append(DBConstants.ALBUMS).append(" INTEGER, ")
                .append(DBConstants.TRACKS).append(" INTEGER, ")
                .append(DBConstants.LINK).append(" TEXT, ")
                .append(DBConstants.DESCRIPTION).append(" TEXT NOT NULL, ")
                .append(DBConstants.SMALL_IMAGE).append(" TEXT NOT NULL, ")
                .append(DBConstants.BIG_IMAGE).append(" TEXT NOT NULL, ")
                .append(DBConstants.IN_FAVORITE).append(" INTEGER")
                .append(");");
        db.execSQL(command.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // there is no way to check changes in Json file (except comparing element by element, but it takes a lot of time),
        // that's why this method is empty. There could be version in file
    }
}
