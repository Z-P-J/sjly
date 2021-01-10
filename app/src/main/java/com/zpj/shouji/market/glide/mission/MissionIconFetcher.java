package com.zpj.shouji.market.glide.mission;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.zpj.rxlife.RxLife;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.utils.AppUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MissionIconFetcher implements DataFetcher<InputStream> {

    private final AppDownloadMission downloadMission;
    private final Context context;

    public MissionIconFetcher(Context context, AppDownloadMission downloadMission){
        this.context = context;
        this.downloadMission = downloadMission;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        Observable.create(
                (ObservableOnSubscribe<InputStream>) emitter -> {
                    Drawable d = null;
                    if (!TextUtils.isEmpty(downloadMission.getAppIcon())) {
                        d = Glide.with(context).asDrawable().load(downloadMission.getAppIcon()).submit().get();
                    } else if (downloadMission.isFinished()) {
                        d = AppUtils.getApkIcon(context, downloadMission.getFilePath());
                    } else if (downloadMission.isInstalled()) {
                        d = AppUtils.getAppIcon(context, downloadMission.getPackageName());
                    }
                    if (d == null) {
                        d = context.getResources().getDrawable(R.drawable.ic_file_apk);
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
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull InputStream inputStream) {
                        callback.onDataReady(inputStream);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        callback.onLoadFailed(new Exception(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    
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
