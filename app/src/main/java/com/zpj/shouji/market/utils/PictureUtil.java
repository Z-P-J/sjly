package com.zpj.shouji.market.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.nanchen.compresshelper.CompressHelper;
import com.zpj.fragmentation.dialog.enums.ImageType;
import com.zpj.fragmentation.dialog.utils.ImageHeaderParser;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.dialog.ShareDialogFragment;
import com.zpj.toast.ZToast;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class PictureUtil {

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 根据路径获得图片并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 320, 480);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 压缩大图片
     *
     * @param srcPath
     * @return
     */
    public static Bitmap compressSizeImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 800f;//这里设置高度为800f  
        float ww = 480f;//这里设置宽度为480f  
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;
    }

    /**
     * 获取图片文件的信息，是否旋转了90度，如果是则反转
     *
     * @param bitmap 需要旋转的图片
     * @param path   图片的路径
     */
    public static Bitmap reviewPicRotate(Bitmap bitmap, String path) {
        int degree = getPicRotate(path);
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            m.setRotate(degree); // 旋转angle度
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
        }
        return bitmap;
    }

    /**
     * 读取图片文件旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片旋转的角度
     */
    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
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
            byte[] data = new byte[8192];
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

    public static void saveImage(Context context, String url) {
        XPermission.create(context, PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        new HttpObserver<>(
                                emitter -> {
                                    File source = Glide.with(context).asFile().load(url).submit().get();
                                    if (source == null) {
                                        emitter.onError(new Exception("图片下载失败！"));
                                        emitter.onComplete();
                                        return;
                                    }

                                    //1. create path
                                    String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES;
                                    File dirFile = new File(dirPath);
                                    if (!dirFile.exists()) {
                                        dirFile.mkdirs();
                                    }

                                    ImageType type = ImageHeaderParser.getImageType(new FileInputStream(source));
                                    String ext = getFileExt(type);
                                    final File target = new File(dirPath, System.currentTimeMillis() + "." + ext);
                                    if (target.exists()) target.delete();
                                    target.createNewFile();
                                    //2. save
                                    writeFileFromIS(target, new FileInputStream(source));
                                    //3. notify
                                    MediaScannerConnection.scanFile(
                                            context,
                                            new String[]{target.getAbsolutePath()},
                                            new String[]{"image/" + ext},
                                            new MediaScannerConnection.OnScanCompletedListener() {
                                                @Override
                                                public void onScanCompleted(final String path, Uri uri) {
                                                    Observable.empty()
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .doOnComplete(() -> ZToast.success("已保存到相册！"))
                                                            .subscribe();
                                                }
                                            }
                                    );


                                    emitter.onComplete();
                                })
                                .onError(throwable -> {
                                    throwable.printStackTrace();
                                    ZToast.error("保存失败！" + throwable.getMessage());
                                })
                                .subscribe();
                    }

                    @Override
                    public void onDenied() {
                        ZToast.warning("没有保存权限，保存功能无法使用！");
                    }
                }).request();
    }

    public static void shareWebImage(Context context, String url) {
        EventBus.showLoading("获取图片...");
        XPermission.create(context, PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        new HttpObserver<File>(
                                emitter -> {
                                    File source = Glide.with(context).asFile().load(url).submit().get();
                                    if (source == null) {
                                        emitter.onError(new Exception("图片下载失败！"));
                                    } else {
                                        //1. create path
                                        String dirPath = getIconPath(context);
                                        final File target = new File(dirPath, source.getName());
                                        if (target.exists()) {
                                            if (source.length() != target.length()) {
                                                target.delete();
                                                target.createNewFile();
                                                //2. save
                                                writeFileFromIS(target, new FileInputStream(source));
                                            }
                                        } else {
                                            //2. save
                                            writeFileFromIS(target, new FileInputStream(source));
                                        }

                                        emitter.onNext(target);
                                    }
                                    emitter.onComplete();
                                })
                                .onSuccess(new IHttp.OnSuccessListener<File>() {
                                    @Override
                                    public void onSuccess(File data) throws Exception {
                                        EventBus.hideLoading(500, () -> {
                                            new ShareDialogFragment()
                                                    .setShareFile(data)
                                                    .show(context);
                                        });
                                    }
                                })
                                .onError(throwable -> {
                                    EventBus.hideLoading();
                                    throwable.printStackTrace();
                                    ZToast.error("保存失败！" + throwable.getMessage());
                                })
                                .subscribe();
                    }

                    @Override
                    public void onDenied() {
                        EventBus.hideLoading();
                        ZToast.warning("没有保存权限，保存功能无法使用！");
                    }
                })
                .request();
    }

    public static void saveIcon(Context context, String url, String fileName, IHttp.OnSuccessListener<File> listener) {
        new HttpObserver<File>(
                emitter -> {
                    File source = Glide.with(context).asFile().load(url).submit().get();
                    if (source == null) {
                        emitter.onComplete();
                        return;
                    }

                    //1. create path
                    String dirPath = getIconPath(context);

//                    ImageType type = ImageHeaderParser.getImageType(new FileInputStream(source));
                    final File target = new File(dirPath, fileName + ".png");
                    if (target.exists()) target.delete();
                    target.createNewFile();
                    //2. save
                    writeFileFromIS(target, new FileInputStream(source));

                    emitter.onNext(target);
                    emitter.onComplete();
                })
                .onSuccess(listener)
                .subscribe();
    }

    public static void saveIcon(IHttp.OnCompleteListener listener) {
        Context context = ContextUtils.getApplicationContext();
        new HttpObserver<>(
                emitter -> {
                    saveIcon(
                            context,
                            UserManager.getInstance().getMemberInfo().getMemberAvatar(),
                            "user_avatar",
                            R.drawable.ic_user_head
                    );
                    saveIcon(
                            context,
                            UserManager.getInstance().getMemberInfo().getMemberBackGround(),
                            "user_background",
                            R.drawable.bg_member_default
                    );

                    emitter.onComplete();
                })
                .onComplete(listener)
                .subscribe();
    }

    public static void saveDefaultIcon(IHttp.OnCompleteListener listener) {
        Context context = ContextUtils.getApplicationContext();
        new HttpObserver<>(
                emitter -> {
                    saveDefaultIcon(
                            context,
                            R.drawable.ic_user_head,
                            "user_avatar"
                    );
                    saveDefaultIcon(
                            context,
                            R.drawable.bg_member_default,
                            "user_background"
                    );
                    emitter.onComplete();
                })
                .onComplete(listener)
                .subscribe();
    }

    public static void saveDefaultAvatar(IHttp.OnCompleteListener listener) {
        Context context = ContextUtils.getApplicationContext();
        new HttpObserver<>(
                emitter -> {
                    saveDefaultIcon(
                            context,
                            R.drawable.ic_user_head,
                            "user_avatar"
                    );

                    emitter.onComplete();
                })
                .onComplete(listener)
                .subscribe();
    }

    public static void saveDefaultBackground(IHttp.OnCompleteListener listener) {
        Context context = ContextUtils.getApplicationContext();
        new HttpObserver<>(
                emitter -> {
                    saveDefaultIcon(
                            context,
                            R.drawable.bg_member_default,
                            "user_background"
                    );

                    emitter.onComplete();
                })
                .onComplete(listener)
                .subscribe();
    }

    private static void saveDefaultIcon(Context context, int id, String fileName) throws Exception {
        //1. create path
        String dirPath = getIconPath(context);

        final File target = new File(dirPath, fileName + ".png");
        if (target.exists()) target.delete();
        target.createNewFile();
        //2. save
        writeFileFromIS(target, context.getResources().openRawResource(id));
    }

    private static void saveIcon(Context context, String url, String fileName, int res) throws Exception {
        File source = null;
        if (!TextUtils.isEmpty(url)) {
            source = Glide.with(context).asFile().load(url).submit().get();
        }
        InputStream inputStream;
        if (source == null) {
            inputStream = context.getResources().openRawResource(res);
        } else {
            inputStream = new FileInputStream(source);
        }
        //1. create path
        String dirPath = getIconPath(context);

        final File target = new File(dirPath, fileName + ".png");
        if (target.exists()) target.delete();
        target.createNewFile();
        //2. save
        writeFileFromIS(target, inputStream);
    }

    public static void saveAvatar(Uri uri, IHttp.OnSuccessListener<File> listener) {
        saveIcon(uri, "user_avatar", listener);
    }

    public static void saveBackground(Uri uri, IHttp.OnSuccessListener<File> listener) {
        saveIcon(uri, "user_background", listener);
    }

    public static void saveIcon(Uri uri, String fileName, IHttp.OnSuccessListener<File> listener) {
        new HttpObserver<File>(
                emitter -> {
                    File source = new File(uri.getPath());
                    //1. create path
                    String dirPath = getIconPath(ContextUtils.getApplicationContext());

//                    ImageType type = ImageHeaderParser.getImageType(new FileInputStream(source));
                    final File target = new File(dirPath, fileName + ".png");
                    if (target.exists()) target.delete();
                    target.createNewFile();
                    //2. save
                    writeFileFromIS(target, new FileInputStream(source));

                    emitter.onNext(target);
                    emitter.onComplete();
                })
                .onSuccess(listener)
                .subscribe();
    }

    public static void loadAvatar(ImageView imageView) {
        loadIcon(imageView, true);
    }

    public static void loadBackground(ImageView imageView) {
        loadIcon(imageView, false);
    }

    private static void loadIcon(ImageView imageView, boolean isAvatar) {
        Log.d("loadIcon", "isAvatar=" + isAvatar);
        int res = isAvatar ? R.drawable.ic_user_head : R.drawable.bg_member_default;
        if (UserManager.getInstance().isLogin()) {
            String fileName = isAvatar ? "user_avatar" : "user_background";
            File file = new File(getIconPath(imageView.getContext()), fileName + ".png");
            RequestOptions tempOptions = new RequestOptions()
                    .error(res)
                    .placeholder(res)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            if (isAvatar) {
                tempOptions = tempOptions.circleCrop();
            }
            RequestOptions options = tempOptions;
            Log.d("loadIcon", "exists=" + file.exists() + " path=" + file.getPath());
            if (file.exists()) {
                Glide.with(imageView)
                        .load(file)
                        .apply(options)
                        .into(imageView);
            } else {
                String url = isAvatar ?
                        UserManager.getInstance().getMemberInfo().getMemberAvatar()
                        : UserManager.getInstance().getMemberInfo().getMemberBackGround();
                saveIcon(imageView.getContext(),
                        url,
                        fileName,
                        new IHttp.OnSuccessListener<File>() {
                            @Override
                            public void onSuccess(File data) throws Exception {
                                Glide.with(imageView)
                                        .load(file)
                                        .apply(options)
                                        .into(imageView);
                            }
                        });
            }
        } else {
            imageView.setImageResource(res);
        }
    }

    public static String getIconPath(Context context) {
        String path = getCachePath(context) + File.separator + "icon";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return path;
    }

    public static String getCachePath(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                cachePath = context.getExternalCacheDir().getParentFile().getAbsolutePath() + File.separator;
            }
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getParentFile().getAbsolutePath() + File.separator;
        }
        if (TextUtils.isEmpty(cachePath)) {
            cachePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/" + context.getPackageName();
        }
        File dirFile = new File(cachePath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        Log.d("getCachePath", "cachePath=" + cachePath);
        return cachePath;
    }

    public static void setWallpaper(Context context, String url) {
        EventBus.showLoading("图片准备中...");
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        HideLoadingEvent.post();
                        EventBus.hideLoading();
                        Observable.timer(250 , TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(() -> {
                                    try {
                                        WallpaperManager wpm = (WallpaperManager) context.getSystemService(
                                                Context.WALLPAPER_SERVICE);
                                        wpm.setBitmap(resource);
                                        ZToast.success("设置壁纸成功！");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ZToast.error("设置壁纸失败！" + e.getMessage());
                                    }
                                })
                                .subscribe();
                    }
                });
    }

    public static File compressImage(Context context, File file) throws IOException {
        int max = ScreenUtils.getScreenHeight(context);
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        boolean isJpg = ".jpg".equalsIgnoreCase(suffix);

        File newFile = new CompressHelper.Builder(context)
                .setMaxWidth(max)  // 默认最大宽度为720
                .setMaxHeight(max) // 默认最大高度为960
                .setQuality(80)    // 默认压缩质量为80
                .setFileName(file.getName()) // 设置你需要修改的文件名
                .setCompressFormat(isJpg ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG) // 设置默认压缩为jpg格式
                .setDestinationDirectoryPath(FileUtils.getCachePath(context) + File.separator + "compress")
                .build()
                .compressToFile(file);
        return newFile.length() < file.length() ? newFile : file;
    }


    public static void saveResource(Context context, int res, String fileName) {
        new HttpObserver<>(
                emitter -> {
                    InputStream inputStream = context.getResources().openRawResource(res);

                    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), res);

                    //1. create path
                    String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES;
                    File dirFile = new File(dirPath);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }

                    ImageType type = ImageHeaderParser.getImageType(inputStream);
                    String ext = getFileExt(type);
                    String name = fileName + "." + ext;
                    final File target = new File(dirPath, name);
                    if (target.exists()) target.delete();
                    target.createNewFile();


                    FileOutputStream fos = new FileOutputStream(target);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

//                    MediaStore.Images.Media.insertImage(context.getContentResolver(),  target.getAbsolutePath(), name, null);

                    //2. save
//                    writeFileFromIS(target, inputStream);
                    //3. notify
                    MediaScannerConnection.scanFile(
                            context,
                            new String[]{target.getAbsolutePath()},
                            new String[]{"image/" + ext},
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(final String path, Uri uri) {
                                    Observable.empty()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnComplete(() -> ZToast.success("已保存到相册！"))
                                            .subscribe();
                                }
                            }
                    );


                    emitter.onComplete();
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    ZToast.error("保存失败！" + throwable.getMessage());
                })
                .subscribe();
    }

}
