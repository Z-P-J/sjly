package com.zpj.fragmentation.dialog.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.EmptyAnimator;
import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.fragmentation.dialog.imagetrans.DialogView;
import com.zpj.fragmentation.dialog.imagetrans.ITConfig;
import com.zpj.fragmentation.dialog.imagetrans.ImageLoad;
import com.zpj.fragmentation.dialog.imagetrans.ImageTransAdapter;
import com.zpj.fragmentation.dialog.imagetrans.ImageTransBuild;
import com.zpj.fragmentation.dialog.imagetrans.OkHttpImageLoad;
import com.zpj.fragmentation.dialog.imagetrans.ScaleType;
import com.zpj.fragmentation.dialog.imagetrans.TileBitmapDrawable;
import com.zpj.fragmentation.dialog.imagetrans.listener.ProgressViewGet;
import com.zpj.fragmentation.dialog.imagetrans.listener.SourceImageViewGet;
import com.zpj.fragmentation.dialog.utils.Utility;
import com.zpj.http.core.ObservableTask;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.FileUtils;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Description: 大图预览的弹窗，使用Transition实现
 * Create by lxj, at 2019/1/22
 */
public class ImageViewerDialogFragment2<T> extends FullScreenDialogFragment {

    protected final ImageTransBuild<T> build = new ImageTransBuild<>();
    protected DialogView<T> dialogView;

    protected View customView;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_image_viewer2;
    }

    protected int getCustomLayoutId() {
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

        super.initView(view, savedInstanceState);

        dialogView = findViewById(R.id._dialog_view);


        build.imageTransAdapter = new ImageTransAdapter() {
            @Override
            protected View onCreateView(View parent, ViewPager viewPager, FullScreenDialogFragment dialogInterface) {
                return null;
            }

            @Override
            protected void onPullRange(float range) {
                ImageViewerDialogFragment2.this.onPullRange(range);
                if (customView != null) customView.setAlpha(1 - range);
            }

            @Override
            protected void onTransform(float ratio) {
                ImageViewerDialogFragment2.this.onTransform(ratio);
                if (customView != null) customView.setAlpha(ratio);
            }

            @Override
            protected void onOpenTransEnd() {
                ImageViewerDialogFragment2.this.onOpenTransEnd();
                if (customView != null) {
                    customView.setAlpha(1f);
                    customView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onOpenTransStart() {
                ImageViewerDialogFragment2.this.onOpenTransStart();
                if (customView != null) customView.setAlpha(0f);
            }
        };

        if (build.progressViewGet == null) {
            build.progressViewGet = new ProgressViewGet<View>() {
                @Override
                public View getProgress(Context context) {
                    ProgressBar progressBar = new ProgressBar(context);
                    progressBar.setMax(100);
//                    int size = ScreenUtils.dp2pxInt(context, 48);
//                    progressBar.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                    return progressBar;
                }

                @Override
                public void onProgressChange(View view, float progress) {
                    if (view instanceof ProgressBar) {
                        ((ProgressBar) view).setProgress((int) (progress * 100));
                    }
                }
            };
        }

        if (getCustomLayoutId() > 0) {
            customView = LayoutInflater.from(getContext()).inflate(getCustomLayoutId(), null, false);
            customView.setVisibility(View.INVISIBLE);
            customView.setAlpha(0);
            getImplView().addView(customView);
        }




    }

    @Override
    public void doShowAnimation() {
        build.offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
        dialogView.onCreate(build, this);
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
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public void doDismissAnimation() {
        dialogView.onDismiss(this);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        TileBitmapDrawable.clearCache();
    }

    protected void onPullRange(float range) {

    }

    protected void onTransform(float ratio) {

    }

    protected void onOpenTransEnd() {

    }

    protected void onOpenTransStart() {

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

    public ImageViewerDialogFragment2<T> setSourceImageView(SourceImageViewGet<T> sourceImageView) {
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

    public ImageViewerDialogFragment2<T> setProgressBar(ProgressViewGet<View> progressViewGet) {
        build.progressViewGet = progressViewGet;
        return this;
    }

    public List<T> getUrls() {
        return new ArrayList<>(build.imageList);
    }

    /**
     * 保存图片到相册，会自动检查是否有保存权限
     */
    protected void save() {
        //check permission
        XPermission.create(getContext(), PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        //save bitmap to album.

                        T url = getUrls().get(dialogView.getCurrentItem());
                        String key = OkHttpImageLoad.generate(url.toString());
                        String destUrl = OkHttpImageLoad.getImageCachePath() + "/" + key;
                        File newFile = new File(destUrl);
                        if (build.imageLoad.isCached(url)) {
                            Utility.saveBmpToAlbum(getContext(), newFile);
                        } else {
                            new ObservableTask<File>(
                                    emitter -> {
                                        File file = Glide.with(ContextUtils.getApplicationContext())
                                                .downloadOnly()
                                                .load(url)
                                                .submit()
                                                .get();

                                        FileUtils.copyFileFast(file, newFile);
                                        emitter.onNext(newFile);
                                        emitter.onComplete();
                                    })
                                    .onSuccess(data -> {
                                        Utility.saveBmpToAlbum(getContext(), data);
                                    })
                                    .subscribe();
                        }
                    }

                    @Override
                    public void onDenied() {
                        Toast.makeText(getContext(), "没有保存权限，保存功能无法使用！", Toast.LENGTH_SHORT).show();
                    }
                }).request();
    }

}
