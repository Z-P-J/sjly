package com.zpj.shouji.market.ui.widget.popup;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.zpj.popup.ZPopup;
import com.zpj.popup.core.ImageViewerPopup;
import com.zpj.popup.impl.AttachListPopup;
import com.zpj.popup.interfaces.IImageLoader;
import com.zpj.popup.photoview.PhotoView;
import com.zpj.popup.widget.LoadingView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.widget.tinted.TintedImageButton;
import com.zpj.widget.toolbar.ZToolBar;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommonImageViewerPopup extends ImageViewerPopup<String>
        implements IImageLoader<String> {

    private List<String> originalImageList;
    private List<String> imageSizeList;

    private ZToolBar titleBar;
    protected TextView tvInfo;
    protected TextView tvIndicator;
    private TintedImageButton btnMore;
    private LoadingView loadingView;

    public static CommonImageViewerPopup with(@NonNull Context context) {
        return new CommonImageViewerPopup(context);
    }

    private CommonImageViewerPopup(@NonNull Context context) {
        super(context);
        isShowIndicator(false);
        isShowPlaceholder(false);
        isShowSaveButton(false);
        setImageLoader(this);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_theme_image_viewer;
    }

    @Override
    protected void initPopupContent() {
        titleBar = findViewById(R.id.tool_bar);
        tvIndicator = findViewById(R.id.tv_indicator);
        tvInfo = findViewById(R.id.tv_info);
        btnMore = findViewById(R.id.btn_more);
        loadingView = findViewById(R.id.lv_loading);
        super.initPopupContent();
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTitle();
                tvIndicator.setText(urls.size() + "/" + (position + 1));
                if (imageSizeList != null) {
                    tvInfo.setText(imageSizeList.get(position));
                }
                loadingView.setVisibility(GONE);
            }
        });
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        btnMore.setOnClickListener(v -> {
            AttachListPopup<String> popup = ZPopup.attachList(context);
            popup.addItems("分享图片", "保存图片", "设为壁纸");
            if (originalImageList != null && !TextUtils.equals(urls.get(position), originalImageList.get(position))) {
                popup.addItem("查看原图");
            }
            popup.setOnDismissListener(this::focusAndProcessBackPress)
                    .setOnSelectListener((pos, title) -> {
                        switch (pos) {
                            case 0:
                                AToast.normal("TODO share");
                                break;
                            case 1:
                                save();
                                break;
                            case 2:
                                ShowLoadingEvent.post("图片准备中...");
                                String url;
                                if (originalImageList != null) {
                                    url = originalImageList.get(position);
                                } else {
                                    url = urls.get(position);
                                }
                                Glide.with(context)
                                        .asBitmap()
                                        .load(url)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                HideLoadingEvent.postEvent();
                                                postDelayed(() -> {
                                                    try {
                                                        WallpaperManager wpm = (WallpaperManager) context.getSystemService(
                                                                Context.WALLPAPER_SERVICE);
                                                        wpm.setBitmap(resource);
                                                        AToast.success("设置壁纸成功！");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        AToast.error("设置壁纸失败！" + e.getMessage());
                                                    }
                                                }, 250);
                                            }
                                        });

                                break;
                            case 3:
                                loadingView.setVisibility(VISIBLE);
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
                                                loadingView.setVisibility(GONE);
                                            }
                                        });
                                break;
                        }
                    })
                    .show(btnMore);
        });


        updateTitle();
        titleBar.getLeftImageButton().setOnClickListener(v -> dismiss());


        tvIndicator.setText(urls.size() + "/" + (position + 1));

        if (imageSizeList != null) {
            tvInfo.setText(imageSizeList.get(position));
        } else {
            tvInfo.setVisibility(GONE);
        }

    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    @Override
    public void loadImage(int position, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(imageView).load(url)
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL))
                .into(imageView);
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

    private void updateTitle() {
        String url = urls.get(position);
        titleBar.setCenterText(url.substring(url.lastIndexOf("/") + 1));
    }

    public CommonImageViewerPopup setOriginalImageList(List<String> originalImageList) {
        this.originalImageList = originalImageList;
        return this;
    }

    public CommonImageViewerPopup setImageSizeList(List<String> imageSizeList) {
        this.imageSizeList = imageSizeList;
        return this;
    }
}
