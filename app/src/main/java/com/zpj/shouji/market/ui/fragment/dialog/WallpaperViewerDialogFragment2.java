package com.zpj.shouji.market.ui.fragment.dialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment2;
import com.zpj.fragmentation.dialog.interfaces.IImageLoader;
import com.zpj.fragmentation.dialog.photoview.PhotoView;
import com.zpj.fragmentation.dialog.widget.LoadingView;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.GetMainActivityEvent;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.model.WallpaperInfo;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeDetailFragment;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.widget.tinted.TintedImageButton;
import com.zpj.widget.toolbar.ZToolBar;

import java.io.File;
import java.util.List;

public class WallpaperViewerDialogFragment2 extends ImageViewerDialogFragment2<String>
        implements IImageLoader<String>, View.OnClickListener {

    private List<String> originalImageList;

    private ZToolBar titleBar;
    private View bottomBar;
    private ImageView ivIcon;
    private TintedImageButton btnUp;
    private LoadingView loadingView;
    private DrawableTintTextView tvSupport;
    private TintedImageButton btnFavorite;
    private TextView tvOrigin;

    private WallpaperInfo wallpaperInfo;

    public WallpaperViewerDialogFragment2() {
        super();
    }

    @Override
    protected int getCustomLayoutId() {
        return R.layout.layout_popup_wallpaper_viewer;
    }

    @Override
    public void onSupportVisible() {
        mSupportVisibleActionQueue.start();
        mDelegate.onSupportVisible();
        lightStatusBar();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (wallpaperInfo == null) {
            dismiss();
            return;
        }
        super.initView(view, savedInstanceState);

        bottomBar = findViewById(R.id.bottom_bar);
        ivIcon = findViewById(R.id.iv_icon);
        btnUp = findViewById(R.id.btn_up);
        titleBar = findViewById(R.id.tool_bar);
        loadingView = findViewById(R.id.lv_loading);

        dialogView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                titleBar.setCenterText(getUrls().size() + "/" + (position + 1));
                loadingView.setVisibility(View.GONE);
            }
        });

        titleBar.getLeftImageButton().setOnClickListener(v -> dismiss());
        titleBar.getRightImageButton().setOnClickListener(v -> {
            new AttachListDialogFragment<String>()
                    .addItems("分享图片", "保存图片", "设为壁纸")
                    .addItemIf(isOriginalImageAvailable(), "查看原图")
//                    .setOnDismissListener(this::focusAndProcessBackPress)
                    .setOnSelectListener((pos, title) -> {
                        switch (pos) {
                            case 0:
                                shareWallpaper();
                                break;
                            case 1:
//                                save();
                                PictureUtil.saveImage(context, getUrls().get(dialogView.getCurrentItem()));
                                break;
                            case 2:
                                setWallpaper();
                                break;
                            case 3:
                                showOriginalImage();
                                break;
                        }
                    })
                    .setAttachView(titleBar.getRightImageButton())
                    .show(context);
//            ZPopup.attachList(context)
//                    .addItems("分享图片", "保存图片", "设为壁纸")
//                    .addItemIf(isOriginalImageAvailable(), "查看原图")
//                    .setOnDismissListener(this::focusAndProcessBackPress)
//                    .setOnSelectListener((pos, title) -> {
//                        switch (pos) {
//                            case 0:
//                                shareWallpaper();
//                                break;
//                            case 1:
////                                save();
//                                PictureUtil.saveImage(context, urls.get(position));
//                                break;
//                            case 2:
//                                setWallpaper();
//                                break;
//                            case 3:
//                                showOriginalImage();
//                                break;
//                        }
//                    })
//                    .show(titleBar.getRightImageButton());
        });
        titleBar.setCenterText(getUrls().size() + "/" + (dialogView.getCurrentItem() + 1));
        titleBar.getCenterTextView().setShadowLayer(8, 4, 4, Color.BLACK);

        Glide.with(context).load(wallpaperInfo.getMemberIcon()).into(ivIcon);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvContent = findViewById(R.id.tv_content);
        TextView tvTime = findViewById(R.id.tv_time);
        tvName.setText(wallpaperInfo.getNickName());
        tvContent.setText("#" + wallpaperInfo.getTag() + "#" + wallpaperInfo.getContent());
        tvTime.setText(wallpaperInfo.getTime());

        TextView tvComment = findViewById(R.id.tv_comment);
        tvSupport = findViewById(R.id.tv_support);
        tvComment.setOnClickListener(this);
        tvSupport.setOnClickListener(this);
        tvComment.setText(String.valueOf(wallpaperInfo.getReplyCount()));
        tvSupport.setText(String.valueOf(wallpaperInfo.getSupportCount()));
        if (wallpaperInfo.isLike()) {
            tvSupport.setDrawableTintColor(Color.RED);
            tvSupport.setTag(true);
        } else {
            tvSupport.setTag(false);
        }

        btnFavorite = findViewById(R.id.btn_favorite);
        btnFavorite.setOnClickListener(this);
        btnFavorite.setTag(false);

        btnUp.setOnClickListener(this);
        btnUp.setVisibility(View.GONE);
        ivIcon.setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
        findViewById(R.id.tv_download).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        tvOrigin = findViewById(R.id.tv_origin);
        tvOrigin.setOnClickListener(this);
        if (!isOriginalImageAvailable()) {
            tvOrigin.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_origin:
                showOriginalImage();
                break;
            case R.id.iv_icon:
                ProfileFragment.start(wallpaperInfo.getMemberId());
                break;
            case R.id.btn_favorite:
                boolean tag = (boolean) btnFavorite.getTag();
                if (tag) {
                    HttpApi.deleteCollectionApi(wallpaperInfo.getId(), "discuss")
                            .onSuccess(doc -> {
                                String info = doc.selectFirst("info").text();
                                if ("success".equals(doc.selectFirst("result").text())) {
                                    AToast.success("取消收藏成功！");
                                    btnFavorite.setTint(Color.WHITE);
                                    btnFavorite.setTag(false);
                                } else {
                                    AToast.error(info);
                                }
                            })
                            .onError(throwable -> AToast.error(throwable.getMessage()))
                            .subscribe();
                } else {
                    HttpApi.addCollectionApi(wallpaperInfo.getId(), "discuss")
                            .onSuccess(doc -> {
                                String info = doc.selectFirst("info").text();
                                if ("success".equals(doc.selectFirst("result").text())) {
                                    AToast.success(info);
                                    btnFavorite.setTint(Color.RED);
                                    btnFavorite.setTag(true);
                                } else {
                                    AToast.error(info);
                                }
                            })
                            .onError(throwable -> AToast.error(throwable.getMessage()))
                            .subscribe();
                }
                break;
            case R.id.btn_down:
                btnUp.setVisibility(View.VISIBLE);
                btnUp.setOnClickListener(this);
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
                animator.setDuration(250);
                animator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    bottomBar.setTranslationY(bottomBar.getHeight() * value);
                    tvOrigin.setTranslationY(bottomBar.getHeight() * value);
                    titleBar.setTranslationY(-titleBar.getHeight() * value);
                    btnUp.setAlpha(value);
                });
                animator.start();
                break;
            case R.id.btn_up:
                ValueAnimator animation = ValueAnimator.ofFloat(1f, 0);
                animation.setDuration(250);
                animation.addUpdateListener(animation1 -> {
                    float value = (float) animation1.getAnimatedValue();
                    bottomBar.setTranslationY(bottomBar.getHeight() * value);
                    tvOrigin.setTranslationY(bottomBar.getHeight() * value);
                    titleBar.setTranslationY(-titleBar.getHeight() * value);
                    btnUp.setAlpha(value);
                    if (value == 0) {
                        btnUp.setVisibility(View.GONE);
                    }
                });
                animation.start();
                break;
            case R.id.tv_download:
