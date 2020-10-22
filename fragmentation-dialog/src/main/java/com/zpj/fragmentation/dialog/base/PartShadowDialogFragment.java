package com.zpj.fragmentation.dialog.base;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.TranslateAnimator;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;
import com.zpj.fragmentation.dialog.enums.PopupPosition;
import com.zpj.fragmentation.dialog.interfaces.OnClickOutsideListener;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Description: 局部阴影的弹窗，类似于淘宝商品列表的下拉筛选弹窗
 * Create by dance, at 2018/12/21
 */
public abstract class PartShadowDialogFragment extends AttachDialogFragment {

    private static final String TAG = "PartShadowDialog";


    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

    }

    @Override
    protected void doAttach() {
        Log.d(TAG, "screenHeight=" + ScreenUtils.getScreenHeight(context) + " getRootView().getMeasuredHeight()=" + getRootView().getMeasuredHeight());

        if (attachView == null)
            throw new IllegalArgumentException("atView must not be null for PartShadowPopupView！");

        // 指定阴影动画的目标View
        shadowBgAnimator.targetView = getImplView();

        //1. apply width and height
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getImplView().getLayoutParams();
        if (rotation == 0) {
            params.width = getImplView().getMeasuredWidth(); // 满宽
        } else if (rotation == 1 || rotation == 3) {
            params.width = getImplView().getMeasuredWidth() - (StatusBarUtils.isNavBarVisible(context) ? ScreenUtils.getNavBarHeight(context) : 0);
        }

        Log.d(TAG, "rotation=" + rotation);

        //水平居中
//        if(isCenterHorizontal && getPopupImplView() != null){
//            getPopupImplView().setTranslationX(XPopupUtils.getWindowWidth(getContext())/2f - getPopupContentView().getMeasuredWidth()/2f);
//        }

        //1. 获取atView在屏幕上的位置
        int[] locations = new int[2];
        attachView.getLocationOnScreen(locations);
        Rect rect = new Rect(locations[0], locations[1], locations[0] + attachView.getMeasuredWidth(),
                locations[1] + attachView.getMeasuredHeight());
        int centerY = rect.top + rect.height() / 2;
        Log.d(TAG, "centerY=" + centerY + " getImplView().getMeasuredHeight()=" + getImplView().getMeasuredHeight() + " getContentView().getMeasuredHeight()" + getContentView().getMeasuredHeight());

        int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
//        if (getActivity() != null && (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                & getActivity().getWindow().getAttributes().flags)
//                == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
//            offset = 0;
//        } else {
//            offset = ScreenUtils.getStatusBarHeight(context);
//        }

        if ((centerY > getImplView().getMeasuredHeight() || popupPosition == PopupPosition.Top) && popupPosition != PopupPosition.Bottom) {
            // 说明atView在Window下半部分，PartShadow应该显示在它上方，计算atView之上的高度
            params.height = rect.top - offset;
            params.width = MATCH_PARENT;
            isShowUp = true;
            params.topMargin = -defaultOffsetY;
            // 同时自定义的impl View应该Gravity居于底部

            FrameLayout.LayoutParams implParams = (FrameLayout.LayoutParams) contentView.getLayoutParams();
            implParams.gravity = Gravity.BOTTOM;
//            if(getMaxHeight()!=0)
//                implParams.height = Math.min(implView.getMeasuredHeight(), getMaxHeight());
            implParams.width = MATCH_PARENT;
            implParams.height = WRAP_CONTENT;
            contentView.setLayoutParams(implParams);

        } else {
            // atView在上半部分，PartShadow应该显示在它下方，计算atView之下的高度
//            params.height = getContentView().getMeasuredHeight() - rect.bottom;
//            params.height = getRootView().getMeasuredHeight() - rect.bottom + offset;
            params.height = ScreenUtils.getScreenHeight(context) - rect.bottom;
            params.width = MATCH_PARENT;
            // 防止伸到导航栏下面
//            if (XPopupUtils.isNavBarVisible(getContext())) {
//                params.height -= XPopupUtils.getNavBarHeight();
//            }
            isShowUp = false;
            params.topMargin = rect.bottom + defaultOffsetY - offset;

            // 同时自定义的impl View应该Gravity居于顶部

            FrameLayout.LayoutParams implParams = (FrameLayout.LayoutParams) contentView.getLayoutParams();
            implParams.gravity = Gravity.TOP;
//            if(getMaxHeight()!=0)
//                implParams.height = Math.min(implView.getMeasuredHeight(), getMaxHeight());
            implParams.width = MATCH_PARENT;
            implParams.height = WRAP_CONTENT;
            contentView.setLayoutParams(implParams);
        }
        getImplView().setLayoutParams(params);

        attachPopupContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (cancelableInTouchOutside) dismiss();
                return false;
            }
        });
        attachPopupContainer.setOnClickOutsideListener(new OnClickOutsideListener() {
            @Override
            public void onClickOutside() {
                if (cancelableInTouchOutside) dismiss();
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                popupContentAnimator = getDialogAnimator((ViewGroup) contentView);
                if (popupContentAnimator != null) {
                    popupContentAnimator.initAnimator();
                    popupContentAnimator.animateShow();
                }
                if (shadowBgAnimator != null) {
                    shadowBgAnimator.initAnimator();
                    shadowBgAnimator.animateShow();
                }
                getImplView().setAlpha(1f);
            }
        });
    }

//    //让触摸透过
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(cancelableInTouchOutside){
//            dismiss();
//        }
//        return !cancelableInTouchOutside;
//    }

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        return new TranslateAnimator(contentView, isShowUp ?
                PopupAnimation.TranslateFromBottom : PopupAnimation.TranslateFromTop);
    }

}
