package com.zpj.fragmentation;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zpj.fragmentation.queue.Action;
import com.zpj.fragmentation.queue.ActionQueue;
import com.zpj.fragmentation.queue.BlockActionQueue;
import com.zpj.fragmentation.swipeback.SwipeBackFragment;
import com.zpj.fragmentation.swipeback.SwipeBackLayout;
import com.zpj.utils.StatusBarUtils;
import com.zpj.widget.toolbar.ZToolBar;

import java.util.concurrent.atomic.AtomicBoolean;

import com.zpj.fragmentation.R;

public abstract class BaseFragment extends SwipeBackFragment {


//    private final BlockActionQueue mSupportVisibleActionQueue;
//    private final BlockActionQueue mEnterAnimationEndActionQueue;


    protected ZToolBar toolbar;

//    public BaseFragment() {
//        Handler handler = new Handler(Looper.getMainLooper());
//        mSupportVisibleActionQueue = new BlockActionQueue(handler);
//        mEnterAnimationEndActionQueue = new BlockActionQueue(handler);
//    }

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (context == null) {
            context = getContext();
        }
        if (getLayoutId() > 0) {
            view = inflater.inflate(getLayoutId(), container, false);
            toolbar = view.findViewById(R.id.tool_bar);
            setToolbarTitle(getToolbarTitle(context));
            setToolbarSubTitle(getToolbarSubTitle(context));
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
            SwipeBackLayout.EdgeLevel level = getEdgeLevel();
            setEdgeLevel(level == null ? SwipeBackLayout.EdgeLevel.MAX : level);
            return attachToSwipeBack(view);
        } else {
            return view;
        }
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected boolean supportSwipeBack() {
        return false;
    }

    public SwipeBackLayout.EdgeLevel getEdgeLevel() {
        return SwipeBackLayout.EdgeLevel.MAX;
    }

    public CharSequence getToolbarTitle(Context context) {
        return null;
    }

    public CharSequence getToolbarSubTitle(Context context) {
        return null;
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

    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

//    @Override
//    public void onEnterAnimationEnd(Bundle savedInstanceState) {
//        super.onEnterAnimationEnd(savedInstanceState);
//        mEnterAnimationEndActionQueue.start();
//    }
//
//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        mSupportVisibleActionQueue.start();
//    }
//
//    @Override
//    public void onSupportInvisible() {
//        super.onSupportInvisible();
//        mSupportVisibleActionQueue.stop();
//    }
//
//    @Override
//    public void onDestroy() {
//        mEnterAnimationEndActionQueue.onDestroy();
//        mSupportVisibleActionQueue.onDestroy();
//        super.onDestroy();
//    }

    public void setToolbarTitle(@StringRes int titleRes) {
        if (toolbar != null && toolbar.getCenterTextView() != null) {
            toolbar.getCenterTextView().setText(titleRes);
        }
    }

    public void setToolbarSubTitle(@StringRes int titleRes) {
        if (toolbar != null && toolbar.getCenterSubTextView() != null) {
            toolbar.getCenterSubTextView().setText(titleRes);
        }
    }

    public void setToolbarTitle(CharSequence title) {
        if (toolbar != null && toolbar.getCenterTextView() != null) {
            toolbar.getCenterTextView().setText(title);
        }
    }

    public void setToolbarSubTitle(CharSequence title) {
        if (toolbar != null && toolbar.getCenterSubTextView() != null) {
            toolbar.getCenterSubTextView().setText(title);
        }
    }

//    protected synchronized void postOnEnterAnimationEnd(final Runnable runnable) {
//        mEnterAnimationEndActionQueue.post(runnable);
//    }
//
//    protected synchronized void postOnEnterAnimationEndDelayed(final Runnable runnable, long delay) {
//        mEnterAnimationEndActionQueue.postDelayed(runnable, delay);
//    }
//
//    protected synchronized void postOnSupportVisible(final Runnable runnable) {
//        mSupportVisibleActionQueue.post(runnable);
//    }
//
//    protected synchronized void postOnSupportVisibleDelayed(final Runnable runnable, long delay) {
//        mSupportVisibleActionQueue.postDelayed(runnable, delay);
//    }




}
