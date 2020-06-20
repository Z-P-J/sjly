package io.haydar.filescanner;

import android.content.Context;

import io.haydar.filescanner.db.DBManager;
import io.haydar.filescanner.util.LogUtil;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore
 * @DATE 2017-04-12
 */

public class FileScannerHelper {
    public static final String TAG = "FileScannerHelper";
    private static FileScannerHelper instance = null;

    private FileScannerHelper() {

    }

    public static FileScannerHelper getInstance() {
        if (instance == null) {
            instance = new FileScannerHelper();
        }
        return instance;
    }


    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        //创建数据库
        DBManager.open(context);
    }

    public static void startScanner() {
        LogUtil.i(TAG, "startScanner: 开始全盘扫描");


    }
}
