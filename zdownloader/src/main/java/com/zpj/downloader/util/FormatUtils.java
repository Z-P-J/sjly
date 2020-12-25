package com.zpj.downloader.util;

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
public class FormatUtils {

	private static final int NUM_1024 = 1024;

	private static final int NUM_1024_1024 = 1024 << 10;

	private static final long NUM_1024_1024_1024 = 1024 << 20;


	public static String formatSize(long bytes) {
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
			return String.format(Locale.CHINA, "%.2f KB/s", speed / NUM_1024);
		} else if (speed < NUM_1024_1024_1024) {
			return String.format(Locale.CHINA, "%.2f MB/s", speed / NUM_1024_1024);
		} else {
			return String.format(Locale.CHINA, "%.2f GB/s", speed / NUM_1024_1024_1024);
		}
	}

}
