package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.checkbox.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

public class CenterSelectDialogFragment<T> extends CenterDialogFragment {

    public interface OnMultiSelectListener<T> {
        void onSelect(List<Integer> selected, List<T> list);
    }

    public interface OnSingleSelectListener<T> {
        void onSelect(int position, T item);
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

    private final List<T> list = new ArrayList<>();

    private boolean isMultiple = false;

    private OnSingleSelectListener<T> onSingleSelectListener;
    private OnMultiSelectListener<T> onMultiSelectListener;
    private IconCallback<T> iconCallback;
    private TitleCallback<T> titleCallback;
    private SubtitleCallback<T> subtitleCallback;

    private EasyRecyclerView<T> recyclerView;
    private SmoothCheckBox selectAllCheckBox;

    protected String negativeText, neutralText, positiveText;
    private String title;

    private TextView tvOk;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_center_impl_list;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setTextColor(DialogThemeUtils.getMajorTextColor(context));
        tvTitle.setText(title);
        LinearLayout buttons = findViewById(R.id.layout_buttons);
        FrameLayout flCheckBox = findViewById(R.id.fl_check_box);
        if (isMultiple) {
            buttons.setVisibility(View.VISIBLE);
            flCheckBox.setVisibility(View.VISIBLE);
            selectAllCheckBox = findViewById(R.id.check_box);
            selectAllCheckBox.setCheckedColor(DialogThemeUtils.getColorPrimary(context));
            View.OnClickListener listener = v -> {
                boolean selectAll = !selectAllCheckBox.isChecked();
//                selectAllCheckBox.setChecked(selectAll, true);
                if (selectAll) {
                    selectedList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        selectedList.add(i);
                    }
                } else {
                    selectedList.clear();
                }
                recyclerView.notifyDataSetChanged();
                updateOkButton();
            };
            selectAllCheckBox.setOnClickListener(listener);
            flCheckBox.setOnClickListener(listener);

