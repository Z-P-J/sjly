package com.zpj.shouji.market.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;

public class FileUtils {

    public enum FILE_TYPE {
        /**
         * 文件类型
         */
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
    private static final String[] MUSIC = {".mp3", ".wma", ".wav", ".mod", ".ra", ".cd", ".md", ".asf", ".aac", ".vqf", ".ape", ".mid", ".ogg", ".m4a", ".vqf", ".flac", ".ape", ".midi"};
    private static final String[] ARCHIVE = {".zip", ".rar", ".7z", ".iso"};
    private static final String[] EBOOK = {".epub", ".umb", ".wmlc", ".pdb", ".mdx", ".xps"};

    //static String document[] = { ".txt", ".doc", ".docx", ".xls", ".htm", ".html", ".jsp", ".rtf", ".wpd", ".pdf", ".ppt" };

    private static final String[][] MIME_MAP_TABLE = {
            //{后缀名， MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {".torrent", "application/x-bittorrent"},
            {"", "*/*"}
    };


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {
                column
        };
        try (Cursor cursor = context.getContentResolver()
                .query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


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

    public static String getFileSliceMD5(File file) {
        if (!file.isFile() || file.length() < 256 * 1024) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            CRC32 crc = new CRC32();
            in = new FileInputStream(file);
            for (int i = 0; i < 256; i++) {
                len = in.read(buffer, 0, 1024);
                digest.update(buffer, 0, len);
                crc.update(buffer, 0, len);
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


    public static String getCRC32(File file) {
        CRC32 crc32 = new CRC32();
        // MessageDigest.get
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                crc32.update(buffer, 0, length);
            }
            return Long.toHexString(crc32.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    public static String longToSize(Long num) {
//        String size;
//        DecimalFormat decimalFormat = new DecimalFormat("#.00");
//        if (num < 1024) {
//            size = decimalFormat.format((double)num) + "B";
//        } else if (num < 1048576) {
//            size = decimalFormat.format((double)num / 1024) + "KB";
//        } else if (num < 1073741824) {
//            size = decimalFormat.format((double)num / 1048576) + "MB";
//        } else {
//            size = decimalFormat.format((double)num / 1073741824) + "GB";
//        }
//        return size;
//    }

    public static FILE_TYPE checkFileType(String fileName) {
        fileName = fileName.toLowerCase();
        if (fileName.endsWith(".torrent")) {
            return FILE_TYPE.TORRENT;
        } else if (fileName.endsWith(".txt")) {
            return FILE_TYPE.TXT;
        } else if (fileName.endsWith(".apk")) {
            return FILE_TYPE.APK;
        } else if (fileName.endsWith(".pdf")) {
            return FILE_TYPE.PDF;
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return FILE_TYPE.DOC;
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            return FILE_TYPE.PPT;
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return FILE_TYPE.XLS;
        } else if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return FILE_TYPE.HTML;
        } else if (fileName.endsWith(".swf")) {
            return FILE_TYPE.SWF;
        } else if (fileName.endsWith(".chm")) {
            return FILE_TYPE.CHM;
        }

        for (String s : IMG) {
            if (fileName.endsWith(s)) {
                return FILE_TYPE.IMAGE;
            }
        }
        for (String s : VIDEO) {
            if (fileName.endsWith(s)) {
                return FILE_TYPE.VIDEO;
            }
        }
        for (String s : ARCHIVE) {
            if (fileName.endsWith(s)) {
                return FILE_TYPE.ARCHIVE;
            }
        }
        for (String s : MUSIC) {
            if (fileName.endsWith(s)) {
                return FILE_TYPE.MUSIC;
            }
        }
        for (String s : EBOOK) {
            if (fileName.endsWith(s)) {
                return FILE_TYPE.EBOOK;
            }
        }
        return FILE_TYPE.UNKNOWN;
    }

//    public static int getFileTypeIconId(String fileName) {
//        FILE_TYPE fileType = checkFileType(fileName);
//        if (fileType.equals(FILE_TYPE.TORRENT)) {
//            return R.drawable.wechat_icon_bt;
//        } else if (fileType.equals(FILE_TYPE.TXT)) {
//            return R.drawable.wechat_icon_txt;
//        } else if (fileType.equals(FILE_TYPE.APK)) {
//            return R.drawable.wechat_icon_apk;
//        } else if (fileType.equals(FILE_TYPE.PDF)) {
//            return R.drawable.wechat_icon_pdf;
//        } else if (fileType.equals(FILE_TYPE.DOC)) {
//            return R.drawable.wechat_icon_word;
//        } else if (fileType.equals(FILE_TYPE.PPT)) {
//            return R.drawable.wechat_icon_ppt;
//        } else if (fileType.equals(FILE_TYPE.XLS)) {
//            return R.drawable.wechat_icon_excel;
//        } else if (fileType.equals(FILE_TYPE.HTML)) {
//            return R.drawable.wechat_icon_html;
//        } else if (fileType.equals(FILE_TYPE.SWF)) {
//            return R.drawable.format_flash;
//        } else if (fileType.equals(FILE_TYPE.CHM)) {
//            return R.drawable.format_chm;
//        } else if (fileType.equals(FILE_TYPE.IMAGE)) {
//            return R.drawable.format_picture;
//        } else if (fileType.equals(FILE_TYPE.VIDEO)) {
//            return R.drawable.format_media;
//        } else if (fileType.equals(FILE_TYPE.ARCHIVE)) {
//            return R.drawable.wechat_icon_zip;
//        } else if (fileType.equals(FILE_TYPE.MUSIC)) {
//            return R.drawable.wechat_icon_music;
//        } else if (fileType.equals(FILE_TYPE.EBOOK)) {
//            return R.drawable.wechat_icon_txt;
//        }
//        return R.drawable.wechat_icon_others;
//    }

    public static void openFile(Context context, File file) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file the file
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (TextUtils.isEmpty(end)) {
            return type;
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (String[] strings : MIME_MAP_TABLE) {
            if (end.equals(strings[0])) {
                type = strings[1];
            }
        }
        return type;
    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }

    public static void writeFile(Context context, String fileName, String content) throws Exception {
        FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        outStream.write(content.getBytes());
        outStream.close();
    }

    public static String readFile(Context context, String fileName) throws FileNotFoundException {
        FileInputStream inStream = null;
        inStream = context.openFileInput(fileName);
        //输出到内存
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] contentByte = outStream.toByteArray();
            return new String(contentByte);
        } catch (Exception e) {
            return null;
        }
    }


    public static String formatFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1fGB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0fMB" : "%.1fMB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0fKB" : "%.1fKB", f);
        } else
            return String.format("%dB", size);
    }

