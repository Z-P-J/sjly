package com.zpj.shouji.market.ui.widget.flowlayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.DotSpan;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlowLayout extends RecyclerView implements IEasy.OnBindViewHolderListener<String> {

    private final List<String> list = new ArrayList<>();
    private final EasyRecyclerView<String> recyclerView;
    private OnItemClickListener onItemClickListener;

    private int selectedPosition = -1;

    private int dp8;

    public FlowLayout(@NonNull Context context) {
        this(context, null);
    }

    public FlowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dp8 = ScreenUtils.dp2pxInt(context, 8);
        if (getPaddingStart() == 0) {
            setPadding(dp8, dp8, dp8, dp8);
        }
        recyclerView = new EasyRecyclerView<>(this);
        recyclerView.setData(list)
                .setItemRes(R.layout.item_wallpaper_tag)
                .setLayoutManager(new FlowLayoutManager())
                .addItemDecoration(new SpaceItemDecoration(dp8))
                .onBindViewHolder(this)
                .onItemClick((holder, view, data) -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(holder.getAdapterPosition(), view, data);
                    }
                })
                .build();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
        TextView tvText = holder.getView(R.id.tv_text);
        int randomColor = Color.rgb(new Random().nextInt(255),
                new Random().nextInt(255),
                new Random().nextInt(255));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(100);
        DotSpan span = new DotSpan(dp8, randomColor);
        if (position != selectedPosition) {
            drawable.setStroke(1, getResources().getColor(R.color.color_text_minor));
            randomColor = Color.WHITE;
            drawable.setColor(randomColor);
            drawable.setAlpha(0xff);
            tvText.setTextColor(getResources().getColor(R.color.color_text_minor));
        } else {
            drawable.setAlpha(0x20);
            tvText.setTextColor(randomColor);
        }
        drawable.setColor(randomColor);

        tvText.setBackground(drawable);

        SpannableString spannableString = new SpannableString(list.get(position));
        spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvText.setText(spannableString);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public void addItems(List<String> items) {
        list.addAll(items);
        recyclerView.notifyDataSetChanged();
    }

    public void addItem(String item) {
        list.add(item);
        recyclerView.notifyItemInserted(list.size() - 1);
    }

    public interface OnItemClickListener {
        void onClick(int index, View v, String text);
    }

}
