package com.zxy.skin.sdk.applicator;


import android.content.res.TypedArray;
import android.graphics.Color;
import android.widget.ImageView;

public class SkinImageViewApplicator extends SkinViewApplicator {

    private static final String TAG = "SkinImageViewApplicator";

    public SkinImageViewApplicator() {
        //super必须调用
        super();
        addAttributeApplicator("tint", new IAttributeApplicator<ImageView>(){

            @Override
            public void onApply(ImageView view, TypedArray typedArray, int typedArrayIndex) {
                view.setColorFilter(typedArray.getColor(typedArrayIndex, Color.TRANSPARENT));
//                Drawable drawable = view.getDrawable();
//                if (drawable == null) {
//                    return;
//                }
//                final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
//                DrawableCompat.setTintList(wrappedDrawable, typedArray.getColorStateList(typedArrayIndex));
//                view.setImageDrawable(drawable);
            }
        });

    }

}


