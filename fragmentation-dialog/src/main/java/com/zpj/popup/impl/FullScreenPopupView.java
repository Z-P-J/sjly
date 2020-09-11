//package com.zpj.popup.impl;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.support.annotation.NonNull;
//import android.view.Gravity;
//import android.view.View;
//import android.view.WindowManager;
//
//import com.zpj.popup.XPopup;
//import com.zpj.popup.animator.PopupAnimator;
//import com.zpj.popup.animator.TranslateAnimator;
//import com.zpj.popup.core.CenterPopupView;
//import com.zpj.popup.enums.PopupAnimation;
//import com.zpj.popup.util.XPopupUtils;
//
///**
// * Description: 宽高撑满的全屏弹窗
// * Create by lxj, at 2019/2/1
// */
//public class FullScreenPopupView extends CenterPopupView {
//    public FullScreenPopupView(@NonNull Context context) {
//        super(context);
//    }
//
//    @Override
//    protected int getMaxWidth() {
//        return 0;
//    }
//
//    @Override
//    protected void initPopupContent() {
//        super.initPopupContent();
//        popupInfo.hasShadowBg = false;
//    }
//
//    @Override
//    public void onNavigationBarChange(boolean show) {
//        if(!show){
//            applyFull();
//            getPopupContentView().setPadding(0,0,0,0);
//        }else {
//            applySize(true);
//        }
//    }
//
//    @Override
//    protected void applySize(boolean isShowNavBar) {
//        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
//        View contentView = getPopupContentView();
//        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
//        params.gravity = Gravity.TOP;
//        contentView.setLayoutParams(params);
//
//        int actualNabBarHeight = isShowNavBar||XPopupUtils.isNavBarVisible(getContext()) ? XPopupUtils.getNavBarHeight() : 0;
//        if (rotation == 0) {
//            contentView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop(), contentView.getPaddingRight(),
//                    actualNabBarHeight);
//        } else if (rotation == 1 || rotation == 3) {
//            contentView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop(), contentView.getPaddingRight(), 0);
//        }
//    }
//
//    Paint paint = new Paint();
//    Rect shadowRect;
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
//        if (popupInfo.hasStatusBarShadow) {
//            paint.setColor(XPopup.statusBarShadowColor);
//            shadowRect = new Rect(0, 0, XPopupUtils.getWindowWidth(getContext()), XPopupUtils.getStatusBarHeight());
//            canvas.drawRect(shadowRect, paint);
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        paint = null;
//    }
//
//    @Override
//    protected PopupAnimator getPopupAnimator() {
//        return new TranslateAnimator(getPopupContentView(), PopupAnimation.TranslateFromBottom);
//    }
//}
