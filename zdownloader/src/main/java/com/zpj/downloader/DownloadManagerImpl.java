package com.zpj.downloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zpj.downloader.constant.DefaultConstant;
import com.zpj.downloader.util.FileUtils;
import com.zpj.downloader.util.NetworkChangeReceiver;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

	@SuppressLint("StaticFieldLeak")
	private static DownloadManagerImpl mManager;
	
	private final Context mContext;

	private final ArrayList<WeakReference<DownloadManagerListener>> mListeners = new ArrayList<>();

	private final DownloaderConfig options;

	private static final AtomicInteger downloadingCount = new AtomicInteger(0);

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

	public static DownloadManagerImpl getInstance() {
		if (mManager == null) {
			throw new RuntimeException("must register first!");
		}
		return mManager;
	}

	public static DownloadManagerImpl get() {
		if (mManager == null) {
			synchronized (DownloadManagerImpl.class) {
				if (mManager == null) {
					return null;
				}
			}
		}
		return mManager;
	}

	public static void register(DownloaderConfig options, Class<? extends BaseMission<?>> clazz) {
		if (mManager == null) {
			synchronized (DownloadManagerImpl.class) {
				if (mManager == null) {
					mManager = new DownloadManagerImpl(options.getContext(), options);
					mManager.loadMissions(clazz);
					IntentFilter intentFilter = new IntentFilter();
					intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
					options.getContext().registerReceiver(NetworkChangeReceiver.getInstance(), intentFilter);
				}
			}
		}
	}

	public static void unRegister() {
		getInstance().removeDownloadManagerListener(null);
		getInstance().pauseAllMissions();
		getInstance().getContext().unregisterReceiver(NetworkChangeReceiver.getInstance());
		INotificationInterceptor interceptor = getInstance().getDownloaderConfig().getNotificationInterceptor();
		if (interceptor != null) {
			interceptor.onCancelAll(getInstance().getContext());
		}
		get().ALL_MISSIONS.clear();
		mManager = null;
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
		for (BaseMission<?> mission : get().ALL_MISSIONS) {
			if (!mission.isFinished() && mission.isWaiting()) {
				mission.start();
				break;
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
	public List<BaseMission<?>> getMissions() {
		Collections.sort(ALL_MISSIONS, new Comparator<BaseMission<?>>() {
			@Override
			public int compare(BaseMission<?> o1, BaseMission<?> o2) {
				return - (int) (o1.getCreateTime() - o2.getCreateTime());
			}
		});
		return ALL_MISSIONS;
	}

	@Override
	public void loadMissions() {
		loadMissions(DownloadMission.class);
	}

	@Override
	public void loadMissions(Class<? extends BaseMission<?>> clazz) {
		long time1 = System.currentTimeMillis();
		ALL_MISSIONS.clear();
		File f;
		if (TASK_PATH != null) {
			f = new File(TASK_PATH);
		} else {
			f = new File(getContext().getFilesDir(), MISSIONS_PATH);
		}

		if (f.exists() && f.isDirectory()) {
			Gson gson = new Gson();
			for (final File sub : f.listFiles()) {
				if (sub.isDirectory()) {
					continue;
				}
				if (sub.getName().endsWith(MISSION_INFO_FILE_SUFFIX_NAME)) {
					String str = FileUtils.readFromFile(sub.getAbsolutePath());
					if (!TextUtils.isEmpty(str)) {
						BaseMission<?> mis = gson.fromJson(str, clazz);
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

		Collections.sort(ALL_MISSIONS, new Comparator<BaseMission<?>>() {
			@Override
			public int compare(BaseMission<?> o1, BaseMission<?> o2) {
				return - (int) (o1.getCreateTime() - o2.getCreateTime());
			}
		});
		long time2  = System.currentTimeMillis();
		Log.d(TAG, "deltaTime=" + (time2 - time1));
	}

	@Override
	public void addDownloadManagerListener(DownloadManagerListener downloadManagerListener) {
		this.mListeners.add(new WeakReference<DownloadManagerListener>(downloadManagerListener));
	}

	@Override
	public void removeDownloadManagerListener(DownloadManagerListener downloadManagerListener) {
		for (WeakReference<DownloadManagerListener> reference : this.mListeners) {
			DownloadManagerListener listener = reference.get();
			if (listener == downloadManagerListener) {
				this.mListeners.remove(reference);
				return;
			}
		}
	}

//	@Override
//	public int startMission(String url) {
//		return startMission(url, "");
//	}
//
//	@Override
//	public int startMission(String url, String name) {
//		return startMission(url, name, MissionConfig.with(null));
//	}
//
//	@Override
//	public int startMission(String url, String name, MissionConfig config) {
//		DownloadMission mission = DownloadMission.create(url, name, config);
//		int i = insertMission(mission);
////		if (downloadManagerListener != null) {
////			downloadManagerListener.onMissionAdd(mission);
////		}
//		mission.init();
//		return i;
//	}
//
//	@Override
//	public void resumeMission(int i) {
//		getMission(i).start();
//	}
//
//	@Override
//	public void resumeMission(String uuid) {
//		getMission(uuid).start();
//	}
//
//	@Override
//	public void resumeAllMissions() {
//		for (DownloadMission downloadMission : ALL_MISSIONS) {
//			downloadMission.start();
//		}
//	}
//
//	@Override
//	public void pauseMission(int i) {
//		getMission(i).pause();
//	}
//
//	@Override
//	public void pauseMission(String uuid) {
//		getMission(uuid).pause();
//	}

	@Override
	public void pauseAllMissions() {
		for (BaseMission<?> downloadMission : ALL_MISSIONS) {
			downloadMission.pause();
		}
	}
	
//	@Override
//	public void deleteMission(int i) {
//		DownloadMission d = getMission(i);
//		d.delete();
//		ALL_MISSIONS.remove(i);
//		onMissionDelete(null);
//	}
//
//	@Override
//	public void deleteMission(String uuid) {
//		DownloadMission d = getMission(uuid);
//		d.delete();
//		ALL_MISSIONS.remove(d);
//		onMissionDelete(null);
//	}
//
//	@Override
//	public void deleteMission(DownloadMission mission) {
//		mission.delete();
//		ALL_MISSIONS.remove(mission);
//		onMissionDelete(mission);
//	}

	@Override
	public void deleteAllMissions() {
		for (BaseMission<?> mission : ALL_MISSIONS) {
			mission.delete();
		}
		ALL_MISSIONS.clear();
		onMissionDelete(null);
	}

//	@Override
//	public void clearMission(int i) {
//		DownloadMission d = getMission(i);
//		d.clear();
//		ALL_MISSIONS.remove(i);
//		onMissionDelete(null);
//	}
//
//	@Override
//	public void clearMission(String uuid) {
//		DownloadMission d = getMission(uuid);
//		d.clear();
//		ALL_MISSIONS.remove(d);
//		onMissionDelete(null);
//	}

	@Override
	public void clearAllMissions() {
		for (BaseMission<?> mission : ALL_MISSIONS) {
			mission.clear();
		}
		ALL_MISSIONS.clear();
		onMissionDelete(null);
	}

	@Override
	public BaseMission<?> getMission(int i) {
		return ALL_MISSIONS.get(i);
	}

	@Override
	public BaseMission<?> getMission(String uuid) {
		for (BaseMission<?> mission : ALL_MISSIONS) {
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

	@Override
	public int insertMission(BaseMission<?> mission) {
		if (ALL_MISSIONS.contains(mission)) {
			return ALL_MISSIONS.indexOf(mission);
		}
		ALL_MISSIONS.add(0, mission);
		onMissionAdd(mission);
//		return ALL_MISSIONS.size() - 1;
		return 0;
	}

	@Override
	public boolean shouldMissionWaiting() {
		return DownloadManagerImpl.getDownloadingCount() >= getDownloaderConfig().getConcurrentMissionCount();
	}


	static void onMissionAdd(BaseMission<?> mission) {
		for (WeakReference<DownloadManagerListener> reference : DownloadManagerImpl.get().mListeners) {
			DownloadManagerListener listener = reference.get();
			if (listener != null) {
				listener.onMissionAdd(mission);
			}
		}
	}

	static void onMissionDelete(BaseMission<?> mission) {
		for (WeakReference<DownloadManagerListener> reference : DownloadManagerImpl.get().mListeners) {
			DownloadManagerListener listener = reference.get();
			if (listener != null) {
				listener.onMissionDelete(mission);
			}
		}
	}

	static void onMissionFinished(BaseMission<?> mission) {
		for (WeakReference<DownloadManagerListener> reference : DownloadManagerImpl.get().mListeners) {
			DownloadManagerListener listener = reference.get();
			if (listener != null) {
				listener.onMissionFinished(mission);
			}
		}
	}


	public static List<? extends BaseMission<?>> getAllMissions() {
		return DownloadManagerImpl.getInstance().getMissions();
	}
























//	public static class MissionConfig<T extends DownloadMission> extends BaseConfig<MissionConfig<T>> {
//
//		private Class<T> clazz;
//		private DownloadManager<T> manager;
//		private transient String url;
//
//		private transient String name;
//
//		MissionConfig() {
//
//		}
//
//		public static MissionConfig<? extends DownloadMission> with(String url) {
//			DownloaderConfig config = DownloadManagerImpl.getInstance().getDownloaderConfig();
//			if (config == null) {
//				config = DownloaderConfig.with(DownloadManagerImpl.getInstance().getContext());
//			}
//			MissionConfig<?> missionConfig = new MissionConfig<>().setUrl(url);
//			missionConfig.setNotificationInterceptor(config.getNotificationInterceptor())
//					.setDownloadPath(config.getDownloadPath())
//					.setBufferSize(config.getBufferSize())
//					.setProgressInterval(config.getProgressInterval())
//					.setBlockSize(config.getBlockSize())
//					.setRetryCount(config.getRetryCount())
//					.setRetryDelay(config.getRetryDelay())
//					.setConnectOutTime(config.getConnectOutTime())
//					.setReadOutTime(config.getReadOutTime())
//					.setUserAgent(config.getUserAgent())
//					.setCookie(config.getCookie())
//					.setEnableNotification(config.getEnableNotification());
//			return missionConfig;
////        return new MissionConfig(url)
////                .setNotificationInterceptor(config.notificationInterceptor)
////                .setDownloadPath(config.downloadPath)
////                .setBufferSize(config.bufferSize)
////                .setProgressInterval(config.progressInterval)
////                .setBlockSize(config.blockSize)
////                .setRetryCount(config.retryCount)
////                .setRetryDelay(config.retryDelay)
////                .setConnectOutTime(config.connectOutTime)
////                .setReadOutTime(config.readOutTime)
////                .setUserAgent(config.userAgent)
////                .setCookie(config.cookie)
////                .setEnableNotification(config.enableNotification);
//		}
//
//		MissionConfig<T> setUrl(String url) {
//			this.url = url;
//			return this;
//		}
//
//		MissionConfig<T> setManager(DownloadManager<T> manager) {
//			this.manager = manager;
//			return this;
//		}
//
//		public MissionConfig<T> setName(String name) {
//			this.name = name;
//			return this;
//		}
//
//		public T buildMission() {
//			T mission = manager.createMission(url, name, this);
//			mission.start();
//			return mission;
//		}
//
//		public void start() {
//			buildMission().start();
//		}
//
//	}

}
