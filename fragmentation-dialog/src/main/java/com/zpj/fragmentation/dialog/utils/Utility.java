package com.zpj.fragmentation.dialog.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zpj.fragmentation.dialog.enums.ImageType;
import com.zpj.fragmentation.dialog.interfaces.IImageLoader;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Description:
 * Create by lxj, at 2018/12/7
 */
public class Utility {

    public static void applyPopupSize(ViewGroup content, int maxWidth, int maxHeight) {
        applyPopupSize(content, maxWidth, maxHeight, null);
    }

    public static void applyPopupSize(final ViewGroup content, final int maxWidth, final int maxHeight, final Runnable afterApplySize) {
        content.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
                View implView = content.getChildAt(0);
                ViewGroup.LayoutParams implParams = implView.getLayoutParams();
                // 假设默认Content宽是match，高是wrap
                int w = content.getMeasuredWidth();
                // response impl view wrap_content params.
                if (implParams.width == FrameLayout.LayoutParams.WRAP_CONTENT) {
//                    w = Math.min(w, implView.getMeasuredWidth());
                }
                if (maxWidth != 0) {
                    params.width = Math.min(w, maxWidth);
                }

                int h = content.getMeasuredHeight();
                // response impl view match_parent params.
                if (implParams.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                    h = ((ViewGroup) content.getParent()).getMeasuredHeight();
                    params.height = h;
                }
                if (maxHeight != 0) {
                    // 如果content的高为match，则maxHeight限制impl
                    if (params.height == FrameLayout.LayoutParams.MATCH_PARENT ||
                            params.height == (ScreenUtils.getScreenHeight(content.getContext()) + StatusBarUtils.getStatusBarHeight(content.getContext()))) {
                        implParams.height = Math.min(implView.getMeasuredHeight(), maxHeight);
                        implView.setLayoutParams(implParams);
                    } else {
                        params.height = Math.min(h, maxHeight);
                    }
//                    int margin = getWindowHeight(content.getContext()) - maxHeight - XPopupUtils.getStatusBarHeight();
//                    params.topMargin = XPopupUtils.getStatusBarHeight() + margin / 2;
//                    params.bottomMargin = margin / 2;
                }
                content.setLayoutParams(params);

                if (afterApplySize != null) {
                    afterApplySize.run();
                }
            }
        });
    }

    public static void setCursorDrawableColor(EditText et, int color) {
        //暂时没有找到有效的方法来动态设置cursor的颜色
    }

    public static BitmapDrawable createBitmapDrawable(Resources resources, int width, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, 20, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, 0, bitmap.getWidth(), 4, paint);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 4, bitmap.getWidth(), 20, paint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
        bitmapDrawable.setGravity(Gravity.BOTTOM);
        return bitmapDrawable;
    }

    public static StateListDrawable createSelector(Drawable defaultDrawable, Drawable focusDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, focusDrawable);
        stateListDrawable.addState(new int[]{}, defaultDrawable);
        return stateListDrawable;
    }

    public static <T> void saveBmpToAlbum(final Context context, final IImageLoader<T> imageLoader, final T uri) {
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Context mContext = context;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                File source = imageLoader.getImageFile(mContext, uri);
                if (source == null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "图片不存在！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                //1. create path
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES;
                File dirFile = new File(dirPath);
                if (!dirFile.exists()) dirFile.mkdirs();
                try {
                    ImageType type = ImageHeaderParser.getImageType(new FileInputStream(source));
                    String ext = getFileExt(type);
                    final File target = new File(dirPath, System.currentTimeMillis() + "." + ext);
                    if (target.exists()) target.delete();
                    target.createNewFile();
                    //2. save
                    writeFileFromIS(target, new FileInputStream(source));
                    //3. notify
                    MediaScannerConnection.scanFile(mContext, new String[]{target.getAbsolutePath()},
                            new String[]{"image/" + ext}, new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(final String path, Uri uri) {
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "已保存到相册！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "没有保存权限，保存功能无法使用！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public static <T> void saveBmpToAlbum(final Context context, final File file) {
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Context mContext = context;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (file == null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "图片不存在！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                //1. create path
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES;
                File dirFile = new File(dirPath);
                if (!dirFile.exists()) dirFile.mkdirs();
                try {
                    ImageType type = ImageHeaderParser.getImageType(new FileInputStream(file));
                    String ext = getFileExt(type);
                    final File target = new File(dirPath, System.currentTimeMillis() + "." + ext);
                    if (target.exists()) target.delete();
                    target.createNewFile();
                    //2. save
                    writeFileFromIS(target, new FileInputStream(file));
                    //3. notify
                    MediaScannerConnection.scanFile(mContext, new String[]{target.getAbsolutePath()},
                            new String[]{"image/" + ext}, new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(final String path, Uri uri) {
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "已保存到相册！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "没有保存权限，保存功能无法使用！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private static String getFileExt(ImageType type) {
        switch (type) {
            case GIF:
                return "gif";
            case PNG:
            case PNG_A:
                return "png";
            case WEBP:
            case WEBP_A:
                return "webp";
            case JPEG:
                return "jpeg";
        }
        return "jpeg";
    }

    private static boolean writeFileFromIS(final File file, final InputStream is) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[8192];
            int len;
            while ((len = is.read(data, 0, 8192)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
