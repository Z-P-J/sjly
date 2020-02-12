package com.zpj.shouji.market.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.zpj.http.core.IHttp;
import com.zpj.http.core.ObservableTask;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 文件扫描器
 */
public class FileScanner implements ObservableOnSubscribe<FileScanner.ScanEvent>, IHttp.OnSuccessListener<FileScanner.ScanEvent> {

    abstract class ScanEvent { }

    private class OnFindFileEvent extends ScanEvent {
        FileItem fileItem;

        OnFindFileEvent(FileItem fileItem) {
            this.fileItem = fileItem;
        }

        public FileItem getFileItem() {
            return fileItem;
        }
    }

    private class OnScanDirEvent extends ScanEvent {
        File dir;

        OnScanDirEvent(File dir) {
            this.dir = dir;
        }

        public File getDir() {
            return dir;
        }
    }

    private class OnCanceledEvent extends ScanEvent {
    }

    private class OnCompleteEvent extends ScanEvent {
    }

//    private class OnUpdateProgressEvent extends ScanEvent {
//        private int totalLength;
//        private int completedLength;
//        OnUpdateProgressEvent(int totalLength, int completedLength) {
//            this.totalLength = totalLength;
//            this.completedLength = completedLength;
//        }
//
//        public int getTotalLength() {
//            return totalLength;
//        }
//
//        public int getCompletedLength() {
//            return completedLength;
//        }
//    }

    public static class Builder {

        private final FileScanner fileScanner;

        Builder(FileScanner fileScanner) {
            this.fileScanner = fileScanner;
        }

        public Builder setFileChecker(FileChecker fileChecker) {
            fileScanner.fileChecker = fileChecker;
            return this;
        }

        public Builder setScanListener(ScanListener scanListener) {
            fileScanner.scanListener = scanListener;
            return this;
        }

        public Builder setDirFilter(DirFilter dirFilter) {
            fileScanner.dirFilter = dirFilter;
            return this;
        }

        public Builder setThreadCount(int threadCount) {
            fileScanner.threadCount = threadCount;
            return this;
        }

        public FileScanner getFileScanner() {
            return fileScanner;
        }

        public FileScanner execute() {
            fileScanner.execute();
            return fileScanner;
        }
    }

    private final Queue<File> mainDirQueue = new LinkedList<File>();
    private final Queue<File> currentDirQueue = new LinkedList<File>();

    private final Context context;

    private int threadCount = 3;
    private boolean running;
    private boolean canceled;
    private int mainDirTotalLength;

    private CountDownLatch countDownLatch;

    private FileChecker fileChecker;
    private ScanListener scanListener;
    private DirFilter dirFilter;


    public static Builder with(Context context) {
        return new Builder(new FileScanner(context));
    }

    private FileScanner(Context context) {
        this.context = context;
    }

    private void execute() {
        if (running || fileChecker == null || scanListener == null) {
            return;
        }

        this.running = true;
        this.canceled = false;

        countDownLatch = new CountDownLatch(threadCount);
        new ObservableTask<>(this)
                .onSubscribe(d -> {
                    if (scanListener != null) {
                        scanListener.onStarted();
                    }
                })
                .onError(throwable -> cancel())
                .onSuccess(this)
                .onComplete(() -> {
                    if (scanListener != null) {
                        scanListener.onCompleted();
                    }
                })
                .subscribe();
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void subscribe(ObservableEmitter<ScanEvent> emitter) throws Exception {
        List<String> paths = FileUtils.getAllAvailableSdcardPath(context);
        if (paths == null) {
//            throw new Exception("Sdcard not available");
            emitter.onComplete();
            return;
        }
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                continue;
            }

            boolean isDirectory = dir.isDirectory();
            Log.d("PackageFragment", "dir=" + dir.getPath());
            Log.d("PackageFragment", "isDirectory=" + isDirectory);
            if (isDirectory) {
                emitter.onNext(new OnScanDirEvent(dir));
            }

            FileItem fileItem = fileChecker.accept(dir);
            if (fileItem != null) {
                emitter.onNext(new OnFindFileEvent(fileItem));
            }

            if (isDirectory) {
                File[] childFiles = dir.listFiles();
                if (childFiles != null && childFiles.length > 0) {
                    for (File childFile : childFiles) {
                        if (childFile != null && childFile.exists()) {
                            fileItem = fileChecker.accept(childFile);
                            if (fileItem != null) {
                                emitter.onNext(new OnFindFileEvent(fileItem));
                            }

                            if (childFile.isDirectory() && (dirFilter == null || dirFilter.accept(childFile))) {
                                mainDirQueue.add(childFile);
                            }
                        }
                    }
                }
            }
        }


