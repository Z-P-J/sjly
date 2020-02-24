package com.zpj.widget;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.zpj.utils.ScreenUtils;


/**

 * 圆角FrameLayout

 *

 * @author ybao

 */

public class RoundFrameLayout extends FrameLayout {

    private static final int SHADOW_COLOR = Color.parseColor("#40cccccc");

    private static ShadowDrawable drawable;

    public RoundFrameLayout(Context context) {

        this(context, null);

    }

    public RoundFrameLayout(Context context, AttributeSet attrs) {

        this(context, attrs, 0);

    }

    public RoundFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (drawable == null) {
            int dp = ScreenUtils.dp2pxInt(context, 8);
            drawable = new ShadowDrawable.Builder()
                    .setBgColor(Color.WHITE)
                    .setShapeRadius(dp)
                    .setShadowColor(SHADOW_COLOR)
                    .setShadowRadius(dp)
                    .setOffsetX(0)
                    .setOffsetY(0)
                    .builder();
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(this, drawable);
    }

}
