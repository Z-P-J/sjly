package com.zpj.blur;

import android.graphics.Bitmap;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/2/13
 */
public interface IBlur {
    /**
     * 高斯模糊图片
     *
     * @param processor BitmapProcessor
     * @param originalBitmap  原图
     * @param radius          模糊半径
     * @param scale           缩小因子
     * @param keepSize        缩小后是否再次放大为原图尺寸
     * @param recycleOriginal 回收原图
     * @return 模糊图
     */
    Bitmap process(BitmapProcessor processor,
                   final Bitmap originalBitmap,
                   final float radius,
                   final float scale,
                   final boolean keepSize,
                   final boolean recycleOriginal);

    /**
     * 回收资源
     */
    void recycle();
}
