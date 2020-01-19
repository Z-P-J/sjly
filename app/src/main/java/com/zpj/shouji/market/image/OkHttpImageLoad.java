//package com.zpj.shouji.market.image;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.zpj.shouji.market.App;
//import com.zpj.shouji.market.utils.ExecutorHelper;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.concurrent.Executor;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//
///**
// * Created by liuting on 16/8/26.
// */
//public class OkHttpImageLoad {
//
////    private Platform mPlatform;
//
//    private OkHttpClient mOkHttpClient;
//
//    private volatile static OkHttpImageLoad mInstance;
//    private HashMap<String, Builder> map = new LinkedHashMap<>();
//
//    private OkHttpImageLoad() {
//        mOkHttpClient = new OkHttpClient();
////        mPlatform = Platform.get();
//    }
//
//    /**
//     * 加载图片
//     *
//     * @param url
//     * @param listener
//     */
//    public static void load(String url, ImageDownLoadListener listener) {
//        if (TextUtils.isEmpty(url)) {
//            listener.onError(new Exception("链接不能为null"));
//            return;
//        }
//        if (mInstance == null) {
//            mInstance = new OkHttpImageLoad();
//        }
//        Builder builder = null;
//        if (mInstance.map.containsKey(url)) {
//            builder = mInstance.map.get(url);
//        } else if (checkImageExists(url)) {
//            //没有发现正在下载，检验是否已经下载过了
//            listener.onSuccess();
//            return;
//        }
//        if (builder == null) {
//            builder = new Builder(url);
//            mInstance.map.put(url, builder);
//        }
//        builder.listener(listener);
//        builder.start();
//    }
//
//    /**
//     * 判断图片是否已经存在
//     *
//     * @param url
//     * @return
//     */
//    public static boolean checkImageExists(String url) {
//        String key = App.generate(url);
//        String destUrl = App.getImageCachePath() + "/" + key;
//        File file = new File(destUrl);
//        if (file.exists()) {
//            int size = App.getMaxSizeOfBitMap(destUrl);
//            if (size > 0) {
//                return true;
//            } else {
//                file.delete();
//                return false;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 解绑监听器,实际下载还在后台进行
//     *
//     * @param url
//     * @param listener
//     */
//    public static void cancel(String url, ImageDownLoadListener listener) {
//        if (mInstance == null) {
//            return;
//        }
//        if (mInstance.map.containsKey(url)) {
//            Builder builder = mInstance.map.get(url);
//            if (builder != null) {
//                builder.removeListener(listener);
//            }
//        }
//    }
//
//    /**
//     * 取消下载图片
//     *
//     * @param url
//     * @param listener
//     */
//    public static void destroy(String url, ImageDownLoadListener listener) {
//        if (mInstance == null) {
//            return;
//        }
//        if (mInstance.map.containsKey(url)) {
//            Builder builder = mInstance.map.get(url);
//            if (builder != null) {
//                mInstance.map.remove(url);
//                builder.cancel();
//                builder.removeListener(listener);
//            }
//        }
//    }
//
//    public static class Builder {
//        protected Request.Builder builder = new Request.Builder();
//        protected String url;
//        private Request request;
//        private Call call;
//        private List<ImageDownLoadListener> imageDownLoadListener = new ArrayList<>();
//        private boolean isSuccess = false;
//        private boolean isStarted = false;
//        private float currentProgress = 0f;
//        private long total = 0L;
//        private State currentState = State.DOWNLOADING;
//
//        private enum State {
//            DOWNLOADING, DOWNLOADERROR, DOWNLOADFINISH
//        }
//
//        public Builder(String url) {
//            this.url = url;
//            request = builder.url(url)
////                    .addHeader("", "")
//                    .header("User-Agent", "okhttp/3.0.1")
////                    .header("Cookie", UserManager.getInstance().getBduss())
//                    .header("Referer", url)
//                    .get().build();
//            call = mInstance.mOkHttpClient.newCall(request);
//        }
//
//        public Builder listener(ImageDownLoadListener listener) {
//            if (!imageDownLoadListener.contains(listener))
//                imageDownLoadListener.add(listener);
//            return this;
//        }
//
//        public void cancel() {
//            if (null == call) {
//                throw new NullPointerException(" cancel() must be called before calling build() ");
//            }
//            if (!isSuccess) {
//                //切换到非UI线程，进行网络的取消工作
//                ExecutorHelper.submit(new Runnable() {
//                    @Override
//                    public void run() {
//                        call.cancel();
//                    }
//                });
//                downloadCancel();
//            }
//        }
//
//        private void execute() {
//            isStarted = true;
//            currentState = State.DOWNLOADING;
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    downloadFail(e);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (call.isCanceled()) {
//                        downloadFail(new Exception("Canceled!"));
//                        return;
//                    }
//                    if (!response.isSuccessful()) {
//                        downloadFail(new Exception("request failed , reponse's code is : " + response.code()));
//                        return;
//                    }
//                    saveFile(response);
//                    downloadSuccess();
//                }
//            });
//        }
//
//        private void saveFile(Response response) throws IOException {
//            InputStream is = null;
//            byte[] buf = new byte[2048];
//            int len;
//            FileOutputStream fos = null;
//            try {
//                is = response.body().byteStream();
//                final long total = response.body().contentLength();
//
//                long sum = 0;
//
//                File dir = new File(App.getImageCachePath());
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                String key = App.generate(url);
//                String destUrl = App.getImageCachePath() + "/" + key;
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
//                response.body().close();
//                if (is != null) is.close();
//                if (fos != null) fos.close();
//            }
//        }
//
//        /**
//         * 如果已经开启就不再执行网络加载操作
//         */
//        public void start() {
//            checkState();
//            if (!isStarted) {
//                execute();
//            }
//        }
//
//        private void checkState() {
//            switch (currentState) {
//                case DOWNLOADING:
//                    refreshProgress(currentProgress, total);
//                    break;
//                case DOWNLOADFINISH:
//                    downloadSuccess();
//            }
//        }
//
//        private void downloadCancel() {
//            for (ImageDownLoadListener listener : imageDownLoadListener)
//                listener.onCancel();
//        }
//
//        private void refreshProgress(final float progress, final long total) {
//            this.currentProgress = progress;
//            this.total = total;
//            ExecutorHelper.submit(new Runnable() {
//                @Override
//                public void run() {
//                    for (ImageDownLoadListener listener : imageDownLoadListener)
//                        listener.inProgress(progress, total);
//                }
//            });
//        }
//
//        private void downloadFail(final Exception e) {
//            currentState = State.DOWNLOADERROR;
//            String key = App.generate(url);
//            String destUrl = App.getImageCachePath() + "/" + key;
//            File file = new File(destUrl);
//            if (file.exists()) file.delete();
//            if (imageDownLoadListener.size() == 0) {
//                //发现没有绑定任何监听，自动移除当前build
//                mInstance.map.remove(url);
//                return;
//            }
//            ExecutorHelper.submit(new Runnable() {
//                @Override
//                public void run() {
//                    for (ImageDownLoadListener listener : imageDownLoadListener)
//                        listener.onError(e);
//                }
//            });
//        }
//
//        private void downloadSuccess() {
//            isSuccess = true;
//            currentState = State.DOWNLOADFINISH;
//            if (imageDownLoadListener.size() == 0) {
//                //发现没有绑定任何监听，自动移除当前build
//                mInstance.map.remove(url);
//                return;
//            }
//            ExecutorHelper.submit(new Runnable() {
//                @Override
//                public void run() {
//                    for (ImageDownLoadListener listener : imageDownLoadListener)
//                        listener.onSuccess();
//                }
//            });
//        }
//
//        public void removeListener(ImageDownLoadListener listener) {
//            imageDownLoadListener.remove(listener);
//            if (imageDownLoadListener.size() == 0 && currentState == State.DOWNLOADFINISH) {
//                mInstance.map.remove(url);
//            }
//        }
//    }
//
//
//    public interface ImageDownLoadListener {
//        void inProgress(float progress, long total);
//
//        void onError(Exception e);
//
//        void onSuccess();
//
//        void onCancel();
//    }
//
//}