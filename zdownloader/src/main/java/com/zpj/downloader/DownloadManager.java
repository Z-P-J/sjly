package com.zpj.downloader;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public interface DownloadManager {

    List<BaseMission<?>> ALL_MISSIONS = new ArrayList<>();

    interface DownloadManagerListener {
        void onMissionAdd(BaseMission<?> mission);

        void onMissionDelete(BaseMission<?> mission);

        void onMissionFinished(BaseMission<?> mission);
    }

//    T createMission(String url, String name);

//    int startMission(String url);
//
//    int startMission(String url, String name);
//
//    int startMission(String url, String name, MissionConfig config);

//    void resumeMission(int id);
//
//    void resumeMission(String uuid);
//
//    void resumeAllMissions();
//
//    void pauseMission(int id);
//
//    void pauseMission(String uuid);

    void pauseAllMissions();

//    void deleteMission(int id);
//
//    void deleteMission(String uuid);
//
//    void deleteMission(DownloadMission mission);

    void deleteAllMissions();

//    void clearMission(int i);
//
//    void clearMission(String uuid);

    void clearAllMissions();

    BaseMission<?> getMission(int id);

    BaseMission<?> getMission(String uuid);

    int insertMission(BaseMission<?> mission);

    int getCount();

    Context getContext();

    DownloaderConfig getDownloaderConfig();

//    ThreadPoolConfig getThreadPoolConfig();

    boolean shouldMissionWaiting();

    void loadMissions();

    void loadMissions(Class<? extends BaseMission<?>> clazz);

    void addDownloadManagerListener(DownloadManagerListener downloadManagerListener);

    void removeDownloadManagerListener(DownloadManagerListener downloadManagerListener);

//    DownloadManagerListener getDownloadManagerListener();

    List<BaseMission<?>> getMissions();

}
