package com.zpj.qxdownloader;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.zpj.qxdownloader.config.MissionConfig;
import com.zpj.qxdownloader.config.QianXunConfig;
import com.zpj.qxdownloader.core.DownloadManager;
import com.zpj.qxdownloader.core.DownloadManagerImpl;
import com.zpj.qxdownloader.core.DownloadMission;
import com.zpj.qxdownloader.util.FileUtil;
import com.zpj.qxdownloader.util.NetworkChangeReceiver;
import com.zpj.qxdownloader.util.content.SPHelper;
import com.zpj.qxdownloader.util.notification.NotifyUtil;
import com.zpj.qxdownloader.util.permission.PermissionUtil;

import java.io.File;

/**
 *
 * @author Z-P-J
 * */
public class QXDownloader {

//    private static DownloadManager mManager;
//    private static DownloadManagerService.DMBinder mBinder;
//    private static ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName p1, IBinder binder) {
//            mBinder = (DownloadManagerService.DMBinder) binder;
//            mManager = mBinder.getDownloadManager();
//            if (registerListener != null) {
//                registerListener.onServiceConnected();
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName p1) {
//
//        }
//    };
//
//    private static RegisterListener registerListener;

    private static volatile boolean isRunning = true;

    private static boolean waitingForInternet = false;

    private QXDownloader() {

    }

//    public interface RegisterListener {
//        void onServiceConnected();
//    }

//    public void setRegisterListener(RegisterListener registerListener) {
//        this.registerListener = registerListener;
//    }

    public static void init(Context context) {
        init(QianXunConfig.with(context));
    }

    public static void init(final QianXunConfig options) {
//        register(context, null);

        final Context context = options.getContext();

        PermissionUtil.grandStoragePermission(context);

        SPHelper.init(context);
        NotifyUtil.init(context);
        DownloadManagerImpl.register(options);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        options.getContext().registerReceiver(NetworkChangeReceiver.getInstance(), intentFilter);
    }

//    public static void register(Context context, RegisterListener registerListener) {
//        NotifyUtil.init(context);
//        QXDownloader.registerListener = registerListener;
//        if (mManager == null || mBinder == null) {
//            Intent intent = new Intent();
//            intent.setClass(context, DownloadManagerService.class);
//            context.startService(intent);
//            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//        }
//    }

    public static void unInit() {
        DownloadManagerImpl.unRegister();
        NotifyUtil.cancelAll();
        System.exit(0);
//        context.unbindService(mConnection);
//        Intent intent = new Intent();
//        intent.setClass(context, DownloadManagerService.class);
//        context.stopService(intent);
    }

    public static DownloadMission download(String url) {
//        哈哈.apk
        int res = DownloadManagerImpl.getInstance().startMission(url);
        if (res == -1) {
//            Log.d("download", "文件已存在！！！");
            return null;
        }
        //        mBinder.onMissionAdded(downloadMission);
        return DownloadManagerImpl.getInstance().getMission(res);
    }

    public static DownloadMission download(String url, String name) {
        int res = DownloadManagerImpl.getInstance().startMission(url, name);
        if (res == -1) {
            return null;
        }
        return DownloadManagerImpl.getInstance().getMission(res);
    }

    public static DownloadMission download(String url, MissionConfig options) {
        int res = DownloadManagerImpl.getInstance().startMission(url, "", options);
        if (res == -1) {
            return null;
        }
        return DownloadManagerImpl.getInstance().getMission(res);
    }

    public static DownloadMission download(String url, String name, MissionConfig options) {
//        哈哈.apk
        int res = DownloadManagerImpl.getInstance().startMission(url, name, options);
        //Log.d("download", "文件已存在！！！");
        if (res == -1) {
            return null;
        }
        //mBinder.onMissionAdded(downloadMission);
        return DownloadManagerImpl.getInstance().getMission(res);
    }

//    public static DownloadMission download(String url, int threadCount) {
////        哈哈.apk
//        int res = DownloadManagerImpl.getInstance().startMission(url, "", threadCount);
//        if (res == -1) {
////            Log.d("download", "文件已存在！！！");
//            return null;
//        }
//        //        mBinder.onMissionAdded(downloadMission);
//        return DownloadManagerImpl.getInstance().getMission(res);
//    }

    public static void pause(DownloadMission mission) {
//        mManager.pauseMission(mission.uuid);
        mission.pause();
//        mBinder.onMissionRemoved(mission);
    }

    public static void resume(DownloadMission mission) {
        mission.start();
//        mBinder.onMissionAdded(mission);
    }

    public static void delete(DownloadMission mission) {
//        mManager.deleteMission(mission.uuid);
//        mission.pause();
//        mission.delete();
        DownloadManagerImpl.getInstance().deleteMission(mission.uuid);
//        mBinder.onMissionRemoved(mission);
    }

    public static void clear(DownloadMission mission) {
//        mManager.clearMission(mission.uuid);
        mission.pause();
        mission.deleteMissionInfo();
        DownloadManagerImpl.getInstance().getMissions().remove(mission);
//        mBinder.onMissionRemoved(mission);
    }

    public static boolean rename(DownloadMission mission, String name) {
        Context context = DownloadManagerImpl.getInstance().getContext();
        if (TextUtils.equals(mission.name, name)) {
            Toast.makeText(context, "请输入不同的名字", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mission.isRunning()) {
            Toast.makeText(context, "请暂停下载后再试", Toast.LENGTH_SHORT).show();
            return false;
        }
        File file = new File(mission.getDownloadPath() + File.separator + mission.name);
        File file2Rename = new File(mission.getDownloadPath() + File.separator + name);
        boolean success = file.renameTo(file2Rename);
        if (success) {
            mission.name = name;
            mission.writeMissionInfo();
            DownloadManager.DownloadManagerListener downloadManagerListener =  DownloadManagerImpl.getInstance().getDownloadManagerListener();
            if (downloadManagerListener != null) {
                downloadManagerListener.onMissionAdd();
            }
            Toast.makeText(context, "重命名成功", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "重命名失败", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static void openFile(DownloadMission mission) {
        if (mission.isFinished()) {
            File file = new File(mission.getDownloadPath(), mission.name);
            Context context = DownloadManagerImpl.getInstance().getContext();
            if (!file.exists()) {
                Toast.makeText(context, "下载文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }
//            File renameFile = new File(Environment.getExternalStorageDirectory().getPath(), baseDownloadTask.getFilename() + ".apk");
//            file.renameTo(renameFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, FileUtil.getFileProviderName(context), file);
                intent.setDataAndType(contentUri, FileUtil.getMIMEType(file));
            } else {
                intent.setDataAndType(Uri.fromFile(file), FileUtil.getMIMEType(file));
            }
//                                intent.setDataAndType(Uri.fromFile(renameFile), FileUtil.getMIMEType(renameFile));
            context.startActivity(intent);
        }
    }

    public static void pauseAll() {
        DownloadManagerImpl.getInstance().pauseAllMissions();
    }

    public static void waitingForInternet() {
        waitingForInternet = true;
        for (DownloadMission mission : DownloadManagerImpl.getInstance().getMissions()) {
            if (mission.isRunning()) {
                mission.waiting();
            }
        }
    }

    public static boolean isWaitingForInternet() {
        return waitingForInternet;
    }

    public static void resumeAll() {
        DownloadManagerImpl.getInstance().resumeAllMissions();
    }

    public static void deleteAll() {
        DownloadManagerImpl.getInstance().deleteAllMissions();
    }

    public static void clearAll() {
        DownloadManagerImpl.getInstance().clearAllMissions();
    }

//    public static DownloadManagerService.DMBinder getBinder() {
//        return mBinder;
//    }

    public static DownloadManager getDownloadManager() {
        return DownloadManagerImpl.getInstance();
    }

}
