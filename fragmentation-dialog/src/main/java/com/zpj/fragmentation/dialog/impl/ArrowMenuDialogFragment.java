package com.zpj.fragmentation.dialog.impl;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zpj.fragmentation.dialog.base.ArrowDialogFragment;
import com.zpj.fragmentation.dialog.model.OptionMenu;
import com.zpj.fragmentation.dialog.widget.OptionMenuView;
import com.zpj.fragmentation.dialog.widget.PopHorizontalScrollView;
import com.zpj.fragmentation.dialog.widget.PopVerticalScrollView;
import com.zpj.utils.ScreenUtils;

import java.util.List;

public class ArrowMenuDialogFragment extends ArrowDialogFragment {

    private int menuRes = 0;

    private List<OptionMenu> optionMenus;

    private int mOrientation = LinearLayout.VERTICAL;

    private OnItemClickListener onItemClickListener;

    @Override
    protected int getContentLayoutId() {
        return 0;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        OptionMenuView mOptionMenuView = new OptionMenuView(context);
        if (mOrientation == LinearLayout.VERTICAL) {
            mOptionMenuView.setMinimumWidth((int) (ScreenUtils.getScreenWidth(context) / 2.8));
        }
        mOptionMenuView.setOrientation(mOrientation);
        mOptionMenuView.setOnOptionMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
            @Override
            public boolean onOptionMenuClick(int position, OptionMenu menu) {
                dismiss();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, menu);
                }
                return true;
            }
        });

        ViewGroup scrollView = getScrollView(mOptionMenuView.getOrientation());
        scrollView.addView(mOptionMenuView);
        mPopLayout.addView(scrollView);


        if (menuRes > 0) {
            mOptionMenuView.inflate(menuRes, new MenuBuilder(context));
        } else {
            mOptionMenuView.setOptionMenus(optionMenus);
        }


    }

    private ViewGroup getScrollView(int orientation) {
        ViewGroup scrollView;
        if (orientation == LinearLayout.HORIZONTAL) {
            scrollView = new PopHorizontalScrollView(getContext());
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setVerticalScrollBarEnabled(false);
        } else {
            scrollView = new PopVerticalScrollView(getContext());
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setVerticalScrollBarEnabled(false);
        }
        return scrollView;
    }



    //----------------------------------setter----------------------------------

    public ArrowMenuDialogFragment setMenuRes(int menuRes) {
        this.menuRes = menuRes;
        return this;
    }

    public ArrowMenuDialogFragment setOptionMenus(List<OptionMenu> optionMenus) {
        this.optionMenus = optionMenus;
        return this;
    }

    public ArrowMenuDialogFragment setOrientation(int orientation) {
        this.mOrientation = orientation;
        return this;
    }

    public ArrowMenuDialogFragment setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

}
