package com.zxy.skin.sdk.applicator;


import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.widget.TextView;

public class SkinCardViewApplicator extends SkinViewApplicator {

    private static final String TAG = "SkinCardViewApplicator";

    public SkinCardViewApplicator() {
        //super必须调用
        super();
        addAttributeApplicator("cardBackgroundColor", new IAttributeApplicator<CardView>(){

            @Override
            public void onApply(CardView view, TypedArray typedArray, int typedArrayIndex) {
                view.setCardBackgroundColor(typedArray.getColorStateList(typedArrayIndex));
            }
        });

    }

}


