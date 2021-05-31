package com.zpj.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.Gravity;
import android.widget.ImageView;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

public class ImagePagerTitle extends CommonPagerTitleView {

    private final ImageView ivIcon;

    public ImagePagerTitle(Context context) {
        super(context);

        int paddingH = UIUtil.dip2px(context, 16);
        int paddingV = UIUtil.dip2px(context, 4);
        setPadding(paddingH, paddingV, paddingH, paddingV);

        ivIcon = new ImageView(context);
        int size = UIUtil.dip2px(context, 24);
        LayoutParams params = new LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        addView(ivIcon, params);
    }

    public void setImageBitmap(Bitmap bitmap) {
        ivIcon.setImageBitmap(bitmap);
    }

    public void setImageDrawable(Drawable drawable) {
        ivIcon.setImageDrawable(drawable);
    }

    public void setImageResource(@DrawableRes int res) {
        ivIcon.setImageResource(res);
    }

}
