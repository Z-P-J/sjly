package com.zpj.downloader.util.io;//package com.zpj.downloader.util.io;
//
//import com.zpj.downloader.get.DownloadRunnable;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class IORunnable implements Runnable {
//
//    private static final byte[] buf = new byte[1024];
//
//    private final String path;
////    private long total;
//    private long start;
//    private final long end;
//    private final InputStream inputStream;
//    private DownloadRunnable downloadRunnable;
//
//    public IORunnable(String path, InputStream inputStream, long start, long end, DownloadRunnable downloadRunnable) {
//        this.path = path;
//        this.inputStream = inputStream;
////        this.total = total;
//        this.start = start;
//        this.end = end;
//        this.downloadRunnable = downloadRunnable;
//    }
//
//
//    @Override
//    public void run() {
//        synchronized (buf) {
//            int total = 0;
//            try {
//                BufferedRandomAccessFile f = new BufferedRandomAccessFile(path, "rw");
//                f.seek(start);
//                BufferedInputStream ipt = new BufferedInputStream(inputStream);
//
//                while (start < end) {
//                    int len = ipt.read(buf, 0, 1024);
//
//                    if (len == -1) {
//                        break;
//                    } else {
//                        start += len;
//                        total += len;
//                        f.write(buf, 0, len);
//                        downloadRunnable.notifyProgress(len);
//                    }
//                }
//                f.close();
//                ipt.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                downloadRunnable.notifyProgress(start - end);
//            }
//        }
//    }
//
//
//}
