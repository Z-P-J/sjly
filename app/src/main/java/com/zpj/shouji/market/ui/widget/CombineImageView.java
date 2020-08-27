package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.DingLayoutManager;
import com.shehuan.niv.NiceImageView;
import com.zpj.shouji.market.R;

import java.util.List;

public class CombineImageView extends NiceImageView {

    public CombineImageView(Context context) {
        this(context, null);
    }

    public CombineImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CombineImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCornerRadius(8);
        setBorderWidth(2);
        setBorderColor(getResources().getColor(R.color.blue_gray));
    }

    public void setUrls(List<String> urlList) {
        String[] urls = new String[Math.min(urlList.size(), 4)];

        for (int i = 0; i < urls.length; i++) {
            urls[i] = urlList.get(i);
        }

        CombineBitmap.init(getContext())
                .setLayoutManager(new DingLayoutManager()) // 必选， 设置图片的组合形式，支持WechatLayoutManager、DingLayoutManager
                .setSize(48) // 必选，组合后Bitmap的尺寸，单位dp
                .setGap(2) // 单个图片之间的距离，单位dp，默认0dp
                .setGapColor(getResources().getColor(R.color.color_background_gray)) // 单个图片间距的颜色，默认白色
//                .setPlaceholder() // 单个图片加载失败的默认显示图片
                .setUrls(urls) // 要加载的图片url数组
                .setImageView(this) // 直接设置要显示图片的ImageView
                // 设置“子图片”的点击事件，需使用setImageView()，index和图片资源数组的索引对应
                .build();
    }

}
