package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.model.AppInfo;

import java.util.List;

public class UpdateRecommendCard extends AppInfoRecommendCard {

    public UpdateRecommendCard(Context context) {
        this(context, null);
    }

    public UpdateRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public String getKey() {
        return HttpPreLoader.HOME_RECENT;
    }

}
