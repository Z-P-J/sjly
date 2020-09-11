package com.zpj.fragmentation.dialog.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.popup.R;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.utils.ScreenUtils;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Description: 加载对话框
 * Create by dance, at 2018/12/16
 */
public class LoadingDialogFragment extends CenterDialogFragment {

    private TextView tvTitle;

    private String title;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_center_impl_loading;
    }

    @Override
    protected boolean onBackPressed() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        cancelable = false;
        cancelableInTouchOutside = false;
        tvTitle = findViewById(R.id.tv_title);
        setup();
    }

    @Override
    protected int getMaxWidth() {
        return WRAP_CONTENT;
    }

    protected void setup() {
        if (title != null && tvTitle != null) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
    }

    public LoadingDialogFragment setTitle(String title) {
        this.title = title;
        setup();
        return this;
    }

}
