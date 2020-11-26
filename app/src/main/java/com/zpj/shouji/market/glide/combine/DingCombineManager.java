package com.zpj.shouji.market.glide.combine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.List;

/**
 * @author Z-P-J
 * 仿钉钉组合头像
 */
public class DingCombineManager implements ICombineManager {

    @Override
    public Bitmap onCombine(int size, int gap, int gapColor, List<Bitmap> bitmaps) {
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        if (gapColor == 0) {
            gapColor = Color.WHITE;
        }
        canvas.drawColor(gapColor);

        int count = bitmaps.size();
        Bitmap subBitmap;

        int[][] dxy = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};

        for (int i = 0; i < count; i++) {
            Bitmap bitmap = bitmaps.get(i);
            if (bitmap == null) {
                continue;
            }
            subBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
            if (count == 2 || (count == 3 && i == 0)) {
                subBitmap = Bitmap.createBitmap(subBitmap, (size + gap) / 4, 0, (size - gap) / 2, size);
            } else if ((count == 3 && (i == 1 || i == 2)) || count == 4) {
                subBitmap = Bitmap.createBitmap(subBitmap, (size + gap) / 4, (size + gap) / 4, (size - gap) / 2, (size - gap) / 2);
            }

            int dx = dxy[i][0];
            int dy = dxy[i][1];

            canvas.drawBitmap(subBitmap, dx * (size + gap) / 2.0f, dy * (size + gap) / 2.0f, null);
        }
        return result;
    }

}
