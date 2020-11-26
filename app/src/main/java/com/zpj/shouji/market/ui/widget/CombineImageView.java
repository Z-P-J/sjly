package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.shouji.market.glide.combine.CombineImage;
import com.zpj.shouji.market.glide.transformations.CircleWithBorderTransformation;

import java.util.List;

public class CombineImageView extends AppCompatImageView {

    public CombineImageView(Context context) {
        this(context, null);
    }

    public CombineImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CombineImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setUrls(List<String> urlList) {

        String[] urls = new String[Math.min(urlList.size(), 4)];

        for (int i = 0; i < urls.length; i++) {
            urls[i] = urlList.get(i);
        }

        CombineImage image = CombineImage.get(urls)
                .setSize(48)
                .setGap(2)
                .setGapColor(Color.LTGRAY);
        Glide.with(this)
                .load(image)
                .apply(RequestOptions.bitmapTransform(new CircleWithBorderTransformation(2, Color.LTGRAY)))
                .into(this);
    }

}
