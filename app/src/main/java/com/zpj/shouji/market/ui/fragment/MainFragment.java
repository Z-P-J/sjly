package com.zpj.shouji.market.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import com.zpj.blur.ZBlurry;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.rxbus.RxObserver;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.event.EventBus;
import com.zpj.shouji.market.event.GetMainFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.MessageInfo;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.dialog.MainActionDialogFragment;
import com.zpj.shouji.market.ui.fragment.homepage.HomeFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyFragment;
import com.zpj.shouji.market.ui.fragment.recommond.GameRecommendFragment;
import com.zpj.shouji.market.ui.fragment.recommond.SoftRecommendFragment;
import com.zpj.shouji.market.ui.widget.navigation.BottomBar;
import com.zpj.shouji.market.ui.widget.navigation.BottomBarTab;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends SkinFragment {


    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

    private final SupportFragment[] mFragments = new SupportFragment[4];

    private BottomBar mBottomBar;

    private ZBlurry blurred;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main3;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
//        return new MyFragmentAnimator();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
        EventBus.onSkinChangeEvent(this, s -> {
            if (blurred != null) {
                blurred.foregroundColor(Color.parseColor(AppConfig.isNightMode() ? "#a0000000" : "#a0ffffff"));
                blurred.startBlur();
            }
        });
//        RxObserver.with(this, SkinChangeEvent.class)
//                .bindToLife(this)
//                .subscribe(skinChangeEvent -> {
//                    if (blurred != null) {
//                        blurred.foregroundColor(Color.parseColor(skinChangeEvent.isNight() ? "#a0000000" : "#a0ffffff"));
//                        blurred.startBlur();
//                    }
////                    AToast.success("SkinChangeEvent");
//                });
        RxObserver.with(this, MessageInfo.class)
                .bindToLife(this)
                .subscribe(info -> mBottomBar.getItem(4).setUnreadCount(info.getTotalCount()));
        RxObserver.with(this, GetMainFragmentEvent.class)
                .bindToLife(this)
                .subscribe(event -> {
                    if (event.getCallback() != null) {
                        event.getCallback().onCallback(MainFragment.this);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        RxBus2.get().unSubscribe(this);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        view.setAlpha(0);

        SupportFragment firstFragment = findChildFragment(HomeFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = new HomeFragment();
            mFragments[SECOND] = new SoftRecommendFragment();
            mFragments[THIRD] = new GameRecommendFragment();
            mFragments[FOURTH] = new MyFragment();

            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(SoftRecommendFragment.class);
            mFragments[THIRD] = findChildFragment(GameRecommendFragment.class);
            mFragments[FOURTH] = findChildFragment(MyFragment.class);
        }



        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);

        mBottomBar = view.findViewById(R.id.bottom_bar);

        blurred = ZBlurry.with(findViewById(R.id.fl_container))
//                .fitIntoViewXY(false)
//                .antiAlias(true)
                .foregroundColor(Color.parseColor(AppConfig.isNightMode() ? "#a0000000" : "#a0ffffff"))
                .scale(0.1f)
                .radius(20)
//                .maxFps(40)
                .blur(mBottomBar, new ZBlurry.Callback() {
                    @Override
                    public void down(Bitmap bitmap) {
                        Log.d("MainFragment", "bitmap=" + bitmap);
                        Drawable drawable = new BitmapDrawable(bitmap);
                        mBottomBar.setBackground(drawable);
                    }
                });
        blurred.pauseBlur();

        BottomBarTab emptyTab = new BottomBarTab(context);
        emptyTab.setOnClickListener(v -> {
//            MainActionPopupEvent.post(true);
            EventBus.sendMainActionEvent(true);
            new MainActionDialogFragment()
//                    .setOnDismissListener(() -> MainActionPopupEvent.post(false))
                    .setOnDismissListener(() -> EventBus.sendMainActionEvent(false))
                    .show(context);
        });

        mBottomBar.addItem(BottomBarTab.build(context, "主页", R.drawable.ic_home_normal, R.drawable.ic_home_checked))
                .addItem(BottomBarTab.build(context, "应用", R.drawable.ic_software_normal, R.drawable.ic_software_checked))
                .addItem(emptyTab)
                .addItem(BottomBarTab.build(context, "游戏", R.drawable.ic_game_normal, R.drawable.ic_game_checked))
                .addItem(BottomBarTab.build(context, "我的", R.drawable.ic_me_normal, R.drawable.ic_me_checked));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                if (position == 2) {
                    floatingActionButton.performClick();
                    return;
                }
                if (position > 2) {
                    position -= 1;
                }
                if (prePosition > 2) {
                    prePosition -= 1;
                }
                showHideFragment(mFragments[position], mFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
            }
        });


        mBottomBar.setCurrentItem(0);


    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance().rsyncMessage(false);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        int pos = mBottomBar.getCurrentItemPosition();
        if (pos > 2) {
            pos-=1;
        }
        mFragments[pos].onSupportVisible();
        if (blurred != null) {
            blurred.startBlur();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (blurred != null) {
            blurred.pauseBlur();
        }
    }

    public void animatedToShow() {
        postOnEnterAnimationEnd(new Runnable() {
            @Override
            public void run() {
                mBottomBar.setCurrentItem(0);
//                if (blurred != null) {
//                    blurred.startBlur();
//                }

//                view.setAlpha(1);
                if (AppConfig.isShowSplash()) {
                    Observable.timer(1000, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete(() -> {
                                view.setAlpha(1);
//                                view.animate()
//                                        .setDuration(500)
//                                        .alpha(1)
//                                        .start();
                            })
                            .subscribe();
                } else {
                    view.animate()
                            .setDuration(500)
                            .alpha(1)
                            .start();
                }

            }
        });
    }

//    @Subscribe
//    public void onSkinChangeEvent(SkinChangeEvent info) {
//        if (blurred != null) {
//            blurred.foregroundColor(Color.parseColor(AppConfig.isNightMode() ? "#a0000000" : "#a0ffffff"));
//            blurred.startBlur();
//        }
//    }

//    @Subscribe
//    public void onUpdateMessageInfoEvent(MessageInfo info) {
//        mBottomBar.getItem(4).setUnreadCount(info.getTotalCount());
//    }

//    @Subscribe
//    public void onGetMainFragmentEvent(GetMainFragmentEvent event) {
//        if (event.getCallback() != null) {
//            event.getCallback().onCallback(this);
//        }
//    }

}
