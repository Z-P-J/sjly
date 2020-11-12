package com.zpj.blur;

import android.graphics.Bitmap;
import android.view.View;

/**
 * @author CuiZhen
 * @date 2019/8/11
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class DefaultSnapshotInterceptor implements ZBlurry.SnapshotInterceptor {
    @Override
    public Bitmap snapshot(BitmapProcessor processor, View from, int backgroundColor, int foregroundColor, float scale, boolean antiAlias) {
        return processor.snapshot(from, backgroundColor, foregroundColor, scale, antiAlias);
    }
}
