package com.zpj.sjly.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.sjly.R;

public class AppFilterLayout extends LinearLayout {

    private static final int[] ids = {R.id.item_0, R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4};
    private OnItemClickListener onItemClickListener;

    public AppFilterLayout(Context context) {
        super(context);
        init(context, null);
    }

    public AppFilterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AppFilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_app_filter, this, true);

        for (int id : ids) {
            TextView item = view.findViewById(id);
            item.setOnClickListener(v -> {
                if (onItemClickListener != null) onItemClickListener.onItemClick(v, item.getText().toString());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String title);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
