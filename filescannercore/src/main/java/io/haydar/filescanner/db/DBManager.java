package io.haydar.filescanner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import io.haydar.filescanner.util.LogUtil;

/**
 * 管理数据库类
 * 打开和关闭数据库
 *
 * @author Haydar
 * @Package io.haydar.aotumusic.db
 * @DATE 2017-03-28
 */

public class DBManager {
    public static final String TAG = "DBManager";
    private static SQLiteDatabase mWriteDB;
    private static SQLiteDatabase mReadDB;
    private static DBHelper mDbHelper;
    private Context context;
    /**
     * 关闭数据库
     */
    public static void close() {
        if (mWriteDB != null) {
            mWriteDB.close();
            mWriteDB = null;
            LogUtil.i(TAG, "close: writeDB-close");
        }
        if (mReadDB != null) {
            mReadDB.close();
            mReadDB = null;
            LogUtil.i(TAG, "close: readDB-close");
        }
    }

    /**
     * 打开数据库
     *
     * @param context
     */
    public static void open(Context context) {
        openWriteDB(context);
        openReadDB(context);

    }

    /**
     * 打开读数据库
     *
     * @param context
     */
    private static void openReadDB(Context context) {
        if (mReadDB == null || !mReadDB.isOpen()) {
            mReadDB = getDbHelper(context).getReadableDatabase();

        }
    }

    /**
     * 打开写数据库
     *
     * @param context
     */
    private static void openWriteDB(Context context) {
        if (mWriteDB == null || !mWriteDB.isOpen()) {
            mWriteDB = getDbHelper(context).getWritableDatabase();
            mWriteDB.enableWriteAheadLogging();

        }


    }


    /**
     * 获得dbhelper
     *
     * @param context
     * @return
     */
    public static DBHelper getDbHelper(Context context) {
        if (mDbHelper == null) {
            //新建dbhelper
            mDbHelper = new DBHelper(context);

        }
        return mDbHelper;
    }


    public static SQLiteDatabase getReadDB(Context context) {
        if(mReadDB==null||!mReadDB.isOpen()){
            openReadDB(context);
        }
        return mReadDB;
    }

    public static SQLiteDatabase getWriteDB(Context context) {
        if(mWriteDB==null||!mWriteDB.isOpen()){
            openWriteDB(context);
        }
        return mWriteDB;
    }
}
