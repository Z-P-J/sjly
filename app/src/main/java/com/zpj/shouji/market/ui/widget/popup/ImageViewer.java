//package com.zpj.shouji.market.ui.widget.popup;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.transition.Transition;
//import com.zpj.popup.core.ImageViewerPopup2;
//import com.zpj.popup.imagetrans.ImageLoad;
//
//public class ImageViewer extends ImageViewerPopup2<String> {
//
//    public static ImageViewer with(Context context) {
//        return new ImageViewer(context);
//    }
//
//
//    public ImageViewer(@NonNull Context context) {
//        super(context);
//        setImageLoad(new ImageLoader());
//    }
//
//    public static class ImageLoader implements ImageLoad<String> {
//
//        @Override
//        public void loadImage(String url, LoadCallback callback, ImageView imageView, String uniqueStr) {
//            Glide.with(imageView).asDrawable().load(url).into(new SimpleTarget<Drawable>() {
//                @Override
//                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                    callback.loadFinish(resource);
//                }
//            });
//        }
//
//        @Override
//        public boolean isCached(String url) {
//            return false;
//        }
//
//        @Override
//        public void cancel(String url, String unique) {
//
//        }
//    }
//
//
//}
