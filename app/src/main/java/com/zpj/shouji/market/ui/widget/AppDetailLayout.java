package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.blur.BlurTransformation2;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.detail.AppCommentFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppInfoFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppRecommendFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppThemeFragment;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;
import com.zpj.widget.toolbar.BaseToolBar;
import com.zpj.widget.toolbar.ZToolBar;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import top.defaults.drawabletoolbox.DrawableBuilder;


/**
 * 处理 header + tab + viewPager + recyclerView
 * Description:NestedScrolling2机制下的嵌套滑动，实现NestedScrollingParent2接口下，处理fling效果的区别
 * @author hufeiyang
 */
public class AppDetailLayout extends NestedScrollingParent2Layout {


    private final LinearLayout llInfo;
    private final MagicIndicator magicIndicator;
    private final FrameLayout flContainer;
    private final ViewPager mViewPager;
    private final View shadowView;

    private final ImageView ivHeader;
    private final NiceImageView icon;
    private final TextView title;
    private final TextView tvVersion;
    private final TextView tvSize;
    private final TextView shortInfo;
    private final TextView shortIntroduce;

    private BaseToolBar toolBar;
    private View buttonBarLayout;
    private NiceImageView ivToolbarAvater;
    private TextView tvToolbarName;

    private int mTopViewHeight;


    public AppDetailLayout(Context context) {
        this(context, null);
    }

    public AppDetailLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppDetailLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        LayoutInflater.from(context).inflate(R.layout.layout_app_detail, this, true);
        llInfo = findViewById(R.id.ll_info);
        flContainer = findViewById(R.id.fl_container);
        mViewPager = findViewById(R.id.view_pager);
        magicIndicator = findViewById(R.id.magic_indicator);
        shadowView = findViewById(R.id.shadow_view);
        shadowView.setAlpha(0);

        ivHeader = findViewById(R.id.iv_header);
        icon = findViewById(R.id.iv_icon);
        title = findViewById(R.id.tv_title);
        tvVersion = findViewById(R.id.tv_version);
        tvSize = findViewById(R.id.tv_size);
        shortInfo = findViewById(R.id.tv_info);
        shortIntroduce = findViewById(R.id.tv_detail);
    }


    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }


    /**
     * 在嵌套滑动的子View未滑动之前，判断父view是否优先与子view处理(也就是父view可以先消耗，然后给子view消耗）
     *
     * @param target   具体嵌套滑动的那个子类
     * @param dx       水平方向嵌套滑动的子View想要变化的距离
     * @param dy       垂直方向嵌套滑动的子View想要变化的距离 dy<0向下滑动 dy>0 向上滑动
     * @param consumed 这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     *                 consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //这里不管手势滚动还是fling都处理
        boolean hideTop = dy > 0 && getScrollY() < mTopViewHeight;
        boolean showTop = dy < 0 && getScrollY() >= 0 && !target.canScrollVertically(-1);
        if (hideTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }


    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        //当子控件处理完后，交给父控件进行处理。
        if (dyUnconsumed < 0) {
            //表示已经向下滑动到头
            scrollBy(0, dyUnconsumed);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }


    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //ViewPager修改后的高度= 总高度-导航栏高度
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup.LayoutParams layoutParams = flContainer.getLayoutParams();
        int height = getMeasuredHeight() - magicIndicator.getMeasuredHeight();
//            layoutParams.height = getMeasuredHeight() - magicIndicator.getMeasuredHeight()
//                    - ScreenUtils.dp2pxInt(getContext(), 56) - StatusBarUtils.getStatusBarHeight(getContext());
        if (toolBar != null) {
            height -= toolBar.getMeasuredHeight();
        }
        layoutParams.height = height;
        flContainer.setLayoutParams(layoutParams);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        mTopViewHeight = llInfo.getMeasuredHeight() - ScreenUtils.dp2pxInt(getContext(), 56) - StatusBarUtils.getStatusBarHeight(getContext());
        mTopViewHeight = llInfo.getMeasuredHeight();
        if (toolBar != null) {
            mTopViewHeight -= toolBar.getMeasuredHeight();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        super.scrollTo(x, y);
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        Log.d("onScrollChange", "scrollX=" + scrollX + " scrollY=" + scrollY + " oldScrollX=" + oldScrollX + " oldScrollY=" + oldScrollY);
//        int[] location = new int[2];
//        magicIndicator.getLocationOnScreen(location);
//        int yPosition = location[1];
//        Log.d("onScrollChange", "yPosition=" + yPosition + " getStatusBarHeight=" + StatusBarUtils.getStatusBarHeight(getContext()) + " location[1]=" + location[1]);
//        int h = ScreenUtils.dp2pxInt(getContext(), 100);
        int h = mTopViewHeight;
        if (oldScrollY <= h) {
            scrollY = Math.min(h, scrollY);
            scrollY = Math.min(scrollY, h);
            float alpha = 1f * scrollY / h;
            if (buttonBarLayout != null) {
                buttonBarLayout.setAlpha(alpha);
            }
            llInfo.setAlpha(1 - alpha);
            shadowView.setAlpha(alpha);
        }
    }

    public void loadInfo(AppDetailInfo info) {
        Glide.with(getContext())
                .load(info.getIconUrl())
                .into(icon);
        Glide.with(getContext())
                .load(info.getIconUrl())
                .into(ivToolbarAvater);

        Glide.with(getContext())
                .asBitmap()
                .load(info.getIconUrl())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation2(0.2f, 0.3f)))
                .into(ivHeader);

        title.setText(info.getName());
        tvVersion.setText(info.getVersion());
        tvSize.setText(info.getSize());
        tvToolbarName.setText(info.getName());
        shortInfo.setText(info.getLanguage() + " | " + info.getFee()
                + " | " + info.getAds() + " | " + info.getFirmware());
        shortIntroduce.setText(info.getLineInfo());

        tvVersion.setBackground(new DrawableBuilder()
                .rectangle()
                .rounded()
                .strokeColor(getResources().getColor(R.color.colorPrimary))
                .solidColor(getResources().getColor(R.color.colorPrimary))
                .build());
        tvSize.setBackground(new DrawableBuilder()
                .rectangle()
                .rounded()
                .strokeColor(getResources().getColor(R.color.light_blue1))
                .solidColor(getResources().getColor(R.color.light_blue1))
                .build());
        int color = Color.WHITE;
        title.setTextColor(color);
        tvVersion.setTextColor(color);
        tvSize.setTextColor(color);
        tvToolbarName.setTextColor(color);
        shortInfo.setTextColor(color);
        shortIntroduce.setTextColor(color);
    }

    public void bindToolbar(ZToolBar toolBar) {
        this.toolBar = toolBar;
        buttonBarLayout = toolBar.getCenterCustomView();
        buttonBarLayout.setAlpha(0);
        ivToolbarAvater = toolBar.findViewById(R.id.toolbar_avatar);
        tvToolbarName = toolBar.findViewById(R.id.toolbar_name);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public MagicIndicator getMagicIndicator() {
        return magicIndicator;
    }

}
