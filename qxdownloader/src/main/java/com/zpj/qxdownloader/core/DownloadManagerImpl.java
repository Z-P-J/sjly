package com.zpj.qxdownloader.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zpj.qxdownloader.config.MissionConfig;
import com.zpj.qxdownloader.config.QianXunConfig;
import com.zpj.qxdownloader.config.ThreadPoolConfig;
import com.zpj.qxdownloader.constant.DefaultConstant;
import com.zpj.qxdownloader.constant.ErrorCode;
import com.zpj.qxdownloader.constant.ResponseCode;
import com.zpj.qxdownloader.util.FileUtil;
import com.zpj.qxdownloader.util.NetworkChangeReceiver;
import com.zpj.qxdownloader.util.Utility;
import com.zpj.qxdownloader.util.io.BufferedRandomAccessFile;

import com.zpj.qxdownloader.jsoup.connection.Connection;
import com.zpj.qxdownloader.jsoup.Jsoup;

import java.io.File;
import java.net.Proxy;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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

	private QianXunConfig options;

	private static AtomicInteger downloadingCount = new AtomicInteger(0);


	private DownloadManagerImpl() {

	}

	private DownloadManagerImpl(Context context, QianXunConfig options) {
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

	public static void register(QianXunConfig options) {
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
	public QianXunConfig getQianXunConfig() {
		return options;
	}

	@Override
	public ThreadPoolConfig getThreadPoolConfig() {
		return options.getThreadPoolConfig();
	}

	@Override
	public List<DownloadMission> getMissions() {
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
			File[] subs = f.listFiles();

			for (final File sub : subs) {
				if (sub.isDirectory()) {
					continue;
				}

				if (sub.getName().endsWith(MISSION_INFO_FILE_SUFFIX_NAME)) {
//					final File newFile = new File(mContext.getExternalFilesDir(MISSIONS_PATH).getAbsolutePath(), sub.getName());
//					FileUtil.copyFile(sub, newFile);
					String str = Utility.readFromFile(sub.getAbsolutePath());
					if (!TextUtils.isEmpty(str)) {
//							Log.d(TAG, "loading mission " + sub.getName());
//							Log.d(TAG, str);
						DownloadMission mis = new Gson().fromJson(str, DownloadMission.class);
//							mis.running = false;
						Log.d("initMissions", "mis=null? " + (mis == null));
						mis.recovered = true;
						mis.init();
						insertMission(mis);
					}
				}
			}
		} else {
			f.mkdirs();
		}
		Collections.sort(ALL_MISSIONS, new Comparator<DownloadMission>() {
			@Override
			public int compare(DownloadMission o1, DownloadMission o2) {
				return - (int) (o1.createTime - o2.createTime);
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
		DownloadMission mission = new DownloadMission();
		mission.url = url;
		mission.originUrl = url;
		mission.name = name;
		mission.uuid = UUID.randomUUID().toString();
		mission.createTime = System.currentTimeMillis();
		mission.timestamp = mission.createTime;
		mission.notifyId = (int)(mission.createTime / 10000) + (int) (mission.createTime % 10000) * 100000;
		mission.missionState = DownloadMission.MissionState.INITING;

		mission.missionConfig = config;

		int i = insertMission(mission);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionAdd();
		}
//		new Initializer(mission).start();
		mission.init();
		return i;
	}

//	public int startMission(String url, String name, int threads, String cookie, String userAgent) {
//		DownloadMission mission = new DownloadMission();
//		mission.uuid = UUID.randomUUID().toString();
//		mission.createTime = System.currentTimeMillis();
//		mission.notifyId = (int)(mission.createTime / 10000) + (int) (mission.createTime % 10000) * 100000;
//		mission.url = url;
//		mission.originUrl = url;
//		mission.name = name;
////		mission.location = options.getDownloadPath();
////		mission.cookie = cookie;
////		mission.hasInit = false;
////		if (!TextUtils.isEmpty(userAgent)) {
////			mission.userAgent = userAgent;
////		} else {
////			mission.userAgent = System.getProperty("http.agent");
////		}
//		mission.timestamp = System.currentTimeMillis();
//		mission.threadCount = threads;
//		int i =  insertMission(mission);
//		if (downloadManagerListener != null) {
//			downloadManagerListener.onMissionAdd();
//		}
//		mission.writeMissionInfo();
//		new Initializer(mission).start();
//		return i;
//	}

	@Override
	public void resumeMission(int i) {
		DownloadMission d = getMission(i);
		d.start();
	}

	@Override
	public void resumeMission(String uuid) {
		DownloadMission d = getMission(uuid);
		d.start();
	}

	@Override
	public void resumeAllMissions() {
		for (DownloadMission downloadMission : ALL_MISSIONS) {
			downloadMission.start();
		}
	}

	@Override
	public void pauseMission(int i) {
		DownloadMission d = getMission(i);
		d.pause();
	}

	@Override
	public void pauseMission(String uuid) {
		DownloadMission d = getMission(uuid);
		d.pause();
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
		d.pause();
		d.delete();
		ALL_MISSIONS.remove(i);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete();
		}
	}

	@Override
	public void deleteMission(String uuid) {
		DownloadMission d = getMission(uuid);
		d.pause();
		d.delete();
		ALL_MISSIONS.remove(d);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete();
		}
	}

	@Override
	public void deleteMission(DownloadMission mission) {
		mission.pause();
		mission.delete();
		ALL_MISSIONS.remove(mission);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete();
		}
	}

	@Override
	public void deleteAllMissions() {
		for (DownloadMission mission : ALL_MISSIONS) {
			mission.pause();
			mission.delete();
		}
		ALL_MISSIONS.clear();
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete();
		}
	}

	@Override
	public void clearMission(int i) {
		DownloadMission d = getMission(i);
		d.pause();
		d.deleteMissionInfo();
		ALL_MISSIONS.remove(i);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete();
		}
	}

	@Override
	public void clearMission(String uuid) {
		DownloadMission d = getMission(uuid);
		d.pause();
		d.deleteMissionInfo();
		ALL_MISSIONS.remove(d);
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete();
		}
	}

	@Override
	public void clearAllMissions() {
		for (DownloadMission mission : ALL_MISSIONS) {
			mission.pause();
			mission.deleteMissionInfo();
		}
		ALL_MISSIONS.clear();
		if (downloadManagerListener != null) {
			downloadManagerListener.onMissionDelete();
		}
	}

	@Override
	public DownloadMission getMission(int i) {
		return ALL_MISSIONS.get(i);
	}

	@Override
	public DownloadMission getMission(String uuid) {
		for (DownloadMission mission : ALL_MISSIONS) {
			if (TextUtils.equals(mission.uuid, uuid)) {
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

//		Log.d("insertMission", "insertMission");
//
//		int i = -1;
//
//		DownloadMission m = null;
//
//		if (ALL_MISSIONS.size() > 0) {
//			do {
//				m = ALL_MISSIONS.get(++i);
//			} while (m.timestamp > mission.timestamp && i < ALL_MISSIONS.size() - 1);
//
//			//if (i > 0) i--;
//		} else {
//			i = 0;
//		}

//		mission.initNotification();
		ALL_MISSIONS.add(mission);
		return ALL_MISSIONS.size() - 1;
	}
	
	private class Initializer extends Thread {
		private DownloadMission mission;
		
		Initializer(DownloadMission mission) {
			this.mission = mission;
		}
		
		@Override
		public void run() {
			try {



				Log.d("Initializer", "run");

				String userAgent = mission.getUserAgent();
				String cookie = mission.getCookie();
				String downloadPath = mission.getDownloadPath();


//				OkHttpClient client = new OkHttpClient();
//				Request request = new Request.Builder()
//						.url(mission.url)
////						.addHeader("User-Agent", UAHelper.getPCBaiduUA())
////						.addHeader("Cookie", UserHelper.getBduss())
////						.addHeader("Accept", "*/*")
////						.addHeader("Referer","https://pan.baidu.com/disk/home")
////						.addHeader("Pragma", "no-cache")
////						.addHeader("Cache-Control", "no-cache")
//						.build();
//
//				Response res = client.newCall(request).execute();
//
//				if (res != null && res.isSuccessful()) {
//					mission.length = res.body().contentLength();
//					Log.d("contentLength", mission.length + "");
//					res.close();
//				}


				Connection.Response response = Jsoup.connect(mission.url)
						.method(Connection.Method.HEAD)
						.followRedirects(false)
						.proxy(Proxy.NO_PROXY)
						.userAgent(userAgent)
						.header("Cookie", cookie)
						.header("Accept", "*/*")
						.header("Referer",mission.url)
//						.header("Access-Control-Expose-Headers", "Content-Disposition")
//						.header("Range", "bytes=0-")
						.headers(mission.getHeaders())
						.timeout(options.getConnectOutTime())
						.ignoreContentType(true)
						.ignoreHttpErrors(true)
						.maxBodySize(0)
						.execute();

				if (handleResponse(response, mission)) {
					return;
				}



				response = Jsoup.connect(mission.url)
						.method(Connection.Method.HEAD)
						.proxy(Proxy.NO_PROXY)
						.userAgent(userAgent)
						.header("Cookie", cookie)
						.header("Accept", "*/*")
						.header("Access-Control-Expose-Headers", "Content-Disposition")
						.header("Referer",mission.url)
						.header("Pragma", "no-cache")
						.header("Range", "bytes=0-")
						.header("Cache-Control", "no-cache")
						.headers(mission.getHeaders())
						.timeout(options.getConnectOutTime())
						.ignoreContentType(true)
						.ignoreHttpErrors(true)
//						.validateTLSCertificates(false)
						.maxBodySize(0)
						.execute();

//				Log.d("statusCode11111111", "       " + response.statusCode());
//
//				Log.d("response.headers()", "1111" + response.headers());
//
//
//				mission.name = getMissionNameFromResponse(response);
//				Log.d("mission.name", "mission.name111=" + mission.name);
//
//				String contentLength = response.header("Content-Length");
//				mission.length = Long.parseLong(contentLength);
//				Log.d("mission.length", "mission.length=" + mission.length);
//
//				if (!checkLength(mission)) {
//					return;
//				}

				if (handleResponse(response, mission)) {
					return;
				}

				if (response.statusCode() != ResponseCode.RESPONSE_206) {
					// Fallback to single thread if no partial content support
					mission.fallback = true;

					Log.d(TAG, "falling back");
				}

				Log.d("mission.name", "mission.name444=" + mission.name);
				if (TextUtils.isEmpty(mission.name)) {
					mission.name = getMissionNameFromUrl(mission, mission.url);
				}

				Log.d("mission.name", "mission.name555=" + mission.name);

				for (DownloadMission downloadMission : ALL_MISSIONS) {
					if (!downloadMission.isIniting() && TextUtils.equals(mission.name, downloadMission.name) &&
							(TextUtils.equals(downloadMission.originUrl.trim(), mission.url.trim()) ||
									TextUtils.equals(downloadMission.redictUrl.trim(), mission.url.trim()))) {
//						if (downloadMission.isIniting()) {
//							Log.d("startMission", "finished");
//						} else {
//							Log.d("startMission", "start");
//							downloadMission.start();
//						}
						downloadMission.start();
						return;
					}
				}

				mission.blocks = mission.length / mission.getBlockSize();

				if (mission.threadCount > mission.blocks) {
					mission.threadCount = (int) mission.blocks;
				}

				if (mission.threadCount <= 0) {
					mission.threadCount = 1;
				}

				if (mission.blocks * mission.getBlockSize() < mission.length) {
					mission.blocks++;
				}


				File loacation = new File(downloadPath);
				if (!loacation.exists()) {
					loacation.mkdirs();
				}
				File file = new File(downloadPath + File.separator + mission.name);
				if (!file.exists()) {
					file.createNewFile();
				}

				Log.d(TAG, "storage=" + Utility.getAvailableSize());
				mission.hasInit = true;

				BufferedRandomAccessFile af = new BufferedRandomAccessFile(downloadPath + File.separator + mission.name, "rw");
				af.setLength(mission.length);
				af.close();

				mission.start();
			} catch (Exception e) {
				// TODO Notify
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public boolean shouldMissionWaiting() {
		return DownloadManagerImpl.getDownloadingCount() >= getQianXunConfig().getConcurrentMissionCount();
	}

	private boolean handleResponse(Connection.Response response, DownloadMission mission) {
		if (response.statusCode() == ResponseCode.RESPONSE_302
				|| response.statusCode() == ResponseCode.RESPONSE_301
				|| response.statusCode() == ResponseCode.RESPONSE_300) {
			String redictUrl = response.header("location");
			Log.d(TAG, "redictUrl=" + redictUrl);
			if (redictUrl != null) {
				mission.url = redictUrl;
				mission.redictUrl = redictUrl;
			}
		} else if (response.statusCode() == ErrorCode.ERROR_SERVER_404){
			mission.errCode = ErrorCode.ERROR_SERVER_404;
			mission.notifyError(ErrorCode.ERROR_SERVER_404);
			return true;
		} else if (response.statusCode() == ResponseCode.RESPONSE_206){
			Log.d("statusCode11111111", "       " + response.statusCode());
			String contentLength = response.header("Content-Length");
			Log.d("response.headers()", "1111" + response.headers());

			if (TextUtils.isEmpty(mission.name)) {
				mission.name = getMissionNameFromResponse(response);
				Log.d("mission.name", "mission.name333=" + mission.name);
			}

			mission.length = Long.parseLong(contentLength);

			Log.d("mission.length", "mission.length=" + mission.length);

			return !checkLength(mission);
		}
		return false;
	}

	private String getMissionNameFromResponse(Connection.Response response) {
		String contentDisposition = response.header("Content-Disposition");
		Log.d("contentDisposition", "contentDisposition=" + contentDisposition);
		if (contentDisposition != null) {
			String[] dispositions = contentDisposition.split(";");
			for (String disposition : dispositions) {
				Log.d("disposition", "disposition=" + disposition);
				if (disposition.contains("filename=")) {
					return disposition.replace("filename=", "");
				}
			}
		}
		return "";
	}

	private String getMissionNameFromUrl(DownloadMission mission, String url) {
		if (!TextUtils.isEmpty(url)) {
			int index = url.lastIndexOf("/");

			if (index > 0) {
				int end = url.lastIndexOf("?");

				if (end < index) {
					end = url.length();
				}

				String name = url.substring(index + 1, end);
				if (!TextUtils.isEmpty(mission.originUrl) && TextUtils.equals(url, mission.originUrl)) {
					String originName = getMissionNameFromUrl(mission, mission.originUrl);
					if (FileUtil.checkFileType(originName) != FileUtil.FILE_TYPE.UNKNOWN) {
						return originName;
					}
				}

				if (FileUtil.checkFileType(name) != FileUtil.FILE_TYPE.UNKNOWN || name.contains(".")) {
					return name;
				} else {
					return name + ".ext";
				}
			}
		}
		return "未知文件.ext";
	}

	private boolean checkLength(DownloadMission mission) {
		if (mission.length <= 0) {
			mission.errCode = ErrorCode.ERROR_SERVER_UNSUPPORTED;
			mission.notifyError(ErrorCode.ERROR_SERVER_UNSUPPORTED);
			return false;
		} else if (mission.length >= Utility.getAvailableSize()) {
			mission.errCode = ErrorCode.ERROR_NO_ENOUGH_SPACE;
			mission.notifyError(ErrorCode.ERROR_NO_ENOUGH_SPACE);
			return false;
		}
		return true;
	}
}
