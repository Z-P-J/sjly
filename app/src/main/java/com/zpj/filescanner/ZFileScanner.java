package com.zpj.filescanner;


import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ZFileScanner<T> {

    private static final String TAG = "ZFileScanner";

    private static final ConcurrentLinkedQueue<File> folderList = new ConcurrentLinkedQueue<>();

    private static final List<ScannerTask> taskList = new ArrayList<>();

    private String type;

//    private OnScanListener<T> onScanListener;

    private static class ScannerTask<T> {

        private final ObservableEmitter<T> callback;
        private final OnScanListener<T> onScanListener;
        private final String type;

        public ScannerTask(ObservableEmitter<T> callback, OnScanListener<T> onScanListener, String type) {
            this.callback = callback;
            this.onScanListener = onScanListener;
            this.type = type;
            start();
        }

        public void start() {
            Observable.create(
                    (ObservableOnSubscribe<File>) emitter -> {
                        while(!folderList.isEmpty()) {
                            File file = folderList.poll();
                            if (file != null) {
//                                Log.d(TAG, "file=" + file.getAbsolutePath());
                                if (file.isDirectory()) {
                                    for (File f : file.listFiles()) {
                                        Log.d(TAG, "file=" + file.getAbsolutePath());
                                        if (f.isDirectory()) {
                                            folderList.add(f);
                                        } else {
                                            if (onScanListener != null) {
                                                if (f.getName().toLowerCase().endsWith(type)) {
                                                    T item = onScanListener.onWrapFile(f);
                                                    if (item != null) {
                                                        callback.onNext(item);
                                                    }
                                                }
                                            }
                                        }
                                    }
//                                    folderList.addAll(Arrays.asList(file.listFiles()));
                                } else {
                                    if (onScanListener != null) {
                                        if (file.getName().toLowerCase().endsWith(type)) {
                                            T item = onScanListener.onWrapFile(file);
                                            if (item != null) {
                                                callback.onNext(item);
                                            }
                                        }
//                                        if (file.getName().toLowerCase().equalsIgnoreCase(type)) {
//                                            callback.onNext(onScanListener.onWrapFile(file));
//                                        }
                                    }
                                }
                            }

                        }
                        synchronized (taskList) {
                            taskList.remove(ScannerTask.this);
                        }
                        emitter.onComplete();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }

    }

    public void setType(@NonNull String type) {
        this.type = type.toLowerCase();
    }

    public void start(final OnScanListener<T> onScanListener) {
//        this.onScanListener = onScanListener;
        folderList.clear();
        taskList.clear();
        if (TextUtils.isEmpty(type)) {
            return;
        }
        Observable.create(
                (ObservableOnSubscribe<T>) emitter -> {
                    Environment.getRootDirectory();

                    File file = Environment.getExternalStorageDirectory();
                    folderList.addAll(Arrays.asList(file.listFiles()));
                    for (int i = 0; i < 3; i++) {
                        taskList.add(new ScannerTask<>(emitter, onScanListener, type));
                    }
                    while(true) {
//                        Log.d(TAG, "folderList.size=" + folderList.size() + " taskList.size=" + taskList.size());
//                        if (!folderList.isEmpty()) {
//                            if (taskList.size() < 3) {
//                                taskList.add(new ScannerTask<>(emitter, onScanListener, type));
//                            }
//                        } else if (taskList.isEmpty()) {
//                            break;
//                        }
                        if (folderList.isEmpty() && taskList.isEmpty()) {
                            break;
                        }
                    }
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (onScanListener != null) {
                            onScanListener.onScanBegin();
                        }
                    }

                    @Override
                    public void onNext(T item) {
                        if (onScanListener != null) {
                            onScanListener.onScanningFiles(item);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (onScanListener != null) {
                            onScanListener.onScanEnd();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (onScanListener != null) {
                            onScanListener.onScanEnd();
                        }
                    }
                });
    }

    public interface OnScanListener<T> {

        /**
         * 扫描开始
         */
        void onScanBegin();

        /**
         * 扫描结束
         */
        void onScanEnd();

        /**
         * 扫描进行中
         * @param paramString 文件夹地址
         * @param progress  扫描进度
         */
        void onScanning(String paramString, int progress);

        T onWrapFile(File file);

        /**
         * 扫描进行中，文件的更新
         * @param file
         * @param type  SCANNER_TYPE_ADD：添加；SCANNER_TYPE_DEL：删除
         */
        void onScanningFiles(T item);

    }


}
