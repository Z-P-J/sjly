package com.zpj.zdialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.utils.ClickHelper;
import com.zpj.zdialog.base.IDialog;
import com.zpj.zdialog.view.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z-P-J
 * @date 2019/6/1 16:25
 */
public class ZSelectDialog<T> {

    public interface Callback<T> {
        void onGetSelectedResult(List<Integer> selected, List<T> list);
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

    private final ZListDialog<T> dialog;

    private List<T> list;

    private boolean isMultiple = false;

    private Callback<T> callback;
    private IconCallback<T> iconCallback;
    private TitleCallback<T> titleCallback;
    private SubtitleCallback<T> subtitleCallback;


    public ZSelectDialog(Context context) {
        dialog = new ZListDialog<>(context);
    }

    public ZSelectDialog<T> setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public ZSelectDialog<T> setItemList(List<T> list) {
        this.list = list;
        dialog.setItemList(list);
        return this;
    }

    public ZSelectDialog<T> setMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
        return this;
    }

    public ZSelectDialog<T> setSelected(int[] selected) {
        for (int position : selected) {
            onSelected(position);
        }
        return this;
    }

    public ZSelectDialog<T> setSelected(int selected) {
        onSelected(selected);
        return this;
    }

    public ZSelectDialog<T> setCallback(Callback<T> callback) {
        this.callback = callback;
        return this;
    }

    public ZSelectDialog<T> setIconCallback(IconCallback<T> iconCallback) {
        this.iconCallback = iconCallback;
        return this;
    }

    public ZSelectDialog<T> setTitleCallback(TitleCallback<T> titleCallback) {
        this.titleCallback = titleCallback;
        return this;
    }

    public ZSelectDialog<T> setSubtitleCallback(SubtitleCallback<T> subtitleCallback) {
        this.subtitleCallback = subtitleCallback;
        return this;
    }

    public void show() {
        dialog.setShowButtons(isMultiple)
                .setItemRes(R.layout.easy_item_select_dialog)
                .setNegativeButton(new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog) {
                        selectedList.clear();
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new IDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(IDialog dialog) {
                        if (callback != null) {
                            callback.onGetSelectedResult(selectedList, list);
                        }
                    }
                })
                .setOnBindChildView(new IEasy.OnBindViewHolderCallback<T>() {
                    @Override
                    public void onBindViewHolder(final EasyViewHolder holder, List<T> list, int position, List<Object> ppayloads) {
                        ImageView iconView = holder.getView(R.id.icon_view);
                        TextView titleView = holder.getView(R.id.title_view);
                        TextView contentView = holder.getView(R.id.content_view);
                        final SmoothCheckBox checkBox = holder.getView(R.id.check_box);
                        checkBox.setChecked(selectedList.contains(position), true);
                        holder.setOnItemClickListener(new ClickHelper.OnClickListener() {
                            @Override
                            public void onClick(View v, float x, float y) {
                                if (isMultiple) {
                                    if (checkBox.isChecked()) {
                                        unSelect(holder.getAdapterPosition());
                                    } else {
                                        onSelected(holder.getAdapterPosition());
                                    }
                                    dialog.notifyItemChanged(holder.getAdapterPosition());
                                } else {
                                    if (!checkBox.isChecked()) {
                                        dialog.notifyItemChanged(selectedList.get(0));
                                        selectedList.clear();
                                        onSelected(holder.getAdapterPosition());
                                        dialog.notifyItemChanged(holder.getAdapterPosition());
                                    }
                                    v.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismiss();
                                        }
                                    }, 500);
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
                    }
                })
                .show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void onSelected(int position) {
        if (!selectedList.contains(position)) {
            selectedList.add(position);
        }
    }

    private void unSelect(int position) {
        selectedList.remove(Integer.valueOf(position));
    }

//    public List<Integer> getSelectedList() {
//        return selectedList;
//    }
}
