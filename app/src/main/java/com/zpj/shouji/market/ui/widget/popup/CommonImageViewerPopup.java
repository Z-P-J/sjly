package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.popup.ZPopup;
import com.zpj.popup.core.ImageViewerPopup;
import com.zpj.popup.impl.AttachListPopup;
import com.zpj.popup.interfaces.IImageLoader;
import com.zpj.popup.photoview.PhotoView;
import com.zpj.popup.widget.LoadingView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.GetMainActivityEvent;
import com.zpj.shouji.market.glide.GlideUtils;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.widget.tinted.TintedImageButton;
import com.zpj.widget.toolbar.ZToolBar;

import java.io.File;
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
                setInfoText();
                loadingView.setVisibility(GONE);
            }
        });
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        btnMore.setOnClickListener(v -> {
            AttachListPopup<String> popup = ZPopup.attachList(context);
            popup.addItems("分享图片", "保存图片", "设为壁纸")
                    .addItemIf(isOriginalImageAvailable(), "查看原图")
                    .setOnDismissListener(this::focusAndProcessBackPress)
                    .setOnSelectListener((pos, title) -> {
                        switch (pos) {
                            case 0:
                                AToast.normal("TODO share");
                                break;
                            case 1:
//                                save();
                                PictureUtil.saveImage(context, urls.get(position));
                                break;
                            case 2:
                                String url;
                                if (originalImageList != null) {
                                    url = originalImageList.get(position);
                                } else {
                                    url = urls.get(position);
                                }
                                PictureUtil.setWallpaper(context, url);
                                break;
                            case 3:
                                showOriginalImage();
                                break;
                        }
                    })
                    .show(btnMore);
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
        GetMainActivityEvent.post(activity -> {
            ISupportFragment fragment = SupportHelper.getBackStackTopFragment(activity.getSupportFragmentManager());
            if (fragment == null) {
                fragment = SupportHelper.getTopFragment(activity.getSupportFragmentManager());
            }
            Log.d("CommonImageViewerPopup", "fragment=" + fragment);
            if (fragment != null) {
                fragment.onSupportVisible();
            }
        });
    }

    @Override
    public void loadImage(int position, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(imageView)
                .load(url)
//                .apply(
//                        new RequestOptions()
////                                .placeholder(R.drawable.bga_pp_ic_holder_light)
////                                .error(R.drawable.bga_pp_ic_holder_light)
//                                .override(Target.SIZE_ORIGINAL)
//                )
                .transition(GlideUtils.DRAWABLE_TRANSITION_NONE)
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
            tvInfo.setVisibility(GONE);
        }
    }


    private boolean isOriginalImageAvailable() {
        return originalImageList != null && !TextUtils.equals(urls.get(position), originalImageList.get(position));
    }

    private void showOriginalImage() {
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
                        setInfoText();
                    }
                });
    }

}
