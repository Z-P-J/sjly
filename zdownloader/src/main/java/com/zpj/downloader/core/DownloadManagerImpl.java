package com.zpj.downloader.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zpj.downloader.config.MissionConfig;
import com.zpj.downloader.config.DownloaderConfig;
import com.zpj.downloader.config.ThreadPoolConfig;
import com.zpj.downloader.constant.DefaultConstant;
import com.zpj.downloader.util.NetworkChangeReceiver;
import com.zpj.downloader.util.Utility;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Z-P-J
 */
public class DownloadManagerImpl implements DownloadManager {

	private static final String TAG = DownloadManagerImpl.class.getSimpleName();

	private static final String MISSIONS_PATH = "missions";

	private static String DOWNLOAD_PATH = DefaultConstant.DOWNLOAD_PATH;

	static String TASK_PATH;

	static String MISSION_INFO_FILE_SUFFIX_NAME = ".zpj";

	private static DownloadManager mManager;
	
	private Context mContext;

	private DownloadManagerListener downloadManagerListener;

	private DownloaderConfig options;

	private static AtomicInteger downloadingCount = new AtomicInteger(0);

	private DownloadManagerImpl(Context context, DownloaderConfig options) {
		mContext = context;
		this.options = options;

		File path = new File(context.getFilesDir(), MISSIONS_PATH);
		if (!path.exists()) {
			path.mkdirs();
		}
//		TASK_PATH = mContext.getExternalFilesDir("tasks").getAbsolutePath();
		TASK_PATH = path.getPath();
		File file = new File(getDownloadPath());
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static DownloadManager getInstance() {
		if (mManager == null) {
			throw new RuntimeException("must register first!");
		}
		return mManager;
	}

	public static void register(DownloaderConfig options) {
		if (mManager == null) {
			mManager = new DownloadManagerImpl(options.getContext(), options);
			mManager.loadMissions();
		}
	}

	public static void unRegister() {
		getInstance().pauseAllMissions();
		getInstance().getContext().unregisterReceiver(NetworkChangeReceiver.getInstance());
	}

	public static void setDownloadPath(String downloadPath) {
		DownloadManagerImpl.DOWNLOAD_PATH = downloadPath;
	}

	private static int getDownloadingCount() {
		return downloadingCount.get();
	}

	private static String getDownloadPath() {
		return DOWNLOAD_PATH;
	}

	static void decreaseDownloadingCount() {
		downloadingCount.decrementAndGet();
		for (DownloadMission mission : ALL_MISSIONS) {
			if (!mission.isFinished() && mission.isWaiting()) {
				mission.start();
			}
		}
	}

	static void increaseDownloadingCount() {
		downloadingCount.incrementAndGet();
	}

	@Override
	public Context getContext() {
		return mContext;
	}

	@Override
	public DownloaderConfig getDownloaderConfig() {
		return options;
	}

	@Override
	public ThreadPoolConfig getThreadPoolConfig() {
		return options.getThreadPoolConfig();
	}

	@Override
	public List<DownloadMission> getMissions() {
		Collections.sort(ALL_MISSIONS, new Comparator<DownloadMission>() {
			@Override
			public int compare(DownloadMission o1, DownloadMission o2) {
				return - (int) (o1.getCreateTime() - o2.getCreateTime());
			}
		});
		return ALL_MISSIONS;
	}

	@Override
	public void loadMissions() {
		long time1 = System.currentTimeMillis();
		ALL_MISSIONS.clear();
		File f;
		if (TASK_PATH != null) {
			f = new File(TASK_PATH);
		} else {
			f = new File(getContext().getFilesDir(), MISSIONS_PATH);
		}

		if (f.exists() && f.isDirectory()) {
			for (final File sub : f.listFiles()) {
				if (sub.isDirectory()) {
					continue;
				}
				if (sub.getName().endsWith(MISSION_INFO_FILE_SUFFIX_NAME)) {
					String str = Utility.readFromFile(sub.getAbsolutePath());
					if (!TextUtils.isEmpty(str)) {
						DownloadMission mis = new Gson().fromJson(str, DownloadMission.class);
						Log.d("initMissions", "mis=null? " + (mis == null));
						if (mis != null) {
							mis.init();
							insertMission(mis);
						}
					}
				}
			}
		} else {
			f.mkdirs();
		}

		Collections.sort(ALL_MISSIONS, new Comparator<DownloadMission>() {
			@Override
			public int compare(DownloadMission o1, DownloadMission o2) {
				return - (int) (o1.getCreateTime() - o2.getCreateTime());
			}
		});
		long time2  = System.currentTimeMillis();
		Log.d(TAG, "deltaTime=" + (time2 - time1));
	}

	@Override
	public void setDownloadManagerListener(DownloadManagerListener downloadManagerListener) {
		this.downloadManagerListener = downloadManagerListener;
	}

	@Override
	public DownloadManagerListener getDownloadManagerListener() {
		return downloadManagerListener;
	}

	@Override
	public int startMission(String url) {
		return startMission(url, "");
	}

	@Override
	public int startMission(String url, String name) {
		return startMission(url, name, MissionConfig.with());
	}

	@Override
	public int startMission(String url, String name, MissionConfig config) {
		DownloadMission mission = DownloadMission.create(url, name, config);
		int i = insertMission(mission);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionAdd(mission);
		}
		mission.init();
		return i;
	}

