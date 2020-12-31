package com.zpj.fragmentation.dialog.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class ImageViewContainer extends FrameLayout {

//    private final XPhotoView photoView;
    private final SubsamplingScaleImageView imageView;
    private final ProgressBar progressBar;
    private final ImageView placeholder;

    public ImageViewContainer(@NonNull Context context) {
        this(context, null);
    }

    public ImageViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        photoView = new XPhotoView(context);
//        addView(photoView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        imageView = new SubsamplingScaleImageView(context);
//        imageView.setDebug(true);
        addView(imageView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        placeholder = new ImageView(getContext());
        placeholder.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView(placeholder, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        progressBar = new ProgressBar(context);
        progressBar.setMax(100);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(progressBar, params);
    }

    public void showPlaceholder(Drawable drawable) {
//        if (placeholder == null) {
//            placeholder = new ImageView(getContext());
//            placeholder.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            addView(placeholder, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }
        placeholder.setVisibility(View.VISIBLE);
        placeholder.setImageDrawable(drawable);
    }

    public SubsamplingScaleImageView getPhotoView() {
        return imageView;
    }

//    public XPhotoView getPhotoView() {
//        return photoView;
//    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgress(float progress) {
        progressBar.setProgress((int) progress);
    }

    public void onLoadFinished() {
        Toast.makeText(getContext(), "onLoadFinished", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(GONE);
        placeholder.setVisibility(GONE);
    }

    public void showProgressBar() {
        progressBar.setVisibility(VISIBLE);
    }

}
