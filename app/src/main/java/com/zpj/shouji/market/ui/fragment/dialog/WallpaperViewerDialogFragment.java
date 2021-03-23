package com.zpj.shouji.market.ui.fragment.dialog;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.fragmentation.dialog.base.ArrowDialogFragment;
import com.zpj.fragmentation.dialog.impl.ArrowMenuDialogFragment;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.fragmentation.dialog.model.OptionMenu;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.glide.transformations.CircleWithBorderTransformation;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.model.WallpaperInfo;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeDetailFragment;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;
import com.zpj.widget.toolbar.ZToolBar;

import java.util.List;

public class WallpaperViewerDialogFragment extends ImageViewerDialogFragment<String>
        implements View.OnClickListener {

    private List<String> originalImageList;

    private ZToolBar titleBar;
    private View bottomBar;
    private ImageView ivIcon;
    private ImageButton btnUp;
    private DrawableTintTextView tvSupport;
    private ImageButton btnFavorite;
    private TextView tvOrigin;

    private WallpaperInfo wallpaperInfo;

    @Override
    protected int getCustomLayoutId() {
        return R.layout.dialog_fragment_wallpaper_viewer;
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

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                titleBar.setCenterText(urls.size() + "/" + (position + 1));
            }
        });

        titleBar.getLeftImageButton().setOnClickListener(v -> dismiss());
        titleBar.getRightImageButton().setOnClickListener(v -> {
            new AttachListDialogFragment<String>()
                    .addItems("分享图片", "保存图片", "设为壁纸")
                    .addItemIf(isOriginalImageAvailable(), "查看原图")
                    .setOnSelectListener((fragment, pos, title) -> {
                        switch (pos) {
                            case 0:
                                shareWallpaper();
                                break;
                            case 1:
                                PictureUtil.saveImage(context, urls.get(pager.getCurrentItem()));
                                break;
                            case 2:
                                setWallpaper();
                                break;
                            case 3:
                                showOriginalImage();
                                break;
                        }
                        fragment.dismiss();
                    })
                    .setAttachView(titleBar.getRightImageButton())
                    .show(context);
        });
        titleBar.setCenterText(urls.size() + "/" + (pager.getCurrentItem() + 1));
        titleBar.getCenterTextView().setShadowLayer(8, 4, 4, Color.BLACK);

        Glide.with(context).load(wallpaperInfo.getMemberIcon())
                .apply(RequestOptions.bitmapTransform(new CircleWithBorderTransformation(0.5f, Color.WHITE)))
                .into(ivIcon);
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
            tvSupport.setDrawableTop(R.drawable.ic_good_checked, Color.RED);
            tvSupport.setTag(true);
        } else {
            tvSupport.setDrawableTop(R.drawable.ic_good, Color.WHITE);
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
                ProfileFragment.start(wallpaperInfo.getNickName());
                break;
            case R.id.btn_favorite:
                boolean tag = (boolean) btnFavorite.getTag();
                if (tag) {
                    HttpApi.deleteCollectionApi(wallpaperInfo.getId(), "discuss")
                            .onSuccess(doc -> {
                                String info = doc.selectFirst("info").text();
                                if ("success".equals(doc.selectFirst("result").text())) {
                                    ZToast.success("取消收藏成功！");
                                    btnFavorite.setColorFilter(Color.WHITE);
                                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                                    btnFavorite.setTag(false);
                                } else {
                                    ZToast.error(info);
                                }
                            })
                            .onError(throwable -> ZToast.error(throwable.getMessage()))
                            .subscribe();
                } else {
                    HttpApi.addCollectionApi(wallpaperInfo.getId(), "discuss")
                            .onSuccess(doc -> {
                                String info = doc.selectFirst("info").text();
                                if ("success".equals(doc.selectFirst("result").text())) {
                                    ZToast.success(info);
                                    btnFavorite.setColorFilter(Color.RED);
                                    btnFavorite.setImageResource(R.drawable.ic_favorite_checked);
                                    btnFavorite.setTag(true);
                                } else {
                                    ZToast.error(info);
                                }
                            })
                            .onError(throwable -> ZToast.error(throwable.getMessage()))
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
                PictureUtil.saveImage(context, urls.get(pager.getCurrentItem()));
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
                ThemeDetailFragment.start(discoverInfo, wallpaperInfo.getSpic());
                break;
            case R.id.tv_support:
                supportWallpaper();
                break;
        }
    }

    @Override
    public void doShowAnimation() {
        super.doShowAnimation();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setDuration(getShowAnimDuration());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                bottomBar.setTranslationY(bottomBar.getHeight() * fraction);
                tvOrigin.setTranslationY(bottomBar.getHeight() * fraction);
                titleBar.setTranslationY(-titleBar.getHeight() * fraction);
                btnUp.setAlpha(fraction);
            }
        });
        animator.start();
    }

    @Override
    public void doDismissAnimation() {
        super.doDismissAnimation();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(getDismissAnimDuration());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                btnUp.setTranslationY(btnUp.getHeight() * fraction);
                if (btnUp.getAlpha() > 0) {
                    btnUp.setAlpha(1 - fraction);
                }
                bottomBar.setTranslationY(bottomBar.getHeight() * fraction);
                tvOrigin.setTranslationY(bottomBar.getHeight() * fraction);
                titleBar.setTranslationY(-titleBar.getHeight() * fraction);
            }
        });
        animator.start();
    }

    //    @Override
