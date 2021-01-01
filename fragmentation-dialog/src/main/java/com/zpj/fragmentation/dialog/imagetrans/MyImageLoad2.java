package com.zpj.fragmentation.dialog.imagetrans;

import android.content.ContentResolver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.zpj.fragmentation.dialog.widget.ImageViewContainer;

import java.io.File;
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

    public void loadImage(final T url, final ImageLoad.LoadCallback callback, final ImageViewContainer imageView, final String unique) {
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
    private void loadImageFromNet(final String url, final String unique, final ImageViewContainer imageView) {
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
    protected void loadImageFromLocal(String path, final String unique, final ImageViewContainer imageView) {
        Log.d("loadImageFromLocal", "path=" + path);
        if (path.toLowerCase().endsWith(".itgif") || path.toLowerCase().endsWith(".gif")) {
            Glide.with(imageView).asDrawable().load(new File(path)).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    onFinishLoad(unique, resource);
                    if (resource instanceof GifDrawable) {
                        imageView.showGif((GifDrawable) resource);
                    } else {
                        imageView.showPlaceholder(resource);
                    }

                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    onLoadFailed(errorDrawable);
                }
            });
//            imageView.showGif(new File(path));
//            onFinishLoad(unique, null);
        } else {
            imageView.getPhotoView().setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
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
            imageView.getPhotoView().setImage(ImageSource.uri(path));
        }




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
