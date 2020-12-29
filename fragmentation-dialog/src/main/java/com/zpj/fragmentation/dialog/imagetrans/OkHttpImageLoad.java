package com.zpj.fragmentation.dialog.imagetrans;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by liuting on 16/8/26.
 */
public class OkHttpImageLoad {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26;

    private static String IMAGE_CACHE_PATH;

    private volatile static OkHttpImageLoad mInstance;
    private HashMap<String, Builder> map = new LinkedHashMap<>();

    private OkHttpImageLoad() {
        IMAGE_CACHE_PATH = ContextUtils.getApplicationContext().getExternalCacheDir().getAbsolutePath() + "/thumbnail_cache";
        File file = new File(IMAGE_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static OkHttpImageLoad getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpImageLoad();
        }
        return mInstance;
    }

    /**
     * 加载图片
     *
     * @param url
     * @param listener
     */
    public void load(String url, ImageDownLoadListener listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onError(new Exception("The link is null"));
            return;
        }
        Builder builder = null;
        if (map.containsKey(url)) {
            builder = map.get(url);
        } else if (checkImageExists(url)) {
            //没有发现正在下载，检验是否已经下载过了
            listener.onSuccess();
            return;
        }
        if (builder == null) {
            builder = new Builder(url);
            map.put(url, builder);
        }
        builder.listener(listener);
        builder.start();
    }

    public static String getImageCachePath() {
        getInstance();
        return IMAGE_CACHE_PATH;
    }

    public static String getCachedPath(String url) {
        String key = generate(url);
        String destUrl = getImageCachePath() + "/" + key;
        return destUrl;
    }

    /**
     * 判断图片是否已经存在
     *
     * @param url
     * @return
     */
    public boolean checkImageExists(String url) {
        String key = generate(url);
        String destUrl = IMAGE_CACHE_PATH + "/" + key;
        File file = new File(destUrl);
        if (file.exists()) {
            int size = getMaxSizeOfBitMap(destUrl);
            if (size > 0) {
                return true;
            } else {
                file.delete();
                return false;
            }
        }
        return false;
    }

    public static String generate(String imageUri) {
        byte[] md5 = getMD5(imageUri.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        if (imageUri.toLowerCase().endsWith(".gif")) {
            return bi.toString(RADIX) + ".itgif";
        }
        return bi.toString(RADIX) + ".it";
    }

    private static byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
        }
        return hash;
    }

    public static int getMaxSizeOfBitMap(String path) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, op);
        return Math.max(op.outWidth, op.outHeight);
    }

    /**
     * 解绑监听器,实际下载还在后台进行
     *
     * @param url
     * @param listener
     */
    public void cancel(String url, ImageDownLoadListener listener) {
        if (map.containsKey(url)) {
            Builder builder = map.get(url);
            if (builder != null) {
                builder.removeListener(listener);
            }
        }
    }

    /**
     * 取消下载图片
     *
     * @param url
     * @param listener
     */
    public void destroy(String url, ImageDownLoadListener listener) {
        if (map.containsKey(url)) {
            Builder builder = map.get(url);
            if (builder != null) {
                map.remove(url);
                builder.cancel();
                builder.removeListener(listener);
            }
        }
    }

    public static class Builder {
        protected String url;
        private Disposable disposable;
        private List<ImageDownLoadListener> imageDownLoadListener = new ArrayList<>();
        private boolean isSuccess = false;
        private boolean isStarted = false;
        private float currentProgress = 0f;
        private long total = 0L;
        private State currentState = State.DOWNLOADING;

        private enum State {
            DOWNLOADING, DOWNLOADERROR, DOWNLOADFINISH
        }

        public Builder(String url) {
            this.url = url;
        }

        public Builder listener(ImageDownLoadListener listener) {
            if (!imageDownLoadListener.contains(listener))
                imageDownLoadListener.add(listener);
            return this;
        }

        public void cancel() {
            if (null == disposable) {
                return;
            }
            if (!isSuccess) {
                //切换到非UI线程，进行网络的取消工作
                disposable.dispose();
                downloadCancel();
            }
        }

        private void execute() {
            isStarted = true;
            currentState = State.DOWNLOADING;

            disposable = Observable.create(
                    new ObservableOnSubscribe<File>() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Exception {
                            File file = Glide.with(ContextUtils.getApplicationContext())
                                    .asFile()
//                                .downloadOnly()
                                    .load(url)
                                    .submit()
                                    .get();
                            String key = generate(url);
                            String destUrl = getImageCachePath() + "/" + key;
                            File newFile = new File(destUrl);
                            FileUtils.copyFileFast(file, newFile);
                            emitter.onNext(newFile);
                            emitter.onComplete();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(file -> {
                        if (disposable.isDisposed()) {
                            downloadFail(new Exception("Canceled!"));
                            return;
                        }
                        downloadSuccess();
                    }, this::downloadFail);

//            disposable = new ObservableTask<File>(
//                    emitter -> {
//                        File file = Glide.with(ContextUtils.getApplicationContext())
//                                .asFile()
////                                .downloadOnly()
//                                .load(url)
//                                .submit()
//                                .get();
//                        String key = generate(url);
//                        String destUrl = getImageCachePath() + "/" + key;
//                        File newFile = new File(destUrl);
//                        FileUtils.copyFileFast(file, newFile);
//                        emitter.onNext(newFile);
//                        emitter.onComplete();
//                    })
//                    .onSuccess(data -> {
//                        if (disposable.isDisposed()) {
//                            downloadFail(new Exception("Canceled!"));
//                            return;
//                        }
//                        downloadSuccess();
//                    })
//                    .onError(new IHttp.OnErrorListener() {
//                        @Override
//                        public void onError(Throwable throwable) {
//                            downloadFail(throwable);
//                        }
//                    })
//                    .subscribe();
        }

//        private void saveFile(Connection.Response response) throws IOException {
//            Log.d("OkHttpImageLoad", "saveFile");
//            InputStream is = null;
//            byte[] buf = new byte[2048];
//            int len;
//            FileOutputStream fos = null;
//            try {
//                long total = 0;
//                String contentLength = response.header("Content-Length");
//                if (contentLength != null) {
//                    total = Long.parseLong(contentLength);
//                }
//
//                is = response.bodyStream();
//
//                if (total <= 0) {
//                    total = is.available();
//                }
//
//                long sum = 0;
//
//                File dir = new File(getImageCachePath());
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                String key = generate(url);
//                String destUrl = getImageCachePath() + "/" + key;
//                File file = new File(destUrl);
//                fos = new FileOutputStream(file);
//                while ((len = is.read(buf)) != -1) {
//                    sum += len;
//                    fos.write(buf, 0, len);
//                    final long finalSum = sum;
//                    refreshProgress(finalSum * 1.0f / total, total);
//                }
//                fos.flush();
//            } finally {
//                if (is != null) is.close();
//                if (fos != null) fos.close();
//            }
//        }

        /**
         * 如果已经开启就不再执行网络加载操作
         */
        public void start() {
            checkState();
            if (!isStarted) {
                execute();
            }
        }

        private void checkState() {
            switch (currentState) {
                case DOWNLOADING:
                    refreshProgress(currentProgress, total);
                    break;
                case DOWNLOADFINISH:
                    downloadSuccess();
            }
        }

        private void downloadCancel() {
            for (ImageDownLoadListener listener : imageDownLoadListener)
                listener.onCancel();
        }

        private void refreshProgress(final float progress, final long total) {
            this.currentProgress = progress;
            this.total = total;
            for (ImageDownLoadListener listener : imageDownLoadListener)
                listener.inProgress(progress, total);
        }

        private void downloadFail(final Throwable e) {
            e.printStackTrace();
            currentState = State.DOWNLOADERROR;
            String key = generate(url);
            String destUrl = getImageCachePath() + "/" + key;
            File file = new File(destUrl);
            if (file.exists()) file.delete();
            if (imageDownLoadListener.size() == 0) {
                //发现没有绑定任何监听，自动移除当前build
                mInstance.map.remove(url);
                return;
            }
            for (ImageDownLoadListener listener : imageDownLoadListener)
                listener.onError(e);
        }

        private void downloadSuccess() {
            isSuccess = true;
            currentState = State.DOWNLOADFINISH;
            if (imageDownLoadListener.size() == 0) {
                //发现没有绑定任何监听，自动移除当前build
                mInstance.map.remove(url);
                return;
            }
            for (ImageDownLoadListener listener : imageDownLoadListener)
                listener.onSuccess();
        }

        public void removeListener(ImageDownLoadListener listener) {
            imageDownLoadListener.remove(listener);
            if (imageDownLoadListener.size() == 0 && currentState == State.DOWNLOADFINISH) {
                mInstance.map.remove(url);
            }
        }
    }


    public interface ImageDownLoadListener {
        void inProgress(float progress, long total);

        void onError(Throwable e);

        void onSuccess();

        void onCancel();
    }

}