//                save();
                break;
            case R.id.tv_share:
                shareWallpaper();
                break;
            case R.id.tv_comment:
                DiscoverInfo discoverInfo = new DiscoverInfo();
                discoverInfo.setId(wallpaperInfo.getId());
                discoverInfo.setMemberId(wallpaperInfo.getMemberId());
                discoverInfo.setContent(wallpaperInfo.getContent());
                discoverInfo.setTime(wallpaperInfo.getTime());
                discoverInfo.setIcon(wallpaperInfo.getMemberIcon());
                discoverInfo.setNickName(wallpaperInfo.getNickName());
                discoverInfo.setContentType("discuss");
                discoverInfo.setSupportCount(String.valueOf(wallpaperInfo.getSupportCount()));
                discoverInfo.setSupportUserInfoList(wallpaperInfo.getSupportUserInfoList());
                ThemeDetailFragment.start(discoverInfo, wallpaperInfo.getSpic(), null);
                break;
            case R.id.tv_support:
                supportWallpaper();
                break;
        }
    }

//    @Override
//    public void onDragChange(int dy, float scale, float fraction) {
//        super.onDragChange(dy, scale, fraction);
//        bottomBar.setTranslationY(bottomBar.getHeight() * fraction);
//        tvOrigin.setTranslationY(bottomBar.getHeight() * fraction);
//        titleBar.setTranslationY(-titleBar.getHeight() * fraction);
//        btnUp.setAlpha(fraction);
//    }


    @Override
    protected void onTransform(float ratio) {
        super.onTransform(ratio);
        float fraction = 1 - ratio;
        bottomBar.setTranslationY(bottomBar.getHeight() * fraction);
        tvOrigin.setTranslationY(bottomBar.getHeight() * fraction);
        titleBar.setTranslationY(-titleBar.getHeight() * fraction);
        btnUp.setAlpha(fraction);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        GetMainActivityEvent.post(new Callback<MainActivity>() {
            @Override
            public void onCallback(MainActivity activity) {
                SupportHelper.getTopFragment(activity.getSupportFragmentManager()).onSupportVisible();
            }
        });
    }

