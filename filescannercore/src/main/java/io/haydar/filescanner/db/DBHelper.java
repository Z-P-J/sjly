package io.haydar.filescanner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore
 * @DATE 2017-04-13
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = "DBHelper";
    public static final String DB_NAME = "FileScanner";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBFolderHelper.deleteTableSql());
        db.execSQL(DBFolderHelper.createTableSql());
        db.execSQL(DBFilesHelper.deleteTableSql());
        db.execSQL(DBFilesHelper.createTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
