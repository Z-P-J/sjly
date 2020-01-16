package com.zpj.shouji.market.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;

public class WallpaperSortLayout extends LinearLayout {

    private static final int[] ids = {R.id.item_0, R.id.item_1, R.id.item_2};
    private OnItemClickListener onItemClickListener;

    private View view;

    public WallpaperSortLayout(Context context) {
        super(context);
        init(context, null);
    }

    public WallpaperSortLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WallpaperSortLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_wallpaper_sort, this, true);

        int i = 0;
        for (int id : ids) {
            TextView item = view.findViewById(id);
            int position = i;
            item.setOnClickListener(v -> {
                if (onItemClickListener != null) onItemClickListener.onItemClick(item, item.getText().toString(), position);
            });
            i++;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TextView view, String title, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedItem(int position) {
        TextView item = view.findViewById(ids[position]);
        item.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

}
