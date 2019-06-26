//package com.zpj.qxdownloader.get;
//
//import android.util.Log;
//
//import com.zpj.qxdownloader.util.io.BufferedRandomAccessFile;
//
//import java.io.BufferedInputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//// Single-threaded fallback mode
//public class DownloadRunnableFallback implements Runnable
//{
//	private final DownloadMission mMission;
//	//private int mId;
//
//	public DownloadRunnableFallback(DownloadMission mission) {
//		//mId = id;
//		mMission = mission;
//	}
//
//	@Override
//	public void run() {
//		try {
//			URL url = new URL(mMission.url);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestProperty("Cookie", mMission.cookie);
//			conn.setRequestProperty("User-Agent", mMission.userAgent);
////			conn.setRequestProperty("Accept", "*/*");
////			conn.setRequestProperty("Referer","https://pan.baidu.com/disk/home");
////			conn.setRequestProperty("Want-Digest", "SHA-512;q=1, SHA-256;q=1, SHA;q=0.1");
////			conn.setRequestProperty("Pragma", "no-cache");
////			conn.setRequestProperty("Cache-Control", "no-cache");
//
//			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 206) {
//				Log.d("DownRunFallback", "error:206");
//				notifyError(DownloadMission.ERROR_SERVER_UNSUPPORTED);
//			} else {
//				BufferedRandomAccessFile f = new BufferedRandomAccessFile(mMission.location + "/" + mMission.name, "rw");
//				f.seek(0);
//				BufferedInputStream ipt = new BufferedInputStream(conn.getInputStream());
//				byte[] buf = new byte[512];
//				int len = 0;
//
//				while ((len = ipt.read(buf, 0, 512)) != -1 && mMission.running) {
//					f.write(buf, 0, len);
//					notifyProgress(len);
//
//					if (Thread.currentThread().interrupted()) {
//						break;
//					}
//
//				}
//
//				f.close();
//				ipt.close();
//			}
//		} catch (Exception e) {
//			notifyError(DownloadMission.ERROR_UNKNOWN);
//		}
//
//		if (mMission.errCode == -1 && mMission.running) {
//			notifyFinished();
//		}
//	}
//
//	private void notifyProgress(final long len) {
//		synchronized (mMission) {
//			mMission.notifyProgress(len);
//		}
//	}
//
//	private void notifyError(final int err) {
//		synchronized (mMission) {
//			mMission.notifyError(err);
//			mMission.pause();
//		}
//	}
//
//	private void notifyFinished() {
//		synchronized (mMission) {
//			mMission.notifyFinished();
//		}
//	}
//}
