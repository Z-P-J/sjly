package com.zpj.fragmentation.dialog.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.dialog.animator.EmptyAnimator;
import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.fragmentation.dialog.imagetrans.DialogView;
import com.zpj.fragmentation.dialog.imagetrans.ITConfig;
import com.zpj.fragmentation.dialog.imagetrans.ImageLoad;
import com.zpj.fragmentation.dialog.imagetrans.ImageTransAdapter;
import com.zpj.fragmentation.dialog.imagetrans.ImageTransBuild;
import com.zpj.fragmentation.dialog.imagetrans.ScaleType;
import com.zpj.fragmentation.dialog.imagetrans.listener.ProgressViewGet;
import com.zpj.fragmentation.dialog.imagetrans.listener.SourceImageViewGet;

import java.util.List;


/**
 * Description: 大图预览的弹窗，使用Transition实现
 * Create by lxj, at 2019/1/22
 */
public class ImageViewerDialogFragment2<T> extends FullScreenDialogFragment {

    protected final ImageTransBuild<T> build = new ImageTransBuild<>();
    protected DialogView<T> dialogView;

    @Override
    protected int getContentLayoutId() {
        return 0;
    }

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        return new EmptyAnimator();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        build.dialog = this;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        dialogView = new DialogView<>(context, build);
        contentView = dialogView;
        super.initView(view, savedInstanceState);
        dialogView.onCreate(this);
    }

    @Override
    public BaseDialogFragment show(SupportFragment fragment) {
        build.checkParam();
        return super.show(fragment);
    }

    @Override
    public BaseDialogFragment show(SupportActivity activity) {
        build.checkParam();
        return super.show(activity);
    }

    @Override
    public BaseDialogFragment show(Context context) {
        build.checkParam();
        return super.show(context);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        dialogView.onDismiss(this);
    }

    public ImageViewerDialogFragment2<T> setNowIndex(int index) {
        build.clickIndex = index;
        build.nowIndex = index;
        return this;
    }

    public ImageViewerDialogFragment2<T> setImageList(List<T> imageList) {
        build.imageList = imageList;
        return this;
    }

    public ImageViewerDialogFragment2<T> setSourceImageView(SourceImageViewGet sourceImageView) {
        build.sourceImageViewGet = sourceImageView;
        return this;
    }

    public ImageViewerDialogFragment2<T> setAdapter(ImageTransAdapter adapter) {
        build.imageTransAdapter = adapter;
        return this;
    }

    public ImageViewerDialogFragment2<T> setImageLoad(ImageLoad<T> imageLoad) {
        build.imageLoad = imageLoad;
        return this;
    }

    public ImageViewerDialogFragment2<T> setScaleType(ScaleType scaleType) {
        build.scaleType = scaleType;
        return this;
    }

    public ImageViewerDialogFragment2<T> setConfig(ITConfig itConfig) {
        build.itConfig = itConfig;
        return this;
    }

    public ImageViewerDialogFragment2<T> setProgressBar(ProgressViewGet progressViewGet) {
        build.progressViewGet = progressViewGet;
        return this;
    }

}
