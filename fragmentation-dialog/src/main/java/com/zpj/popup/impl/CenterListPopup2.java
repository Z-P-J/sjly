package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.interfaces.OnSelectListener;
import com.zpj.popup.util.XPopupUtils;
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
public class CenterListPopup2<T> extends CenterPopup<CenterListPopup2<T>>
        implements IEasy.OnBindViewHolderListener<T>,
        IEasy.OnItemClickListener<T> {

    protected final List<T> data = new ArrayList<>();


    protected EasyRecyclerView<T> recyclerView;
    protected String title;
    protected TextView tvTitle;

    private PopupAnimator popupAnimator;

    protected OnSelectListener<T> selectListener;

    protected int bindItemLayoutId = R.layout._xpopup_adapter_text;

    private IEasy.OnBindViewHolderListener<T> onBindViewHolderListener;

    public CenterListPopup2(@NonNull Context context) {
        super(context);
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public CenterListPopup2<T> bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_center_impl_list : bindLayoutId;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        tvTitle = findViewById(R.id.tv_title);

        if (tvTitle != null) {
            if (TextUtils.isEmpty(title)) {
                tvTitle.setVisibility(GONE);
                findViewById(R.id.xpopup_divider).setVisibility(GONE);
            } else {
                tvTitle.setText(title);
            }
        }

        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recyclerView));
        recyclerView.setData(data)
                .setItemRes(bindItemLayoutId)
                .onBindViewHolder(this)
                .onItemClick(this)
                .build();


//        float maxHeight = XPopupUtils.getWindowHeight(getContext()) * .86f;
//        - XPopupUtils.getStatusBarHeight()
//        float margin = XPopupUtils.getWindowHeight(getContext()) * 0.14f;
//        MarginLayoutParams params = (MarginLayoutParams) centerPopupContainer.getLayoutParams();
//        params.topMargin = (int) (XPopupUtils.getStatusBarHeight() + margin / 2);
//        params.bottomMargin = (int) (margin / 2);
    }

    public CenterListPopup2<T> setData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
        return this;
    }
    public CenterListPopup2<T> addData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
        return this;
    }


    public CenterListPopup2<T> setOnSelectListener(OnSelectListener<T> selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    int checkedPosition = -1;

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public CenterListPopup2<T> setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }

    public CenterListPopup2<T> setOnBindViewHolderListener(IEasy.OnBindViewHolderListener<T> onBindViewHolderListener) {
        this.onBindViewHolderListener = onBindViewHolderListener;
        return this;
    }

    public CenterListPopup2<T> setPopupAnimator(PopupAnimator popupAnimator) {
        this.popupAnimator = popupAnimator;
        return this;
    }

    //    @Override
//    protected int getMaxWidth() {
//        return popupInfo.maxWidth == 0 ? (int) (super.getMaxWidth() * .9f)
//                : popupInfo.maxWidth;
//    }

//    @Override
//    protected int getMaxHeight() {
//        return popupInfo.maxHeight == 0 ? (int) (XPopupUtils.getWindowHeight(getContext()) * .86f)
//                : popupInfo.maxHeight;
//    }


    @Override
    protected int getMaxHeight() {
        return 0;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<T> list, int position, List<Object> payloads) {
        if (onBindViewHolderListener != null) {
            onBindViewHolderListener.onBindViewHolder(holder, list, position, payloads);
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, T item) {
//        int position = holder.getRealPosition();
//        if (selectListener != null) {
//            if (position >= 0 && position < data.size())
//                selectListener.onSelect(position, data.get(position));
//        }
//        if (checkedPosition != -1) {
//            checkedPosition = position;
//            recyclerView.notifyDataSetChanged();
//        }
//        if (popupInfo.autoDismiss) dismiss();
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return popupAnimator == null ? super.getPopupAnimator() : popupAnimator;
    }

    public CenterListPopup2<T> setTitle(String title) {
        this.title = title;
        return self();
    }
}
