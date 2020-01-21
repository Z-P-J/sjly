package com.zpj.recyclerview;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.classic.common.MultipleStatusView;
import com.zpj.dialog.R;
import com.zpj.dialog.view.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

public class EasyRecyclerLayout<T> extends FrameLayout {

    public interface OnSelectChangeListener<T> {
        void onChange(List<T> list, int position, boolean isChecked);
        void onSelectAll();
        void onUnSelectAll();
    }

    private static final String TAG = "EasyRecyclerLayout";

    private final List<Integer> selectedList = new ArrayList<>();

    private OnSelectChangeListener<T> onSelectChangeListener;
    private EasyRecyclerView<T> easyRecyclerView;
    private SwipeRefreshLayout refreshLayout;
    private MultipleStatusView statusView;

    private boolean showCheckBox = false;

    private boolean selectMode = false;

    private boolean enableSwipeRefresh = false;

    private boolean enableLoadMore = false;

    private boolean enableSelection = true;

    public EasyRecyclerLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public EasyRecyclerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EasyRecyclerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.easy_layout_recycler_2, this);
        refreshLayout = view.findViewById(R.id.layout_swipe_refresh);
        refreshLayout.setEnabled(false);
        statusView = view.findViewById(R.id.multiple_status_view);
        RecyclerView recyclerView = new RecyclerView(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        statusView.showContent(recyclerView, layoutParams);
        easyRecyclerView = new EasyRecyclerView<>(recyclerView);
    }

    public EasyRecyclerLayout<T> setItemRes(@LayoutRes final int res) {
        easyRecyclerView.setItemRes(R.layout.easy_item_recycler_layout);
        easyRecyclerView.onCreateViewHolder(new IEasy.OnCreateViewHolderListener<T>() {
            @Override
            public void onCreateViewHolder(ViewGroup parent, View view, int viewType) {
                FrameLayout container = view.findViewById(R.id.container);
                View content = LayoutInflater.from(getContext()).inflate(res, null, false);
                container.addView(content);
            }
        });
        return this;
    }

    public EasyRecyclerLayout<T> setData(List<T> list) {
        easyRecyclerView.setData(list);
        return this;
    }

    public EasyRecyclerLayout<T> setItemAnimator(RecyclerView.ItemAnimator animator) {
        easyRecyclerView.setItemAnimator(animator);
        return this;
    }

    public EasyRecyclerLayout<T> setEnableSwipeRefresh(boolean enableSwipeRefresh) {
        this.enableSwipeRefresh = enableSwipeRefresh;
        refreshLayout.setEnabled(enableSwipeRefresh);
        return this;
    }

    public EasyRecyclerLayout<T> setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
        return this;
    }

    public EasyRecyclerLayout<T> setEnableSelection(boolean enableSelection) {
        this.enableSelection = enableSelection;
        return this;
    }

    public EasyRecyclerLayout<T> addOnScrollListener(final RecyclerView.OnScrollListener onScrollListener) {
        easyRecyclerView.addOnScrollListener(onScrollListener);
        return this;
    }

    public EasyRecyclerLayout<T> setOnRefreshListener(final SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (enableSwipeRefresh) {
                    refreshLayout.setRefreshing(true);
                    if (onRefreshListener != null) {
                        onRefreshListener.onRefresh();
                    }
                    refreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                        }
                    }, 1000);
                }
            }
        });
        return this;
    }

    public EasyRecyclerLayout<T> setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
        return this;
    }

    public EasyRecyclerLayout<T> setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        easyRecyclerView.setLayoutManager(layoutManager);
        return this;
    }

    public EasyRecyclerLayout<T> setHeaderView(View headerView) {
        easyRecyclerView.setHeaderView(headerView);
        return this;
    }

    public EasyRecyclerLayout<T> setHeaderView(@LayoutRes int layoutRes, IEasy.OnBindHeaderListener callback) {
        easyRecyclerView.setHeaderView(layoutRes, callback);
        return this;
    }

    public EasyRecyclerLayout<T> setFooterView(View headerView) {
        easyRecyclerView.setFooterView(headerView);
        return this;
    }

    public EasyRecyclerLayout<T> setFooterView(@LayoutRes int layoutRes, IEasy.OnCreateFooterListener callback) {
        easyRecyclerView.setFooterView(layoutRes, callback);
        return this;
    }

    public EasyRecyclerLayout<T> onLoadMore(IEasy.OnLoadMoreListener onLoadMoreListener) {
        easyRecyclerView.onLoadMore(onLoadMoreListener);
        enableLoadMore = true;
        return this;
    }

    public EasyRecyclerLayout<T> setOnSelectChangeListener(OnSelectChangeListener<T> onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
        return this;
    }

    public EasyRecyclerLayout<T> onViewClick(@IdRes int id, IEasy.OnClickListener<T> onClickListener) {
        easyRecyclerView.onViewClick(id, onClickListener);
        return this;
    }

    public EasyRecyclerLayout<T> onItemClick(IEasy.OnItemClickListener<T> listener) {
        easyRecyclerView.onItemClick(listener);
        return this;
    }

    public EasyRecyclerLayout<T> onItemLongClick(IEasy.OnItemLongClickListener<T> listener) {
        easyRecyclerView.onItemLongClick(listener);
        return this;
    }

    public EasyRecyclerLayout<T> onBindViewHolder(final IEasy.OnBindViewHolderListener<T> callback) {
        easyRecyclerView.onBindViewHolder(new IEasy.OnBindViewHolderListener<T>() {
            @Override
            public void onBindViewHolder(final EasyViewHolder holder, List<T> list, final int position, List<Object> payloads) {
                holder.setPosition(position);
                final RelativeLayout checkBoxContainer = holder.getView(R.id.easy_recycler_layout_check_box_container);
                final SmoothCheckBox checkBox = holder.getView(R.id.easy_recycler_layout_check_box);
                checkBox.setChecked(selectedList.contains(position), false);
                checkBox.setClickable(false);
                checkBox.setOnCheckedChangeListener(null);
                if (showCheckBox) {
                    checkBoxContainer.setVisibility(enableSelection ? VISIBLE : GONE);
                } else {
                    checkBoxContainer.setVisibility(selectMode ? VISIBLE : GONE);
                }
                checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBoxContainer.performClick();
                    }
                });
                checkBoxContainer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false, true);
                            unSelect(holder.getHolderPosition());
                        } else {
                            checkBox.setChecked(true, true);
                            onSelected(holder.getHolderPosition());
                        }
                    }
                });

                Log.d(TAG, "onBindViewHolder position=" + position + " selected=" + selectedList.contains(position));
                holder.setItemClickCallback(new IEasy.OnItemClickCallback() {
                    @Override
                    public boolean shouldIgnoreClick(View view) {
                        Log.d(TAG, "shouldIgnoreClick selectMode=" + selectMode);
                        if (selectMode) {
                            if (checkBox.isChecked()) {
                                checkBox.setChecked(false, true);
                                unSelect(holder.getHolderPosition());
                            } else {
                                checkBox.setChecked(true, true);
                                onSelected(holder.getHolderPosition());
                            }
//                            easyRecyclerView.notifyItemChanged(holder.getHolderPosition());
                            return true;
                        }
                        return false;
                    }
                });
                if (callback != null) {
                    callback.onBindViewHolder(holder, list, position, payloads);
                }
            }
        });
        return this;
    }

    /**
     * 设置重试点击事件
     *
     * @param onRetryClickListener 重试点击事件
     */
    public EasyRecyclerLayout<T> setOnRetryClickListener(OnClickListener onRetryClickListener) {
        statusView.setOnRetryClickListener(onRetryClickListener);
        return this;
    }

    public EasyRecyclerLayout<T> setOnViewStatusChangeListener(MultipleStatusView.OnViewStatusChangeListener onViewStatusChangeListener) {
        statusView.setOnViewStatusChangeListener(onViewStatusChangeListener);
        return this;
    }

    public void build() {
        easyRecyclerView.build();
        if (enableLoadMore) {
            statusView.showContent();
            return;
        }
        if (easyRecyclerView.getData().isEmpty()) {
            statusView.showLoading();
        } else {
            statusView.showContent();
        }
    }

    /**
     * 显示空视图
     */
    public final void showEmpty() {
        statusView.showEmpty();
    }

    /**
     * 显示空视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public final void showEmpty(int layoutId, ViewGroup.LayoutParams layoutParams) {
        statusView.showEmpty(layoutId, layoutParams);
    }

    /**
     * 显示空视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    public final void showEmpty(View view, ViewGroup.LayoutParams layoutParams) {
        statusView.showEmpty(view, layoutParams);
    }

    /**
     * 显示错误视图
     */
    public final void showError() {
        statusView.showError();
    }

    /**
     * 显示错误视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public final void showError(int layoutId, ViewGroup.LayoutParams layoutParams) {
        statusView.showError(layoutId, layoutParams);
    }

    /**
     * 显示错误视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    public final void showError(View view, ViewGroup.LayoutParams layoutParams) {
        statusView.showError(view, layoutParams);
    }

    /**
     * 显示加载中视图
     */
    public final void showLoading() {
        statusView.showLoading();
    }

    /**
     * 显示加载中视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public final void showLoading(int layoutId, ViewGroup.LayoutParams layoutParams) {
        statusView.showLoading(layoutId, layoutParams);
    }

    /**
     * 显示加载中视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    public final void showLoading(View view, ViewGroup.LayoutParams layoutParams) {
        statusView.showLoading(view, layoutParams);
    }

    /**
     * 显示无网络视图
     */
    public final void showNoNetwork() {
        statusView.showNoNetwork();
    }

    /**
     * 显示无网络视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public final void showNoNetwork(int layoutId, ViewGroup.LayoutParams layoutParams) {
        statusView.showNoNetwork(layoutId, layoutParams);
    }

    /**
     * 显示无网络视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    public final void showNoNetwork(View view, ViewGroup.LayoutParams layoutParams) {
        statusView.showNoNetwork(view, layoutParams);
    }

    /**
     * 显示内容视图
     */
    public final void showContent() {
        statusView.showContent();
    }

    /**
     * 显示内容视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public final void showContent(int layoutId, ViewGroup.LayoutParams layoutParams) {
        statusView.showContent(layoutId, layoutParams);
    }

    /**
     * 显示内容视图
     *
     * @param view         自定义视图
     * @param layoutParams 布局参数
     */
    public final void showContent(View view, ViewGroup.LayoutParams layoutParams) {
        statusView.showContent(view, layoutParams);
    }



    public void enterSelectMode() {
        if (selectMode) {
            return;
        }
        refreshLayout.setEnabled(false);
        easyRecyclerView.getAdapter().setLoadMoreEnabled(false);
        selectMode = true;
        easyRecyclerView.notifyDataSetChanged();
    }

    public void exitSelectMode() {
        if (!selectMode) {
            return;
        }
        refreshLayout.setEnabled(enableSwipeRefresh);
        easyRecyclerView.getAdapter().setLoadMoreEnabled(true);
        selectMode = false;
        selectedList.clear();
        easyRecyclerView.notifyDataSetChanged();
    }

    private void onSelectChange(int position, boolean isChecked) {
        if (showCheckBox) {
            if (selectMode && getSelectedCount() == 0) {
                selectMode = false;
            } else if (!selectMode && getSelectedCount() > 0) {
                selectMode = true;
            }
        }
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onChange(easyRecyclerView.getData(), position, isChecked);
        }
    }

    private void onSelectAll() {
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onSelectAll();
        }
    }

    private void onUnSelectAll() {
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onUnSelectAll();
        }
    }

    private void onSelected(int position) {
        if (!selectedList.contains(position)) {
            selectedList.add(position);
            onSelectChange(position, true);
            if (selectedList.size() == easyRecyclerView.getData().size()) {
                onSelectAll();
            }
        }
    }

    private void unSelect(int position) {
        if (selectedList.contains(position)) {
            selectedList.remove(Integer.valueOf(position));
            onSelectChange(position, false);
            if (selectedList.size() == 0) {
                onUnSelectAll();
            }
        }
    }

    public void selectAll() {
        if (!selectMode && showCheckBox) {
            selectMode = true;
        }
        selectedList.clear();
        for (int i = 0; i < easyRecyclerView.getAdapter().getItemCount(); i++) {
            selectedList.add(i);
            onSelectChange(i, true);
            notifyItemChanged(i);
        }
        onSelectAll();
    }

    public void unSelectAll() {
        for (int i : selectedList) {
            onSelectChange(i, false);
        }
        selectedList.clear();
        easyRecyclerView.notifyDataSetChanged();
        onUnSelectAll();
    }

    public List<Integer> getSelectedList() {
        return selectedList;
    }

    public int getSelectedCount() {
        return selectedList.size();
    }

    public List<T> getSelectedItem() {
        List<T> selectedItems = new ArrayList<>();
        for (Integer i : selectedList) {
            if (i < easyRecyclerView.getData().size()) {
                selectedItems.add(easyRecyclerView.getData().get(i));
            }
        }
        return selectedItems;
    }

    public void notifyDataSetChanged() {
        if ((easyRecyclerView.getData() == null || easyRecyclerView.getData().isEmpty()) && !enableLoadMore) {
            showEmpty();
        } else {
            easyRecyclerView.notifyDataSetChanged();
            showContent();
        }
        stopRefresh();
    }

    public void notifyItemChanged(int position) {
        easyRecyclerView.notifyItemChanged(position);
    }

    public void notifyItemChanged(int position, Object payload) {
        easyRecyclerView.notifyItemChanged(position, payload);
    }

    public void notifyItemRemoved(int position) {
        easyRecyclerView.getAdapter().notifyItemRemoved(position);
        if (easyRecyclerView.getData().isEmpty()) {
            showEmpty();
        }
    }

    public void notifyItemInserted(int position) {
        easyRecyclerView.getAdapter().notifyItemInserted(position);
        if (!easyRecyclerView.getData().isEmpty()) {
            showContent();
        }
    }

    public boolean isSelectMode() {
        return selectMode;
    }

    public boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }

    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    public void startRefresh() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
    }

    public void stopRefresh() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    public List<T> getData() {
        return easyRecyclerView.getData();
    }

    public EasyRecyclerView<T> getEasyRecyclerView() {
        return easyRecyclerView;
    }
}
