package com.zpj.downloader.util;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zpj.downloader.R;
import com.zpj.downloader.core.DownloadManagerImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.zip.CRC32;

/**
 * @author Z-P-J
 */
public class FileUtil {

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

    public static String getExtension(String path) {
        return path.substring(path.indexOf('.')+1, path.length()).toLowerCase();
    }

    public static void openFile(File f, Context c) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        String type = MimeTypes.getMimeType(f.getPath(), f.isDirectory());
        if (type != null && type.trim().length() != 0 && !type.equals("*/*")) {
            Uri uri = fileToContentUri(c, f, intent);

            intent.setDataAndType(uri, type);

            Intent activityIntent = intent;

            try {
                c.startActivity(activityIntent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(c, "无法找到可以打开此文件的应用", Toast.LENGTH_SHORT).show();
//                openWith(f, c);
            }
        } else {
            // failed to load mime type
//            openWith(f, c);
            Toast.makeText(c, "无法找到可以打开此文件的应用", Toast.LENGTH_SHORT).show();
        }
    }


    private static final String INTERNAL_VOLUME = "internal";
    public static final String EXTERNAL_VOLUME = "external";

    private static final String EMULATED_STORAGE_SOURCE = System.getenv("EMULATED_STORAGE_SOURCE");
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");
    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
    public static String normalizeMediaPath(String path) {
        // Retrieve all the paths and check that we have this environment vars
        if (TextUtils.isEmpty(EMULATED_STORAGE_SOURCE) ||
                TextUtils.isEmpty(EMULATED_STORAGE_TARGET) ||
                TextUtils.isEmpty(EXTERNAL_STORAGE)) {
            return path;
        }

        // We need to convert EMULATED_STORAGE_SOURCE -> EMULATED_STORAGE_TARGET
        if (path.startsWith(EMULATED_STORAGE_SOURCE)) {
            path = path.replace(EMULATED_STORAGE_SOURCE, EMULATED_STORAGE_TARGET);
        }
        return path;
    }

    public static Uri fileToContentUri(Context context, File file, Intent chooserIntent) {
        // Normalize the path to ensure media search
        final String normalizedPath = normalizeMediaPath(file.getAbsolutePath());

        // Check in external and internal storages
        Uri uri = fileToContentUri(context, normalizedPath, file.isDirectory(), EXTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }

        uri = fileToContentUri(context, normalizedPath, file.isDirectory(), INTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }

        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return FileProvider.getUriForFile(context, "com.amaze.filemanager", file);
    }

    private static Uri fileToContentUri(Context context, String path, boolean isDirectory, String volume) {
        final String where = MediaStore.MediaColumns.DATA + " = ?";
        Uri baseUri;
        String[] projection;
        int mimeType = Icons.getTypeOfFile(path, isDirectory);

        switch (mimeType) {
            case Icons.IMAGE:
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.VIDEO:
                baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.AUDIO:
                baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            default:
                baseUri = MediaStore.Files.getContentUri(volume);
                projection = new String[]{BaseColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        }

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(baseUri, projection, where, new String[]{path}, null);
        try {
            if (c != null && c.moveToNext()) {
                boolean isValid = false;
                if (mimeType == Icons.IMAGE || mimeType == Icons.VIDEO || mimeType == Icons.AUDIO) {
                    isValid = true;
                } else {
                    int type = c.getInt(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
                    isValid = type != 0;
                }

                if (isValid) {
                    // Do not force to use content uri for no media files
                    long id = c.getLong(c.getColumnIndexOrThrow(BaseColumns._ID));
                    return Uri.withAppendedPath(baseUri, String.valueOf(id));
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }


    public static void openFile(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, FileUtil.getFileProviderName(context), file);
            context.grantUriPermission(context.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, FileUtil.getMIMEType(file));
        } else {
            intent.setDataAndType(uri, FileUtil.getMIMEType(file));
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

    public static boolean copyFile(File oldFile, File newFile) {
        try {
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldFile);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
