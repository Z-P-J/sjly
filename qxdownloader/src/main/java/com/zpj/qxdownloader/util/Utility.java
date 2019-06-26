package com.zpj.qxdownloader.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
* @author Z-P-J
* */
public class Utility {

	private static final int NUM_1024 = 1024;

	private static final int NUM_1024_1024 = 1024 << 10;

	private static final int NUM_1024_1024_1024 = 1024 << 20;

	public enum FileType {
		/**
		 * 文件类型
		 */
		APP,
		VIDEO,
		EXCEL,
		WORD,
		POWERPOINT,
		MUSIC,
		ARCHIVE,
		UNKNOWN
	}



	public static String formatBytes(long bytes) {
		if (bytes < NUM_1024) {
			return String.format(Locale.CHINA, "%d B", bytes);
		} else if (bytes < NUM_1024_1024) {
			return String.format(Locale.CHINA, "%.2f kB", (float) bytes / NUM_1024);
		} else if (bytes < NUM_1024_1024_1024) {
			return String.format(Locale.CHINA, "%.2f MB", (float) bytes / NUM_1024_1024);
		} else {
			return String.format(Locale.CHINA, "%.2f GB", (float) bytes / NUM_1024_1024_1024);
		}
	}
	
	public static String formatSpeed(double speed) {
		if (speed < NUM_1024) {
			return String.format(Locale.CHINA, "%.2f B/s", speed);
		} else if (speed < NUM_1024_1024) {
			return String.format(Locale.CHINA, "%.2f kB/s", speed / NUM_1024);
		} else if (speed < NUM_1024_1024_1024) {
			return String.format(Locale.CHINA, "%.2f MB/s", speed / NUM_1024_1024);
		} else {
			return String.format(Locale.CHINA, "%.2f GB/s", speed / NUM_1024_1024_1024);
		}
	}
	
	public static void writeToFile(String fileName, String content) {
//		Log.d("writeToFile", "fileName=" + fileName);
		try {
			writeToFile(fileName, content.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeToFile(String fileName, byte[] content) {
		File f = new File(fileName);
		
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileOutputStream opt = new FileOutputStream(f, false);
			opt.write(content, 0, content.length);
			opt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readFromFile(String file) {
		try {
			File f = new File(file);
			
			if (!f.exists() || !f.canRead()) {
				return null;
			}

			StringBuilder sb = new StringBuilder();
			FileInputStream fileInputStream = new FileInputStream(f);

			BufferedInputStream ipt = new BufferedInputStream(fileInputStream);
			
			byte[] buf = new byte[512];

			
			while (ipt.available() > 0) {
				int len = ipt.read(buf, 0, 512);
				sb.append(new String(buf, 0, len, StandardCharsets.UTF_8));
			}

			ipt.close();
			fileInputStream.close();

			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getFileExt(String url) {
		if (url.indexOf("?")>-1) {
			url = url.substring(0,url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf(".") );
			if (ext.indexOf("%")>-1) {
				ext = ext.substring(0,ext.indexOf("%"));
			}
			if (ext.indexOf("/")>-1) {
				ext = ext.substring(0,ext.indexOf("/"));
			}
			return ext.toLowerCase();

		}
	}
	
	public static FileType getFileType(String file) {
		if (file.endsWith(".apk")) {
			return FileType.APP;
		} else if (file.endsWith(".mp3") || file.endsWith(".wav") || file.endsWith(".flac")) {
			return FileType.MUSIC;
		} else if (file.endsWith(".mp4") || file.endsWith(".mpeg") || file.endsWith(".rm") || file.endsWith(".rmvb")
					|| file.endsWith(".flv") || file.endsWith(".webp")) {
			return FileType.VIDEO;
		} else if (file.endsWith(".doc") || file.endsWith(".docx")) {
			return FileType.WORD;
		} else if (file.endsWith(".xls") || file.endsWith(".xlsx")) {
			return FileType.EXCEL;
		} else if (file.endsWith(".ppt") || file.endsWith(".pptx")) {
			return FileType.POWERPOINT;
		} else if (file.endsWith(".zip") || file.endsWith(".rar") || file.endsWith(".7z") || file.endsWith(".gz")
					|| file.endsWith("tar") || file.endsWith(".bz")) {
			return FileType.ARCHIVE;
		} else {
			return FileType.UNKNOWN;
		}
	}
	
	public static boolean isDirectoryAvailble(String path) {
		File dir = new File(path);
		return dir.exists() && dir.isDirectory();
	}
	
	public static String checksum(String path, String algorithm) {
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		FileInputStream i = null;
		
		try {
			i = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		byte[] buf = new byte[1024];
		int len = 0;
		
		try {
			while ((len = i.read(buf)) != -1) {
				md.update(buf, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] digest = md.digest();
		
		// HEX
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		
		return sb.toString();
		
	}

	public static boolean checkURL(String url) {
		try {
			URL u = new URL(url);
			u.openConnection();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public static long getAvailableSize() {
		String sdcard = Environment.getExternalStorageState();
		String state = Environment.MEDIA_MOUNTED;
		File file = Environment.getExternalStorageDirectory();
		StatFs statFs = new StatFs(file.getPath());
		if(sdcard.equals(state)) {
			//获得Sdcard上每个block的size
			long blockSize = statFs.getBlockSizeLong();
			//获取可供程序使用的Block数量
			long blockavailable = statFs.getAvailableBlocksLong();
			//计算标准大小使用：1024，当然使用1000也可以
			return blockSize * blockavailable;
		} else {
			return -1;
		}
//		File path = Environment.getExternalStorageDirectory();
//		StatFs stat = new StatFs(path.getPath());
//		long blockSize = stat.getBlockSizeLong();
//		long availableBlocks = stat.getAvailableBlocksLong();
//		return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
	}
}
