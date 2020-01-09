package com.zpj.market.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.market.R;

public class AppSortLayout extends LinearLayout {

    private static final int[] ids = {R.id.item_0, R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4};
    private OnItemClickListener onItemClickListener;

    private View view;

    public AppSortLayout(Context context) {
        super(context);
        init(context, null);
    }

    public AppSortLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AppSortLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_app_sort, this, true);

        for (int id : ids) {
            TextView item = view.findViewById(id);
            item.setOnClickListener(v -> {
                if (onItemClickListener != null) onItemClickListener.onItemClick(item, item.getText().toString());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TextView view, String title);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void setSelectedItem(int position) {
        TextView item = view.findViewById(ids[position]);
        item.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

}
