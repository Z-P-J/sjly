package com.zpj.shouji.market.ui.widget.popup;

import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.popup.ZPopup;
import com.zpj.popup.core.ImageViewerPopup;
import com.zpj.popup.impl.AttachListPopup;
import com.zpj.popup.interfaces.IImageLoader;
import com.zpj.popup.photoview.PhotoView;
import com.zpj.popup.widget.LoadingView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.GetMainActivityEvent;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.model.WallpaperInfo;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.shouji.market.ui.fragment.base.ListenerFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeDetailFragment;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.widget.tinted.TintedImageButton;
import com.zpj.widget.toolbar.ZToolBar;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WallpaperViewerPopup extends ImageViewerPopup<String>
        implements IImageLoader<String>, ListenerFragment.FragmentLifeCycler {

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

    public static WallpaperViewerPopup with(@NonNull Context context) {
        return new WallpaperViewerPopup(context);
    }

    private WallpaperViewerPopup(@NonNull Context context) {
        super(context);
        isShowIndicator(false);
        isShowPlaceholder(false);
        isShowSaveButton(false);
        setImageLoader(this);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_wallpaper_viewer;
    }

    @Override
    protected void initPopupContent() {
        bottomBar = findViewById(R.id.bottom_bar);
        ivIcon = findViewById(R.id.iv_icon);
        btnUp = findViewById(R.id.btn_up);
        titleBar = findViewById(R.id.tool_bar);
        loadingView = findViewById(R.id.lv_loading);
        super.initPopupContent();
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                titleBar.setCenterText(urls.size() + "/" + (position + 1));
                loadingView.setVisibility(GONE);
            }
        });
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        titleBar.getLeftImageButton().setOnClickListener(v -> dismiss());
        titleBar.getRightImageButton().setOnClickListener(v -> {
            AttachListPopup<String> popup = ZPopup.attachList(context);
            popup.addItems("分享图片", "保存图片", "设为壁纸");
            if (isOriginalImageAvailable()) {
                popup.addItem("查看原图");
            }
            popup.setOnDismissListener(this::focusAndProcessBackPress)
                    .setOnSelectListener((pos, title) -> {
                        switch (pos) {
                            case 0:
                                shareWallpaper();
                                break;
                            case 1:
//                                save();
                                PictureUtil.saveImage(context, urls.get(position));
                                break;
                            case 2:
                                setWallpaper();
                                break;
                            case 3:
                                showOriginalImage();
                                break;
                        }
                    })
                    .show(titleBar.getRightImageButton());
        });
        titleBar.setCenterText(urls.size() + "/" + (position + 1));
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
        btnUp.setVisibility(GONE);
        ivIcon.setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
        findViewById(R.id.tv_download).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        tvOrigin = findViewById(R.id.tv_origin);
        tvOrigin.setOnClickListener(this);
        if (!isOriginalImageAvailable()) {
            tvOrigin.setVisibility(GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_origin:
                showOriginalImage();
                break;
            case R.id.iv_icon:
                ProfileFragment.start(wallpaperInfo.getMemberId(), this);
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
                btnUp.setVisibility(VISIBLE);
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
                        btnUp.setVisibility(GONE);
                    }
                });
                animation.start();
                break;
            case R.id.tv_download:
                save();
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
                ThemeDetailFragment.start(discoverInfo, wallpaperInfo.getSpic(), this);
                break;
            case R.id.tv_support:
                supportWallpaper();
                break;
        }
    }

    @Override
    public ImageViewerPopup<String> show() {
        if (wallpaperInfo == null) {
            return null;
        }
        return super.show();
    }

    @Override
    public void onDragChange(int dy, float scale, float fraction) {
        super.onDragChange(dy, scale, fraction);
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

    @Override
    public void loadImage(int position, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(imageView).load(url)
//                .apply(new RequestOptions()
//                        .override(Target.SIZE_ORIGINAL))
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

    public WallpaperViewerPopup setWallpaperInfo(WallpaperInfo wallpaperInfo) {
        this.wallpaperInfo = wallpaperInfo;
        return this;
    }

    public WallpaperViewerPopup setOriginalImageList(List<String> originalImageList) {
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
        String url;
        if (originalImageList != null) {
            url = originalImageList.get(position);
        } else {
            url = urls.get(position);
        }
        PictureUtil.setWallpaper(context, url);
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
                        loadingView.setVisibility(GONE);
                        tvOrigin.setVisibility(GONE);
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
        AToast.normal("TODO Share");
    }

    private boolean isOriginalImageAvailable() {
        return originalImageList != null && !TextUtils.equals(urls.get(position), originalImageList.get(position));
    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void onFragmentDestroy() {
        focusAndProcessBackPress();
    }
}
