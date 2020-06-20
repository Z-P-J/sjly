package io.haydar.filescanner;

import android.content.Context;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore
 * @DATE 2017-04-13
 */

public class ScannerUtil {

    /**
     * 全盘扫描
     * @param mContext
     */
    public static void scanAllDirAsync(Context mContext) {
        LocalFileCacheManager.getInstance(mContext).startAllScan();
    }


    /**
     * 获得文件夹hashcode
     * @param filePath
     * @return
     */
    public static String getFolderId(String filePath) {
        return String.valueOf(filePath.toLowerCase().hashCode());
    }

    /**
     * 是否需要全盘扫描
     * @param mContext
     * @return
     */
    public static boolean isNeedToScannerAll(Context mContext) {
      return   LocalFileCacheManager.getInstance(mContext).isNeedToScannerAll();
    }

    /**
     * 增量扫描
     * @param mContext
     */
    public static void updateAllDirAsync(Context mContext) {
        LocalFileCacheManager.getInstance(mContext).startUpdate();
    }
}
