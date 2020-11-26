//package com.zpj.shouji.market.ui.fragment;
//
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.View;
//
//import com.zpj.blur.ZBlurry;
//import com.zpj.fragmentation.BaseFragment;
//import com.zpj.fragmentation.SupportFragment;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.constant.AppConfig;
//import com.zpj.shouji.market.event.GetMainFragmentEvent;
//import com.zpj.shouji.market.event.MainActionPopupEvent;
//import com.zpj.shouji.market.event.SkinChangeEvent;
//import com.zpj.shouji.market.manager.UserManager;
//import com.zpj.shouji.market.model.MessageInfo;
//import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
//import com.zpj.shouji.market.ui.fragment.base.BaseContainerFragment;
//import com.zpj.shouji.market.ui.fragment.dialog.MainActionDialogFragment;
//import com.zpj.shouji.market.ui.fragment.homepage.HomeFragment;
//import com.zpj.shouji.market.ui.fragment.profile.MyFragment;
//import com.zpj.shouji.market.ui.fragment.recommond.GameRecommendFragment2;
//import com.zpj.shouji.market.ui.fragment.recommond.SoftRecommendFragment2;
//import com.zpj.shouji.market.ui.widget.ZViewPager;
//import com.zpj.shouji.market.ui.widget.navigation.BottomBar;
//import com.zpj.shouji.market.ui.widget.navigation.BottomBarTab;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//
//public class MainFragment_bak extends BaseFragment {
//
//    private final List<BaseFragment> fragments = new ArrayList<>();
//    private ZViewPager viewPager;
//    private BottomBar mBottomBar;
//
//    private ZBlurry blurred;
//
//    public static class FirstFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            HomeFragment fragment = findChildFragment(HomeFragment.class);
//            if (fragment == null) {
//                fragment = new HomeFragment();
//            }
//            return fragment;
//        }
//
//    }
//
//    public static class SecondFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            SoftRecommendFragment2 fragment = findChildFragment(SoftRecommendFragment2.class);
//            if (fragment == null) {
//                fragment = new SoftRecommendFragment2();
//            }
//            return fragment;
//        }
//
//    }
//
//    public static class ThirdFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            GameRecommendFragment2 fragment = findChildFragment(GameRecommendFragment2.class);
//            if (fragment == null) {
//                fragment = new GameRecommendFragment2();
//            }
//            return fragment;
//        }
//
//    }
//
//    public static class FourthFragment extends BaseContainerFragment {
//
//        @Override
//        protected SupportFragment getRootFragment() {
//            MyFragment fragment = findChildFragment(MyFragment.class);
//            if (fragment == null) {
//                fragment = new MyFragment();
//            }
//            return fragment;
//        }
//
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_main;
//    }
//
//    @Override
//    protected boolean supportSwipeBack() {
//        return false;
//    }
//
////    @Override
////    public FragmentAnimator onCreateFragmentAnimator() {
////        return new DefaultHorizontalAnimator();
////    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//
//        view.setAlpha(0);
//
////        HomeFragment homeFragment = findChildFragment(HomeFragment.class);
////        if (homeFragment == null) {
////            homeFragment = new HomeFragment();
////        }
////
////        SoftRecommendFragment2 softFragment = findChildFragment(SoftRecommendFragment2.class);
////        if (softFragment == null) {
////            softFragment = new SoftRecommendFragment2();
////        }
////
////        GameRecommendFragment2 game = findChildFragment(GameRecommendFragment2.class);
////        if (game == null) {
////            game = new GameRecommendFragment2();
////        }
////
//
//        MyFragment profileFragment = findChildFragment(MyFragment.class);
//        if (profileFragment == null) {
//            profileFragment = new MyFragment();
//        }
//
//        FirstFragment homeFragment = findChildFragment(FirstFragment.class);
//        if (homeFragment == null) {
//            homeFragment = new FirstFragment();
//        }
//
//        SecondFragment softFragment = findChildFragment(SecondFragment.class);
//        if (softFragment == null) {
//            softFragment = new SecondFragment();
//        }
//
//        ThirdFragment game = findChildFragment(ThirdFragment.class);
//        if (game == null) {
//            game = new ThirdFragment();
//        }
//
////        FourthFragment profileFragment = findChildFragment(FourthFragment.class);
////        if (profileFragment == null) {
////            profileFragment = new FourthFragment();
////        }
//
//        fragments.clear();
//        fragments.add(homeFragment);
//        fragments.add(softFragment);
//        fragments.add(game);
//        fragments.add(profileFragment);
//
////        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
//
//        mBottomBar = view.findViewById(R.id.bottom_bar);
//
//        blurred = ZBlurry.with(findViewById(R.id.fl_blur))
////                .fitIntoViewXY(false)
////                .antiAlias(true)
//                .foregroundColor(Color.parseColor(AppConfig.isNightMode() ? "#a0000000" : "#a0ffffff"))
//                .scale(0.1f)
//                .radius(20)
////                .maxFps(40)
//                .blur(mBottomBar, new ZBlurry.Callback() {
//                    @Override
//                    public void down(Bitmap bitmap) {
//                        Log.d("MainFragment", "bitmap=" + bitmap);
//                        Drawable drawable = new BitmapDrawable(bitmap);
//                        mBottomBar.setBackground(drawable);
//                    }
//                });
//        blurred.pauseBlur();
//
//        BottomBarTab emptyTab = new BottomBarTab(context);
//        emptyTab.setOnClickListener(v -> {
//            MainActionPopupEvent.post(true);
//            new MainActionDialogFragment()
//                    .setOnDismissListener(() -> MainActionPopupEvent.post(false))
//                    .show(context);
//        });
//
//        mBottomBar.addItem(BottomBarTab.build(context, "主页", R.drawable.ic_home_normal, R.drawable.ic_home_checked))
//                .addItem(BottomBarTab.build(context, "应用", R.drawable.ic_software_normal, R.drawable.ic_software_checked))
//                .addItem(emptyTab)
//                .addItem(BottomBarTab.build(context, "游戏", R.drawable.ic_game_normal, R.drawable.ic_game_checked))
//                .addItem(BottomBarTab.build(context, "我的", R.drawable.ic_me_normal, R.drawable.ic_me_checked));
//
//        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(int position, int prePosition) {
//                if (position == 2) {
//                    emptyTab.performClick();
//                    return;
//                }
//                if (position > 2) {
//                    position -= 1;
//                }
//                if(viewPager.getCurrentItem() != position) {
//                    viewPager.setCurrentItem(position, false);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(int position) {
//
//            }
//
//            @Override
//            public void onTabReselected(int position) {
//            }
//        });
//
//        viewPager = view.findViewById(R.id.vp);
////        viewPager.setScrollerSpeed(500);
//        viewPager.setCanScroll(false);
//
//        viewPager.setOffscreenPageLimit(fragments.size());
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        UserManager.getInstance().rsyncMessage(false);
//    }
//
//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        if (blurred != null) {
//            blurred.startBlur();
//        }
////        if (viewPager != null && !fragments.isEmpty()) {
////            fragments.get(viewPager.getCurrentItem()).onSupportVisible();
////        }
//    }
//
//    @Override
//    public void onSupportInvisible() {
//        super.onSupportInvisible();
//        if (blurred != null) {
//            blurred.pauseBlur();
//        }
//    }
//
////    @Override
////    public void onSupportVisible() {
//////        if (viewPager != null && !fragments.isEmpty()) {
//////            fragments.get(viewPager.getCurrentItem()).onSupportVisible();
//////        } else {
//////            darkStatusBar();
//////        }
////
////        if (viewPager != null && !fragments.isEmpty()) {
////            fragments.get(viewPager.getCurrentItem()).onSupportVisible();
////        }
////    }
////
////    @Override
////    public void onSupportInvisible() {
////        if (viewPager != null && !fragments.isEmpty()) {
////            fragments.get(viewPager.getCurrentItem()).onSupportInvisible();
////        } else {
////            darkStatusBar();
////        }
////    }
//
//    public void animatedToShow() {
//        postOnEnterAnimationEnd(new Runnable() {
//            @Override
//            public void run() {
//                FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, null);
//                viewPager.setAdapter(adapter);
//
//                mBottomBar.setCurrentItem(0);
//
////                view.setAlpha(1);
//                if (AppConfig.isShowSplash()) {
//                    Observable.timer(1000, TimeUnit.MILLISECONDS)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .doOnComplete(() -> {
//                                view.setAlpha(1);
////                                view.animate()
////                                        .setDuration(500)
////                                        .alpha(1)
////                                        .start();
//                            })
//                            .subscribe();
//                } else {
//                    view.animate()
//                            .setDuration(500)
//                            .alpha(1)
//                            .start();
//                }
//
//            }
//        });
//    }
//
//    @Subscribe
//    public void onSkinChangeEvent(SkinChangeEvent info) {
//        if (blurred != null) {
//            blurred.foregroundColor(Color.parseColor(AppConfig.isNightMode() ? "#a0000000" : "#a0ffffff"));
//            blurred.startBlur();
//        }
//    }
//
//    @Subscribe
//    public void onUpdateMessageInfoEvent(MessageInfo info) {
//        mBottomBar.getItem(4).setUnreadCount(info.getTotalCount());
//    }
//
//    @Subscribe
//    public void onGetMainFragmentEvent(GetMainFragmentEvent event) {
//        if (event.getCallback() != null) {
//            event.getCallback().onCallback(this);
//        }
//    }
//
//}
