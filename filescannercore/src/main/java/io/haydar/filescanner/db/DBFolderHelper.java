package io.haydar.filescanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;

import io.haydar.filescanner.FileInfo;
import io.haydar.filescanner.util.LogUtil;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore.db
 * @DATE 2017-04-13
 */

public class DBFolderHelper {

    public static final String TAG = "TableFolderHelper";
    private Context mContext;

    public DBFolderHelper(Context context) {
        mContext = context;
    }

    public static String deleteTableSql() {
        return "DROP TABLE IF EXISTS " + FolderDBContract.TABLE_NAME;
    }

    public static String createTableSql() {
        return "CREATE TABLE " + FolderDBContract.TABLE_NAME + "(" +
                FolderDBContract.COLUMN_NAME_DIR_PATH + " TEXT NOT NULL PRIMARY KEY, " +
                FolderDBContract.COLUMN_NAME_MODIFIED_TIME + " INTEGER," +
                FolderDBContract.COLUMN_NAME_FILE_COUNT + " INTEGER)";

    }


    public void insertNewDirs(ArrayList<FileInfo> dirsList) {
        DBManager.getWriteDB(mContext).beginTransaction();
        try {
            for (FileInfo fileInfo : dirsList) {
                ContentValues contentValues = new ContentValues();
                if (TextUtils.isEmpty(fileInfo.getFilePath())) {
                    continue;
                }
                contentValues.put(FolderDBContract.COLUMN_NAME_DIR_PATH, fileInfo.getFilePath());
                contentValues.put(FolderDBContract.COLUMN_NAME_MODIFIED_TIME, fileInfo.getLastModifyTime());
                contentValues.put(FolderDBContract.COLUMN_NAME_FILE_COUNT, fileInfo.getCount());
                DBManager.getWriteDB(mContext).insert(FolderDBContract.TABLE_NAME, null, contentValues);
            }
            DBManager.getWriteDB(mContext).setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.getWriteDB(mContext).endTransaction();
        }
    }

    public void clearTable() {
        DBManager.getWriteDB(mContext).delete(FolderDBContract.TABLE_NAME, null, null);
    }

    public int getFolderCount() {
        Cursor cursor = null;
        try {
            cursor = DBManager.getReadDB(mContext).rawQuery("SELECT COUNT(" + FolderDBContract.COLUMN_NAME_DIR_PATH + ") AS NUM FROM " + FolderDBContract.TABLE_NAME, null);
            if (cursor == null) {
                return 0;
            }
            if (cursor.moveToFirst()) {
                int num = cursor.getInt(cursor.getColumnIndexOrThrow("NUM"));
                LogUtil.i(TAG, "getFolderCount: num=" + num);
                return num;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public ArrayList<FileInfo> getAllFolder() {
        Cursor cursor = null;
        ArrayList<FileInfo> dirsList = null;
        try {
            cursor = DBManager.getReadDB(mContext).rawQuery("SELECT * FROM " + FolderDBContract.TABLE_NAME, null);
            if (cursor == null) {
                return null;
            }
            FileInfo fileInfo;
            dirsList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String dirPath = cursor.getString(cursor.getColumnIndexOrThrow(FolderDBContract.COLUMN_NAME_DIR_PATH));
                long modifiedTime = cursor.getLong(cursor.getColumnIndexOrThrow(FolderDBContract.COLUMN_NAME_MODIFIED_TIME));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow(FolderDBContract.COLUMN_NAME_FILE_COUNT));
                fileInfo = new FileInfo();
                fileInfo.setCount(count);
                fileInfo.setFilePath(dirPath);
                fileInfo.setLastModifyTime(modifiedTime);
                dirsList.add(fileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return dirsList;
    }

    /**
     * 批量删除文件夹
     *
     * @param deleteDirsList
     */
    public void deleteFolder(ArrayList<FileInfo> deleteDirsList) {
        DBManager.getWriteDB(mContext).beginTransaction();
        try {
            for (FileInfo fileInfo : deleteDirsList) {
                String whereClause = FolderDBContract.COLUMN_NAME_DIR_PATH + " like " + "'%" + fileInfo.getFilePath() + "%'";
                DBManager.getWriteDB(mContext).delete(FolderDBContract.TABLE_NAME, whereClause, null);
            }
            DBManager.getWriteDB(mContext).setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.getWriteDB(mContext).endTransaction();
        }
    }

    /**
     * 查找数据库里是否有对应的地址目录
     *
     * @param filePath
     * @return
     */
    public boolean isFolderByPath(String filePath) {
        Cursor cursor = null;
        boolean flag = false;
        try {
            cursor = DBManager.getReadDB(mContext).rawQuery("SELECT * FROM " + FolderDBContract.TABLE_NAME + " where " + FolderDBContract.COLUMN_NAME_DIR_PATH + " = '" + filePath+"'", null);
            if (cursor == null) {
                flag = false;
            }
            if (cursor.getCount() > 0) {
                flag = true;
            } else {
                flag = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            flag = false;

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return flag;
    }

    public void updateFolder(FileInfo fileInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FolderDBContract.COLUMN_NAME_MODIFIED_TIME, fileInfo.getLastModifyTime());
        contentValues.put(FolderDBContract.COLUMN_NAME_FILE_COUNT, fileInfo.getCount());
        String selection = FolderDBContract.COLUMN_NAME_DIR_PATH + " = ?";
        String[] whereArgs = {fileInfo.getFilePath()};
        DBManager.getWriteDB(mContext).update(FolderDBContract.TABLE_NAME, contentValues, selection, whereArgs);
    }


    private static class FolderDBContract {
        private final static String TABLE_NAME = "folder_table";
        private final static String COLUMN_NAME_DIR_PATH = "dir_path";
        private final static String COLUMN_NAME_MODIFIED_TIME = "modified_time";
        private final static String COLUMN_NAME_FILE_COUNT = "file_count";
    }


}
