//package com.zpj.qxdownloader.get;
//
//import android.util.Log;
//import android.util.SparseIntArray;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//
//public class FilteredDownloadManagerWrapper implements DownloadManager
//{
//
//	private boolean mDownloaded; // T=Filter downloaded files; F=Filter downloading files
//	private DownloadManager mManager;
//	private final HashMap<Integer, Integer> mElementsMap = new HashMap<Integer, Integer>();
////	private SparseIntArray mElementsMap = new SparseIntArray();
//
//	public FilteredDownloadManagerWrapper(DownloadManager manager, boolean filterDownloaded) {
//		mManager = manager;
//		mDownloaded = filterDownloaded;
//		refreshMap();
//	}
//
//	public void refreshMap() {
//
//		Log.d("refreshMap", "refreshMap");
//		mElementsMap.clear();
//
//		int size = 0;
//		for (int i = 0; i < mManager.getCount(); i++) {
//			if (mManager.getMission(i).finished == mDownloaded) {
//				mElementsMap.put(size++, i);
//			}
//		}
//	}
//
//	private int toRealPosition(int pos) {
//		if (mElementsMap.containsKey(pos)) {
//			return mElementsMap.get(pos);
//		} else {
//			return -1;
//		}
//	}
//
//	private int toFakePosition(int pos) {
//		Log.d("toFakePosition", "toFakePosition");
////		for (int i = 0; i < mElementsMap.size(); i++) {
////			int key = mElementsMap.keyAt(i);
////			int value = mElementsMap.get(key);
////			if (value == pos) {
////				return key;
////			}
////		}
//		for (Entry<Integer, Integer> entry : mElementsMap.entrySet()) {
//			if (entry.getValue() == pos) {
//				return entry.getKey();
//			}
//		}
//
//		return -1;
//	}
//
//	@Override
//	public int startMission(String url, String name, int threads) {
//		Log.d("startMission", "startMission");
//		int ret = mManager.startMission(url, name, threads);
//		refreshMap();
//		return toFakePosition(ret);
//	}
//
//	@Override
//	public int startMission(String url, String name, int threads, String cookie, String userAgent) {
//		int ret = mManager.startMission(url, name, threads, cookie, userAgent);
//		refreshMap();
//		return toFakePosition(ret);
//	}
//
//	@Override
//	public void resumeMission(int id) {
//		mManager.resumeMission(toRealPosition(id));
//	}
//
//	@Override
//	public void resumeMission(String uuid) {
//		mManager.resumeMission(uuid);
//	}
//
//	@Override
//	public void resumeAllMissions() {
//		mManager.resumeAllMissions();
//	}
//
//	@Override
//	public void pauseMission(int id) {
//		mManager.pauseMission(toRealPosition(id));
//	}
//
//	@Override
//	public void pauseMission(String uuid) {
//		mManager.pauseMission(uuid);
//	}
//
//	@Override
//	public void pauseAllMissions() {
//		mManager.pauseAllMissions();
//	}
//
//	@Override
//	public void deleteMission(int id) {
//		mManager.deleteMission(toRealPosition(id));
//	}
//
//	@Override
//	public void deleteMission(String uuid) {
//		mManager.deleteMission(uuid);
//	}
//
//	@Override
//	public void deleteAllMissions() {
//		mManager.deleteAllMissions();
//	}
//
//	@Override
//	public void clearMission(int i) {
//		mManager.clearMission(i);
//	}
//
//	@Override
//	public void clearMission(String uuid) {
//		mManager.clearMission(uuid);
//	}
//
//	@Override
//	public void clearAllMissions() {
//		mManager.clearAllMissions();
//	}
//
//	@Override
//	public DownloadMission getMission(int id) {
//		return mManager.getMission(toRealPosition(id));
//	}
//
//	@Override
//	public DownloadMission getMission(String uuid) {
//		return mManager.getMission(uuid);
//	}
//
//
//	@Override
//	public int getCount() {
//		return mElementsMap.size();
//	}
//
////	@Override
////	public String getLocation() {
////		return mManager.getLocation();
////	}
//
//	@Override
//	public void loadMissions() {
////		mManager.loadMissions();
//		refreshMap();
//	}
//
//	@Override
//	public void setDownloadManagerListener(DownloadManagerListener downloadManagerListener) {
//		mManager.setDownloadManagerListener(downloadManagerListener);
//	}
//
//	@Override
//	public List<DownloadMission> getMissions() {
//		return mManager.getMissions();
//	}
//
//}
