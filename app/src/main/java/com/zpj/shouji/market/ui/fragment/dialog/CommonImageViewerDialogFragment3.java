package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment3;
import com.zpj.fragmentation.dialog.interfaces.IImageLoader;
import com.zpj.fragmentation.dialog.photoview.PhotoView;
import com.zpj.fragmentation.dialog.widget.LoadingView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.widget.toolbar.ZToolBar;

import java.io.File;
import java.util.List;

public class CommonImageViewerDialogFragment3 extends ImageViewerDialogFragment3<String>
        implements IImageLoader<String> {

    private List<String> originalImageList;
    private List<String> imageSizeList;

    private ZToolBar titleBar;
    protected TextView tvInfo;
    protected TextView tvIndicator;
    private ImageButton btnMore;
    private LoadingView loadingView;

    public CommonImageViewerDialogFragment3() {
        super();
        isShowIndicator(false);
        isShowPlaceholder(false);
        isShowSaveButton(false);
    }

    @Override
    protected int getCustomLayoutId() {
        return R.layout.dialog_fragment_theme_image_viewer;
    }

    @Override
    public void onSupportVisible() {
        mSupportVisibleActionQueue.start();
        mDelegate.onSupportVisible();
        lightStatusBar();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        titleBar = findViewById(R.id.tool_bar);
        tvIndicator = findViewById(R.id.tv_indicator);
        tvInfo = findViewById(R.id.tv_info);
        btnMore = findViewById(R.id.btn_more);
        loadingView = findViewById(R.id.lv_loading);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTitle();
                tvIndicator.setText(urls.size() + "/" + (position + 1));
                setInfoText();
                loadingView.setVisibility(View.GONE);
            }
        });

        btnMore.setOnClickListener(v -> {
            new AttachListDialogFragment<String>()
                    .addItems("分享图片", "保存图片", "设为壁纸")
                    .addItemIf(isOriginalImageAvailable(), "查看原图")
//                    .setOnDismissListener(this::focusAndProcessBackPress)
                    .setOnSelectListener((fragment, pos, text) -> {
                        switch (pos) {
                            case 0:
                                PictureUtil.shareWebImage(context, getOriginalImageUrl());
                                break;
                            case 1:
                                PictureUtil.saveImage(context, urls.get(position));
                                break;
                            case 2:
                                PictureUtil.setWallpaper(context, getOriginalImageUrl());
                                break;
                            case 3:
                                showOriginalImage();
                                break;
                        }
                    })
                    .setAttachView(btnMore)
                    .show(context);
        });

        updateTitle();
        titleBar.getLeftImageButton().setOnClickListener(v -> dismiss());
        titleBar.getCenterTextView().setShadowLayer(8, 4, 4, Color.BLACK);


        tvIndicator.setText(urls.size() + "/" + (position + 1));

        setInfoText();

    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        ISupportFragment fragment = SupportHelper.getBackStackTopFragment(_mActivity.getSupportFragmentManager());
        if (fragment == null) {
            fragment = SupportHelper.getTopFragment(_mActivity.getSupportFragmentManager());
        }
        Log.d("CommonImageViewerPopup", "fragment=" + fragment);
        if (fragment != null) {
            fragment.onSupportVisible();
        }
    }

//    @Override
//    public void loadImage(int position, @NonNull String url, @NonNull ImageView imageView) {
//        Glide.with(imageView)
//                .load(url)
////                .apply(
////                        new RequestOptions()
//////                                .placeholder(R.drawable.bga_pp_ic_holder_light)
//////                                .error(R.drawable.bga_pp_ic_holder_light)
////                                .override(Target.SIZE_ORIGINAL)
////                )
//                .transition(GlideUtils.DRAWABLE_TRANSITION_NONE)
//                .into(imageView);
//    }

    @Override
    public void loadImage(int position, @NonNull String url, @NonNull ImageView imageView, Runnable runnable) {
        Glide.with(imageView)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
    }

    @Override
    public File getImageFile(@NonNull Context context, @NonNull String url) {
        try {
            return Glide.with(context).downloadOnly().load(url).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getOriginalImageUrl() {
        String url;
        if (originalImageList != null) {
            url = originalImageList.get(position);
        } else {
            url = urls.get(position);
        }
        return url;
    }

    private void updateTitle() {
        String url = urls.get(position);
        titleBar.setCenterText(url.substring(url.lastIndexOf("/") + 1));
    }

    public CommonImageViewerDialogFragment3 setOriginalImageList(List<String> originalImageList) {
        this.originalImageList = originalImageList;
        return this;
    }

    public CommonImageViewerDialogFragment3 setImageSizeList(List<String> imageSizeList) {
        this.imageSizeList = imageSizeList;
        return this;
    }

    private void setInfoText() {
        if (imageSizeList != null) {
            if (isOriginalImageAvailable()) {
                tvInfo.setText(String.format("查看原图(%s)", imageSizeList.get(position)));
                tvInfo.setOnClickListener(v -> showOriginalImage());
            } else {
                tvInfo.setText(imageSizeList.get(position));
                tvInfo.setOnClickListener(null);
            }
        } else {
            tvInfo.setVisibility(View.GONE);
        }
    }


    private boolean isOriginalImageAvailable() {
        return originalImageList != null && !TextUtils.equals(urls.get(position), originalImageList.get(position));
    }

    private void showOriginalImage() {
        loadingView.setVisibility(View.VISIBLE);
        urls.set(position, originalImageList.get(position));
        PhotoView current = pager.findViewWithTag(pager.getCurrentItem());
        Glide.with(context)
                .asDrawable()
                .load(originalImageList.get(position))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        current.setImageDrawable(resource);
                        updateTitle();
                        loadingView.setVisibility(View.GONE);
                        setInfoText();
                    }
                });
    }

}
