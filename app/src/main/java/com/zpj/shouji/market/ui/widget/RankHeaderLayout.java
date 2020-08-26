package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.utils.ScreenUtils;

public class RankHeaderLayout extends FrameLayout {

    private TextView tvTitle;
    private TextView tvInfo;
    private ImageView ivIcon;
    private ImageView ivBg;

    public RankHeaderLayout(@NonNull Context context) {
        this(context, null);
    }

    public RankHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RankHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RankHeaderLayout);
        int rankNum = a.getInt(R.styleable.RankHeaderLayout_rank_num, 3);
        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.layout_rank_header, this, true);
        tvTitle = findViewById(R.id.tv_title);
        tvInfo = findViewById(R.id.tv_info);
        ivIcon = findViewById(R.id.iv_icon);
        ivBg = findViewById(R.id.iv_bg);
        TextView tvRank = findViewById(R.id.tv_rank);
        tvRank.setText(String.valueOf(rankNum));
        tvRank.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) Math.sqrt((4 - rankNum)) * 48);
        Space space = findViewById(R.id.space);
        int height = ScreenUtils.dp2pxInt(context, (float) (Math.pow(2, 3 - rankNum) * 16 - 8));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        space.setLayoutParams(params);

    }

    public void loadApp(AppInfo appInfo) {
        tvTitle.setText(appInfo.getAppTitle());
        tvInfo.setText(appInfo.getAppSize());
        Glide.with(getContext()).load(appInfo.getAppIcon()).into(ivIcon);
        Glide.with(getContext())
                .asDrawable()
                .load(appInfo.getAppIcon())
                .apply(RequestOptions.bitmapTransform(new CropBlurTransformation(25, 0.3f)))
                .into(ivBg);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailFragment.start(appInfo);
            }
        });
    }

}
