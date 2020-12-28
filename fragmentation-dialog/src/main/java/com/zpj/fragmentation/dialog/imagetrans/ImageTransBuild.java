package com.zpj.fragmentation.dialog.imagetrans;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.imagetrans.listener.ProgressViewGet;
import com.zpj.fragmentation.dialog.imagetrans.listener.SourceImageViewGet;
import com.zpj.fragmentation.dialog.impl.FullScreenDialogFragment;

import java.util.List;

/**
 * Created by liuting on 18/3/14.
 */

public class ImageTransBuild<T> {
    public int clickIndex;
    public int nowIndex;
    public List<T> imageList;
    public SourceImageViewGet<T> sourceImageViewGet;
    public ProgressViewGet<View> progressViewGet;
    public ITConfig itConfig;
    public ImageTransAdapter imageTransAdapter;
    public ImageLoad<T> imageLoad;
    public ScaleType scaleType = ScaleType.CENTER_CROP;
    public FullScreenDialogFragment dialog;
    public int offset = 0;

    public void checkParam() {
        if (itConfig == null)
            itConfig = new ITConfig();
        if (imageTransAdapter == null) {
            imageTransAdapter = new ImageTransAdapter() {
                @Override
                protected View onCreateView(View parent, ViewPager viewPager, FullScreenDialogFragment dialogInterface) {
                    return null;
                }
            };
        }
        if (imageLoad == null)
            imageLoad = new MyImageLoad<>();
        if (imageList == null)
            throw new NullPointerException("not set ImageList");
    }

    boolean needTransOpen(int pos, boolean change) {
        boolean need = pos == clickIndex;
        if (need && change) {
            clickIndex = -1;
        }
        return need;
    }

    View inflateProgress(Context context, FrameLayout rootView) {
        if (progressViewGet != null) {
            View progress = progressViewGet.getProgress(context);
            progress.setVisibility(View.VISIBLE);
            if (progress == null) return null;
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (progress.getLayoutParams() != null) {
                width = progress.getLayoutParams().width;
                height = progress.getLayoutParams().height;
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            lp.gravity = Gravity.CENTER;
            rootView.addView(progress, lp);
            return progress;
        }
        return null;
    }
}
