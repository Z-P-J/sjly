package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.R;

/**
 * Description: 加载对话框
 * Create by dance, at 2018/12/16
 */
public class LoadingPopup extends CenterPopup {
    private TextView tv_title;
    public LoadingPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId != 0 ? bindLayoutId : R.layout._xpopup_center_impl_loading;
    }

    /**
     * 绑定已有布局
     * @param layoutId 如果要显示标题，则要求必须有id为tv_title的TextView，否则无任何要求
     * @return
     */
    public LoadingPopup bindLayout(int layoutId){
        bindLayoutId = layoutId;
        return this;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        tv_title = findViewById(R.id.tv_title);
        setup();
    }

    protected void setup(){
        if(title!=null && tv_title!=null){
            tv_title.setVisibility(VISIBLE);
            tv_title.setText(title);
        }
    }

    private String title;
    public LoadingPopup setTitle(String title){
        this.title = title;
        setup();
        return this;
    }
}
