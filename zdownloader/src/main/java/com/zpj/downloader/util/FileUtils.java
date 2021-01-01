package com.zpj.downloader.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author Z-P-J
 */
public class FileUtils {

    public enum FileType {
        VIDEO,
        MUSIC,
        IMAGE,
        EBOOK,
        ARCHIVE,
        HTML,
        TXT,
        APK,
        TORRENT,
        PDF,
        PPT,
        DOC,
        SWF,
        CHM,
        XLS,
        UNKNOWN
    }

    private static final String[] IMG = {".bmp", ".jpg", ".jpeg", ".png", ".tiff", ".gif", ".pcx", ".tga", ".exif", ".fpx", ".svg", ".psd",
            ".cdr", ".pcd", ".dxf", ".ufo", ".eps", ".ai", ".raw", ".wmf"};
    private static final String[] VIDEO = {".mp4", ".avi", ".mov", ".wmv", ".asf", ".navi", ".3gp", ".mkv", ".f4v", ".rmvb", ".webm", ".flv", ".rm", ".ts", ".vob", ".m2ts"};
    private static final String[] MUSIC = {".mp3", ".wma", ".wav", ".mod", ".ra", ".cd", ".md", ".asf", ".aac", ".vqf", ".ape", ".mid", ".ogg", ".m4a", ".vqf", ".flac", ".midi"};
    private static final String[] ARCHIVE = {".zip", ".rar", ".7z", ".iso", ".tar", ".gz", ".bz"};
    private static final String[] EBOOK = {".epub", ".umb", ".wmlc", ".pdb", ".mdx", ".xps"};


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }


    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static FileType checkFileType(String fileName) {
        fileName = fileName.toLowerCase();
        if (fileName.endsWith(".torrent")) {
            return FileType.TORRENT;
        } else if (fileName.endsWith(".txt")) {
            return FileType.TXT;
        } else if (fileName.endsWith(".apk")) {
            return FileType.APK;
        } else if (fileName.endsWith(".pdf")) {
            return FileType.PDF;
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return FileType.DOC;
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            return FileType.PPT;
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return FileType.XLS;
        } else if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return FileType.HTML;
        } else if (fileName.endsWith(".swf")) {
            return FileType.SWF;
        } else if (fileName.endsWith(".chm")) {
            return FileType.CHM;
        }

        for (String s : IMG) {
            if (fileName.endsWith(s)) {
                return FileType.IMAGE;
            }
        }
        for (String s : VIDEO) {
            if (fileName.endsWith(s)) {
                return FileType.VIDEO;
            }
        }
        for (String s : ARCHIVE) {
            if (fileName.endsWith(s)) {
                return FileType.ARCHIVE;
            }
        }
        for (String s : MUSIC) {
            if (fileName.endsWith(s)) {
                return FileType.MUSIC;
            }
        }
        for (String s : EBOOK) {
            if (fileName.endsWith(s)) {
                return FileType.EBOOK;
            }
        }
        return FileType.UNKNOWN;
    }

    public static String getExtension(String path) {
        return path.substring(path.indexOf('.')+1).toLowerCase();
    }

    public static void openFile(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, FileUtils.getFileProviderName(context), file);

            context.grantUriPermission(context.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, FileUtils.getMIMEType(file));
        } else {
            intent.setDataAndType(uri, FileUtils.getMIMEType(file));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file the file
     */
    public static String getMIMEType(File file) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (!TextUtils.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type;
    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
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
    }

    public static void writeToFile(String fileName, String content) {
        writeToFile(fileName, content.getBytes(StandardCharsets.UTF_8));
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


//    public static void writeFile(Context context, String fileName, String content) throws Exception {
//        FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//        outStream.write(content.getBytes());
//        outStream.close();
//    }
//
//    public static String readFile(Context context, String fileName) throws FileNotFoundException {
//        FileInputStream inStream = null;
//        inStream = context.openFileInput(fileName);
//        //输出到内存
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        int len = 0;
//        byte[] buffer = new byte[1024];
//        try {
//            while ((len = inStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, len);
//            }
//            byte[] contentByte = outStream.toByteArray();
//            return new String(contentByte);
//        } catch (Exception e) {
//            return null;
//        }
//    }

}
