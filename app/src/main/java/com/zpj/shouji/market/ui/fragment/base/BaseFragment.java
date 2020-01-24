package com.zpj.shouji.market.ui.fragment.base;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.MainFragment;

import me.yokeyword.fragmentation.SwipeBackLayout;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

public abstract class BaseFragment extends SwipeBackFragment {

    protected Context context;
    protected CommonTitleBar titleBar;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View view;
        if (getLayoutId() > 0) {
            view = inflater.inflate(getLayoutId(), container, false);
            titleBar = view.findViewById(R.id.title_bar);
            if (titleBar != null && titleBar.getLeftImageButton() != null) {
                if (titleBar.getLeftImageButton() != null) {
                    toolbarLeftImageButton(titleBar.getLeftImageButton());
                } else if (titleBar.getLeftCustomView() != null) {
                    toolbarLeftCustomView(titleBar.getLeftCustomView());
                } else if (titleBar.getLeftTextView() != null) {
                    toolbarLeftTextView(titleBar.getLeftTextView());
                }
                if (titleBar.getRightImageButton() != null) {
                    toolbarRightImageButton(titleBar.getRightImageButton());
                } else if (titleBar.getRightCustomView() != null) {
                    toolbarRightCustomView(titleBar.getRightCustomView());
                } else if (titleBar.getRightTextView() != null) {
                    toolbarRightTextView(titleBar.getRightTextView());
                }
                if (titleBar.getCenterTextView() != null) {
                    titleBar.getCenterTextView().setText(getToolbarTitle());
                }
            }
            initView(view, savedInstanceState);
        } else {
            view = super.onCreateView(inflater, container, savedInstanceState);
        }
        if (view != null && supportSwipeBack()) {
            setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX);
            return attachToSwipeBack(view);
        } else {
            return view;
        }
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    protected boolean supportSwipeBack() {
        return false;
    }

    protected String getToolbarTitle() {
        return "标题";
    }

    public void toolbarLeftImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }

    public void toolbarLeftCustomView(@NonNull View view) {

    }

    public void toolbarLeftTextView(@NonNull TextView view) {

    }

    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {

    }

    public void toolbarRightCustomView(@NonNull View view) {

    }

    public void toolbarRightTextView(@NonNull TextView view) {

    }

    public void setToolbarTitle(String title) {
        if (titleBar != null && titleBar.getCenterTextView() != null) {
            titleBar.getCenterTextView().setText(title);
        }
    }

    public void setToolbarSubTitle(String title) {
        if (titleBar != null && titleBar.getCenterSubTextView() != null) {
            titleBar.getCenterSubTextView().setText(title);
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        lightStatusBar();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        lightStatusBar();
    }

    protected void darkStatusBar() {
        if (_mActivity == null) {
            return;
        }
        StatusBarUtils.setDarkMode(_mActivity.getWindow());
    }

    protected void lightStatusBar() {
        if (_mActivity == null) {
            return;
        }
        StatusBarUtils.setLightMode(_mActivity.getWindow());
    }
}
