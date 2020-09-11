package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.BottomDialogFragment;
import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.widget.CheckView;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.util.ArrayList;
import java.util.List;

public class BottomListDialogFragment<T> extends BottomDialogFragment implements IEasy.OnBindViewHolderListener<T>, IEasy.OnItemClickListener<T>  {

    private final List<T> list = new ArrayList<>();

    protected EasyRecyclerView<T> recyclerView;
    private TextView tvTitle;

    private OnSelectListener<T> selectListener;
    private int checkedPosition = -1;
    private int selectedPosition = -1;

    String title;
    int[] iconIds;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_bottom_impl_list;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        tvTitle = findViewById(R.id.tv_title);

        if (tvTitle != null) {
            if (TextUtils.isEmpty(title)) {
                tvTitle.setVisibility(View.GONE);
                findViewById(R.id.xpopup_divider).setVisibility(View.GONE);
            } else {
                tvTitle.setText(title);
            }
        }

        EasyRecyclerView<T> recyclerView = new EasyRecyclerView<>(findViewById(R.id.recyclerView));
        recyclerView.setData(list)
                .setItemRes(R.layout._xpopup_adapter_text)
                .onBindViewHolder(this)
                .onItemClick(this)
                .build();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<T> list, int position, List<Object> payloads) {
        holder.setText(R.id.tv_text, list.get(position).toString());
        if (iconIds != null && iconIds.length > position) {
            holder.getView(R.id.iv_image).setVisibility(View.VISIBLE);
            holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
        } else {
            holder.getView(R.id.iv_image).setVisibility(View.GONE);
        }

        // 对勾View
        if (checkedPosition != -1) {
            if (holder.getView(R.id.check_view) != null) {
                holder.getView(R.id.check_view).setVisibility(position == checkedPosition ? View.VISIBLE : View.GONE);
                holder.<CheckView>getView(R.id.check_view).setColor(XPopup.getPrimaryColor());
            }
            holder.<TextView>getView(R.id.tv_text).setTextColor(
                    getResources().getColor(position == checkedPosition ?
                            R.color._xpopup_text_major_color : R.color._xpopup_text_normal_color));
        } else if (selectedPosition != -1) {
            holder.<TextView>getView(R.id.tv_text).setTextColor(
                    getResources().getColor(position == selectedPosition ?
                            R.color._xpopup_text_major_color : R.color._xpopup_text_normal_color));
        }
        if (position == (list.size() - 1)) {
            holder.getView(R.id.xpopup_divider).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, T data) {
        int position = holder.getRealPosition();
        if (selectListener != null) {
            selectListener.onSelect(BottomListDialogFragment.this, position, list.get(position));
        }
        if (checkedPosition != -1) {
            checkedPosition = position;
            recyclerView.notifyDataSetChanged();
        } else if (selectedPosition != -1) {
            selectedPosition = position;
            recyclerView.notifyDataSetChanged();
        }
    }

    public BottomListDialogFragment<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public BottomListDialogFragment<T> setData(List<T> list) {
        this.list.addAll(list);
        return this;
    }

    public BottomListDialogFragment<T> setIconIds(int[] iconIds) {
        this.iconIds = iconIds;
        return this;
    }

    public BottomListDialogFragment<T> setOnSelectListener(OnSelectListener<T> selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public BottomListDialogFragment<T> setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }

    public BottomListDialogFragment<T> setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        return this;
    }

    public interface OnSelectListener<T> {
        void onSelect(BottomListDialogFragment<T> fragment, int position, T item);
    }

}
