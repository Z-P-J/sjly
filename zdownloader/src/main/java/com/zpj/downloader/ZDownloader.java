package com.zpj.downloader;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.zpj.downloader.config.DownloaderConfig;
import com.zpj.downloader.config.MissionConfig;
import com.zpj.downloader.core.DownloadManager;
import com.zpj.downloader.core.DownloadManagerImpl;
import com.zpj.downloader.core.DownloadMission;
import com.zpj.downloader.util.FileUtil;
import com.zpj.downloader.util.NetworkChangeReceiver;
import com.zpj.downloader.util.content.SPHelper;
import com.zpj.downloader.util.notification.NotifyUtil;
import com.zpj.downloader.util.permission.PermissionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Z-P-J
 * */
public class ZDownloader {

    private static boolean waitingForInternet = false;

    private ZDownloader() {
        throw new RuntimeException("Wrong operation!");
    }

    public static void init(Context context) {
        init(DownloaderConfig.with(context));
    }

    public static <T extends DownloadMission> void init(final DownloaderConfig options, Class<T> clazz) {
        final Context context = options.getContext();

//        PermissionUtil.grandStoragePermission(context);

        SPHelper.init(context);
        NotifyUtil.init(context);
        DownloadManagerImpl.register(options, clazz);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        options.getContext().registerReceiver(NetworkChangeReceiver.getInstance(), intentFilter);
    }

    public static void init(final DownloaderConfig options) {
        init(options, DownloadMission.class);
    }

    public static void onDestroy() {
        DownloadManagerImpl.unRegister();
        NotifyUtil.cancelAll();
//        System.exit(0);
//        context.unbindService(mConnection);
//        Intent intent = new Intent();
//        intent.setClass(context, DownloadService.class);
//        context.stopService(intent);
    }

    public static DownloadMission download(String url) {
        int res = DownloadManagerImpl.getInstance().startMission(url);
        if (res == -1) {
            return null;
        }
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
        int res = DownloadManagerImpl.getInstance().startMission(url, name, options);
        if (res == -1) {
            return null;
        }
        return DownloadManagerImpl.getInstance().getMission(res);
    }

    public static void pause(DownloadMission mission) {
        mission.pause();
    }

    public static void pause(String uuid) {
        getDownloadManager().getMission(uuid).pause();
    }

    public static void resume(DownloadMission mission) {
        mission.start();
    }

    public static void resume(String uuid) {
        getDownloadManager().getMission(uuid).start();
    }

    public static void delete(DownloadMission mission) {
        DownloadManagerImpl.getInstance().deleteMission(mission);
    }

    public static void clear(DownloadMission mission) {
        mission.clear();
        DownloadManagerImpl.getInstance().getMissions().remove(mission);
    }

    public static void clear(String uuid) {
        clear(DownloadManagerImpl.getInstance().getMission(uuid));
    }

    public static boolean rename(DownloadMission mission, String name) {
        Context context = DownloadManagerImpl.getInstance().getContext();
        if (TextUtils.equals(mission.getTaskName(), name)) {
            Toast.makeText(context, "请输入不同的名字", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mission.isRunning()) {
            Toast.makeText(context, "请暂停下载后再试", Toast.LENGTH_SHORT).show();
            return false;
        }
//        File file = new File(mission.getDownloadPath() + File.separator + mission.getTaskName());
//        File file2Rename = new File(mission.getDownloadPath() + File.separator + name);
        boolean success = mission.renameTo(name);
        if (success) {
            DownloadManager.DownloadManagerListener downloadManagerListener =  DownloadManagerImpl.getInstance().getDownloadManagerListener();
            if (downloadManagerListener != null) {
                downloadManagerListener.onMissionAdd(mission);
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
            File file = mission.getFile();
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

    public static DownloadManager getDownloadManager() {
        return DownloadManagerImpl.getInstance();
    }

    public static List<DownloadMission> getAllMissions() {
        return DownloadManagerImpl.getInstance().getMissions();
    }

    public static List<DownloadMission> getAllMissions(boolean downloading) {
        List<DownloadMission> downloadMissionList = new ArrayList<>();
        for (DownloadMission mission : getAllMissions()) {
            if (mission.isFinished() != downloading) {
                downloadMissionList.add(mission);
            }
        }
        return downloadMissionList;
    }

    public static List<DownloadMission> getRunningMissions() {
        List<DownloadMission> downloadMissionList = new ArrayList<>();
        for (DownloadMission mission : getAllMissions()) {
            if (mission.isRunning()) {
                downloadMissionList.add(mission);
            }
        }
        return downloadMissionList;
    }

    public static List<DownloadMission> getMissions(DownloadMission.MissionStatus status) {
        List<DownloadMission> downloadMissionList = new ArrayList<>();
        for (DownloadMission mission : getAllMissions()) {
            if (status == mission.getStatus()) {
                downloadMissionList.add(mission);
            }
        }
        return downloadMissionList;
    }

}
