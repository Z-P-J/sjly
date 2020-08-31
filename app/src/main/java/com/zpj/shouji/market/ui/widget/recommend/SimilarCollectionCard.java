package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;

import com.zpj.shouji.market.model.CollectionInfo;

import java.util.List;

public class SimilarCollectionCard extends CollectionRecommendCard {

    public SimilarCollectionCard(Context context) {
        this(context, null);
    }

    public SimilarCollectionCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimilarCollectionCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("相关应用集");
    }

    @Override
    public void setData(List<CollectionInfo> data) {
        list.clear();
        list.addAll(data);
        if (list.size() % 2 != 0) {
            list.remove(list.size() - 1);
        }
        recyclerView.notifyDataSetChanged();
    }

}
