package com.zpj.shouji.market.glide.apk;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.AppUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ApkIconFetcher implements DataFetcher<InputStream> {
    private InstalledAppInfo installedAppInfo;
    private Context context;

    public ApkIconFetcher(Context context, InstalledAppInfo installedAppInfo){
        this.context = context;
        this.installedAppInfo = installedAppInfo;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
//        try {
//            Drawable d = null;
//            if (installedAppInfo.isTempInstalled()) {
//                d = AppUtil.getAppIcon(context, installedAppInfo.getPackageName());
//            } else if (installedAppInfo.isTempXPK()){
//                d = AppUtil.readApkIcon(context, installedAppInfo.getApkFilePath());
//            }
//            if (d == null){
//                callback.onLoadFailed(new Exception("Not Support!"));
//                return;
//            }
//
//            Bitmap iconBitmap;
//            if (d instanceof BitmapDrawable) {
//                iconBitmap = ((BitmapDrawable) d).getBitmap();
//            } else {
//                iconBitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(),
//                        d.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
//                Canvas canvas = new Canvas(iconBitmap);
//                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                d.draw(canvas);
//            }
//            InputStream inputStream = bitmap2InputStream(iconBitmap);
//            callback.onDataReady(inputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//            callback.onLoadFailed(e);
//        }
        Observable.create(
                (ObservableOnSubscribe<InputStream>) emitter -> {
                    Drawable d = null;
                    if (installedAppInfo.isTempInstalled()) {
                        d = AppUtil.getAppIcon(context, installedAppInfo.getPackageName());
                    } else if (installedAppInfo.isTempXPK()){
                        d = AppUtil.readApkIcon(context, installedAppInfo.getApkFilePath());
                    }
                    if (d == null){
                        callback.onLoadFailed(new Exception("Not Support!"));
                        return;
                    }

                    Bitmap iconBitmap;
                    if (d instanceof BitmapDrawable) {
                        iconBitmap = ((BitmapDrawable) d).getBitmap();
                    } else {
                        iconBitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(),
                                d.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                        Canvas canvas = new Canvas(iconBitmap);
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        d.draw(canvas);
                    }
                    emitter.onNext(bitmap2InputStream(iconBitmap));
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(InputStream inputStream) {
                        callback.onDataReady(inputStream);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        callback.onLoadFailed(new Exception(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    // 将Bitmap转换成InputStream
    private InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
