package com.zpj.shouji.market.ui.widget;

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

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.PopupInfo;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;

import java.util.ArrayList;
import java.util.List;

public class BottomListPopupMenu extends BottomPopupView
         implements IEasy.OnBindViewHolderListener<MenuItem> {

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    @LayoutRes
    private int headerRes = -1;
    private IEasy.OnBindHeaderListener onBindHeaderListener;

    @MenuRes
    private int menuRes;

    private BottomListPopupMenu(@NonNull Context context) {
        super(context);
        popupInfo = new PopupInfo();
    }

    public static BottomListPopupMenu with(Context context) {
        return new BottomListPopupMenu(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_menu_bottom_sheet_dialog;
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
            list.add(menu.getItem(i));
        }
        EasyRecyclerView<MenuItem> recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        recyclerView.setData(list)
                .setItemRes(R.layout.item_menu_bottom_sheet_dialog)
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
//        setContentView(R.layout.layout_menu_bottom_sheet_dialog);
//        setOnViewCreateListener(this);
//    }

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
        holder.getImageView(R.id.iv_icon).setImageDrawable(list.get(position).getIcon());
        holder.getTextView(R.id.tv_title).setText(list.get(position).getTitle());
    }

    public interface OnItemClickListener {
        void onClick(BottomListPopupMenu menu, View view, MenuItem data);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(BottomListPopupMenu menu, View view, MenuItem data);
    }
}
