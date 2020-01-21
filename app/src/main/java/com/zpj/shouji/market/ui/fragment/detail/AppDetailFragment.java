package com.zpj.shouji.market.ui.fragment.detail;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.AppItem;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.CollectionAppInfo;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.model.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.adapter.ZFragmentPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.utils.ScreenUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppDetailFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"详情", "评论", "发现", "推荐"};

    private static final String SOFT_URL = "http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=";
    private static final String GAME_URL = "http://tt.shouji.com.cn/androidv3/game_show.jsp?id=";
    private final static String KEY_ID = "app_id";
    private final static String KEY_TYPE = "app_type";

    private String url;
    private String id;
    private String type;

    private AppDetailInfo appDetailInfo;
    private CircleImageView icon;
    private TextView title;
    private TextView shortInfo;
    private TextView shortIntroduce;

    private static AppDetailFragment newInstance(String type, String id) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        args.putString(KEY_TYPE, type);
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AppDetailFragment newInstance(AppItem item) {
        return newInstance(item.getAppType(), item.getAppId());
    }

    public static AppDetailFragment newInstance(AppUpdateInfo item) {
        return newInstance(item.getAppType(), item.getId());
    }

    public static AppDetailFragment newInstance(InstalledAppInfo appInfo) {
        return newInstance(appInfo.getAppType(), appInfo.getId());
    }

    public static AppDetailFragment newInstance(UserDownloadedAppInfo info) {
        return newInstance(info.getAppType(), info.getId());
    }

    public static AppDetailFragment newInstance(CollectionAppInfo info) {
        return newInstance(info.getAppType(), info.getId());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            type = getArguments().getString(KEY_TYPE);
            id = getArguments().getString(KEY_ID);
            if ("game".equals(type)) {
                url = GAME_URL + id;
            } else {
                url = SOFT_URL + id;
            }
        } else {
            pop();
            return;
        }

        icon = view.findViewById(R.id.iv_icon);
        title = view.findViewById(R.id.tv_title);
        shortInfo = view.findViewById(R.id.tv_info);
        shortIntroduce = view.findViewById(R.id.tv_detail);



        ArrayList<Fragment> list = new ArrayList<>();
        AppInfoFragment infoFragment = findChildFragment(AppInfoFragment.class);
        if (infoFragment == null) {
            infoFragment = new AppInfoFragment();
        }

        AppCommentFragment commentFragment = findChildFragment(AppCommentFragment.class);
        if (commentFragment == null) {
                commentFragment = AppCommentFragment.newInstance("http://tt.shouji.com.cn/app/comment_index_xml_v5.jsp?versioncode=198&type=" + type + "&id=" + id);
        }

        AppExploreFragment exploreFragment = findChildFragment(AppExploreFragment.class);
        if (exploreFragment == null) {
            exploreFragment = AppExploreFragment.newInstance("http://tt.shouji.com.cn/app/faxian.jsp?versioncode=198&apptype=" + type + "&appid=" + id);
        }

        AppRecommendFragment recommendFragment = findChildFragment(AppRecommendFragment.class);
        if (recommendFragment == null) {
            recommendFragment = AppRecommendFragment.newInstance(id);
        }
        list.add(infoFragment);
        list.add(commentFragment);
        list.add(exploreFragment);
        list.add(recommendFragment);

        ZFragmentPagerAdapter adapter = new ZFragmentPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.LTGRAY);
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(12);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setLineHeight(ScreenUtil.dp2px(context, 1));
//                indicator.setLineWidth(ScreenUtil.dp2px(context, 1));
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        getAppInfo(url);
    }

    private void getAppInfo(final String url){
        Log.d("apppppp", url);
        ExecutorHelper.submit(() -> {
            try {
                Document doc  = HttpUtil.getDocument(url);
                AppDetailInfo info = AppDetailInfo.create(doc);
                post(() -> {
                    appDetailInfo = info;
                    EventBus.getDefault().post(info);
                    Glide.with(context).load(appDetailInfo.getIconUrl()).into(icon);
                    title.setText(info.getName());
                    shortInfo.setText(info.getBaseInfo());
                    shortIntroduce.setText(info.getLineInfo());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

//    public void getColor(Bitmap bitmap) {
//        // Palette的部分
//        Palette.Builder builder = Palette.from(bitmap);
//        builder.generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(Palette palette) {
//                //获取到充满活力的这种色调
//                Palette.Swatch vibrant = palette.getMutedSwatch();
//                //根据调色板Palette获取到图片中的颜色设置到toolbar和tab中背景，标题等，使整个UI界面颜色统一
//                if (rootView != null) {
//                    if (vibrant != null) {
//                        ValueAnimator colorAnim2 = ValueAnimator.ofArgb(Color.rgb(110, 110, 100), ColorHelper.colorBurn(vibrant.getRgb()));
//                        colorAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator animation) {
//                                rootView.setBackgroundColor((Integer) animation.getAnimatedValue());
//                                // toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
//                            }
//                        });
//                        colorAnim2.setDuration(300);
//                        colorAnim2.setRepeatMode(ValueAnimator.RESTART);
//                        colorAnim2.start();
//
//                        if (Build.VERSION.SDK_INT >= 21) {
//                            Window window = getActivity().getWindow();
//                            window.setStatusBarColor(ColorHelper.colorBurn(vibrant.getRgb()));
//                            int barColor = ColorHelper.colorBurn(vibrant.getRgb());
//                            window.setNavigationBarColor(barColor);
//                        }
//                    }
//                }
//
//            }
//        });
//    }


}
