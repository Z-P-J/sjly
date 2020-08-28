package com.zpj.shouji.market.ui.fragment.homepage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.ColorChangeEvent;
import com.zpj.shouji.market.event.MainActionPopupEvent;
import com.zpj.shouji.market.event.ToolbarColorChangeEvent;
import com.zpj.shouji.market.ui.widget.recommend.RecommendBanner;
import com.zpj.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RecommendFragment2 extends BaseFragment {

    private static final String TAG = "RecommendFragment";

//    private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

    private RecommendBanner mBanner;
    private NestedScrollView scrollView;
//    private UpdateRecommendCard updateRecommendCard;
//    private CollectionRecommendCard collectionRecommendCard;
//    private SoftRecommendCard softRecommendCard;
//    private GameRecommendCard gameRecommendCard;
//    private SubjectRecommendCard subjectRecommendCard;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        LinearLayout llContainer = view.findViewById(R.id.ll_container);
        mBanner = view.findViewById(R.id.rb_banner);
        scrollView = view.findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int h = ScreenUtils.dp2pxInt(context, 100);
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
                    int color = alphaColor(Color.WHITE, alpha * 0.95f);
//                    boolean isDark = android.support.v4.graphics.ColorUtils.calculateLuminance(color) <= 0.5;
//                    Log.d(TAG, "isDark=" + isDark);
                    ToolbarColorChangeEvent.post(color, alpha >= 0.5);

//                    if (scrollY >= oldScrollY) {
//                        ColorChangeEvent.post(alpha < 0.5f);
//                    } else {
//                        ColorChangeEvent.post(alpha < 0.5f);
//                    }
                    ColorChangeEvent.post(alpha < 0.5f);
                } else {
                    ColorChangeEvent.post(false);
                }
            }
        });
//        updateRecommendCard = view.findViewById(R.id.rc_update);
//        collectionRecommendCard = view.findViewById(R.id.rc_collection);
//        softRecommendCard = view.findViewById(R.id.rc_soft);
//        gameRecommendCard = view.findViewById(R.id.rc_game);
//        subjectRecommendCard = view.findViewById(R.id.rc_subject);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSupportVisible() {
//        super.onSupportVisible();
//        lightStatusBar();
        if (mBanner != null) {
            mBanner.onResume();
        }
    }

    @Override
    public void onSupportInvisible() {
//        super.onSupportInvisible();
//        darkStatusBar();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBanner != null) {
            mBanner.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBanner != null) {
            mBanner.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBanner != null) {
            mBanner.onStop();
        }
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

    public static int alphaColor(int color, float alpha) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & color;
        return a + rgb;
    }

}
