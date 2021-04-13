package com.zpj.shouji.market.ui.fragment.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.skin.SkinEngine;

import java.util.ArrayList;
import java.util.List;

public class BottomListMenuDialogFragment extends BottomDragDialogFragment
         implements IEasy.OnBindViewHolderListener<MenuItem> {

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    protected final List<Integer> hideMenuItemList = new ArrayList<>();

    private TextView tvTitle;

    @LayoutRes
    private int headerRes = -1;
    private IEasy.OnBindHeaderListener onBindHeaderListener;

    @MenuRes
    private int menuRes;

    private String title;

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_bottom_sheet_menu;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setCornerRadiusDp(20);
        super.initView(view, savedInstanceState);

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        tvTitle = findViewById(R.id.tv_title);
        if (TextUtils.isEmpty(title)) {
            title = "更多操作";
        }
        tvTitle.setText(title);

        MenuInflater inflater = new MenuInflater(getContext());
        @SuppressLint("RestrictedApi") Menu menu = new MenuBuilder(context);
        inflater.inflate(menuRes, menu);
        int size = menu.size();
        List<MenuItem> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (hideMenuItemList.contains(item.getItemId())) {
                continue;
            }
            list.add(item);
        }
        EasyRecyclerView<MenuItem> recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        recyclerView.setData(list)
                .setItemRes(R.layout.item_menu)
                .setLayoutManager(new LinearLayoutManager(getContext()))
                .onBindViewHolder(this)
                .setHeaderView(headerRes, onBindHeaderListener)
                .onItemClick((holder, view1, data) -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(BottomListMenuDialogFragment.this, view1, data);
                    }
                })
                .onItemLongClick((holder, view12, data) -> {
                    if (onItemLongClickListener != null) {
                        return onItemLongClickListener.onLongClick(BottomListMenuDialogFragment.this, view12, data);
                    }
                    return false;
                })
                .build();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<MenuItem> list, int position, List<Object> payloads) {
        ImageView ivIcon = holder.getView(R.id.iv_icon);
//        ivIcon.setTint(ThemeUtils.getTextColorMajor(context));
        ivIcon.setImageDrawable(list.get(position).getIcon());
        SkinEngine.applyViewAttr(ivIcon, "tint", R.attr.textColorMajor);
        holder.setText(R.id.tv_title, list.get(position).getTitle());
    }

    public BottomListMenuDialogFragment addHideItem(List<Integer> list) {
        hideMenuItemList.addAll(list);
        return this;
    }

    public BottomListMenuDialogFragment addHideItem(int id) {
        hideMenuItemList.add(id);
        return this;
    }

    public BottomListMenuDialogFragment setMenu(@MenuRes int menuRes) {
        this.menuRes = menuRes;
        return this;
    }

    public BottomListMenuDialogFragment onItemClick(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public BottomListMenuDialogFragment setHeaderView(@LayoutRes int layoutRes, IEasy.OnBindHeaderListener l) {
        this.headerRes = layoutRes;
        this.onBindHeaderListener = l;
        return this;
    }

    public BottomListMenuDialogFragment onItemLongClick(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        return this;
    }

    public BottomListMenuDialogFragment setTitle(String title) {
        this.title = title;
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
        return this;
    }

    public interface OnItemClickListener {
        void onClick(BottomListMenuDialogFragment menu, View view, MenuItem data);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(BottomListMenuDialogFragment menu, View view, MenuItem data);
    }
}
