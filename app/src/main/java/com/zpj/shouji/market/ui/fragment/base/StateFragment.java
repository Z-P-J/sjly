package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.statemanager.StateManager;
import com.zpj.shouji.market.R;
import com.zpj.skin.SkinEngine;

public abstract class StateFragment extends SkinFragment {

    protected StateManager stateManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        if (stateManager == null) {
//            stateManager = StateManager.with(view);
//            stateManager.showLoading();
//            view = stateManager.getStateView();
//            SkinEngine.setBackground(view, R.attr.backgroundColor);
            view = initStateManager(view);
        }
        return view;
    }

    @Override
    public View attachToSwipeBack(View view) {
        if (stateManager == null) {
            view = initStateManager(view);
        }
        return super.attachToSwipeBack(view);
    }

    private View initStateManager(View view) {
        stateManager = StateManager.with(view)
                .onRetry(manager -> onRetry());
        stateManager.showLoading();
        view = stateManager.getStateView();
        SkinEngine.setBackground(view, R.attr.backgroundColor);
        return view;
    }

    protected void onRetry() {
        showLoading();
    }

    public void showLoading() {
        if (stateManager != null) {
            stateManager.showLoading();
        }
    }

    public void showContent() {
        if (stateManager != null) {
            stateManager.showContent();
        }
    }

    public void showError() {
        if (stateManager != null) {
            stateManager.showError();
        }
    }

    public void showError(String msg) {
        if (stateManager != null) {
            stateManager.showError(msg);
        }
    }

    public void showEmpty() {
        if (stateManager != null) {
            stateManager.showEmpty();
        }
    }

    public void showNoNetwork() {
        if (stateManager != null) {
            stateManager.showNoNetwork();
        }
    }

    public void showLogin() {
        if (stateManager != null) {
            stateManager.showLogin();
        }
    }


}