            TextView tvCancel = buttons.findViewById(R.id.tv_cancel);
            if (!TextUtils.isEmpty(negativeText)) {
                tvCancel.setText(negativeText);
            }
            tvOk = buttons.findViewById(R.id.tv_ok);
            if (TextUtils.isEmpty(positiveText)) {
                positiveText = String.valueOf(tvOk.getText());
            }
            updateOkButton();
            tvCancel.setTextColor(DialogThemeUtils.getNegativeTextColor(context));
            tvOk.setTextColor(DialogThemeUtils.getPositiveTextColor(context));
            tvCancel.setOnClickListener(v -> dismiss());
            tvOk.setOnClickListener(v -> onSelect());
        } else {
            buttons.setVisibility(View.GONE);
            flCheckBox.setVisibility(View.GONE);
        }

        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recyclerView));
        recyclerView.setData(list)
                .setItemRes(R.layout._dialog_item_select)
                .setLayoutManager(new LinearLayoutManager(context))
                .onBindViewHolder((holder, list, position, payloads) -> {
                    ImageView iconView = holder.getView(R.id.icon_view);
                    TextView titleView = holder.getView(R.id.title_view);
                    titleView.setTextColor(DialogThemeUtils.getMajorTextColor(context));
                    TextView contentView = holder.getView(R.id.content_view);
                    contentView.setTextColor(DialogThemeUtils.getNormalTextColor(context));
                    final SmoothCheckBox checkBox = holder.getView(R.id.check_box);
                    checkBox.setCheckedColor(DialogThemeUtils.getColorPrimary(context));
                    checkBox.setChecked(selectedList.contains(position), false);
                    holder.setOnItemClickListener(v -> {
                        if (isMultiple) {
                            if (checkBox.isChecked()) {
                                unSelect(holder.getAdapterPosition());
                            } else {
                                onSelected(holder.getAdapterPosition());
                            }
//                            easyRecyclerView.notifyItemChanged(holder.getAdapterPosition());
                            checkBox.setChecked(!checkBox.isChecked(), true);
                        } else {
                            if (!checkBox.isChecked()) {
                                if (selectedList.size() > 0) {
                                    int selected = selectedList.get(0);
                                    selectedList.clear();
                                    recyclerView.notifyItemChanged(selected);
                                }
                                onSelected(holder.getAdapterPosition());
//                                easyRecyclerView.notifyItemChanged(holder.getAdapterPosition());
                                checkBox.setChecked(true, true);
                                onSelect();
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
                })
                .build();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
//        if (onSingleSelectListener != null) {
//            onSingleSelectListener.onSelect(selectedList.get(0), list.get(selectedList.get(0)));
//        } else if (onMultiSelectListener != null) {
//            onMultiSelectListener.onSelect(selectedList, list);
//        }
    }

    private void onSelect() {
        if (isMultiple && onMultiSelectListener != null) {
            onMultiSelectListener.onSelect(selectedList, list);
        } else if (!isMultiple && onSingleSelectListener != null) {
            onSingleSelectListener.onSelect(selectedList.get(0), list.get(selectedList.get(0)));
        }
        dismiss();
    }

    public CenterSelectDialogFragment<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public CenterSelectDialogFragment<T> setNegativeText(String negativeText) {
        this.negativeText = negativeText;
        return this;
    }

    public CenterSelectDialogFragment<T> setPositiveText(String positiveText) {
        this.positiveText = positiveText;
        return this;
    }

    public CenterSelectDialogFragment<T> setData(List<T> list) {
        this.list.addAll(list);
        return this;
    }

    public CenterSelectDialogFragment<T> setMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
        return this;
    }

    public CenterSelectDialogFragment<T> setSelected(int[] selected) {
        for (int position : selected) {
            onSelected(position);
        }
        if (selected.length > 1) {
            isMultiple = true;
        }
        return this;
    }

    public CenterSelectDialogFragment<T> setSelected(int selected) {
        onSelected(selected);
        return this;
    }

    public CenterSelectDialogFragment<T> setOnSingleSelectListener(OnSingleSelectListener<T> onSingleSelectListener) {
        isMultiple = false;
        this.onSingleSelectListener = onSingleSelectListener;
        return this;
    }

    public CenterSelectDialogFragment<T> setOnMultiSelectListener(OnMultiSelectListener<T> onMultiSelectListener) {
        isMultiple = true;
        this.onMultiSelectListener = onMultiSelectListener;
        return this;
    }

    public CenterSelectDialogFragment<T> setIconCallback(IconCallback<T> iconCallback) {
        this.iconCallback = iconCallback;
        return this;
    }

    public CenterSelectDialogFragment<T> setTitleCallback(TitleCallback<T> titleCallback) {
        this.titleCallback = titleCallback;
        return this;
    }

    public CenterSelectDialogFragment<T> setSubtitleCallback(SubtitleCallback<T> subtitleCallback) {
        this.subtitleCallback = subtitleCallback;
        return this;
    }


    private void onSelected(int position) {
        if (!selectedList.contains(position)) {
            selectedList.add(position);
            updateOkButton();
        }
    }

    private void unSelect(int position) {
        selectedList.remove(Integer.valueOf(position));
        updateOkButton();
    }

    private void updateOkButton() {
        if (isMultiple) {
            if (selectedList.size() == list.size()) {
                if (!selectAllCheckBox.isChecked()) {
                    selectAllCheckBox.setChecked(true, true);
                }
            } else {
                if (selectAllCheckBox.isChecked()) {
                    selectAllCheckBox.setChecked(false, true);
                }
            }
            if (selectedList.isEmpty()) {
                tvOk.setText(positiveText);
            } else {
                tvOk.setText(positiveText + "(" + selectedList.size() + ")");
            }

        }
    }

    @Override
    protected int getMaxWidth() {
        return (int) (ScreenUtils.getScreenWidth(context) * .9f);
    }


}