        mainDirTotalLength = mainDirQueue.size();

        for (int w = 0; w < threadCount; w++) {
            new ObservableTask<>(new FileScanObservable(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .onSuccess(this)
                    .onComplete(() -> {
                        synchronized (FileScanner.this) {
                            countDownLatch.countDown();
                        }
                    })
                    .subscribe();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            cancel();
        }

        fileChecker.onFinished();

        running = false;
        if (isCanceled()) {
            emitter.onNext(new OnCanceledEvent());
        } else {
            emitter.onNext(new OnCompleteEvent());
        }
        emitter.onComplete();
    }

    @Override
    public void onSuccess(ScanEvent data) throws Exception {
        if (scanListener != null) {
            if (data instanceof OnFindFileEvent) {
                scanListener.onFindFile(((OnFindFileEvent) data).getFileItem());
            } else if (data instanceof OnScanDirEvent) {
                scanListener.onScanDir(((OnScanDirEvent) data).getDir());
            }
//            else if (data instanceof OnUpdateProgressEvent) {
//                scanListener.onUpdateProgress(((OnUpdateProgressEvent) data).getTotalLength(),
//                        ((OnUpdateProgressEvent) data).getCompletedLength());
//            }
            else if (data instanceof OnCanceledEvent) {
                scanListener.onCanceled();
            } else if (data instanceof OnCompleteEvent) {
                scanListener.onCompleted();
            }
        }
    }

    public interface DirFilter {
        boolean accept(File dir);
    }

    public interface ScanListener {
        void onStarted();

        void onFindFile(FileItem fileItem);

//        void onUpdateProgress(int totalLength, int completedLength);

        void onCompleted();

        void onCanceled();

        void onScanDir(File dir);
    }

    public interface FileChecker {
        /**
         * 检查这个文件是不是你要的
         *
         * @param pathname 这个文件
         * @return null: 不要这个文件；否则请返回一个实现了FileItem接口的一个对象，你可以在此解析你需要的数据并封装在这个对象中，ScanListener会将这个对象再回调给你
         */
        FileItem accept(File pathname);

        void onFinished();
    }

    public interface FileItem {
        String getFilePath();

        long getFileLength();
    }

    private class FileScanObservable implements ObservableOnSubscribe<ScanEvent> {

        private final FileScanner fileScanner;
        private final FileChecker fileChecker;
        private final DirFilter dirFilter;

        FileScanObservable(FileScanner fileScanner) {
            this.fileScanner = fileScanner;
            this.fileChecker = fileScanner.fileChecker;
            this.dirFilter = fileScanner.dirFilter;
        }

        @Override
        public void subscribe(ObservableEmitter<ScanEvent> emitter) throws Exception {
            File currentDir = null;
            while (!fileScanner.canceled) {
//                currentDir = getNextDir();
                synchronized (currentDirQueue) {
                    // 从主目录队列中取出一个目录放到当前目录队列中，如果两个队列都空的就结束
                    if (currentDirQueue.isEmpty()) {
                        if (mainDirQueue.isEmpty()) {
                            currentDir = null;
                        } else {
                            currentDirQueue.add(mainDirQueue.poll());

                            // 用主目录的数量来计算进度
//                            emitter.onNext(new OnUpdateProgressEvent(mainDirTotalLength, mainDirTotalLength - mainDirQueue.size()));
                        }
                    } else {
                        // 严谨的过虑一下null或不存在的情况
                        currentDir = currentDirQueue.poll();
                        emitter.onNext(new OnScanDirEvent(currentDir));
                    }
                }
                if (currentDir == null) {
                    break;
                }

                if (!currentDir.exists()) {
                    continue;
                }

                File[] childFiles = currentDir.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    continue;
                }

                for (File childFile : childFiles) {
                    if (fileScanner.canceled) {
                        break;
                    }

                    FileItem fileItem = fileChecker.accept(childFile);
                    if (fileItem != null) {
                        emitter.onNext(new OnFindFileEvent(fileItem));
                    }

                    if (childFile.isDirectory() && (dirFilter == null || dirFilter.accept(childFile))) {
                        synchronized (currentDirQueue) {
                            currentDirQueue.add(childFile);
                        }
                    }
                }
            }
            emitter.onComplete();
        }

    }

//    private static class MultiThreadScanTask implements Runnable {
//        private final Context context;
//        private FileScanner fileScanner;
//
//        private int childThreadCount;
//        private int mainDirTotalLength;
//        private long lastCallScanDirTime;
//        private final Queue<File> mainDirQueue = new LinkedList<File>();
//        ;
//        private final Queue<File> currentDirQueue = new LinkedList<File>();
//        ;
//        private CountDownLatch countDownLatch;
//
//        private FileChecker fileChecker;
//        private DirFilter dirFilter;
//        private CallbackHandler callbackHandler;
//
//        public MultiThreadScanTask(Context context, FileScanner fileScanner, int childThreadCount) {
//            this.context = context;
//            this.fileScanner = fileScanner;
//            this.childThreadCount = childThreadCount;
//
//            countDownLatch = new CountDownLatch(childThreadCount);
//
//            fileChecker = fileScanner.fileChecker;
//            dirFilter = fileScanner.dirFilter;
//            callbackHandler = fileScanner.callbackHandler;
//        }
//
//        @Override
//        public void run() {
//
//            List<String> paths = FileUtils.getAllAvailableSdcardPath(context);
//            if (paths == null) {
////                throw new Exception("Sdcard not available");
//                return;
//            }
//            for (String path : paths) {
//                File dir = new File(path);
//                if (dir == null || !dir.exists()) {
//                    continue;
//                }
//
//                boolean isDirectory = dir.isDirectory();
//                Log.d("PackageFragment", "dir=" + dir.getPath());
//                Log.d("PackageFragment", "isDirectory=" + isDirectory);
//                if (isDirectory) {
//                    callbackScanDir(dir);
//                }
//
//                FileItem fileItem = fileChecker.accept(dir);
//                if (fileItem != null) {
//                    callbackHandler.callbackFindFile(fileItem);
//                }
//
//                if (isDirectory) {
//                    File[] childFiles = dir.listFiles();
//                    if (childFiles != null && childFiles.length > 0) {
//                        for (File childFile : childFiles) {
//                            if (childFile != null && childFile.exists()) {
//                                fileItem = fileChecker.accept(childFile);
//                                if (fileItem != null) {
//                                    callbackHandler.callbackFindFile(fileItem);
//                                }
//
//                                if (childFile.isDirectory() && (dirFilter == null || dirFilter.accept(childFile))) {
//                                    mainDirQueue.add(childFile);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            mainDirTotalLength = mainDirQueue.size();
//
//            for (int w = 0; w < childThreadCount; w++) {
//                HttpApi.with(new ChildScanTask(this)).subscribe();
////                new Thread(new ChildScanTask(this)).start();
//            }
//
//            try {
//                countDownLatch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                fileScanner.cancel();
//            }
//
//            fileChecker.onFinished();
//
//            fileScanner.running = false;
//            if (fileScanner.isCanceled()) {
//                callbackHandler.callbackCanceled();
//            } else {
//                callbackHandler.callbackCompleted();
//            }
//        }
//
//        private void callbackScanDir(File currentDir) {
//            long currentTime = System.currentTimeMillis();
//            if ((currentTime - lastCallScanDirTime) >= 1000) {
//                callbackHandler.callbackScanDir(currentDir);
//                lastCallScanDirTime = currentTime;
//            }
//        }
//
//        public File getNextDir() {
//            synchronized (currentDirQueue) {
//                // 从主目录队列中取出一个目录放到当前目录队列中，如果两个队列都空的就结束
//                if (currentDirQueue.isEmpty()) {
//                    if (mainDirQueue.isEmpty()) {
//                        return null;
//                    }
//
//                    currentDirQueue.add(mainDirQueue.poll());
//
//                    // 用主目录的数量来计算进度
//                    callbackHandler.callbackUpdateProgress(mainDirTotalLength, mainDirTotalLength - mainDirQueue.size());
//                }
//
//                // 严谨的过虑一下null或不存在的情况
//                File currentDir = currentDirQueue.poll();
//                callbackScanDir(currentDir);
//                return currentDir;
//            }
//        }
//
//        private synchronized void childTaskFinished() {
//            countDownLatch.countDown();
//        }
//
//        private synchronized void childTaskFindDir(File childDir) {
//            synchronized (currentDirQueue) {
//                currentDirQueue.add(childDir);
//            }
//        }
//
//        private static class ChildScanTask implements Runnable {
//            private MultiThreadScanTask multiThreadScanTask;
//
//            private FileChecker fileChecker;
//            private DirFilter dirFilter;
//            private CallbackHandler callbackHandler;
//
//            public ChildScanTask(MultiThreadScanTask multiThreadScanTask) {
//                this.multiThreadScanTask = multiThreadScanTask;
//
//                fileChecker = multiThreadScanTask.fileScanner.fileChecker;
//                dirFilter = multiThreadScanTask.fileScanner.dirFilter;
//                callbackHandler = multiThreadScanTask.fileScanner.callbackHandler;
//            }
//
//            @Override
//            public void run() {
//                // 子任务的处理逻辑就是，不停的获取下一个要扫描的文件夹 没有文件夹的话就结束，扫描到子文件夹的话就再加到队列中
//                File currentDir;
//                while (!multiThreadScanTask.fileScanner.canceled) {
//                    currentDir = multiThreadScanTask.getNextDir();
//                    if (currentDir == null) {
//                        break;
//                    }
//
//                    if (!currentDir.exists()) {
//                        continue;
//                    }
//
//                    File[] childFiles = currentDir.listFiles();
//                    if (childFiles == null || childFiles.length == 0) {
//                        continue;
//                    }
//
//                    for (File childFile : childFiles) {
//                        if (multiThreadScanTask.fileScanner.canceled) {
//                            break;
//                        }
//
//                        FileItem fileItem = fileChecker.accept(childFile);
//                        if (fileItem != null) {
//                            callbackHandler.callbackFindFile(fileItem);
//                        }
//
//                        if (childFile.isDirectory() && (dirFilter == null || dirFilter.accept(childFile))) {
//                            multiThreadScanTask.childTaskFindDir(childFile);
//                        }
//                    }
//                }
//
//                multiThreadScanTask.childTaskFinished();
//            }
//        }
//    }
//
//    private class CallbackHandler extends Handler {
//        private static final int WHAT_CALLBACK_STARTED = 11101;
//        private static final int WHAT_CALLBACK_COMPLETED = 11102;
//        private static final int WHAT_CALLBACK_CANCELED = 11103;
//        private static final int WHAT_CALLBACK_UPDATE_PROGRESS = 11104;
//        private static final int WHAT_CALLBACK_FIND_FILE = 11105;
//        private static final int WHAT_CALLBACK_SCAN_DIR = 11106;
//
//        public CallbackHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case WHAT_CALLBACK_STARTED:
//                    if (!canceled) {
//                        scanListener.onStarted();
//                    }
//                    break;
//                case WHAT_CALLBACK_COMPLETED:
//                    if (!canceled) {
//                        scanListener.onCompleted();
//                    }
//                    break;
//                case WHAT_CALLBACK_CANCELED:
//                    scanListener.onCanceled();
//                    break;
//                case WHAT_CALLBACK_UPDATE_PROGRESS:
//                    if (!canceled) {
//                        scanListener.onUpdateProgress(msg.arg1, msg.arg2);
//                    }
//                    break;
//                case WHAT_CALLBACK_FIND_FILE:
//                    if (!canceled) {
//                        scanListener.onFindFile((FileItem) msg.obj);
//                    }
//                    break;
//                case WHAT_CALLBACK_SCAN_DIR:
//                    if (!canceled) {
//                        scanListener.onScanDir((File) msg.obj);
//                    }
//                    break;
//            }
//        }
//
//        public void callbackStarted() {
//            if (Looper.getMainLooper() == Looper.myLooper()) {
//                scanListener.onStarted();
//            } else {
//                obtainMessage(WHAT_CALLBACK_STARTED).sendToTarget();
//            }
//        }
//
//        public void callbackScanDir(File file) {
//            if (Looper.getMainLooper() == Looper.myLooper()) {
//                scanListener.onScanDir(file);
//            } else {
//                obtainMessage(WHAT_CALLBACK_SCAN_DIR, file).sendToTarget();
//            }
//        }
//
//        public void callbackFindFile(FileItem fileItem) {
//            if (Looper.getMainLooper() == Looper.myLooper()) {
//                scanListener.onFindFile(fileItem);
//            } else {
//                obtainMessage(WHAT_CALLBACK_FIND_FILE, fileItem).sendToTarget();
//            }
//        }
//
//        public void callbackUpdateProgress(int totalLength, int completedLength) {
//            if (Looper.getMainLooper() == Looper.myLooper()) {
//                scanListener.onUpdateProgress(totalLength, completedLength);
//            } else {
//                obtainMessage(WHAT_CALLBACK_UPDATE_PROGRESS, totalLength, completedLength).sendToTarget();
//            }
//        }
//
//        public void callbackCompleted() {
//            if (Looper.getMainLooper() == Looper.myLooper()) {
//                scanListener.onCompleted();
//            } else {
//                obtainMessage(WHAT_CALLBACK_COMPLETED).sendToTarget();
//            }
//        }
//
//        public void callbackCanceled() {
//            if (Looper.getMainLooper() == Looper.myLooper()) {
//                scanListener.onCanceled();
//            } else {
//                obtainMessage(WHAT_CALLBACK_CANCELED).sendToTarget();
//            }
//        }
//    }
}
