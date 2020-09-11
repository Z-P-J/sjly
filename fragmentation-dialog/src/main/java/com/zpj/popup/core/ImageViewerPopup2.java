package com.zpj.popup.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.zpj.popup.animator.EmptyAnimator;
import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.imagetrans.DialogView;
import com.zpj.popup.imagetrans.ITConfig;
import com.zpj.popup.imagetrans.ImageLoad;
import com.zpj.popup.imagetrans.ImageTransAdapter;
import com.zpj.popup.imagetrans.ImageTransBuild;
import com.zpj.popup.imagetrans.ScaleType;
import com.zpj.popup.imagetrans.listener.ProgressViewGet;
import com.zpj.popup.imagetrans.listener.SourceImageViewGet;
import com.zpj.popup.impl.FullScreenPopup;

import java.util.List;


/**
 * Description: 大图预览的弹窗，使用Transition实现
 * Create by lxj, at 2019/1/22
 */
public class ImageViewerPopup2<T> extends FullScreenPopup<ImageViewerPopup2<T>> {

    protected final ImageTransBuild<T> build;
    protected DialogView<T> dialogView;

    public ImageViewerPopup2(@NonNull Context context) {
        super(context);
        build = new ImageTransBuild<>();
        build.dialog = this;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        dialogView.onCreate(this);
    }

    @Override
    protected View getContentView() {
        dialogView = new DialogView<>(context, build);
        return dialogView;
    }

    @Override
    public ImageViewerPopup2<T> show() {
        build.checkParam();
        return super.show();
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new EmptyAnimator();
    }

    @Override
    protected void onShow() {
        super.onShow();
    }

//    @Override
//    public void dismiss() {
//        if (dialogView.isDismissed()) {
//            super.dismiss();
//        } else {
//            dialogView.onDismiss(this);
//        }
//    }

    @Override
    public void hide() {
        dismiss();
    }

        @Override
    protected void onDismiss() {
        super.onDismiss();
        dialogView.onDismiss(this);
    }

    @Override
    protected boolean onBackPressed() {
        dialogView.onDismiss(this);
        return true;
    }

    public ImageViewerPopup2<T> setNowIndex(int index) {
        build.clickIndex = index;
        build.nowIndex = index;
        return this;
    }

    public ImageViewerPopup2<T> setImageList(List<T> imageList) {
        build.imageList = imageList;
        return this;
    }

    public ImageViewerPopup2<T> setSourceImageView(SourceImageViewGet sourceImageView) {
        build.sourceImageViewGet = sourceImageView;
        return this;
    }

    public ImageViewerPopup2<T> setAdapter(ImageTransAdapter adapter) {
        build.imageTransAdapter = adapter;
        return this;
    }

    public ImageViewerPopup2<T> setImageLoad(ImageLoad<T> imageLoad) {
        build.imageLoad = imageLoad;
        return this;
    }

    public ImageViewerPopup2<T> setScaleType(ScaleType scaleType) {
        build.scaleType = scaleType;
        return this;
    }

    public ImageViewerPopup2<T> setConfig(ITConfig itConfig) {
        build.itConfig = itConfig;
        return this;
    }

    public ImageViewerPopup2<T> setProgressBar(ProgressViewGet progressViewGet) {
        build.progressViewGet = progressViewGet;
        return this;
    }

}
