package com.zpj.sjly.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment{
    protected boolean isVisible;
    private boolean isFragmentVisible;
    private boolean isFirst = false;
    private boolean isInit = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = onBuildView(inflater, container, savedInstanceState);
        isInit = true;
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            if (isInit) {
                lazyLoadData();
                isInit = false;
            }
        } else {
            isVisible = false;
        }
    }

    @Nullable
    protected abstract View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    protected abstract void lazyLoadData();
}