//    @Override
//    public void loadImage(int position, @NonNull String url, @NonNull ImageView imageView) {
//        Glide.with(imageView).load(url)
////                .apply(new RequestOptions()
////                        .override(Target.SIZE_ORIGINAL))
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

    public WallpaperViewerDialogFragment2 setWallpaperInfo(WallpaperInfo wallpaperInfo) {
        this.wallpaperInfo = wallpaperInfo;
        return this;
    }

    public WallpaperViewerDialogFragment2 setOriginalImageList(List<String> originalImageList) {
        this.originalImageList = originalImageList;
        return this;
    }

    private void getWallpaperInfo() {
        HttpApi.get("http://tt.tljpxm.com/app/comment_topic.jsp?t=discuss&parent=" + wallpaperInfo.getId())
                .onSuccess(new IHttp.OnSuccessListener<Document>() {
                    @Override
                    public void onSuccess(Document doc) throws Exception {
                        for (Element element : doc.select("item")) {
                            DiscoverInfo item = DiscoverInfo.from(element);
                        }
                    }
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {

                    }
                })
                .subscribe();
    }

    private void setWallpaper() {
        PictureUtil.setWallpaper(context, getOriginalImageUrl());
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

    private void showOriginalImage() {
        loadingView.setVisibility(View.VISIBLE);
        getUrls().set(dialogView.getCurrentItem(), originalImageList.get(dialogView.getCurrentItem()));
        PhotoView current = dialogView.getViewPager().findViewWithTag(dialogView.getCurrentItem());
        Glide.with(context)
                .asDrawable()
                .load(originalImageList.get(dialogView.getCurrentItem()))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        current.setImageDrawable(resource);
                        loadingView.setVisibility(View.GONE);
                        tvOrigin.setVisibility(View.GONE);
                    }
                });
    }

    private void supportWallpaper() {
        HttpApi.likeApi("wallpaper", wallpaperInfo.getId())
                .onSuccess(data -> {
                    String result = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        if ((boolean) tvSupport.getTag()) {
                            tvSupport.setTag(false);
                            tvSupport.setDrawableTintColor(Color.WHITE);
                            wallpaperInfo.setSupportCount(wallpaperInfo.getSupportCount() - 1);
                        } else {
                            tvSupport.setTag(true);
                            tvSupport.setDrawableTintColor(Color.RED);
                            wallpaperInfo.setSupportCount(wallpaperInfo.getSupportCount() + 1);
                        }
                        tvSupport.setText(String.valueOf(wallpaperInfo.getSupportCount()));
                    } else {
                        AToast.error(result);
                    }
                })
                .onError(throwable -> {
                    AToast.error("出错了！" + throwable.getMessage());
                })
                .subscribe();
    }

    private void shareWallpaper() {
        PictureUtil.shareWebImage(context, getOriginalImageUrl());
    }

    private boolean isOriginalImageAvailable() {
        return originalImageList != null && !TextUtils.equals(getUrls().get(dialogView.getCurrentItem()), originalImageList.get(dialogView.getCurrentItem()));
    }

}