	@Override
	public void resumeMission(int i) {
		getMission(i).start();
	}

	@Override
	public void resumeMission(String uuid) {
		getMission(uuid).start();
	}

	@Override
	public void resumeAllMissions() {
		for (DownloadMission downloadMission : ALL_MISSIONS) {
			downloadMission.start();
		}
	}

	@Override
	public void pauseMission(int i) {
		getMission(i).pause();
	}

	@Override
	public void pauseMission(String uuid) {
		getMission(uuid).pause();
	}

	@Override
	public void pauseAllMissions() {
		for (DownloadMission downloadMission : ALL_MISSIONS) {
			downloadMission.pause();
		}
	}
	
	@Override
	public void deleteMission(int i) {
		DownloadMission d = getMission(i);
		d.delete();
		ALL_MISSIONS.remove(i);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete(d);
		}
	}

	@Override
	public void deleteMission(String uuid) {
		DownloadMission d = getMission(uuid);
		d.delete();
		ALL_MISSIONS.remove(d);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete(d);
		}
	}

	@Override
	public void deleteMission(DownloadMission mission) {
		mission.delete();
		ALL_MISSIONS.remove(mission);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete(mission);
		}
	}

	@Override
	public void deleteAllMissions() {
		for (DownloadMission mission : ALL_MISSIONS) {
			mission.delete();
		}
		ALL_MISSIONS.clear();
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete(null);
		}
	}

	@Override
	public void clearMission(int i) {
		DownloadMission d = getMission(i);
		d.clear();
		ALL_MISSIONS.remove(i);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete(null);
		}
	}

	@Override
	public void clearMission(String uuid) {
		DownloadMission d = getMission(uuid);
		d.clear();
		ALL_MISSIONS.remove(d);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete(null);
		}
	}

	@Override
	public void clearAllMissions() {
		for (DownloadMission mission : ALL_MISSIONS) {
			mission.clear();
		}
		ALL_MISSIONS.clear();
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete(null);
		}
	}

	@Override
	public DownloadMission getMission(int i) {
		return ALL_MISSIONS.get(i);
	}

	@Override
	public DownloadMission getMission(String uuid) {
		for (DownloadMission mission : ALL_MISSIONS) {
			if (TextUtils.equals(mission.getUuid(), uuid)) {
				return mission;
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		return ALL_MISSIONS.size();
	}
	
	private int insertMission(DownloadMission mission) {
		ALL_MISSIONS.add(mission);
		return ALL_MISSIONS.size() - 1;
	}

	@Override
	public boolean shouldMissionWaiting() {
		return DownloadManagerImpl.getDownloadingCount() >= getDownloaderConfig().getConcurrentMissionCount();
	}

}
