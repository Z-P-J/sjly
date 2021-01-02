package com.zpj.shouji.market.utils;


import android.arch.lifecycle.LifecycleOwner;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.zpj.rxlife.RxLife;

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

public class FileScanner<T> {

    private static final String TAG = "ZFileScanner";

    private static final ConcurrentLinkedQueue<File> folderList = new ConcurrentLinkedQueue<>();

    private static final List<ScannerTask<?>> taskList = new ArrayList<>();

    private String type;
    private LifecycleOwner lifecycleOwner;

    private static class ScannerTask<T> {

        private final ObservableEmitter<T> callback;
        private final OnScanListener<T> onScanListener;
        private final String type;
        private final LifecycleOwner lifecycleOwner;

        public ScannerTask(ObservableEmitter<T> callback, OnScanListener<T> onScanListener, String type, LifecycleOwner lifecycleOwner) {
            this.callback = callback;
            this.onScanListener = onScanListener;
            this.type = type;
            this.lifecycleOwner = lifecycleOwner;
            start();
        }

        public void start() {
            Observable<File> observable = Observable.create(
                    (ObservableOnSubscribe<File>) emitter -> {
                        while (!folderList.isEmpty()) {
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
                    .observeOn(AndroidSchedulers.mainThread());
            if (lifecycleOwner != null) {
                observable = observable.compose(RxLife.bindLifeOwner(lifecycleOwner));
            }
            observable.subscribe();
        }

    }

    public FileScanner<T> setType(@NonNull String type) {
        this.type = type.toLowerCase();
        return this;
    }

    public FileScanner<T> bindLife(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        return this;
    }

    public void start(final OnScanListener<T> onScanListener) {
//        this.onScanListener = onScanListener;
        folderList.clear();
        taskList.clear();
        if (TextUtils.isEmpty(type)) {
            return;
        }
        Observable<T> observable = Observable.create(
                (ObservableOnSubscribe<T>) emitter -> {
                    Environment.getRootDirectory();

                    File file = Environment.getExternalStorageDirectory();
                    folderList.addAll(Arrays.asList(file.listFiles()));
                    for (int i = 0; i < 3; i++) {
                        taskList.add(new ScannerTask<>(emitter, onScanListener, type, lifecycleOwner));
                    }
                    while (true) {
                        if (folderList.isEmpty() && taskList.isEmpty()) {
                            break;
                        }
                    }
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        if (lifecycleOwner != null) {
            observable = observable.compose(RxLife.bindLifeOwner(lifecycleOwner));
        }
        observable.subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (onScanListener != null) {
                    onScanListener.onScanBegin();
                }
            }

            @Override
            public void onNext(@NonNull T item) {
                if (onScanListener != null) {
                    onScanListener.onScanningFiles(item);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
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
         *
         * @param paramString 文件夹地址
         * @param progress    扫描进度
         */
        void onScanning(String paramString, int progress);

        T onWrapFile(File file);

        /**
         * 扫描进行中，文件的更新
         *
         */
        void onScanningFiles(T item);

    }


}
