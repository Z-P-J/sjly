package com.zpj.fragmentation.dialog.imagetrans;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.imagetrans.listener.OnPullCloseListener;
import com.zpj.fragmentation.dialog.imagetrans.listener.OnTransformListener;

import java.util.UUID;

/**
 * Created by liuting on 18/3/19.
 */

public class ImageItemView<T> extends FrameLayout implements
        TransformAttacher.TransStateChangeListener,
        OnPullCloseListener,
        View.OnLongClickListener,
        TransImageView.OnClickListener {
    private ImageTransBuild<T> build;
    private TransImageView imageView;
    private View progressBar;
    private int pos;
    private T url;
    private OnTransformListener transformOpenListener;

    private boolean transOpenEnd;
    private boolean loadFinish = false;
    private boolean isCached = false;
    private String uniqueStr;
    private boolean needTransOpen;
    private boolean isOpened;

    public ImageItemView(@NonNull Context context, ImageTransBuild<T> build, int pos, T url) {
        super(context);
        this.build = build;
        this.pos = pos;
        this.url = url;
        uniqueStr = UUID.randomUUID().toString();
    }

    public void setUrl(T url) {
        this.url = url;
    }

    void init(boolean opened) {
        this.isOpened = opened;
//        imageView = new TransImageView(getContext());
//        imageView.setOnTransformListener(transformOpenListener);
//        addView(imageView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        progressBar = build.inflateProgress(getContext(), this);
//        hideProgress();
//        needTransOpen = build.needTransOpen(pos, true);
//        imageView.settingConfig(build.itConfig, new ThumbConfig(build.sourceImageViewGet.getImageView(pos), getResources(), build.scaleType));
//        imageView.setTransStateChangeListener(this);
//        imageView.setOnPullCloseListener(this);
//        imageView.setOnLongClickListener(this);
//        imageView.setOnClickListener(this);
//        if (needTransOpen || opened) loadImage();

        build.sourceImageViewGet.updateImageView(this, pos);
    }

    public void update(View view) {
        imageView = new TransImageView(getContext());
        imageView.setOnTransformListener(transformOpenListener);
        addView(imageView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progressBar = build.inflateProgress(getContext(), this);
        hideProgress();
        needTransOpen = build.needTransOpen(pos, true);
        imageView.settingConfig(build.itConfig, new ThumbConfig(view, getResources(), build.scaleType));
        imageView.setTransStateChangeListener(this);
        imageView.setOnPullCloseListener(this);
        imageView.setOnLongClickListener(this);
        imageView.setOnClickListener(this);
        if (needTransOpen || isOpened) loadImage();
    }

    void loadImageWhenTransEnd() {
        if (!needTransOpen) loadImage();
    }

    void loadImage() {
        isCached = build.imageLoad.isCached(url);
        final boolean needShowThumb = !build.itConfig.noThumb && !(build.itConfig.noThumbWhenCached && build.imageLoad.isCached(url));
        if (needShowThumb) {
            imageView.showThumb(needTransOpen);
        } else if (!needTransOpen) {
            imageView.setBackgroundAlpha(255);
        }
        build.imageLoad.loadImage(url, new ImageLoad.LoadCallback() {
            @Override
            public void progress(float progress) {
                if (transOpenEnd) {
                    progressChange(progress);
                }
            }

            @Override
            public void loadFinish(Drawable drawable) {
                hideProgress();
                loadFinish = true;
                imageView.showImage(drawable, needTransOpen || needShowThumb);
            }
        }, imageView, uniqueStr);
    }

    void onDismiss() {
        imageView.showCloseTransform();
    }

    void onDestroy() {
        build.imageLoad.cancel(url, uniqueStr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    public void bindTransOpenListener(OnTransformListener listener) {
        this.transformOpenListener = listener;
    }


    private void showProgress() {
        if (progressBar != null && !loadFinish) progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void progressChange(float progress) {
        if (progressBar != null) build.progressViewGet.onProgressChange(progressBar, progress);
    }

    @Override
    public void onChange(TransformAttacher.TransState state) {
        switch (state) {
            case OPEN_TO_THUMB:
            case OPEN_TO_ORI:
                if (transformOpenListener != null) transformOpenListener.transformStart();
                break;
            case THUMB:
            case ORI:
                if (!transOpenEnd) {
                    transOpenEnd = true;
                    if (!isCached)
                        showProgress();
                    if (transformOpenListener != null) transformOpenListener.transformEnd();
                }
                break;
            case THUMB_TO_CLOSE:
            case ORI_TO_CLOSE:
                build.imageTransAdapter.onCloseTransStart();
                getViewPager(getParent()).setCanScroll(false);
                break;
            case CLOSEED:
                build.imageTransAdapter.onCloseTransEnd();
                build.dialog.dismiss();
                break;
        }
    }

    InterceptViewPager getViewPager(ViewParent parent) {
        if (parent == null) return null;
        if (parent instanceof InterceptViewPager) {
            return (InterceptViewPager) parent;
        } else {
            return getViewPager(parent.getParent());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        build.imageTransAdapter.onLongClick(v, pos);
        return false;
    }

    @Override
    public boolean onClick(View v) {
        return build.imageTransAdapter.onClick(v, pos);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onPull(float range) {
        build.imageTransAdapter.onPullRange(range);
    }

    @Override
    public void onCancel() {
        build.imageTransAdapter.onPullCancel();
    }
}
