package com.zpj.shouji.market.glide.combine;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.utils.AppUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CombineImageFetcher implements DataFetcher<InputStream> {

    private final CombineImage combineImage;
    private Context context;

    public CombineImageFetcher(Context context, CombineImage combineImage){
        this.context = context;
        this.combineImage = combineImage;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        try {
            List<Bitmap> bitmaps = new ArrayList<>();
            for (String url : combineImage.getUrls()) {
                Bitmap bitmap = Glide.with(context).asBitmap().load(url).submit().get();
                bitmaps.add(bitmap);
            }

//            int count = combineImage.getUrls().size();
//            Bitmap[] bitmaps = new Bitmap[count];
//            for (int i = 0; i < count; i++) {
//                String url = combineImage.getUrls().get(i);
//                Bitmap bitmap = Glide.with(context).asBitmap().load(url).submit().get();
//                bitmaps[i] = bitmap;
//            }

            if (combineImage.getCombineManager() == null) {
                combineImage.setCombineManager(new DingCombineManager());
            }
            Bitmap bitmap = combineImage.getCombineManager().onCombine(combineImage.getSize(), combineImage.getGap(), combineImage.getGapColor(), bitmaps);
//            Bitmap bitmap = combineBitmap(combineImage.getSize(), combineImage.getGap(), combineImage.getGapColor(), bitmaps);
            InputStream inputStream = bitmap2InputStream(bitmap); // iconBitmap
            callback.onDataReady(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onLoadFailed(e);
        }
    }

//    public Bitmap combineBitmap(int size, int gap, int gapColor, List<Bitmap> bitmaps) {
//        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(result);
//        if (gapColor == 0) {
//            gapColor = Color.WHITE;
//        }
//        canvas.drawColor(gapColor);
//
//        int count = bitmaps.size();
//        Bitmap subBitmap;
//
//        int[][] dxy = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
//
//        for (int i = 0; i < count; i++) {
//            Bitmap bitmap = bitmaps.get(i);
//            if (bitmap == null) {
//                continue;
//            }
//            subBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
//            if (count == 2 || (count == 3 && i == 0)) {
//                subBitmap = Bitmap.createBitmap(subBitmap, (size + gap) / 4, 0, (size - gap) / 2, size);
//            } else if ((count == 3 && (i == 1 || i == 2)) || count == 4) {
//                subBitmap = Bitmap.createBitmap(subBitmap, (size + gap) / 4, (size + gap) / 4, (size - gap) / 2, (size - gap) / 2);
//            }
//
//            int dx = dxy[i][0];
//            int dy = dxy[i][1];
//
//            canvas.drawBitmap(subBitmap, dx * (size + gap) / 2.0f, dy * (size + gap) / 2.0f, null);
//        }
//        return result;
//    }

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
