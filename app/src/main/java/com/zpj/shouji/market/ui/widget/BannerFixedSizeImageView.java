//package com.zpj.shouji.market.ui.widget;
//
//import android.content.Context;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//
//import com.shehuan.niv.NiceImageView;
//import com.zpj.utils.ScreenUtils;
//
//public class BannerFixedSizeImageView extends NiceImageView {
//    public BannerFixedSizeImageView(Context context) {
//        super(context);
//    }
//
//    public BannerFixedSizeImageView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public BannerFixedSizeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        heightMeasureSpec = (int) ((float) widthMeasureSpec
//                / ScreenUtils.getScreenHeight(getContext())
//                * ScreenUtils.getScreenWidth(getContext()));
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//}
