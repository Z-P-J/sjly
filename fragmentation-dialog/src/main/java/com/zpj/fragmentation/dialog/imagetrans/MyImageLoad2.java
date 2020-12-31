package com.zpj.fragmentation.dialog.imagetrans;

import android.content.ContentResolver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.HashMap;
import java.util.regex.Pattern;


/**
 * Created by liuting on 17/6/1.
 */

public class MyImageLoad2<T> {
    private static final Pattern webPattern = Pattern.compile("http[s]*://[[[^/:]&&[a-zA-Z_0-9]]\\.]+(:\\d+)?(/[a-zA-Z_0-9]+)*(/[a-zA-Z_0-9]*([a-zA-Z_0-9]+\\.[a-zA-Z_0-9]+)*)?(\\?(&?[a-zA-Z_0-9]+=[%[a-zA-Z_0-9]-]*)*)*(#[[a-zA-Z_0-9]|-]+)?(.jpg|.png|.gif|.jpeg)?");
    private static final String ASSET_PATH_SEGMENT = "android_asset";
    private static final HashMap<String, ImageLoad.LoadCallback> loadCallbackMap = new HashMap<>();
    private static final HashMap<String, OkHttpImageLoad.ImageDownLoadListener> imageDownLoadListenerMap = new HashMap<>();

    public void loadImage(final T url, final ImageLoad.LoadCallback callback, final SubsamplingScaleImageView imageView, final String unique) {
        addLoadCallback(unique, callback);
        String link = url.toString();
        Uri uri = Uri.parse(link);
        if (isLocalUri(uri.getScheme())) {
            if (isAssetUri(uri)) {
                //是asset资源文件
            } else {
                //是本地文件
                loadImageFromLocal(uri.getPath(), unique, imageView);
            }
        } else {
            if (isNetUri(link)) {
                loadImageFromNet(link, unique, imageView);
                return;
            }
            Log.e("MyImageLoad", "未知的图片URL的类型");
        }
    }

    /**
     * 从网络加载图片
     */
    private void loadImageFromNet(final String url, final String unique, final SubsamplingScaleImageView imageView) {
        OkHttpImageLoad.ImageDownLoadListener loadListener = new OkHttpImageLoad.ImageDownLoadListener() {
            @Override
            public void inProgress(float progress, long total) {
                onProgress(unique, progress);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess() {
                loadImageFromLocal(OkHttpImageLoad.getCachedPath(url), unique, imageView);
            }

            @Override
            public void onCancel() {

            }

        };
        imageDownLoadListenerMap.put(unique, loadListener);
        OkHttpImageLoad.getInstance().load(url, loadListener);
    }

    /**
     * 从本地加载图片
     */
    protected void loadImageFromLocal(String path, final String unique, final SubsamplingScaleImageView imageView) {
//        imageView.setLoadListener(new XPhotoViewListener.OnXPhotoLoadListener() {
//            @Override
//            public void onImageLoadStart(XPhotoView view) {
//
//            }
//
//            @Override
//            public void onImageLoaded(XPhotoView view) {
//                onFinishLoad(unique, null);
//            }
//        });
//        imageView.setImage(new File(path));
        imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {

            }

            @Override
            public void onImageLoaded() {
                onFinishLoad(unique, null);
            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {

            }

            @Override
            public void onTileLoadError(Exception e) {

            }

            @Override
            public void onPreviewReleased() {

            }
        });
        imageView.setImage(ImageSource.uri(path));



    }

    public static void addLoadCallback(String unique, ImageLoad.LoadCallback callback) {
        loadCallbackMap.put(unique, callback);
    }

    public static void removeLoadCallback(String unique) {
        loadCallbackMap.remove(unique);
    }

    public static void onFinishLoad(String unique, Drawable drawable) {
        ImageLoad.LoadCallback loadCallback = loadCallbackMap.remove(unique);
        if (loadCallback != null) {
            loadCallback.loadFinish(drawable);
        }
    }

    public static void onProgress(String unique, float progress) {
        ImageLoad.LoadCallback loadCallback = loadCallbackMap.get(unique);
        if (loadCallback != null) {
            loadCallback.progress(progress);
        }
    }


    public boolean isCached(T url) {
        String link = url.toString();
        if (isLocalUri(Uri.parse(link).getScheme())) {
            //是本地图片不用预览图
            return true;
        }
        return OkHttpImageLoad.getInstance().checkImageExists(link);
//        return false;
    }

    public void cancel(T url, String unique) {
        removeLoadCallback(unique);
        String link = url.toString();
        OkHttpImageLoad.getInstance().destroy(link, imageDownLoadListenerMap.remove(unique));
    }

    private static boolean isNetUri(String url) {
        return webPattern.matcher(url).find();
    }

    private static boolean isLocalUri(String scheme) {
        return ContentResolver.SCHEME_FILE.equals(scheme)
                || ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme);
    }

    public static boolean isAssetUri(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme()) && !uri.getPathSegments().isEmpty()
                && ASSET_PATH_SEGMENT.equals(uri.getPathSegments().get(0));
    }

}
