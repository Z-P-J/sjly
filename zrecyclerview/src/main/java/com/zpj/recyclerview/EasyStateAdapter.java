package com.zpj.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.fingdo.statelayout.StateLayout;
import com.fingdo.statelayout.helper.AnimationHelper;

import java.util.List;

public class EasyStateAdapter<T> extends EasyAdapter<T> {

    private static final String TAG = "EasyStateAdapter";

    private static final int STATE_CONTENT = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_EMPTY = 2;
    private static final int STATE_ERROR = 3;
    private static final int STATE_NO_NETWORK = 4;

    private static final int TYPE_STATE = -3;

    private final Context context;

    private int state = STATE_CONTENT;
    private int preState = STATE_CONTENT;

    private final StateLayout stateLayout;

    EasyStateAdapter(Context context, List<T> list, int itemRes,
                     IEasy.OnGetChildViewTypeListener onGetChildViewTypeListener,
                     IEasy.OnGetChildLayoutIdListener onGetChildLayoutIdListener,
                     IEasy.OnCreateViewHolderListener<T> onCreateViewHolder,
                     IEasy.OnBindViewHolderListener<T> onBindViewHolderListener,
                     IEasy.OnItemClickListener<T> onClickListener,
                     IEasy.OnItemLongClickListener<T> onLongClickListener,
                     SparseArray<IEasy.OnClickListener<T>> onClickListeners) {
        super(list, itemRes, onGetChildViewTypeListener, onGetChildLayoutIdListener,
                onCreateViewHolder, onBindViewHolderListener, onClickListener,
                onLongClickListener, onClickListeners);
        this.context = context;
        stateLayout = new StateLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        stateLayout.setLayoutParams(params);
    }

    @NonNull
    @Override
    public EasyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_STATE) {
            Log.d(TAG, "onCreateViewHolder TYPE_STATE");
            return new EasyViewHolder(stateLayout);
        }
        return super.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull EasyViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (state == STATE_CONTENT) {
            super.onBindViewHolder(holder, position, payloads);
        } else {

//            StateLayout statusView = (StateLayout) holder.getItemView();
//            Log.d(TAG, "onCreateViewHolder state=" + state);
//            if (state == STATE_LOADING) {
//                statusView.showLoadingView();
//            } else if (state == STATE_EMPTY) {
//                statusView.showEmptyView();
//            } else if (state == STATE_ERROR) {
//                statusView.showErrorView();
//            } else if (state == STATE_NO_NETWORK) {
//                statusView.showNoNetworkView();
//            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (state != STATE_CONTENT) {
            return TYPE_STATE;
        }
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        if (state != STATE_CONTENT) {
            return 1;
        }
        return super.getItemCount();
    }

    /**
     * 显示空视图
     */
    public final void showEmpty() {
        changeState(STATE_EMPTY);
        stateLayout.showEmptyView();
    }

    public void showEmptyView(int msgId) {
        changeState(STATE_EMPTY);
        stateLayout.showEmptyView(msgId);
    }

    public void showEmptyView(String msg) {
        changeState(STATE_EMPTY);
        stateLayout.showEmptyView(msg);
    }

    public void showEmptyView(int msgId, int imgId) {
        changeState(STATE_EMPTY);
        stateLayout.showEmptyView(msgId, imgId);
    }

    public void showEmptyView(String msg, int imgId) {
        changeState(STATE_EMPTY);
        stateLayout.showEmptyView(msg, imgId);
    }

    /**
     * 显示错误视图
     */
    public final void showError() {
        changeState(STATE_ERROR);
        stateLayout.showErrorView();
    }

    public void showErrorView(int msgId) {
        changeState(STATE_ERROR);
        stateLayout.showErrorView(msgId);
    }

    public void showErrorView(String msg) {
        changeState(STATE_ERROR);
        stateLayout.showErrorView(msg);
    }

    public void showErrorView(int msgId, int imgId) {
        changeState(STATE_ERROR);
        stateLayout.showErrorView(msgId, imgId);
    }

    public void showErrorView(String msg, int imgId) {
        changeState(STATE_ERROR);
        stateLayout.showErrorView(msg, imgId);
    }

    /**
     * 显示加载中视图
     */
    public final void showLoading() {
        changeState(STATE_LOADING);
        stateLayout.showLoadingView();
    }

    public void showLoadingView(View view) {
        changeState(STATE_LOADING);
        stateLayout.showLoadingView(view);
    }

    public void showLoadingView(View view, boolean showTip) {
        changeState(STATE_LOADING);
        stateLayout.showLoadingView(view, showTip);
    }

    public void showLoadingView(int msgId) {
        changeState(STATE_LOADING);
        stateLayout.showLoadingView(msgId);
    }

    public void showLoadingView(String msg) {
        changeState(STATE_LOADING);
        stateLayout.showLoadingView(msg);
    }

    /**
     * 显示无网络视图
     */
    public final void showNoNetwork() {
        changeState(STATE_NO_NETWORK);
        stateLayout.showNoNetworkView();
    }

    public void showNoNetworkView(int msgId) {
        changeState(STATE_NO_NETWORK);
        stateLayout.showNoNetworkView(msgId);
    }

    public void showNoNetworkView(String msg) {
        changeState(STATE_NO_NETWORK);
        stateLayout.showNoNetworkView(msg);
    }

    public void showNoNetworkView(int msgId, int imgId) {
        changeState(STATE_NO_NETWORK);
        stateLayout.showNoNetworkView(msgId, imgId);
    }


    /**
     * 显示内容视图
     */
    public final void showContent() {
        state = STATE_CONTENT;
        setLoadMoreEnabled(true);
        notifyDataSetChanged();
    }

    private void changeState(int state) {
        preState = this.state;
        this.state = state;
        setLoadMoreEnabled(false);
        list.clear();
        notifyDataSetChanged();
    }


}
