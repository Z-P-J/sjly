package com.zpj.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


/**
 * Created by jingbin on 2019/02/15.
 * https://blog.csdn.net/qq_33169861/article/details/83586075
 * 解决全屏下，键盘遮挡问题，但这不是全屏，所以要解决状态栏和导航栏高度的问题
 * 解决 webview 下方的布局，当键盘弹起时，布局不在键盘上方的问题
 * Modify by Z-P-J
 */

public class KeyboardRepairer implements ViewTreeObserver.OnGlobalLayoutListener {

    private final View mChildOfContent;
    private int usableHeightPrevious;
    private final FrameLayout.LayoutParams frameLayoutParams;
    private final int statusBarHeight;
//    private final int navigationHeight;
    private boolean fitsSystemWindows = true;


    public static KeyboardRepairer with(View view) {
        return new KeyboardRepairer(view);
    }

    public static KeyboardRepairer with(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        return new KeyboardRepairer(content.getChildAt(0));
    }


    private KeyboardRepairer(View view) {
        this.mChildOfContent = view;
        statusBarHeight = ScreenUtils.getStatusBarHeight(view.getContext());
//        navigationHeight = getNavigationBarHeight(view.getContext());
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    public KeyboardRepairer setFitsSystemWindows(boolean fitsSystemWindows) {
        this.fitsSystemWindows = fitsSystemWindows;
        return this;
    }

    public void init() {
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (!fitsSystemWindows) {
            usableHeightNow += statusBarHeight;
        }
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            Log.d("KeyboardRepairer", "usableHeightSansKeyboard=" + usableHeightSansKeyboard + " heightDifference=" + heightDifference + " statusBarHeight=" + statusBarHeight);
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                /** keyboard probably just became hidden
                 * 由于此方法考虑到的是全屏的键盘弹起方案，
                 * 若不是全屏的时候，底部的布局会显示不全，且缺少的高度正是一个状态栏的高度 和 底部导航栏高度，
                 * 所以这里减少一个状态栏的高度，相当于向上移动一个状态栏的高度，这时候刚好会显示完成。
                 * */
                if (fitsSystemWindows) {
                    frameLayoutParams.height = usableHeightSansKeyboard - statusBarHeight;
                } else {
                    frameLayoutParams.height = usableHeightSansKeyboard;
                }
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    @Override
    public void onGlobalLayout() {
        possiblyResizeChildOfContent();
    }
}
