package com.zpj.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RSInvalidStateException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * 参考https://github.com/goweii/Blurred
 */
public final class GaussianBlur implements IBlur {
//    private static GaussianBlur INSTANCE = null;

    private boolean mRealTimeMode = false;

    private final RenderScript renderScript;
    private final ScriptIntrinsicBlur gaussianBlur;

    private Allocation mInput = null;
    private Allocation mOutput = null;

    GaussianBlur(Context context) {
        Utils.requireNonNull(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            throw new RuntimeException("Call requires API level " + Build.VERSION_CODES.JELLY_BEAN_MR1 + " (current min is " + Build.VERSION.SDK_INT + ")");
        }
        renderScript = RenderScript.create(context.getApplicationContext());
        gaussianBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
    }

//    public static GaussianBlur get(Context context) {
//        if (INSTANCE == null) {
//            synchronized (GaussianBlur.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new GaussianBlur(context);
//                }
//            }
//        }
//        return INSTANCE;
//    }

    public GaussianBlur realTimeMode(boolean realTimeMode) {
        mRealTimeMode = realTimeMode;
        if (!mRealTimeMode) {
            destroyAllocations();
        }
//        BitmapProcessor.get().realTimeMode(realTimeMode);
        return this;
    }

    /**
     * RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
     * 模糊
     * 采用系统自带的RenderScript
     * 输出图与原图参数相同
     *
     * @param originalBitmap 原图
     * @param scale          缩放因子（>=1）
     * @param radius         模糊半径
     * @return 模糊Bitmap
     */
    @Override
    public Bitmap process(final BitmapProcessor processor,
                          final Bitmap originalBitmap,
                          final float radius,
                          final float scale,
                          final boolean keepSize,
                          final boolean recycleOriginal) {
        Utils.requireNonNull(originalBitmap, "待模糊Bitmap不能为空");
        float newRadius = radius < 0 ? 0 : radius;
        float newScale = scale <= 0 ? 1 : scale;
        if (newRadius == 0) {
            if (newScale == 1) {
                return originalBitmap;
            }
            Bitmap scaleBitmap = processor.scaleBitmap(originalBitmap, newScale);
            if (recycleOriginal) {
                originalBitmap.recycle();
            }
            return scaleBitmap;
        }
        if (newRadius > 25) {
            newScale = newScale / (newRadius / 25);
            newRadius = 25;
        }
        if (newScale == 1) {
            Bitmap output = blurIn25(originalBitmap, newRadius);
            if (recycleOriginal) {
                originalBitmap.recycle();
            }
            return output;
        }
        final int width = originalBitmap.getWidth();
        final int height = originalBitmap.getHeight();
        Bitmap input = processor.scaleBitmap(originalBitmap, newScale);
        if (recycleOriginal) {
            originalBitmap.recycle();
        }
        Bitmap output = blurIn25(input, newRadius);
        input.recycle();
        if (!keepSize) {
            return output;
        }
        Bitmap outputScaled = processor.scaleBitmap(output, width, height);
        output.recycle();
        return outputScaled;
    }

    @Override
    public void recycle() {
        gaussianBlur.destroy();
        renderScript.destroy();
        destroyAllocations();
//        INSTANCE = null;
    }

    /**
     * RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
     * <p>
     * 高斯模糊
     * 采用系统自带的RenderScript
     * 图像越大耗时越长，测试时1280*680的图片耗时在30~60毫秒
     * 建议在子线程模糊通过Handler回调获取
     *
     * @param input  原图
     * @param radius 模糊半径
     */
    private Bitmap blurIn25(final Bitmap input, final float radius) {
        Utils.requireNonNull(input, "待模糊Bitmap不能为空");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            throw new RuntimeException("Call requires API level " + Build.VERSION_CODES.JELLY_BEAN_MR1 + " (current min is " + Build.VERSION.SDK_INT + ")");
        }
        final float newRadius;
        if (radius < 0) {
            newRadius = 0;
        } else if (radius > 25) {
            newRadius = 25;
        } else {
            newRadius = radius;
        }
        if (mRealTimeMode) {
            tryReuseAllocation(input);
        } else {
            createAllocation(input);
        }
        try {
            gaussianBlur.setRadius(newRadius);
            gaussianBlur.setInput(mInput);
            gaussianBlur.forEach(mOutput);
            Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
            mOutput.copyTo(output);
            return output;
        } finally {
            if (!mRealTimeMode) {
                destroyAllocations();
            }
        }
    }

    private void destroyAllocations() {
        if (mInput != null) {
            try {
                mInput.destroy();
                mInput = null;
            } catch (RSInvalidStateException ignore) {
            }
        }
        if (mOutput != null) {
            try {
                mOutput.destroy();
                mOutput = null;
            } catch (RSInvalidStateException ignore) {
            }
        }
    }

    private void tryReuseAllocation(Bitmap bitmap) {
        if (mInput == null) {
            createAllocation(bitmap);
        }
        if (mInput.getType().getX() != bitmap.getWidth() || mInput.getType().getY() != bitmap.getHeight()) {
            createAllocation(bitmap);
        }
        try {
            mInput.copyFrom(bitmap);
        } catch (RSIllegalArgumentException ignore) {
            destroyAllocations();
            createAllocation(bitmap);
        }
    }

    private void createAllocation(Bitmap bitmap) {
        destroyAllocations();
        mInput = Allocation.createFromBitmap(renderScript, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mOutput = Allocation.createTyped(renderScript, mInput.getType());
    }
}