//    public void onDragChange(int dy, float scale, float fraction) {
//        super.onDragChange(dy, scale, fraction);
//        bottomBar.setTranslationY(bottomBar.getHeight() * fraction);
//        tvOrigin.setTranslationY(bottomBar.getHeight() * fraction);
//        titleBar.setTranslationY(-titleBar.getHeight() * fraction);
//        btnUp.setAlpha(fraction);
//    }


//    @Override
//    protected void onTransform(float ratio) {
//        super.onTransform(ratio);
//        float fraction = 1 - ratio;
//        Log.d("onTransform", "fraction=" + fraction);
//        bottomBar.setTranslationY(bottomBar.getHeight() * fraction);
//        tvOrigin.setTranslationY(bottomBar.getHeight() * fraction);
//        titleBar.setTranslationY(-titleBar.getHeight() * fraction);
//        btnUp.setAlpha(Math.abs(fraction));
//    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
//        GetMainActivityEvent.post(new Callback<MainActivity>() {
//            @Override
//            public void onCallback(MainActivity activity) {
//                SupportHelper.getTopFragment(activity.getSupportFragmentManager()).onSupportVisible();
//            }
//        });
        SupportHelper.getTopFragment(_mActivity.getSupportFragmentManager()).onSupportVisible();
    }

//    @Override
//    public void loadImage(int position, @NonNull String url, @NonNull ImageView imageView) {
//        Glide.with(imageView).load(url)
////                .apply(new RequestOptions()
////                        .override(Target.SIZE_ORIGINAL))
//                .transition(GlideUtils.DRAWABLE_TRANSITION_NONE)
//                .into(imageView);
//    }

    public WallpaperViewerDialogFragment setWallpaperInfo(WallpaperInfo wallpaperInfo) {
        this.wallpaperInfo = wallpaperInfo;
        return this;
    }

    public WallpaperViewerDialogFragment setOriginalImageList(List<String> originalImageList) {
        this.originalImageList = originalImageList;
        return this;
    }

    private void getWallpaperInfo() {
//        http://tt.tljpxm.com
        HttpApi.getXml("/app/comment_topic.jsp?t=discuss&parent=" + wallpaperInfo.getId())
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
            url = originalImageList.get(position);
        } else {
            url = urls.get(position);
        }
        return url;
    }

    private void showOriginalImage() {
        urls.set(position, originalImageList.get(position));
        loadNewUrl(position, originalImageList.get(position));
        tvOrigin.setVisibility(View.GONE);
    }

    private void supportWallpaper() {
        HttpApi.likeApi("wallpaper", wallpaperInfo.getId())
                .onSuccess(data -> {
                    String result = data.selectFirst("info").text();
                    if ("success".equals(data.selectFirst("result").text())) {
                        if ((boolean) tvSupport.getTag()) {
                            tvSupport.setTag(false);
                            tvSupport.setDrawableTop(R.drawable.ic_good, Color.WHITE);
                            wallpaperInfo.setSupportCount(wallpaperInfo.getSupportCount() - 1);
                        } else {
                            tvSupport.setTag(true);
                            tvSupport.setDrawableTop(R.drawable.ic_good_checked, Color.RED);
                            wallpaperInfo.setSupportCount(wallpaperInfo.getSupportCount() + 1);
                        }
                        tvSupport.setText(String.valueOf(wallpaperInfo.getSupportCount()));
                    } else {
                        ZToast.error(result);
                    }
                })
                .onError(throwable -> {
                    ZToast.error("出错了！" + throwable.getMessage());
                })
                .subscribe();
    }

    private void shareWallpaper() {
        PictureUtil.shareWebImage(context, getOriginalImageUrl());
    }

    private boolean isOriginalImageAvailable() {
        return originalImageList != null && !TextUtils.equals(urls.get(position), originalImageList.get(position));
    }

}
