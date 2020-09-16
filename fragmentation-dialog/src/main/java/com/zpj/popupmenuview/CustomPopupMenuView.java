//package com.zpj.popupmenuview;
//
//import android.animation.Animator;
//import android.animation.ObjectAnimator;
//import android.animation.ValueAnimator;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.LinearLayout;
//
//import com.zpj.popup.R;
//
///**
// * Created by felix on 16/11/18.
// */
//public class CustomPopupMenuView extends PopupView implements BaseOptionMenuView.OnOptionMenuClickListener, BaseOptionMenuView.OnItemViewCreateListener {
//
//    public interface OnBuildViewListener {
//        void onBuildChildView(CustomPopupMenuView popupMenuView, View itemView, int position);
//    }
//
//    private Activity activity;
//
//    private PopLayout mPopLayout;
//
//    private BaseOptionMenuView mOptionMenuView;
//
//    private PopVerticalScrollView mVerticalScrollView;
//
//    private PopHorizontalScrollView mHorizontalScrollView;
//
//    private BaseOptionMenuView.OnOptionMenuClickListener mOnOptionMenuClickListener;
//
//    private OnBuildViewListener onBuildListener;
//
//
//    private CustomPopupMenuView(Context context, int menuRes) {
//        super(context);
//        mOptionMenuView = new BaseOptionMenuView(context, menuRes);
//        mOptionMenuView.setOnOptionMenuClickListener(this);
//        mOptionMenuView.setOnItemViewCreateListener(this);
//        mPopLayout = new PopLayout(context);
//        ViewGroup scrollView = getScrollView(mOptionMenuView.getOrientation());
//        scrollView.addView(mOptionMenuView);
////        CardView cardView = new CardView(context);
////        cardView.setCardElevation(20);
////        cardView.setUseCompatPadding(true);
////        cardView.setRadius(20);
////        cardView.addView(scrollView);
//        mPopLayout.addView(scrollView);
//        setContentView(mPopLayout);
//    }
//
//    public static CustomPopupMenuView with(Context context, int menuRes) {
//        return new CustomPopupMenuView(context, menuRes);
//    }
//
//    public CustomPopupMenuView initViews(int count, OnBuildViewListener onBuildListener) {
//        this.onBuildListener = onBuildListener;
//        mOptionMenuView.initViews(count);
//        measureContentView();
//        return this;
//    }
//
//    public CustomPopupMenuView setOrientation(int orientation) {
//        mOptionMenuView.setOrientation(orientation);
//        measureContentView();
//        return this;
//    }
//
//    public CustomPopupMenuView setBackgroundAlpha(Activity mActivity, float alpha, long duration) {
//        this.activity = mActivity;
//        objectAnimatorsForDialogShow.add(getBackgroundAlphaAnim(1.0f, alpha, duration));
//        objectAnimatorsForDialogDismiss.add(getBackgroundAlphaAnim(alpha, 1.0f, duration));
////        if (activity != null) {
////            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
////            lp.alpha = alpha;
////            activity.getWindow().setAttributes(lp);
////            this.activity = activity;
////        }
//        return this;
//    }
//
//    public CustomPopupMenuView setPopupViewBackgroundColor(int color) {
//        mPopLayout.setBackgroundColor(color);
//        return this;
//    }
//
//    private ValueAnimator getBackgroundAlphaAnim(float start, float end, long duration) {
//        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
//        animator.setDuration(duration);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (Float) animation.getAnimatedValue();
//                if (activity != null) {
//                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//                    lp.alpha = value;
//                    activity.getWindow().setAttributes(lp);
//                }
//            }
//        });
//        return animator;
//    }
//
//    /**
//     * 横向
//     */
//    public static final int DIRECTION_X = 0;
//    /**
//     * 纵向
//     */
//    public static final int DIRECTION_Y = 1;
//
////    private AnimatorSet animatorSetForDialogShow = new AnimatorSet();
////    private AnimatorSet animatorSetForDialogDismiss = new AnimatorSet();
////    private List<Animator> objectAnimatorsForDialogShow = new ArrayList<>();
////    private List<Animator> objectAnimatorsForDialogDismiss = new ArrayList<>();
//
//    public CustomPopupMenuView setAnimationTranslationShow(int direction, int duration, float... values) {
//        return setAnimationTranslation(true, direction, duration, values);
//    }
//
//    public CustomPopupMenuView setAnimationTranslationDismiss(int direction, int duration, float... values) {
//        return setAnimationTranslation(false, direction, duration, values);
//    }
//
//    private CustomPopupMenuView setAnimationTranslation(boolean isShow, int direction, int duration, float... values) {
//        if (direction != DIRECTION_X && direction != DIRECTION_Y) {
//            direction = DIRECTION_X;
//        }
//        String propertyName = "";
//        switch (direction) {
//            case DIRECTION_X:
//                propertyName = "translationX";
//                break;
//            case DIRECTION_Y:
//                propertyName = "translationY";
//                break;
//        }
//
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mPopLayout, propertyName, values)
//                .setDuration(duration);
//        if (isShow) {
//            objectAnimatorsForDialogShow.add(AnimHelper.createDelayedZoomInAnim(mPopLayout, 1f, 0f));
//        } else {
//            objectAnimatorsForDialogDismiss.add(AnimHelper.createDelayedZoomOutAnim(mPopLayout, 1f, 0f));
//        }
//        return this;
//    }
//
//    public CustomPopupMenuView setAnimationAlphaShow(int duration, float... values) {
//        return setAnimationAlpha(true, duration, values);
//    }
//
//    /**
//     * 对话框消失时候的渐变动画
//     *
//     * @param duration 动画执行的时间长度
//     * @param values   动画移动的位置
//     */
//    public CustomPopupMenuView setAnimationAlphaDismiss(int duration, float... values) {
//        return setAnimationAlpha(false, duration, values);
//    }
//
//    private CustomPopupMenuView setAnimationAlpha(boolean isShow, int duration, float... values) {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mPopLayout, "alpha", values).setDuration(duration);
//        if (isShow) {
//            objectAnimatorsForDialogShow.add(animator);
//        } else {
//            objectAnimatorsForDialogDismiss.add(animator);
//        }
//        return this;
//    }
//
//    public CustomPopupMenuView setOnPopupWindowDismissListener(OnDismissListener onDismissListener) {
//        setOnDismissListener(onDismissListener);
//        return this;
//    }
//
//    public CustomPopupMenuView setOnMenuClickListener(BaseOptionMenuView.OnOptionMenuClickListener listener) {
//        mOnOptionMenuClickListener = listener;
//        return this;
//    }
//
//
//    public int getOrientation() {
//        return mOptionMenuView.getOrientation();
//    }
//
//    // 暂时暴露出
//    @Deprecated
//    public PopLayout getPopLayout() {
//        return mPopLayout;
//    }
//
//    // 暂时暴露出
//    @Deprecated
//    public BaseOptionMenuView getMenuView() {
//        return mOptionMenuView;
//    }
//
//
//    @Override
//    public void show(View anchor) {
//        super.show(anchor);
//    }
//
//    @Override
//    public void show(View anchor, int animRes) {
//        super.show(anchor);
//        setAnimationStyle(animRes);
//    }
//
//    @Override
//    public void show(View anchor, Rect frame, Point origin) {
////        onDialogShowing();
//        setAnimationStyle(R.style.pop_animation);
//        mOptionMenuView.notifyMenusChange();
//        super.show(anchor, frame, origin);
//    }
//
//    @Override
//    public void showAtTop(View anchor, Point origin, int xOff, int yOff) {
//        mPopLayout.setSiteMode(PopLayout.SITE_BOTTOM);
//        mPopLayout.setOffset(origin.x - xOff);
//        super.showAtTop(anchor, origin, xOff, yOff);
//    }
//
//    @Override
//    public void showAtLeft(View anchor, Point origin, int xOff, int yOff) {
//        mPopLayout.setSiteMode(PopLayout.SITE_RIGHT);
//        mPopLayout.setOffset(-origin.y - yOff);
//        super.showAtLeft(anchor, origin, xOff, yOff);
//    }
//
//    @Override
//    public void showAtRight(View anchor, Point origin, int xOff, int yOff) {
//        mPopLayout.setSiteMode(PopLayout.SITE_LEFT);
//        mPopLayout.setOffset(-origin.y - yOff);
//        super.showAtRight(anchor, origin, xOff, yOff);
//    }
//
//    @Override
//    public void showAtBottom(View anchor, Point origin, int xOff, int yOff) {
//        mPopLayout.setSiteMode(PopLayout.SITE_TOP);
//        mPopLayout.setOffset(origin.x - xOff);
//        super.showAtBottom(anchor, origin, xOff, yOff);
//    }
//
//    @Override
//    public void dismiss() {
////        Toast.makeText(activity, "dismiss", Toast.LENGTH_SHORT).show();
////        setBackgroundAlpha(activity, 1.0f);
//        super.dismiss();
////        if (animatorSetForDialogDismiss.isRunning()) {
////            return;
////        }
////        if (animatorSetForDialogDismiss != null && objectAnimatorsForDialogDismiss != null && objectAnimatorsForDialogDismiss.size() > 0) {
////            animatorSetForDialogDismiss.playTogether(objectAnimatorsForDialogDismiss);
////            animatorSetForDialogDismiss.start();
////        }
//    }
//
//    @Override
//    public void onItemViewCreate(View itemView, int position) {
//        if (onBuildListener != null) {
//            onBuildListener.onBuildChildView(this, itemView, position);
//        }
//
//        itemView.setOnClickListener(v -> {
//            if (mOnOptionMenuClickListener != null) {
//                if (mOnOptionMenuClickListener.onOptionMenuClick(position, itemView)) {
//                    dismiss();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void setOnDismissListener(OnDismissListener onDismissListener) {
//        super.setOnDismissListener(onDismissListener);
//    }
//
//    @Override
//    public boolean onOptionMenuClick(int position, View itemView) {
//        if (mOnOptionMenuClickListener != null) {
//            if (mOnOptionMenuClickListener.onOptionMenuClick(position, itemView)) {
//                dismiss();
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private ViewGroup getScrollView(int orientation) {
//        if (orientation == LinearLayout.HORIZONTAL) {
//            if (mHorizontalScrollView == null) {
//                mHorizontalScrollView = new PopHorizontalScrollView(getContext());
//                mHorizontalScrollView.setHorizontalScrollBarEnabled(false);
//                mHorizontalScrollView.setVerticalScrollBarEnabled(false);
//            }
//            return mHorizontalScrollView;
//        } else {
//            if (mVerticalScrollView == null) {
//                mVerticalScrollView = new PopVerticalScrollView(getContext());
//                mVerticalScrollView.setHorizontalScrollBarEnabled(false);
//                mVerticalScrollView.setVerticalScrollBarEnabled(false);
//            }
//            return mVerticalScrollView;
//        }
//    }
//
//    protected void onDialogShowing() {
//        if (animatorSetForDialogShow != null && objectAnimatorsForDialogShow != null && objectAnimatorsForDialogShow.size() > 0) {
//            animatorSetForDialogShow.playTogether(objectAnimatorsForDialogShow);
//            animatorSetForDialogShow.start();
//        }
//        //TODO 缩放的动画效果不好，不能从控件所在的位置开始缩放
////        ObjectAnimator.ofFloat(rlOutsideBackground.findViewById(R.id.rlParentForAnimate), "scaleX", 0.3f, 1.0f).setDuration(500).start();
////        ObjectAnimator.ofFloat(rlOutsideBackground.findViewById(R.id.rlParentForAnimate), "scaleY", 0.3f, 1.0f).setDuration(500).start();
//    }
//
//    @SuppressLint("NewApi")
//    private void onDialogDismiss() {
//        if (animatorSetForDialogDismiss.isRunning()) {
//            return;
//        }
//        if (animatorSetForDialogDismiss != null && objectAnimatorsForDialogDismiss != null && objectAnimatorsForDialogDismiss.size() > 0) {
//            animatorSetForDialogDismiss.playTogether(objectAnimatorsForDialogDismiss);
//            animatorSetForDialogDismiss.start();
//            animatorSetForDialogDismiss.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            });
//        } else {
//
//        }
//    }
//
//}
