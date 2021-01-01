package com.zpj.shouji.market.glide.combine;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CombineImageFetcher implements DataFetcher<InputStream> {

    private final CombineImage combineImage;
    private Context context;

    public CombineImageFetcher(Context context, CombineImage combineImage) {
        this.context = context;
        this.combineImage = combineImage;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        Observable.create(
                (ObservableOnSubscribe<InputStream>) emitter -> {
                    List<Bitmap> bitmaps = new ArrayList<>();
                    for (String url : combineImage.getUrls()) {
//                            File file = Glide.with(context).downloadOnly().load(url).submit().get();
//                            Bitmap bitmap = Glide.with(context).asBitmap().load(file).submit().get();
                        Bitmap bitmap = Glide.with(context).asBitmap().load(url).submit().get();
                        bitmaps.add(bitmap);
                    }
                    if (combineImage.getCombineManager() == null) {
                        combineImage.setCombineManager(new DingCombineManager());
                    }
                    Bitmap bitmap = combineImage.getCombineManager().onCombine(combineImage.getSize(), combineImage.getGap(), combineImage.getGapColor(), bitmaps);
                    emitter.onNext(bitmap2InputStream(bitmap));
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
