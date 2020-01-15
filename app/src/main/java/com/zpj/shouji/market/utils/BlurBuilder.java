package com.zpj.shouji.market.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class BlurBuilder {

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    private static Bitmap tab_bg = null;
    private static Bitmap overlay = null;
    private static boolean blurFlag = false;

    public static boolean isBlurFlag() {
        return BlurBuilder.blurFlag;
    }

    public static void setBlurFlag(boolean blurFlag) {
        BlurBuilder.blurFlag = blurFlag;
    }

    public static Bitmap blur(View v) {
        if (tab_bg == null) {
            Log.i("", "tab_bg == null");
            blurFlag = false;
            return null;
        }
        blurFlag = true;
        blur(v.getContext(), tab_bg);
        return overlay;
    }

    public static void blur(Context ctx, Bitmap image) {
        if (overlay != null) {
            recycle();
        }
        try {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            overlay = Bitmap.createScaledBitmap(image, (int) (width),
                    (int) (height), false);

            overlay = FastBlur.doBlur(overlay, (int) BLUR_RADIUS, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap blur(Bitmap image) {
        try {
            image = Bitmap.createBitmap(image, image.getWidth() / 4, image.getHeight() / 4, image.getWidth() / 2, image.getHeight() / 2);
            int width = Math.round(image.getWidth() * BITMAP_SCALE / 2);
            int height = Math.round(image.getHeight() * BITMAP_SCALE / 2);

            Bitmap overlay = Bitmap.createScaledBitmap(image, width,
                    height, false);

            overlay = FastBlur.doBlur(overlay, (int) BLUR_RADIUS, true);
            return overlay;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable blur(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return new BitmapDrawable(null, blur(bitmap));
//        return blur(bitmap);
    }

    public static void getScreenshot(View v) {
        if (tab_bg != null) {
            recycle();
        }
        try {
            tab_bg = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Bitmap.Config.RGB_565);
            Canvas c = new Canvas(tab_bg);
            c.translate(-v.getScrollX(), -v.getScrollY());
            v.draw(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static void snapShotWithoutStatusBar(Activity activity) {
        if (tab_bg != null) {
            recycle();
        }
        View view = activity.getWindow().getDecorView();
        getScreenshot(view);
//        try {
//            view.setDrawingCacheEnabled(true);
//            view.buildDrawingCache();
//            tab_bg = view.getDrawingCache();
//            Rect frame = new Rect();
//            activity.getWindow().getDecorView()
//                    .getWindowVisibleDisplayFrame(frame);
//            int statusBarHeight = frame.top;
//
//            int width = ScreenUtils.getScreenWidth(activity);
//            int height = ScreenUtils.getScreenHeight(activity);
//            tab_bg = Bitmap.createBitmap(tab_bg, 0, statusBarHeight, width,
//                    height - statusBarHeight);
//            view.destroyDrawingCache();
//        } catch (Exception e) {
//            e.printStackTrace();
//            getScreenshot(view);
//        }
    }

    public static void recycle() {

        try {
            if (tab_bg != null) {
                tab_bg.recycle();
                System.gc();
                tab_bg = null;
            }
            if (overlay != null) {
                overlay.recycle();
                System.gc();
                overlay = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
