package com.zpj.shouji.market.ui.fragment.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.fragmentation.dialog.imagetrans.ITConfig;
import com.zpj.fragmentation.dialog.imagetrans.MyImageLoad;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment2;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.widget.tinted.TintedImageButton;
import com.zpj.widget.toolbar.ZToolBar;

import java.util.List;

public class CommonImageViewerDialogFragment2_bak extends ImageViewerDialogFragment2<String> {

    private List<String> originalImageList;
    private List<String> imageSizeList;

    private ZToolBar titleBar;
    private RelativeLayout bottomBar;
    protected TextView tvInfo;
    protected TextView tvIndicator;
    private TintedImageButton btnMore;
//    private LoadingView loadingView;

    public CommonImageViewerDialogFragment2_bak() {
        super();
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
    protected void onBeforeShow() {
        build.imageLoad = new MyImageLoad<String>() {
            @Override
            public boolean isCached(String url) {
                return false;
            }
        };
        build.itConfig = new ITConfig().largeThumb();
        super.onBeforeShow();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        titleBar = findViewById(R.id.tool_bar);
        bottomBar = findViewById(R.id.bottom_bar);
        tvIndicator = findViewById(R.id.tv_indicator);
        tvInfo = findViewById(R.id.tv_info);
        btnMore = findViewById(R.id.btn_more);
//        loadingView = findViewById(R.id.lv_loading);

        dialogView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTitle();
                tvIndicator.setText(getUrls().size() + "/" + (position + 1));
                setInfoText();
//                loadingView.setVisibility(View.GONE);
            }
        });

        btnMore.setOnClickListener(v -> {
            new AttachListDialogFragment<String>()
                    .addItems("分享图片", "保存图片", "设为壁纸")
                    .addItemIf(isOriginalImageAvailable(), "查看原图")
//                    .setOnDismissListener(this::focusAndProcessBackPress)
                    .setOnSelectListener((fragment, pos, title) -> {
                        fragment.dismiss();
                        switch (pos) {
                            case 0:
                                PictureUtil.shareWebImage(context, getOriginalImageUrl());
                                break;
                            case 1:
                                PictureUtil.saveImage(context, getUrls().get(dialogView.getCurrentItem()));
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


        tvIndicator.setText(getUrls().size() + "/" + (dialogView.getCurrentItem() + 1));

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
//        GetMainActivityEvent.post(activity -> {
//            ISupportFragment fragment = SupportHelper.getBackStackTopFragment(activity.getSupportFragmentManager());
//            if (fragment == null) {
//                fragment = SupportHelper.getTopFragment(activity.getSupportFragmentManager());
//            }
//            Log.d("CommonImageViewerPopup", "fragment=" + fragment);
//            if (fragment != null) {
//                fragment.onSupportVisible();
//            }
//        });
    }

    @Override
    protected void onTransform(float ratio) {
        super.onTransform(ratio);
        float fraction = 1 - ratio;
        Log.d("onTransform", "fraction=" + fraction);
        bottomBar.setTranslationY(bottomBar.getHeight() * fraction);
        titleBar.setTranslationY(-titleBar.getHeight() * fraction);
    }

    private String getOriginalImageUrl() {
        String url;
        if (originalImageList != null) {
            url = originalImageList.get(dialogView.getCurrentItem());
        } else {
            url = getUrls().get(dialogView.getCurrentItem());
        }
        return url;
    }

    private void updateTitle() {
        String url = getUrls().get(dialogView.getCurrentItem());
        titleBar.setCenterText(url.substring(url.lastIndexOf("/") + 1));
    }

    public CommonImageViewerDialogFragment2_bak setOriginalImageList(List<String> originalImageList) {
        this.originalImageList = originalImageList;
        return this;
    }

    public CommonImageViewerDialogFragment2_bak setImageSizeList(List<String> imageSizeList) {
        this.imageSizeList = imageSizeList;
        return this;
    }

    private void setInfoText() {
        if (imageSizeList != null) {
            if (isOriginalImageAvailable()) {
                tvInfo.setText(String.format("查看原图(%s)", imageSizeList.get(dialogView.getCurrentItem())));
                tvInfo.setOnClickListener(v -> showOriginalImage());
            } else {
                tvInfo.setText(imageSizeList.get(dialogView.getCurrentItem()));
                tvInfo.setOnClickListener(null);
            }
        } else {
            tvInfo.setVisibility(View.GONE);
        }
    }


    private boolean isOriginalImageAvailable() {
        return originalImageList != null && !TextUtils.equals(getUrls().get(dialogView.getCurrentItem()), originalImageList.get(dialogView.getCurrentItem()));
    }

    private void showOriginalImage() {
//        loadingView.setVisibility(View.VISIBLE);
        getUrls().set(dialogView.getCurrentItem(), originalImageList.get(dialogView.getCurrentItem()));
        dialogView.loadNewUrl(originalImageList.get(dialogView.getCurrentItem()));
        setInfoText();
//        PhotoView current = dialogView.getViewPager().findViewWithTag(dialogView.getCurrentItem());
//        Glide.with(context)
//                .asDrawable()
//                .load(originalImageList.get(dialogView.getCurrentItem()))
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        current.setImageDrawable(resource);
//                        updateTitle();
//                        loadingView.setVisibility(View.GONE);
//                        setInfoText();
//                    }
//                });
    }

}
