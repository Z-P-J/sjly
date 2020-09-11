package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.interfaces.OnSelectListener;
import com.zpj.popup.widget.CheckView;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: 在中间的列表对话框
 * Create by dance, at 2018/12/16
 */
public class SimpleCenterListPopup extends CenterListPopup2<String> {


    private int[] iconIds;

    EasyRecyclerView<String> recyclerView;

    public SimpleCenterListPopup(@NonNull Context context) {
        super(context);
    }

    public SimpleCenterListPopup setStringData(String title, String[] data, int[] iconIds) {
        this.title = title;
        this.data.addAll(Arrays.asList(data));
        this.iconIds = iconIds;
        return this;
    }

    private OnSelectListener<String> selectListener;

    public SimpleCenterListPopup setOnSelectListener(OnSelectListener<String> selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    private int checkedPosition = -1;

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public SimpleCenterListPopup setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
        holder.setText(R.id.tv_text, list.get(position));
        if (iconIds != null && iconIds.length > position) {
            holder.getView(R.id.iv_image).setVisibility(VISIBLE);
            holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
        } else {
            holder.getView(R.id.iv_image).setVisibility(GONE);
        }

        // 对勾View
        if (checkedPosition != -1) {
            if (holder.getView(R.id.check_view) != null) {
                holder.getView(R.id.check_view).setVisibility(position == checkedPosition ? VISIBLE : GONE);
                holder.<CheckView>getView(R.id.check_view).setColor(XPopup.getPrimaryColor());
            }
            holder.<TextView>getView(R.id.tv_text).setTextColor(position == checkedPosition ?
                    XPopup.getPrimaryColor() : getResources().getColor(R.color._xpopup_title_color));
        }
        if (position == (data.size() - 1)) {
            holder.getView(R.id.xpopup_divider).setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, String str) {
        int position = holder.getRealPosition();
        if (selectListener != null) {
            if (position >= 0 && position < data.size())
                selectListener.onSelect(position, data.get(position));
        }
        if (checkedPosition != -1) {
            checkedPosition = position;
            recyclerView.notifyDataSetChanged();
        }
        if (popupInfo.autoDismiss) dismiss();
    }
}
