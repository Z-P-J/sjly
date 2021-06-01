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
import com.zpj.shouji.market.ui.widget.DialogHeaderLayout;
import com.zpj.skin.SkinEngine;

import java.util.ArrayList;
import java.util.List;

public class BottomListMenuDialogFragment<T extends BottomListMenuDialogFragment<T>>
        extends BottomDragDialogFragment<T>
         implements IEasy.OnBindViewHolderListener<MenuItem> {

    private OnItemClickListener<T> onItemClickListener;
    private OnItemLongClickListener<T> onItemLongClickListener;

    protected final List<Integer> hideMenuItemList = new ArrayList<>();

    private DialogHeaderLayout mHeaderLayout;

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

        mHeaderLayout = findViewById(R.id.layout_dialog_header);
        mHeaderLayout.setOnCloseClickListener(view1 -> dismiss());


        if (TextUtils.isEmpty(title)) {
            title = "更多操作";
        }
        mHeaderLayout.setTitle(title);

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
                        onItemClickListener.onClick(self(), view1, data);
                    }
                })
                .onItemLongClick((holder, view12, data) -> {
                    if (onItemLongClickListener != null) {
                        return onItemLongClickListener.onLongClick(self(), view12, data);
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

    public T addHideItem(List<Integer> list) {
        hideMenuItemList.addAll(list);
        return self();
    }

    public T addHideItem(int id) {
        hideMenuItemList.add(id);
        return self();
    }

    public T setMenu(@MenuRes int menuRes) {
        this.menuRes = menuRes;
        return self();
    }

    public T onItemClick(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
        return self();
    }

    public T setHeaderView(@LayoutRes int layoutRes, IEasy.OnBindHeaderListener l) {
        this.headerRes = layoutRes;
        this.onBindHeaderListener = l;
        return self();
    }

    public T onItemLongClick(OnItemLongClickListener<T> listener) {
        this.onItemLongClickListener = listener;
        return self();
    }

    public T setTitle(String title) {
        this.title = title;
        if (mHeaderLayout != null) {
            mHeaderLayout.setTitle(title);
        }
        return self();
    }

    public interface OnItemClickListener<T> {
        void onClick(T menu, View view, MenuItem data);
    }

    public interface OnItemLongClickListener<T> {
        boolean onLongClick(T menu, View view, MenuItem data);
    }
}
