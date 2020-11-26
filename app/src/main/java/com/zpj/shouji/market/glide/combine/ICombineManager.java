package com.zpj.shouji.market.glide.combine;

import android.graphics.Bitmap;

import java.util.List;

public interface ICombineManager {
    Bitmap onCombine(int size, int gap, int gapColor, List<Bitmap> bitmaps);
}
