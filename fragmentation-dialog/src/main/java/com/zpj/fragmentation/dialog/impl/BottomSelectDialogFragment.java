package com.zpj.fragmentation.dialog.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.BottomDialogFragment;
import com.zpj.popup.R;
import com.zpj.popup.core.BottomPopup;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.widget.checkbox.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

public class BottomSelectDialogFragment<T> extends BottomDialogFragment {

    public interface OnMultiSelectListener<T> {
        void onSelect(List<Integer> selected, List<T> list);
    }

    public interface OnSingleSelectListener<T> {
        void onSelect(int position, T item);
    }

    public interface IconCallback<T> {
        void onGetIcon(ImageView icon, T item, int position);
    }

    public interface TitleCallback<T> {
        void onGetTitle(TextView titleView, T item, int position);
    }

    public interface SubtitleCallback<T> {
        void onGetSubtitle(TextView subtitleView, T item, int position);
    }

    private final List<Integer> selectedList = new ArrayList<>();

    private final List<T> list = new ArrayList<>();

    private boolean isMultiple = false;

    private OnSingleSelectListener<T> onSingleSelectListener;
    private OnMultiSelectListener<T> onMultiSelectListener;
    private IconCallback<T> iconCallback;
    private TitleCallback<T> titleCallback;
    private SubtitleCallback<T> subtitleCallback;

    String title;

    @Override
    protected int getContentLayoutId() {
        return R.layout._xpopup_bottom_impl_list;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        LinearLayout buttons = findViewById(R.id.layout_buttons);
        if (isMultiple) {
            buttons.setVisibility(View.VISIBLE);
        } else {
            buttons.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        EasyRecyclerView<T> easyRecyclerView = new EasyRecyclerView<>(recyclerView);
        easyRecyclerView.setData(list)
                .setItemRes(R.layout._zpopup_item_bottom_select)
                .setLayoutManager(new LinearLayoutManager(context))
                .onBindViewHolder((holder, list, position, ppayloads) -> {
                    ImageView iconView = holder.getView(R.id.icon_view);
                    TextView titleView = holder.getView(R.id.title_view);
                    TextView contentView = holder.getView(R.id.content_view);
                    final SmoothCheckBox checkBox = holder.getView(R.id.check_box);
                    checkBox.setChecked(selectedList.contains(position), true);
                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isMultiple) {
                                if (checkBox.isChecked()) {
                                    unSelect(holder.getAdapterPosition());
                                } else {
                                    onSelected(holder.getAdapterPosition());
                                }
                                easyRecyclerView.notifyItemChanged(holder.getAdapterPosition());
                            } else {
                                if (!checkBox.isChecked()) {
                                    easyRecyclerView.notifyItemChanged(selectedList.get(0));
                                    selectedList.clear();
                                    onSelected(holder.getAdapterPosition());
                                    easyRecyclerView.notifyItemChanged(holder.getAdapterPosition());
                                }
                                dismiss();
                            }
                        }
                    });
                    if (iconCallback == null) {
                        iconView.setVisibility(View.GONE);
                    } else {
                        iconView.setVisibility(View.VISIBLE);
                        iconCallback.onGetIcon(iconView, list.get(position), position);
                    }
                    if (titleCallback == null) {
                        titleView.setVisibility(View.GONE);
                    } else {
                        titleView.setVisibility(View.VISIBLE);
                        titleCallback.onGetTitle(titleView, list.get(position), position);
                    }
                    if (subtitleCallback == null) {
                        contentView.setVisibility(View.GONE);
                    } else {
                        contentView.setVisibility(View.VISIBLE);
                        subtitleCallback.onGetSubtitle(contentView, list.get(position), position);
                    }
                })
                .build();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        if (onSingleSelectListener != null) {
            onSingleSelectListener.onSelect(selectedList.get(0), list.get(selectedList.get(0)));
        } else if (onMultiSelectListener != null) {
            onMultiSelectListener.onSelect(selectedList, list);
        }
    }

    public BottomSelectDialogFragment<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public BottomSelectDialogFragment<T> setData(List<T> list) {
        this.list.addAll(list);
        return this;
    }

    public BottomSelectDialogFragment<T> setMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
        return this;
    }

    public BottomSelectDialogFragment<T> setSelected(int[] selected) {
        for (int position : selected) {
            onSelected(position);
        }
        if (selected.length > 1) {
            isMultiple = true;
        }
        return this;
    }

    public BottomSelectDialogFragment<T> setSelected(int selected) {
        onSelected(selected);
        return this;
    }

    public BottomSelectDialogFragment<T> setOnSingleSelectListener(OnSingleSelectListener<T> onSingleSelectListener) {
        this.onSingleSelectListener = onSingleSelectListener;
        return this;
    }

    public BottomSelectDialogFragment<T> setOnMultiSelectListener(OnMultiSelectListener<T> onMultiSelectListener) {
        this.onMultiSelectListener = onMultiSelectListener;
        return this;
    }

    public BottomSelectDialogFragment<T> setIconCallback(IconCallback<T> iconCallback) {
        this.iconCallback = iconCallback;
        return this;
    }

    public BottomSelectDialogFragment<T> setTitleCallback(TitleCallback<T> titleCallback) {
        this.titleCallback = titleCallback;
        return this;
    }

    public BottomSelectDialogFragment<T> setSubtitleCallback(SubtitleCallback<T> subtitleCallback) {
        this.subtitleCallback = subtitleCallback;
        return this;
    }


    private void onSelected(int position) {
        if (!selectedList.contains(position)) {
            selectedList.add(position);
        }
    }

    private void unSelect(int position) {
        selectedList.remove(Integer.valueOf(position));
    }


}
