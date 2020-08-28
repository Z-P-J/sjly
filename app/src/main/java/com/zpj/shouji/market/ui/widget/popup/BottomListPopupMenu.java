package com.zpj.shouji.market.ui.widget.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.zpj.popup.core.BottomPopup;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.widget.tinted.TintedImageView;

import java.util.ArrayList;
import java.util.List;

public class BottomListPopupMenu extends BottomPopup<BottomListPopupMenu>
         implements IEasy.OnBindViewHolderListener<MenuItem> {

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    protected final List<Integer> hideMenuItemList = new ArrayList<>();

    @LayoutRes
    private int headerRes = -1;
    private IEasy.OnBindHeaderListener onBindHeaderListener;

    @MenuRes
    private int menuRes;

    protected BottomListPopupMenu(@NonNull Context context) {
        super(context);
    }

    public static BottomListPopupMenu with(Context context) {
        return new BottomListPopupMenu(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_bottom_sheet_menu;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        MenuInflater inflater = new MenuInflater(getContext());
        @SuppressLint("RestrictedApi") Menu menu = new MenuBuilder(getContext());
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
                        onItemClickListener.onClick(BottomListPopupMenu.this, view1, data);
                    }
                })
                .onItemLongClick((holder, view12, data) -> {
                    if (onItemLongClickListener != null) {
                        return onItemLongClickListener.onLongClick(BottomListPopupMenu.this, view12, data);
                    }
                    return false;
                })
                .build();
    }

    //    public BottomListPopupMenu() {
//        setContentView(R.layout.layout_popup_bottom_sheet_menu);
//        setOnViewCreateListener(this);
//    }

    public BottomListPopupMenu addHideItem(List<Integer> list) {
        hideMenuItemList.addAll(list);
        return this;
    }

    public BottomListPopupMenu addHideItem(int id) {
        hideMenuItemList.add(id);
        return this;
    }

    public BottomListPopupMenu setMenu(@MenuRes int menuRes) {
        this.menuRes = menuRes;
        return this;
    }

    public BottomListPopupMenu onItemClick(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public BottomListPopupMenu setHeaderView(@LayoutRes int layoutRes, IEasy.OnBindHeaderListener l) {
        this.headerRes = layoutRes;
        this.onBindHeaderListener = l;
        return this;
    }

    public BottomListPopupMenu onItemLongClick(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        return this;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<MenuItem> list, int position, List<Object> payloads) {
        TintedImageView ivIcon = holder.getView(R.id.iv_icon);
        ivIcon.setTint(getResources().getColor(R.color.color_text_major));
        ivIcon.setImageDrawable(list.get(position).getIcon());
        holder.setText(R.id.tv_title, list.get(position).getTitle());
    }

    public interface OnItemClickListener {
        void onClick(BottomListPopupMenu menu, View view, MenuItem data);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(BottomListPopupMenu menu, View view, MenuItem data);
    }
}
