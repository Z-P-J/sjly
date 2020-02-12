package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;

/**
 * @author Z-P-J
 * @date 2019/5/17 16:14
 */
public class DetailLayout extends LinearLayout {

    private TextView detailTitle;
    private TextView detailContent;

    public DetailLayout(Context context) {
        super(context);
        init(context, null);
    }

    public DetailLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DetailLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_detail, this, true);
        if (attrs != null) {
            detailTitle = view.findViewById(R.id.detail_title);
            detailContent = view.findViewById(R.id.detail_content);
            detailContent.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    Clipboard.with().setText(detailContent.getText().toString());
                    return true;
                }
            });
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DetailLayout);
            String leftText = typedArray.getString(R.styleable.DetailLayout_left_text);
            String rightText = typedArray.getString(R.styleable.DetailLayout_right_text);
            int rightTextMaxems = typedArray.getInt(R.styleable.DetailLayout_right_text_maxEms, 10);
            int rightTextMaxlines = typedArray.getInt(R.styleable.DetailLayout_right_text_maxLines, 3);
//            int right_text_lineSpacingExtra = typedArray.getInt(R.styleable.DetailLayout_right_text_lineSpacingExtra, 5);
            int rightTextColor = typedArray.getColor(R.styleable.DetailLayout_right_text_color, Color.BLACK);

            typedArray.recycle();
            detailTitle.setText(leftText + ":");
            detailContent.setText(rightText);
            detailContent.setMaxEms(rightTextMaxems);
            detailContent.setMaxLines(rightTextMaxlines);
            detailContent.setTextColor(rightTextColor);
            detailContent.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    public void setTitle(String title) {
        detailTitle.setText(title + ":");
    }

    public void setContent(String content) {
        if (content == null) {
            content = "无";
        }
        detailContent.setText(content);
    }

    public TextView getContentTextView() {
        return detailContent;
    }

    public TextView getTitleTextView() {
        return detailTitle;
    }
}
