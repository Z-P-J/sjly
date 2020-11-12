package com.zpj.shouji.market.ui.fragment.homepage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.ColorChangeEvent;
import com.zpj.shouji.market.event.MainActionPopupEvent;
import com.zpj.shouji.market.event.ScrollChangeEvent;
import com.zpj.shouji.market.event.ToolbarColorChangeEvent;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.widget.SmartNestedScrollView;
import com.zpj.shouji.market.ui.widget.recommend.CollectionRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GameRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.GuessYouLikeRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
import com.zpj.shouji.market.ui.widget.recommend.RecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SoftRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.SubjectRecommendCard;
import com.zpj.shouji.market.ui.widget.recommend.UpdateRecommendCard;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.statelayout.StateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.Queue;

public class RecommendFragment2 extends SkinFragment
        implements NestedScrollView.OnScrollChangeListener,
        SmartNestedScrollView.ISmartScrollChangedListener {

    private static final String TAG = "RecommendFragment22";

    protected final Queue<RecommendCard> recommendCardList = new LinkedList<>();

    private StateLayout stateLayout;
    private SmartNestedScrollView scrollView;
    private LinearLayout llContainer;
    private RecommendBanner mBanner;

    private View loadingFooter;

    private boolean isBannerLoaded = false;

//    private boolean hasLoading;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        loadingFooter = LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false);

        stateLayout = findViewById(R.id.state_layout);
        stateLayout.showLoadingView();

//        mBanner = view.findViewById(R.id.rb_banner);
        llContainer = findViewById(R.id.ll_container);
        scrollView = view.findViewById(R.id.scroll_view);
        scrollView.setScanScrollChangedListener(this);
        scrollView.setOnScrollChangeListener(this);

        mBanner = new RecommendBanner(context);
        mBanner.loadData(new Runnable() {
            @Override
            public void run() {
                isBannerLoaded = true;
                stateLayout.showContentView();
                ColorChangeEvent.post(true);
                ScrollChangeEvent.post(scrollView.isScrolledToTop() ? 0 : 1);
            }
        });
        llContainer.addView(mBanner);

//        postOnEnterAnimationEnd(new Runnable() {
//            @Override
//            public void run() {
//                stateLayout.showContentView();
//                ColorChangeEvent.post(true);
//            }
//        });

        llContainer.addView(loadingFooter);

        recommendCardList.add(new UpdateRecommendCard(context));
        recommendCardList.add(new CollectionRecommendCard(context));
        recommendCardList.add(new SoftRecommendCard(context));
        recommendCardList.add(new GameRecommendCard(context));
        recommendCardList.add(new SubjectRecommendCard(context));
        recommendCardList.add(new GuessYouLikeRecommendCard(context));

        onScrolledToBottom();

//        stateLayout.showContentView();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSupportVisible() {
        Log.d(TAG, "onSupportVisible");
        super.onSupportVisible();
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                llContainer.setFocusable(true);
//                llContainer.setFocusableInTouchMode(true);
//            }
//        }, 2000);
//        lightStatusBar();
        if (isBannerLoaded) {
            if (scrollView != null) {
                ScrollChangeEvent.post(scrollView.isScrolledToTop() ? 0 : 1);
            } else {
                ScrollChangeEvent.post(0);
            }
        }
        if (mBanner != null) {
            mBanner.onResume();
        }
    }

    @Override
    public void onSupportInvisible() {
        Log.d(TAG, "onSupportInvisible");
        super.onSupportInvisible();
//        llContainer.setFocusable(false);
//        llContainer.setFocusableInTouchMode(false);
//        darkStatusBar();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    protected void initStatusBar() {

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (mBanner != null) {
            mBanner.onResume();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (mBanner != null) {
            mBanner.onStop();
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach context");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach activity");
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        Log.d(TAG, "onEnterAnimationEnd");
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onLazyInitView");
        super.onLazyInitView(savedInstanceState);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        Log.d(TAG, "onAttachFragment");
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged hidden=" + hidden);
    }

    @Subscribe
    public void onMainActionPopupEvent(MainActionPopupEvent event) {
        if (isSupportVisible() && mBanner != null) {
            if (event.isShow()) {
                mBanner.onPause();
            } else {
                mBanner.onResume();
            }
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int h = ScreenUtils.dp2pxInt(context, 80);
        if (oldScrollY <= h) {
            if (Math.abs(scrollY - oldScrollY) < 2) {
                return;
            }
//                    scrollY = Math.min(h, scrollY);
//                    int mScrollY = Math.min(scrollY, h);
            Log.d(TAG, "scrollY=" + scrollY + " h=" + h + " oldScrollY=" + oldScrollY);
            float alpha = 1f * scrollY / h;
            Log.d(TAG, "alpha=" + alpha);
            alpha = Math.min(alpha, 1f);
            ScrollChangeEvent.post(alpha);
//            int color = ColorUtils.alphaColor(Color.WHITE, alpha * 0.95f);
////                    boolean isDark = android.support.v4.graphics.ColorUtils.calculateLuminance(color) <= 0.5;
////                    Log.d(TAG, "isDark=" + isDark);
//            ToolbarColorChangeEvent.post(color, alpha >= 0.5);
//
//            ColorChangeEvent.post(alpha < 0.5f);
            if (mBanner != null) {
                if (alpha < 0.5) {
                    mBanner.onResume();
                } else {
                    mBanner.onPause();
                }
            }
        } else {
            ScrollChangeEvent.post(1f);
//            ColorChangeEvent.post(false);
            if (mBanner != null) {
                mBanner.onPause();
            }
        }
    }

    @Override
    public void onScrolledToBottom() {
        if (recommendCardList.size() >= 2) {
            RecommendCard recommendCard = recommendCardList.remove();
            RecommendCard recommendCard2 = recommendCardList.remove();
            recommendCard2.loadData(null);
            recommendCard.loadData(() -> {
                addCard(recommendCard, false);
                addCard(recommendCard2, recommendCardList.isEmpty());
            });
        } else if (recommendCardList.size() == 1) {
            RecommendCard recommendCard = recommendCardList.remove();
            recommendCard.loadData(() -> addCard(recommendCard, true));
        }
    }

    @Override
    public void onScrolledToTop() {
//        ToolbarColorChangeEvent.post(Color.TRANSPARENT, false);
//        ColorChangeEvent.post(true);
        ScrollChangeEvent.post(0f);
        if (mBanner != null) {
            mBanner.onResume();
        }
    }

    protected void addCard(RecommendCard recommendCard, boolean hasNoMore) {
        llContainer.removeView(loadingFooter);
        llContainer.addView(recommendCard);

        if (hasNoMore) {
            loadingFooter.findViewById(R.id.ll_container_progress).setVisibility(View.GONE);
            TextView tvMsg = loadingFooter.findViewById(R.id.tv_msg);
            tvMsg.setVisibility(View.VISIBLE);
        }

        llContainer.addView(loadingFooter);
    }

}
