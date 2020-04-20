package com.zpj.popupmenuview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
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

    private static final String TAG = "PopupMenuView";

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

    public void show(View anchor, int px, int py) {
        Log.d(TAG, "px=" + px + " py=" + py);
        onDialogShowing();

        Point origin = new Point(px, py);
        Rect frame = new Rect();


        int[] location = reviseFrameAndOrigin(anchor, frame, origin);

        int x = location[0], y = location[1];
        int width = anchor.getWidth(), height = anchor.getHeight();
        int contentWidth = getContentWidth(), contentHeight = getContentHeight();
        Log.d("whwhwhwhwh", "width=" + width + "   height=" + height);

        Point offset = getOffset(frame, new Rect(x, y + height, contentWidth + x,
                contentHeight + y + height), x + origin.x, y + origin.y);

        int sites = mSites;
        do {
            int site = sites & 0x03;
            Log.d("popupview", "x=" + x + " y=" + y + " contentHeight=" + contentHeight + " contentWidth=" + contentWidth
                    + " frame.left" + frame.left + " frame.right" + frame.right + " frame.top" + frame.top + " frame.bottom" + frame.bottom);
            Log.d("sites", "sites=" + sites);
            Log.d(TAG, "origin=" + origin + " offset=" + offset);
            switch (site) {
                case SITE_TOP:
                    if (y - contentHeight > frame.top) {
                        showAtTop(anchor, origin, offset.x, -height - contentHeight);
                        Log.d(TAG, "showAtTop");
//                        mPopLayout.setSiteMode(PopLayout.SITE_BOTTOM);
//                        mPopLayout.setOffset(origin.x - offset.x);
//                        showAtLocation(anchor, Gravity.TOP, px, py);
                        return;
                    }
                    break;
                case SITE_LEFT:
                    if (x - contentWidth > frame.left) {
                        showAtLeft(anchor, origin, -contentWidth, offset.y);
                        Log.d(TAG, "showAtLeft");
//                        mPopLayout.setSiteMode(PopLayout.SITE_RIGHT);
//                        mPopLayout.setOffset(-origin.y - offset.y);
//                        showAtLocation(anchor, Gravity.START, px, py);
                        return;
                    }
                    break;
                case SITE_RIGHT:
                    if (x + width + contentWidth < frame.right) {
                        showAtRight(anchor, origin, width, offset.y);
                        Log.d(TAG, "showAtRight");
//                        mPopLayout.setSiteMode(PopLayout.SITE_LEFT);
//                        mPopLayout.setOffset(-origin.y - offset.y);
//                        showAtLocation(anchor, Gravity.END, px, py);
                        return;
                    }
                    break;
                case SITE_BOTTOM:
                    if (y + height + contentHeight < frame.bottom) {
                        showAtBottom(anchor, origin, offset.x, offset.y);
                        Log.d(TAG, "showAtBottom");
//                        mPopLayout.setSiteMode(PopLayout.SITE_TOP);
//                        mPopLayout.setOffset(origin.x - offset.x);
//                        showAtLocation(anchor, Gravity.BOTTOM, px, py);
                        return;
                    }
                    break;
            }
            if (sites <= 0) {
                showAtTop(anchor, origin, offset.x, -height - contentHeight);
                Log.d(TAG, "showAtTop--222222");
                break;
            }
            sites >>= 2;
        } while (sites >= 0);
    }

//    @Override
//    public void showAtLocation(View parent, int gravity, int x, int y) {
////        switch (gravity) {
////            case Gravity.START:
////                mPopLayout.setSiteMode(PopLayout.SITE_RIGHT);
////                break;
////            case Gravity.TOP:
////                mPopLayout.setSiteMode(PopLayout.SITE_BOTTOM);
////                break;
////            case Gravity.END:
////                mPopLayout.setSiteMode(PopLayout.SITE_LEFT);
////                break;
////            case Gravity.BOTTOM:
////                mPopLayout.setSiteMode(PopLayout.SITE_TOP);
////                break;
////            default:
////                break;
////        }
//        super.showAtLocation(parent, gravity, x, y);
//    }

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
