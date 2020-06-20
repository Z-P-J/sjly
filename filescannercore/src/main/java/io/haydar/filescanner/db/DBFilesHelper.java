package io.haydar.filescanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.haydar.filescanner.FileInfo;
import io.haydar.filescanner.FileScanner;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore.db
 * @DATE 2017-04-13
 */

public class DBFilesHelper {
    private Context mContext;

    public DBFilesHelper(Context context) {
        this.mContext = context;
    }

    public static String deleteTableSql() {
        return "DROP TABLE IF EXISTS " + FilesDBContract.TABLE_NAME;
    }

    public static String createTableSql() {
        return "CREATE TABLE " + FilesDBContract.TABLE_NAME + "(" +
                FilesDBContract.COLUMN_NAME_DATA + " TEXT NOT NULL PRIMARY KEY," +
                FilesDBContract.COLUMN_NAME_FOLDER_ID + " TEXT," +
                FilesDBContract.COLUMN_NAME_SIZE + " INTEGER," +
                FilesDBContract.COLUMN_NAME_MODIFIED_TIME + " INTEGER)";

    }

    public void insertNewFiles(ArrayList<FileInfo> filesArrayList, String folder_id, FileScanner.ScannerListener mCommonListener) {
        DBManager.getWriteDB(mContext).beginTransaction();
        try {
            for (FileInfo fileInfo : filesArrayList) {
                ContentValues contentValues = new ContentValues();
                if (!TextUtils.isEmpty(fileInfo.getFilePath())) {
                    contentValues.put(FilesDBContract.COLUMN_NAME_DATA, fileInfo.getFilePath());
                }
                contentValues.put(FilesDBContract.COLUMN_NAME_MODIFIED_TIME, fileInfo.getLastModifyTime());
                contentValues.put(FilesDBContract.COLUMN_NAME_SIZE, fileInfo.getFileSize());
                contentValues.put(FilesDBContract.COLUMN_NAME_FOLDER_ID, folder_id);
                DBManager.getWriteDB(mContext).insert(FilesDBContract.TABLE_NAME, null, contentValues);
                if (mCommonListener != null) {
                    mCommonListener.onScanningFiles(fileInfo, FileScanner.SCANNER_TYPE_ADD);
                }
            }
            DBManager.getWriteDB(mContext).setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.getWriteDB(mContext).endTransaction();
        }
    }

    public void clearTable() {
        DBManager.getWriteDB(mContext).delete(FilesDBContract.TABLE_NAME, null, null);
    }

    public void deleteFilesByFolderId(String folderId, FileScanner.ScannerListener mCommonListener) {
        if (mCommonListener != null) {
            List<FileInfo> delList = getFilesByFolderId(folderId);
            for (FileInfo fileInfo : delList) {
                mCommonListener.onScanningFiles(fileInfo, FileScanner.SCANNER_TYPE_DEL);
            }
        }
        String whereClause = FilesDBContract.COLUMN_NAME_FOLDER_ID + "=" + folderId;
        DBManager.getWriteDB(mContext).delete(FilesDBContract.TABLE_NAME, whereClause, null);


    }

    public ArrayList<FileInfo> getFilesByFolderId(String folderId) {
        Cursor cursor = null;
        ArrayList<FileInfo> filesList = null;
        try {
            cursor = DBManager.getReadDB(mContext).rawQuery("SELECT * FROM " + FilesDBContract.TABLE_NAME + " where " + FilesDBContract.COLUMN_NAME_FOLDER_ID + "=" + folderId, null);
            if (cursor == null) {
                return null;
            }
            FileInfo fileInfo;
            filesList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String _data = cursor.getString(cursor.getColumnIndexOrThrow(FilesDBContract.COLUMN_NAME_DATA));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(FilesDBContract.COLUMN_NAME_SIZE));
                long time = cursor.getLong(cursor.getColumnIndexOrThrow(FilesDBContract.COLUMN_NAME_MODIFIED_TIME));
                fileInfo = new FileInfo();
                fileInfo.setFilePath(_data);
                fileInfo.setLastModifyTime(time);
                fileInfo.setFileSize(size);
                filesList.add(fileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return filesList;
    }

    public ArrayList<FileInfo> getAllFiles() {
        Cursor cursor = null;
        ArrayList<FileInfo> filesList = null;
        try {
            cursor = DBManager.getReadDB(mContext).rawQuery("SELECT * FROM " + FilesDBContract.TABLE_NAME, null);
            if (cursor == null) {
                return null;
            }
            FileInfo fileInfo;
            filesList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String _data = cursor.getString(cursor.getColumnIndexOrThrow(FilesDBContract.COLUMN_NAME_DATA));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(FilesDBContract.COLUMN_NAME_SIZE));
                long time = cursor.getLong(cursor.getColumnIndexOrThrow(FilesDBContract.COLUMN_NAME_MODIFIED_TIME));
                fileInfo = new FileInfo();
                fileInfo.setFilePath(_data);
                fileInfo.setLastModifyTime(time);
                fileInfo.setFileSize(size);
                filesList.add(fileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return filesList;
    }

    public void deleteFile(String filePath) {
        String whereClause = FilesDBContract.COLUMN_NAME_DATA + "=" + "'"+filePath+"'";
        DBManager.getWriteDB(mContext).delete(FilesDBContract.TABLE_NAME, whereClause, null);

    }

    private static class FilesDBContract {
        private final static String TABLE_NAME = "files_table";
        private final static String COLUMN_NAME_DATA = "_data";
        private final static String COLUMN_NAME_FOLDER_ID = "folder_id";
        private final static String COLUMN_NAME_SIZE = "_size";
        private final static String COLUMN_NAME_MODIFIED_TIME = "modified_time";
    }
}
