package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.geek.banner.Banner;
import com.geek.banner.loader.BannerEntry;
import com.geek.banner.loader.BannerLoader;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.transformations.blur.BlurTransformation;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.subject.SubjectRecommendListFragment;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class RecommendBanner extends LinearLayout implements View.OnClickListener {

    private final List<AppInfo> bannerItemList = new ArrayList<>();
//    private final BannerViewHolder bannerViewHolder = new BannerViewHolder();

    private final ImageView ivBg;
    private final Banner banner;

    private int currentPosition = 0;

    public RecommendBanner(Context context) {
        this(context, null);
    }

    public RecommendBanner(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecommendBanner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_recommend_header, this, true);
        banner = findViewById(R.id.banner2);
        banner.setBannerLoader(new AppBannerLoader());

        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                AppDetailFragment.start(bannerItemList.get(position));
            }
        });

        banner.setBannerPagerChangedListener(new Banner.OnBannerPagerChangedListener() {
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setBg(position);
            }
        });

        ViewGroup.LayoutParams params = banner.getLayoutParams();
        int screenWidth = ScreenUtils.getScreenWidth(context);

//        params.height = (int) ((float) screenWidth * screenWidth / ScreenUtils.getScreenHeight(context));
        params.height = (int) ((float) screenWidth / 2f);


        ivBg = findViewById(R.id.iv_bg);

        findViewById(R.id.tv_common_app).setOnClickListener(this);
        findViewById(R.id.tv_recent_download).setOnClickListener(this);
        findViewById(R.id.tv_subjects).setOnClickListener(this);
        findViewById(R.id.tv_collections).setOnClickListener(this);
    }

    public void loadData(Runnable runnable, IHttp.OnErrorListener onErrorListener) {
//        if (HttpPreLoader.getInstance().hasKey(PreloadApi.HOME_BANNER)) {
//            HttpPreLoader.getInstance().setLoadListener(PreloadApi.HOME_BANNER, document -> {
//                onGetDoc(document, runnable);
//            });
//        } else {
//            HttpApi.getXml(PreloadApi.HOME_BANNER.getUrl())
//                    .onSuccess(document -> {
//                        onGetDoc(document, runnable);
//                    })
//                    .subscribe();
//        }

        HttpApi.getXml(PreloadApi.HOME_BANNER.getUrl())
                .onSuccess(document -> {
                    onGetDoc(document, runnable);
                })
                .onError(onErrorListener)
                .subscribe();
    }

    private void onGetDoc(Document document, Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        Elements elements = document.select("item");
        bannerItemList.clear();
        for (Element element : elements) {
            AppInfo info = AppInfo.parse(element);
            if (info == null) {
                continue;
            }
            bannerItemList.add(info);
            if (bannerItemList.size() >= 8) {
                break;
            }
        }
        if (!bannerItemList.isEmpty()) {
            setBg(0);
        }
        banner.loadImagePaths(bannerItemList);
        banner.startAutoPlay();

    }

    public void onResume() {
        if (banner != null) {
            banner.startAutoPlay();
        }
//        setBg(currentPosition);
    }

    public void onPause() {
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }

    public void onStop() {
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }

    private void setBg(int position) {
        currentPosition = position;
        Glide.with(ivBg.getContext())
                .asBitmap()
                .load(bannerItemList.get(position).getAppIcon())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        getColor(resource);
                        ivBg.setImageBitmap(resource);
//                        llContainer.setBackground(new BitmapDrawable(getResources(), resource));
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_app:
                ToolBarAppListFragment.startRecommendSoftList();
                break;
            case R.id.tv_recent_download:
                ToolBarAppListFragment.startRecentDownload();
                break;
            case R.id.tv_subjects:
                SubjectRecommendListFragment.start("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes");
                break;
            case R.id.tv_collections:
                CollectionRecommendListFragment.start();
                break;
        }
    }

//    public void getColor(Bitmap bitmap) {
//        // Palette的部分
//        Palette.Builder builder = Palette.from(bitmap);
//        builder.generate(palette -> {
//            //获取到充满活力的这种色调
////                Palette.Swatch s = palette.getMutedSwatch();
//            //获取图片中充满活力的色调
////                Palette.Swatch s = palette.getVibrantSwatch();
//            Palette.Swatch s = palette.getDominantSwatch();//独特的一种
//            Palette.Swatch s1 = palette.getVibrantSwatch();       //获取到充满活力的这种色调
//            Palette.Swatch s2 = palette.getDarkVibrantSwatch();    //获取充满活力的黑
//            Palette.Swatch s3 = palette.getLightVibrantSwatch();   //获取充满活力的亮
//            Palette.Swatch s4 = palette.getMutedSwatch();           //获取柔和的色调
//            Palette.Swatch s5 = palette.getDarkMutedSwatch();      //获取柔和的黑
//            Palette.Swatch s6 = palette.getLightMutedSwatch();    //获取柔和的亮
//            if (s != null) {
//                boolean isDark = ColorUtils.calculateLuminance(s.getRgb()) <= 0.5;
//                ColorChangeEvent.post(isDark);
//            }
//        });
//
//    }

//    public static class BannerViewHolder implements MZViewHolder<AppInfo> {
//        private ImageView mImageView;
//
//        @Override
//        public View createView(Context context) {
//            // 返回页面布局
//            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false);
//            mImageView = view.findViewById(R.id.img_view);
//            return view;
//        }
//
//        @Override
//        public void onBind(Context context, int position, AppInfo item) {
//            Glide.with(context).load(item.getAppIcon()).into(mImageView);
//        }
//    }

    public static class AppBannerLoader implements BannerLoader<AppInfo, View> {

        @Override
        public void loadView(Context context, BannerEntry entry, int position, View itemView) {
            ImageView mImageView = itemView.findViewById(R.id.img_view);
            ImageView ivIcon = itemView.findViewById(R.id.iv_icon);
            TextView tvTitle = itemView.findViewById(R.id.tv_title);
            TextView tvInfo = itemView.findViewById(R.id.tv_info);
            DownloadButton downloadButton = itemView.findViewById(R.id.tv_download);


            AppInfo appInfo = (AppInfo) entry.getBannerPath();
            int placeholderId = GlideRequestOptions.getPlaceholderId();
            Glide.with(context).load(appInfo.getAppIcon())
                    .apply(new RequestOptions()
                            .error(placeholderId)
                            .placeholder(placeholderId))
                    .into(mImageView);
            Glide.with(context).load(appInfo.getAppIcon())
                    .apply(
                            GlideRequestOptions.with()
                                    .centerCrop()
                                    .roundedCorners(4)
                                    .get()
                    )
                    .into(ivIcon);
            tvTitle.setText(appInfo.getAppTitle());
            tvInfo.setText(appInfo.getAppSize());
            downloadButton.bindApp(appInfo);
        }

        @Override
        public View createView(Context context, int position) {
            return LayoutInflater.from(context).inflate(R.layout.item_banner_recommend, null, false);
        }

    }

}
