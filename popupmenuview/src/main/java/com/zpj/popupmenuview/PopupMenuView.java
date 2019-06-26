package com.zpj.popupmenuview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 16/11/18.
 */
public class PopupMenuView extends PopupView implements OptionMenuView.OnOptionMenuClickListener {

    private Activity activity;

    private PopLayout mPopLayout;

    private OptionMenuView mOptionMenuView;

    private PopVerticalScrollView mVerticalScrollView;

    private PopHorizontalScrollView mHorizontalScrollView;

    private OptionMenuView.OnOptionMenuClickListener mOnOptionMenuClickListener;

    public PopupMenuView(Context context) {
        this(context, 0);
    }

    public PopupMenuView(Context context, int menuRes) {
        super(context);
        mOptionMenuView = new OptionMenuView(context, menuRes);
        mOptionMenuView.setOnOptionMenuClickListener(this);
        mPopLayout = new PopLayout(context);
        ViewGroup scrollView = getScrollView(mOptionMenuView.getOrientation());
        scrollView.addView(mOptionMenuView);
        mPopLayout.addView(scrollView);
        setContentView(mPopLayout);
    }

    public PopupMenuView(Context context, int menuRes, Menu menu) {
        this(context);
        inflate(menuRes, menu);
    }

    public void inflate(int menuRes, Menu menu) {
        mOptionMenuView.inflate(menuRes, menu);
        measureContentView();
    }

    public PopupMenuView setMenuItems(List<OptionMenu> optionMenus) {
        mOptionMenuView.setOptionMenus(optionMenus);
        measureContentView();
        return this;
    }

    public PopupMenuView setBackgroundAlpha(Activity activity, float alpha, long duration) {
        this.activity = activity;
        objectAnimatorsForDialogShow.add(getBackgroundAlphaAnim(1.0f, alpha, duration));
        objectAnimatorsForDialogDismiss.add(getBackgroundAlphaAnim(alpha, 1.0f, duration));
//        if (activity != null) {
//            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//            lp.alpha = alpha;
//            activity.getWindow().setAttributes(lp);
//            this.activity = activity;
//        }
        return this;
    }

    private ValueAnimator getBackgroundAlphaAnim(float start, float end, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                if (activity != null) {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = value;
                    activity.getWindow().setAttributes(lp);
                }
            }
        });
        return animator;
    }

    public PopupMenuView setBackgroundColor(int color) {
        mPopLayout.setBackgroundColor(color);
        return this;
    }

    public List<OptionMenu> getMenuItems() {
        return mOptionMenuView.getOptionMenus();
    }

    public PopupMenuView setOrientation(int orientation) {
        mOptionMenuView.setOrientation(orientation);
        measureContentView();
        return this;
    }

    public int getOrientation() {
        return mOptionMenuView.getOrientation();
    }

    // 暂时暴露出
    @Deprecated
    public PopLayout getPopLayout() {
        return mPopLayout;
    }

    // 暂时暴露出
    @Deprecated
    public OptionMenuView getMenuView() {
        return mOptionMenuView;
    }

    public PopupMenuView setOnMenuClickListener(OptionMenuView.OnOptionMenuClickListener listener) {
        mOnOptionMenuClickListener = listener;
        return this;
    }

    @Override
    public void show(View anchor) {
        super.show(anchor);
    }

    @Override
    public void show(View anchor, Rect frame, Point origin) {
        mOptionMenuView.notifyMenusChange();
        super.show(anchor, frame, origin);
    }

    @Override
    public void showAtTop(View anchor, Point origin, int xOff, int yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_BOTTOM);
        mPopLayout.setOffset(origin.x - xOff);
        super.showAtTop(anchor, origin, xOff, yOff);
    }

    @Override
    public void showAtLeft(View anchor, Point origin, int xOff, int yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_RIGHT);
        mPopLayout.setOffset(-origin.y - yOff);
        super.showAtLeft(anchor, origin, xOff, yOff);
    }

    @Override
    public void showAtRight(View anchor, Point origin, int xOff, int yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_LEFT);
        mPopLayout.setOffset(-origin.y - yOff);
        super.showAtRight(anchor, origin, xOff, yOff);
    }

    @Override
    public void showAtBottom(View anchor, Point origin, int xOff, int yOff) {
        mPopLayout.setSiteMode(PopLayout.SITE_TOP);
        mPopLayout.setOffset(origin.x - xOff);
        super.showAtBottom(anchor, origin, xOff, yOff);
    }

    @Override
    public boolean onOptionMenuClick(int position, OptionMenu menu) {
        if (mOnOptionMenuClickListener != null) {
            if (mOnOptionMenuClickListener.onOptionMenuClick(position, menu)) {
                dismiss();
                return true;
            }
        }
        return false;
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        if (animatorSetForDialogDismiss.isRunning()) {
//            return;
//        }
//        if (animatorSetForDialogDismiss != null && objectAnimatorsForDialogDismiss != null && objectAnimatorsForDialogDismiss.size() > 0) {
//            animatorSetForDialogDismiss.playTogether(objectAnimatorsForDialogDismiss);
//            animatorSetForDialogDismiss.start();
//        }
    }

    private ViewGroup getScrollView(int orientation) {
        if (orientation == LinearLayout.HORIZONTAL) {
            if (mHorizontalScrollView == null) {
                mHorizontalScrollView = new PopHorizontalScrollView(getContext());
                mHorizontalScrollView.setHorizontalScrollBarEnabled(false);
                mHorizontalScrollView.setVerticalScrollBarEnabled(false);
            }
            return mHorizontalScrollView;
        } else {
            if (mVerticalScrollView == null) {
                mVerticalScrollView = new PopVerticalScrollView(getContext());
                mVerticalScrollView.setHorizontalScrollBarEnabled(false);
                mVerticalScrollView.setVerticalScrollBarEnabled(false);
            }
            return mVerticalScrollView;
        }
    }
}
