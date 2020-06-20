package io.haydar.filescanner;

import java.util.ArrayList;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore
 * @DATE 2017-04-14
 */

public class ScannerWrapper {
    public static final String TAG = "ScannerWrapper";

    public static ArrayList scanDirs(String absolutePath) {

        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();

        if (FileScannerJni.isLoadJNISuccess()) {
            fileInfoArrayList = FileScannerJni.scanDirs(absolutePath);
        }
        return fileInfoArrayList;
    }

    public static ArrayList<FileInfo> scanFiles(String filePath,String type) {
        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();
        if (FileScannerJni.isLoadJNISuccess()) {
            fileInfoArrayList = FileScannerJni.scanFiles(filePath,type);
        }
        return fileInfoArrayList;

    }

    public static long getFileLastModifiedTime(String filePath) {
        if (FileScannerJni.isLoadJNISuccess()) {
            return FileScannerJni.getFileLastModifiedTime(filePath);
        } else {
            return -1l;
        }

    }

    public static ArrayList<FileInfo> scanUpdateDirs(String filePath) {
        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();
        if (FileScannerJni.isLoadJNISuccess()) {
            fileInfoArrayList = FileScannerJni.scanUpdateDirs(filePath);
        }
        return fileInfoArrayList;
    }
}
