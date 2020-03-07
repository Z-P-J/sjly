package com.zpj.fragmentation;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zpj.utils.StatusBarUtils;
import com.zpj.widget.toolbar.ZToolbar;

import java.util.concurrent.atomic.AtomicBoolean;

import me.yokeyword.fragmentation.R;

public abstract class BaseFragment extends SwipeBackFragment {

    protected Context context;
    protected ZToolbar toolbar;
    private final AtomicBoolean isEnterAnimationEnd = new AtomicBoolean(false);
    private Runnable onEnterAnimationEndRunnable;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View view;
        if (getLayoutId() > 0) {
            view = inflater.inflate(getLayoutId(), container, false);
            toolbar = view.findViewById(R.id.tool_bar);
            initView(view, savedInstanceState);
            if (toolbar != null) {
                if (toolbar.getLeftImageButton() != null) {
                    toolbarLeftImageButton(toolbar.getLeftImageButton());
                } else if (toolbar.getLeftCustomView() != null) {
                    toolbarLeftCustomView(toolbar.getLeftCustomView());
                } else if (toolbar.getLeftTextView() != null) {
                    toolbarLeftTextView(toolbar.getLeftTextView());
                }
                if (toolbar.getRightImageButton() != null) {
                    toolbarRightImageButton(toolbar.getRightImageButton());
                } else if (toolbar.getRightCustomView() != null) {
                    toolbarRightCustomView(toolbar.getRightCustomView());
                } else if (toolbar.getRightTextView() != null) {
                    toolbarRightTextView(toolbar.getRightTextView());
                }

                if (toolbar.getCenterTextView() != null) {
                    toolbarCenterTextView(toolbar.getCenterTextView());
                } else if (toolbar.getCenterSubTextView() != null) {
                    toolbarCenterSubTextView(toolbar.getCenterSubTextView());
                } else if (toolbar.getCenterCustomView() != null) {
                    toolbarCenterCustomView(toolbar.getCenterCustomView());
                }
            }
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

    public void toolbarCenterTextView(@NonNull TextView view) {

    }

    public void toolbarCenterSubTextView(@NonNull TextView view) {

    }

    public void toolbarCenterCustomView(@NonNull View view) {

    }

    public void setToolbarTitle(String title) {
        if (toolbar != null && toolbar.getCenterTextView() != null) {
            toolbar.getCenterTextView().setText(title);
        }
    }

    public void setToolbarSubTitle(String title) {
        if (toolbar != null && toolbar.getCenterSubTextView() != null) {
            toolbar.getCenterSubTextView().setText(title);
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        darkStatusBar();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        isEnterAnimationEnd.set(true);
        if (onEnterAnimationEndRunnable != null) {
            post(onEnterAnimationEndRunnable);
        }
    }

    protected synchronized void postOnEnterAnimationEnd(Runnable runnable) {
        if (isEnterAnimationEnd.get()) {
            if (runnable != null) {
                post(runnable);
            }
            this.onEnterAnimationEndRunnable = null;
        } else {
            this.onEnterAnimationEndRunnable = runnable;
        }

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
