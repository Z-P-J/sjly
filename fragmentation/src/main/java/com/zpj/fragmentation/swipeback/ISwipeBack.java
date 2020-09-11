package com.zpj.fragmentation.swipeback;

import android.support.annotation.FloatRange;

/**
 * @Author Z-P-J
 */

public interface ISwipeBack {

//    View attachToSwipeBack(View view);

    SwipeBackLayout getSwipeBackLayout();

    void setSwipeBackEnable(boolean enable);

    void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel);

    void setEdgeLevel(int widthPixel);

    void setEdgeOrientation(@SwipeBackLayout.EdgeOrientation int orientation);

    /**
     * Set the offset of the parallax slip.
     */
    void setParallaxOffset(@FloatRange(from = 0.0f, to = 1.0f) float offset);

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     *
     * @return true: Activity可以滑动退出, 并且总是优先;  false: Fragment优先滑动退出
     */
    boolean swipeBackPriority();
}
