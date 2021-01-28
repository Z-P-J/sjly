package com.zpj.shouji.market.ui.widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.toast.ZToast;
import com.zxy.skin.sdk.SkinEngine;

public class InfoCardView extends CardView {

    private final TextView tvTitle;
    private final TextView tvContent;
    private final ImageView ivIcon;

    public InfoCardView(@NonNull Context context) {
        this(context, null);
    }

    public InfoCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        SkinEngine.applyViewAttr(this, "cardBackgroundColor", R.attr.backgroundColorCard);

        LayoutInflater.from(context).inflate(R.layout.layout_card_info, this, true);
        tvTitle = findViewById(R.id._card_tv_title);
        tvContent = findViewById(R.id._card_tv_content);
        ivIcon = findViewById(R.id._card_iv_icon);
        setOnIconClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText(null, tvContent.getText()));
                ZToast.success("已复制到粘贴板");
            }
        });

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InfoCardView);
            tvTitle.setText(ta.getString(R.styleable.InfoCardView_info_card_title));
            tvContent.setText(ta.getString(R.styleable.InfoCardView_info_card_content));
            tvContent.setMaxLines(ta.getInt(R.styleable.InfoCardView_info_card_content_max_lines, 3));
            Drawable rightIcon = ta.getDrawable(R.styleable.InfoCardView_info_card_right_icon);
            if (rightIcon != null) {
                ivIcon.setVisibility(VISIBLE);
                ivIcon.setImageDrawable(rightIcon);
            } else {
                ivIcon.setVisibility(GONE);
            }
            ta.recycle();
        }

    }

    public void setTitle(CharSequence text) {
        tvTitle.setText(text);
    }

    public void setContent(CharSequence text) {
        tvContent.setText(text);
    }

    public void setOnIconClickListener(OnClickListener listener) {
        ivIcon.setOnClickListener(listener);
    }

}
