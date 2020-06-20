package io.haydar.filescanner;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import io.haydar.filescanner.util.LogUtil;

/**
 * @author Haydar
 * @Package io.haydar.filescannercore
 * @DATE 2017-04-13
 */

public class FileScanner {
    public static final String TAG = "FileScanner";
    private Context mContext;
    private static FileScanner instance;
    private static String type;
    public static final int SCANNER_TYPE_ADD=1;
    public static final int SCANNER_TYPE_DEL=2;

    public static FileScanner getInstance(Context paramContext) {
        if (instance == null) {
            instance = new FileScanner(paramContext);
        }
        return instance;
    }

    private FileScanner(Context context) {
        this.mContext = context;

    }

    public FileScanner setType(String s) {
        type = s;
        return instance;
    }

    public static String getType() {
        return type;
    }

    /**
     * 开始扫描
     */
    public void start(ScannerListener mCommonListener) {
        if (TextUtils.isEmpty(type)) {
            LogUtil.i(TAG, "start: 需要查找的类型为空");
            return;
        }
        LogUtil.i(TAG, "start: -----start scan-----");
        setCommonListener(mCommonListener);
        //判断是否全盘扫描
        boolean bool = isNeedToScannerAll();
        if (bool) {
            //全盘扫描
            LogUtil.i(TAG, "start: 全盘扫描");
            ScannerUtil.scanAllDirAsync(mContext);
        } else {
            //增量扫描
            LogUtil.i(TAG, "start: 增量扫描");
            ScannerUtil.updateAllDirAsync(mContext);
        }

    }

    private boolean isNeedToScannerAll() {
        return ScannerUtil.isNeedToScannerAll(mContext);
    }


    private void setCommonListener(ScannerListener mCommonListener) {
        LocalFileCacheManager.getInstance(mContext).setCommonListener(mCommonListener);
    }

    public void clear() {
        LocalFileCacheManager.getInstance(mContext).clear();
    }

    public ArrayList<FileInfo> getAllFiles() {
        return LocalFileCacheManager.getInstance(mContext).getAllFiles();
    }

    public interface ScannerListener {

        /**
         * 扫描开始
         */
        void onScanBegin();

        /**
         * 扫描结束
         */
        void onScanEnd();

        /**
         * 扫描进行中
         * @param paramString 文件夹地址
         * @param progress  扫描进度
         */
        void onScanning(String paramString, int progress);

        /**
         * 扫描进行中，文件的更新
         * @param info
         * @param type  SCANNER_TYPE_ADD：添加；SCANNER_TYPE_DEL：删除
         */
        void onScanningFiles(FileInfo info,int type);
    }
}

