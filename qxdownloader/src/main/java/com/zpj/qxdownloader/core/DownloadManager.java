package com.zpj.qxdownloader.core;

import android.content.Context;

import com.zpj.qxdownloader.config.MissionConfig;
import com.zpj.qxdownloader.config.QianXunConfig;
import com.zpj.qxdownloader.config.ThreadPoolConfig;

import java.util.ArrayList;
import java.util.List;

public interface DownloadManager
{
	int BLOCK_SIZE = 1024 * 1024;

	List<DownloadMission> ALL_MISSIONS = new ArrayList<>();

	interface DownloadManagerListener {
		void onMissionAdd();
		void onMissionDelete();
	}

	int startMission(String url);
	int startMission(String url, String name);
	int startMission(String url, String name, MissionConfig config);
//	int startMission(String url, String name, int threads);
//	int startMission(String url, String name, int threads, String cookie, String userAgent);

	void resumeMission(int id);
	void resumeMission(String uuid);
	void resumeAllMissions();

	void pauseMission(int id);
	void pauseMission(String uuid);
	void pauseAllMissions();

	void deleteMission(int id);
	void deleteMission(String uuid);
	void deleteMission(DownloadMission mission);
	void deleteAllMissions();

	void clearMission(int i);
	void clearMission(String uuid);
	void clearAllMissions();

	DownloadMission getMission(int id);
	DownloadMission getMission(String uuid);

	int getCount();

	Context getContext();

	QianXunConfig getQianXunConfig();

	ThreadPoolConfig getThreadPoolConfig();

	boolean shouldMissionWaiting();

//	String getDownloadPath();
//
//	void setDownloadPath(String downloadPath);

	void loadMissions();

	void setDownloadManagerListener(DownloadManagerListener downloadManagerListener);

	public DownloadManagerListener getDownloadManagerListener();

	List<DownloadMission> getMissions();
}
