package com.zpj.shouji.market.ui.widget.flowlayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
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

public class FlowLayout extends RecyclerView implements IEasy.OnBindViewHolderListener<FlowLayout.FlowItem> {

    private final List<FlowItem> list = new ArrayList<>();
    private final EasyRecyclerView<FlowItem> recyclerView;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

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
        int dp12 = ScreenUtils.dp2pxInt(context, 12);
        int dp4 = dp8 / 2;
        if (getPaddingStart() == 0) {
            setPadding(dp12, dp12, dp12, dp12);
        }
        setOverScrollMode(OVER_SCROLL_NEVER);
        recyclerView = new EasyRecyclerView<>(this);
        recyclerView.setData(list)
                .setItemRes(R.layout.item_wallpaper_tag)
                .setLayoutManager(new FlowLayoutManager())
                .addItemDecoration(new ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                        outRect.top = dp4 / 2;
                        outRect.left = dp4;
                        outRect.right = dp4;
                        outRect.bottom = dp4 / 2;
                    }
                })
                .onBindViewHolder(this)
                .onItemClick((holder, view, data) -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(holder.getAdapterPosition(), view, data.text);
                    }
                })
                .onItemLongClick((holder, view, data) -> {
                    if (onItemLongClickListener != null) {
                        return onItemLongClickListener.onLongClick(holder.getAdapterPosition(), view, data.text);
                    }
                    return false;
                })
                .build();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<FlowItem> list, int position, List<Object> payloads) {
        FlowItem item = list.get(position);
        TextView tvText = holder.getView(R.id.tv_text);
        int color;
        GradientDrawable drawable;
        if (item.drawable == null) {
            color = Color.rgb(new Random().nextInt(255),
                    new Random().nextInt(255),
                    new Random().nextInt(255));
            drawable = new GradientDrawable();
            drawable.setCornerRadius(100);
            drawable.setColor(color);
            item.color = color;
            item.drawable = drawable;
        } else {
            color = item.color;
            drawable = item.drawable;
        }

//        new DrawableBuilder().rectangle()
//                .rounded()
//                .strokeWidth(1)
//                .strokeColor(getResources().getColor(R.color.color_text_minor))
//                .build();

        if (position != selectedPosition) {
            drawable.setStroke(1, getResources().getColor(R.color.color_text_minor));
            drawable.setColor(Color.WHITE);
            drawable.setAlpha(0xff);
            tvText.setTextColor(getResources().getColor(R.color.color_text_minor));
        } else {
            drawable.setStroke(0, Color.TRANSPARENT);
            drawable.setAlpha(0x20);
            drawable.setColor(color);
            tvText.setTextColor(color);
        }
        tvText.setBackground(drawable);

//        if (item.drawable == null) {
//            int randomColor = Color.rgb(new Random().nextInt(255),
//                    new Random().nextInt(255),
//                    new Random().nextInt(255));
//            GradientDrawable drawable = new GradientDrawable();
//            drawable.setCornerRadius(100);
//            if (position != selectedPosition) {
//                drawable.setStroke(1, getResources().getColor(R.color.color_text_minor));
//                randomColor = Color.WHITE;
//                drawable.setColor(randomColor);
//                drawable.setAlpha(0xff);
//                tvText.setTextColor(getResources().getColor(R.color.color_text_minor));
//            } else {
//                drawable.setAlpha(0x20);
//                tvText.setTextColor(randomColor);
//            }
//            drawable.setColor(randomColor);
//
//            tvText.setBackground(drawable);
//            item.drawable = drawable;
//            item.color = randomColor;
//        } else {
//            tvText.setBackground(item.drawable);
//        }

        DotSpan span = new DotSpan(dp8, color);
        SpannableString spannableString = new SpannableString(item.text);
        spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvText.setText(spannableString);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        recyclerView.notifyDataSetChanged();
    }

    public void setSpace(int space) {
        RecyclerView view = recyclerView.getRecyclerView();
        for (int i = view.getItemDecorationCount() - 1; i >= 0; i-- ) {
            view.removeItemDecorationAt(i);
        }
        int padding = ScreenUtils.dp2pxInt(getContext(), 16) - space;
        setPadding(padding, padding, padding, padding);
        view.addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                outRect.top = space / 2;
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space / 2;
            }
        });
        recyclerView.notifyDataSetChanged();
    }

    public void addItems(List<String> items) {
        for (String text : items) {
            FlowItem flowItem = new FlowItem();
            flowItem.text = text;
            list.add(flowItem);
        }
//        list.addAll(items);
        recyclerView.notifyDataSetChanged();
    }

    public void addItem(String text) {
        FlowItem flowItem = new FlowItem();
        flowItem.text = text;
        list.add(flowItem);
        recyclerView.notifyItemInserted(list.size() - 1);
    }

    public int count() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public void remove(int index) {
        list.remove(index);
        recyclerView.notifyItemRemoved(index);
    }

    public void remove(String str) {
        remove(str, false);
    }

    public void remove(String str, boolean all) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (str.equals(list.get(i).text)) {
                remove(i);
                if (!all) {
                    return;
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(int index, View v, String text);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(int index, View v, String text);
    }

    public static class FlowItem {
        String text;
        int color;
        GradientDrawable drawable;
    }

}
