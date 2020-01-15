package it.liuting.imagetrans;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by liuting on 18/3/13.
 */

class ThumbConfig {

    final RectF imageRectF = new RectF();
    ScaleType scaleType = ScaleType.CENTER_CROP;
    WeakReference<Drawable> thumbnailWeakRefe;

    ThumbConfig(@Nullable View view, Resources resources, ScaleType scaleType) {
        this.scaleType = scaleType;
        int screenWidth = resources.getDisplayMetrics().widthPixels;
        int screenHeight = resources.getDisplayMetrics().heightPixels;
        Rect rect = new Rect();
        if (view == null) {
            //如果view为空,则定义从中心点放大图片
            rect.left = (int) (screenWidth * .5f);
            rect.right = (int) (screenWidth * .5f);
            rect.top = (int) (screenHeight * .5f);
            rect.bottom = (int) (screenHeight * .5f);
            imageRectF.set(rect);
            return;
        }
        int[] a = new int[2];
        view.getLocationInWindow(a);
        rect.left = a[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rect.top = a[1];
        } else {
            rect.top = a[1] - getStatesBarHeight(view.getContext());
        }
        rect.right = rect.left + view.getWidth();
        rect.bottom = rect.top + view.getHeight();
        imageRectF.set(rect);
        if (view instanceof ImageView) {
            thumbnailWeakRefe = new WeakReference<>(((ImageView) view).getDrawable());
        }
    }

    static int getStatesBarHeight(Context context) {
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height",
                        "dimen", "android");
        int cStatusHeight = 0;
        if (resourceId > 0) {
            cStatusHeight = context.getResources()
                    .getDimensionPixelSize(resourceId);
        }
        return cStatusHeight;
    }
}