    /**
     * 获取机身内存占用情况，第一个为可用，第二个为总共
     * @return
     */
    public static String[] getSpaceSize(){
        File sdcard_filedir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
        long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
        long totalSpace = sdcard_filedir.getTotalSpace();
        String usable = formatFileSize(usableSpace);
        String total = formatFileSize(totalSpace);
        return new String[] {usable,total};
    }

//    /**
//     * 获取下载目录文件大小
//     * @return
//     */
//    public static String getCacheSize(){
//        try {
//           String path = isExistDir(Params.DEFAULT_PATH);
//           File file = new File(path);
//            return convertFileSize(getFileSizes(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "0";
//    }

    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }
    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在，不存在就创建
     */
    public static String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        return downloadFile.getAbsolutePath();
    }

    public static String subSuffix(String fileNameOrPath) {
        if (fileNameOrPath == null) {
            return null;
        }

        int dotIndex = fileNameOrPath.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        }

        return fileNameOrPath.substring(dotIndex);
    }

    public static List<String> getAllAvailableSdcardPath(Context context) {
        String[] paths;
        Method getVolumePathsMethod;
        List<String> storagePathList = new LinkedList<>();
        try {
            getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
        } catch (NoSuchMethodException e) {
            Log.e("getAllAvailableSdcardPath", "not found StorageManager.getVolumePaths() method");
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                storagePathList.add(Environment.getExternalStorageDirectory().getPath());
                return storagePathList;
            } else {
                return null;
            }
        }

        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            paths = (String[]) getVolumePathsMethod.invoke(sm);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        if (paths == null || paths.length == 0) {
            return null;
        }

        // 去掉不可用的存储器

        Collections.addAll(storagePathList, paths);
        Iterator<String> storagePathIterator = storagePathList.iterator();

        String path;
        Method getVolumeStateMethod = null;
        while (storagePathIterator.hasNext()) {
            path = storagePathIterator.next();
            if (getVolumeStateMethod == null) {
                try {
                    getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            String status;
            try {
                status = (String) getVolumeStateMethod.invoke(sm, path);
            } catch (Exception e) {
                e.printStackTrace();
                storagePathIterator.remove();
                continue;
            }
            if (!(Environment.MEDIA_MOUNTED.equals(status) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(status))) {
                storagePathIterator.remove();
            }
        }
        return storagePathList;
    }
}
