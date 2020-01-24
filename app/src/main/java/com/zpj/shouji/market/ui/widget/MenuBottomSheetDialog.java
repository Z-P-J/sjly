//package com.zpj.shouji.market.ui.dialog;
//
//import android.content.Context;
//import android.content.ContextWrapper;
//import android.support.annotation.LayoutRes;
//import android.support.annotation.MenuRes;
//import android.support.v4.app.FragmentActivity;
//import android.support.v7.view.menu.MenuBuilder;
//import android.support.v7.widget.LinearLayoutManager;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//
//import com.zpj.recyclerview.EasyRecyclerView;
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.recyclerview.IEasy;
//import com.zpj.shouji.market.R;
//import com.zpj.zdialog.ZBottomSheetDialog;
//import com.zpj.zdialog.base.IDialog;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MenuBottomSheetDialog extends ZBottomSheetDialog
//         implements IDialog.OnViewCreateListener,
//        IEasy.OnBindViewHolderListener<MenuItem> {
//
//    private OnItemClickListener onItemClickListener;
//    private OnItemLongClickListener onItemLongClickListener;
//
//    @LayoutRes
//    private int headerRes = -1;
//    private IEasy.OnBindHeaderListener onBindHeaderListener;
//
//    @MenuRes
//    private int menuRes;
//
//    public static MenuBottomSheetDialog with(Context context) {
//        MenuBottomSheetDialog dialog = new MenuBottomSheetDialog();
//        FragmentActivity activity;
//        if (context instanceof FragmentActivity) {
//            activity = (FragmentActivity) context;
//        } else {
//            activity = ((FragmentActivity) ((ContextWrapper) context).getBaseContext());
//        }
//        dialog.setFragmentActivity(activity);
//        return dialog;
//    }
//
//    public MenuBottomSheetDialog() {
//        setContentView(R.layout.layout_menu_bottom_sheet_dialog);
//        setOnViewCreateListener(this);
//    }
//
//    public MenuBottomSheetDialog setMenu(@MenuRes int menuRes) {
//        this.menuRes = menuRes;
//        return this;
//    }
//
//    public MenuBottomSheetDialog onItemClick(OnItemClickListener listener) {
//        this.onItemClickListener = listener;
//        return this;
//    }
//
//    public MenuBottomSheetDialog setHeaderView(@LayoutRes int layoutRes, IEasy.OnBindHeaderListener l) {
//        this.headerRes = layoutRes;
//        this.onBindHeaderListener = l;
//        return this;
//    }
//
//    public MenuBottomSheetDialog onItemLongClick(OnItemLongClickListener listener) {
//        this.onItemLongClickListener = listener;
//        return this;
//    }
//
//    @Override
//    public void onViewCreate(IDialog dialog, View view) {
//        MenuInflater inflater = new MenuInflater(getContext());
//        Menu menu = new MenuBuilder(getContext());
//        inflater.inflate(menuRes, menu);
//        int size = menu.size();
//        List<MenuItem> list = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            list.add(menu.getItem(i));
//        }
//        EasyRecyclerView<MenuItem> recyclerView = new EasyRecyclerView<>(view.findViewById(R.id.recycler_view));
//        recyclerView.setData(list)
//                .setItemRes(R.layout.item_menu_bottom_sheet_dialog)
//                .setLayoutManager(new LinearLayoutManager(getContext()))
//                .onBindViewHolder(this)
//                .setHeaderView(headerRes, onBindHeaderListener)
//                .onItemClick((holder, view1, data, x, y) -> {
//                    if (onItemClickListener != null) {
//                        onItemClickListener.onClick(MenuBottomSheetDialog.this, view1, data);
//                    }
//                })
//                .onItemLongClick((holder, view12, data, x, y) -> {
//                    if (onItemLongClickListener != null) {
//                        return onItemLongClickListener.onLongClick(MenuBottomSheetDialog.this, view12, data);
//                    }
//                    return false;
//                })
//                .parse();
//    }
//
//    @Override
//    public void onBindViewHolder(EasyViewHolder holder, List<MenuItem> list, int position, List<Object> payloads) {
//        holder.getImageView(R.id.iv_icon).setImageDrawable(list.get(position).getIcon());
//        holder.getTextView(R.id.tv_title).setText(list.get(position).getTitle());
//    }
//
//    public interface OnItemClickListener {
//        void onClick(IDialog dialog, View view, MenuItem data);
//    }
//
//    public interface OnItemLongClickListener {
//        boolean onLongClick(IDialog dialog, View view, MenuItem data);
//    }
//}
