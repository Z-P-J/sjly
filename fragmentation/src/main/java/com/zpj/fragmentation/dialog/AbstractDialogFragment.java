package com.zpj.fragmentation.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.DialogFragmentAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;

public abstract class AbstractDialogFragment extends SupportFragment {

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    protected abstract boolean onBackPressed();

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId() > 0) {
            view = inflater.inflate(getLayoutId(), container, false);
        } else {
            view = super.onCreateView(inflater, container, savedInstanceState);
        }
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public final FragmentAnimator getFragmentAnimator() {
        return new DialogFragmentAnimator();
    }

    @Override
    public final FragmentAnimator onCreateFragmentAnimator() {
        return new DialogFragmentAnimator();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public final boolean onBackPressedSupport() {
        if (onBackPressed()) {
            return true;
        }
        pop();
        return true;
    }
}
