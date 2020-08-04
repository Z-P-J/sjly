package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shehuan.niv.NiceImageView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.blur.BlurTransformation2;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.widget.popup.ThemeMorePopupMenu;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.toolbar.BaseToolBar;
import com.zpj.widget.toolbar.ZToolBar;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

import top.defaults.drawabletoolbox.DrawableBuilder;


/**
 * 处理 header + tab + viewPager + recyclerView
 * Description:NestedScrolling2机制下的嵌套滑动，实现NestedScrollingParent2接口下，处理fling效果的区别
 * @author hufeiyang
 */
public class ThemeDetailLayout extends NestedScrollingParent2Layout {


    private final LinearLayout themeLayout;
    private final MagicIndicator magicIndicator;
    private final FrameLayout flContainer;
    private final ViewPager mViewPager;
    private final View shadowView;
    private final ImageView ivHeader;


    private BaseToolBar toolBar;
    private View buttonBarLayout;
    private NiceImageView ivToolbarAvater;
    private TextView tvToolbarName;

    private int mTopViewHeight;


    public ThemeDetailLayout(Context context) {
        this(context, null);
    }

    public ThemeDetailLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeDetailLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.WHITE);
        LayoutInflater.from(context).inflate(R.layout.layout_theme_detail, this, true);
        themeLayout = findViewById(R.id.layout_theme);
        themeLayout.setBackgroundColor(Color.TRANSPARENT);

        ivHeader = findViewById(R.id.iv_header);

        TextView tvShareInfo = themeLayout.findViewById(R.id.share_info);
        TextView tvInfo = themeLayout.findViewById(R.id.text_info);
        TextView tvPhoneType = themeLayout.findViewById(R.id.phone_type);
        TextView tvContent = themeLayout.findViewById(R.id.tv_content);
        tvShareInfo.setTextColor(Color.WHITE);
        tvInfo.setTextColor(Color.WHITE);
        tvPhoneType.setTextColor(Color.WHITE);
        tvContent.setTextColor(Color.WHITE);

        flContainer = findViewById(R.id.fl_container);
        mViewPager = findViewById(R.id.view_pager);
        magicIndicator = findViewById(R.id.magic_indicator);
        shadowView = findViewById(R.id.shadow_view);
        shadowView.setAlpha(0);
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
        mTopViewHeight = themeLayout.getMeasuredHeight();
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
        int h = mTopViewHeight;
        if (oldScrollY <= h) {
            scrollY = Math.min(h, scrollY);
            scrollY = Math.min(scrollY, h);
            float alpha = 1f * scrollY / h;
            if (buttonBarLayout != null) {
                buttonBarLayout.setAlpha(alpha);
            }
            themeLayout.setAlpha(1 - alpha);
            shadowView.setAlpha(alpha);
        }
    }

    public void loadInfo(DiscoverInfo info) {
        EasyViewHolder holder = new EasyViewHolder(themeLayout);
        DiscoverBinder binder = new DiscoverBinder(false);
        List<DiscoverInfo> discoverInfoList = new ArrayList<>();
        discoverInfoList.add(info);
        binder.onBindViewHolder(holder, discoverInfoList, 0, new ArrayList<>(0));
        holder.setOnItemLongClickListener(v -> {
            ThemeMorePopupMenu.with(getContext())
                    .setDiscoverInfo(info)
                    .show();
            return true;
        });

        Glide.with(getContext())
                .load(info.getIcon())
                .into(ivToolbarAvater);

        Glide.with(getContext())
                .asBitmap()
                .load(info.getIcon())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation2(0.2f, 0.3f)))
                .into(ivHeader);


        tvToolbarName.setText(info.getNickName());
        int color = Color.WHITE;
        tvToolbarName.setTextColor(color);

        post(() -> {
            ViewGroup.LayoutParams layoutParams = flContainer.getLayoutParams();
            int height = getMeasuredHeight() - magicIndicator.getMeasuredHeight();
//            layoutParams.height = getMeasuredHeight() - magicIndicator.getMeasuredHeight()
//                    - ScreenUtils.dp2pxInt(getContext(), 56) - StatusBarUtils.getStatusBarHeight(getContext());
            height -= toolBar.getMeasuredHeight();
            layoutParams.height = height;
            flContainer.setLayoutParams(layoutParams);

            mTopViewHeight = themeLayout.getMeasuredHeight();
            mTopViewHeight += ScreenUtils.dp2pxInt(getContext(), 80);
            if (toolBar != null) {
                mTopViewHeight -= toolBar.getMeasuredHeight();
            }
        });
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
