package io.haydar.filescanner;


import java.util.ArrayList;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore
 * @DATE 2017-04-14
 */

public class FileScannerJni {

    public static final String TAG = "FileScannerJni";
    private static boolean mLoadSuccess;

    static {
        try {
            System.loadLibrary("FileScanner");
            mLoadSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            mLoadSuccess = false;
            // Log.e(TAG, "static initializer: loadLibrary error!!!" + e.getMessage());
        }

    }


    public static boolean isLoadJNISuccess() {
        return mLoadSuccess;
    }

    public static native ArrayList<FileInfo> scanDirs(String paramString);

    public static native ArrayList<FileInfo> scanFiles(String filePath, String type);

    public static native long getFileLastModifiedTime(String filePath);

    public static native ArrayList<FileInfo> scanUpdateDirs(String filePath);
}
