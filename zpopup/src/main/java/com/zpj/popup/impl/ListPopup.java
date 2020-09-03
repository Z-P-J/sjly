package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.popup.R;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.interfaces.OnCancelListener;
import com.zpj.popup.interfaces.OnConfirmListener;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.IEasy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z-P-J
 * @date 2019/6/1 16:25
 */
public class ListPopup<T> extends CenterPopup<ListPopup<T>> {

    private EasyRecyclerView<T> easyRecyclerView;
    private OnCancelListener<ListPopup<T>> cancelListener;
    private OnConfirmListener<ListPopup<T>> confirmListener;

    private List<T> list;

    private IEasy.OnBindViewHolderListener<T> callback;

    private RecyclerView.LayoutManager layoutManager;

    private final List<Integer> selectPositions = new ArrayList<>();

    private boolean isShowButtons = false;

    @LayoutRes
    private int itemRes = 0;

    private String title;

    public ListPopup(Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout._xpopup_center_impl_list;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        LinearLayout buttons = findViewById(R.id.layout_buttons);
        buttons.setVisibility(isShowButtons ? View.VISIBLE : View.GONE);

        buttons.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener != null) {
                    confirmListener.onConfirm(ListPopup.this);
                }
                if (popupInfo.autoDismiss) {
                    dismiss();
                }
            }
        });
        buttons.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onCancel(self());
                }
                if (popupInfo.autoDismiss) {
                    dismiss();
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        easyRecyclerView = new EasyRecyclerView<>(recyclerView);
        easyRecyclerView.setData(list)
                .setItemRes(itemRes)
                .setLayoutManager(layoutManager == null ? new LinearLayoutManager(context) : layoutManager)
                .onBindViewHolder(callback)
                .build();

    }

    public ListPopup<T> setConfirmButton(OnConfirmListener<ListPopup<T>> listener) {
        this.confirmListener = listener;
        return this;
    }

    public ListPopup<T> setCancelButton(OnCancelListener<ListPopup<T>> listener) {
        this.cancelListener = listener;
        return this;
    }

    public ListPopup<T> setItemRes(@LayoutRes int res) {
        this.itemRes = res;
        return this;
    }

    public ListPopup<T> setItemList(List<T> list) {
        this.list = list;
        return this;
    }

    public ListPopup<T> setOnBindChildView(IEasy.OnBindViewHolderListener<T> callback) {
        this.callback = callback;
        return this;
    }

    public ListPopup<T> setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        return this;
    }

    public ListPopup<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public ListPopup<T> setShowButtons(boolean isShowButtons) {
        this.isShowButtons = isShowButtons;
        return this;
    }

    public void addSelectPosition(int position) {
        if (!selectPositions.contains(position)) {
            selectPositions.add(position);
        }
    }

    public List<Integer> getSelectPositions() {
        return selectPositions;
    }

    public void notifyDataSetChanged() {
        easyRecyclerView.notifyDataSetChanged();
    }

    public void notifyItemChanged(int position) {
        easyRecyclerView.notifyItemChanged(position);
    }

    public EasyAdapter<T> getAdapter() {
        return easyRecyclerView.getAdapter();
    }

}
