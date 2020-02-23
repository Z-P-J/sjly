package com.zpj.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;

import com.zpj.widget.R;
import com.zpj.widget.switcher.OnCheckedChangeListener;
import com.zpj.widget.switcher.SwitcherX;

public class SwitchSettingItem extends CommonSettingItem {

    private SwitcherX switcher;

    public SwitchSettingItem(Context context) {
        this(context, null);
    }

    public SwitchSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initAttribute(Context context, AttributeSet attrs) {
        super.initAttribute(context, attrs);
    }

    @Override
    public void inflateRightContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.z_setting_right_container_switch);
        viewStub.setInflatedId(R.id.switcher);
        switcher = (SwitcherX) viewStub.inflate();
        switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                SwitchSettingItem.super.onItemClick();
            }
        });
    }

    @Override
    public void onItemClick() {
        super.onItemClick();
        setChecked(!switcher.isChecked());
    }

    public boolean isChecked() {
        return switcher.isChecked();
    }

    public void setChecked(boolean isChecked) {
        setChecked(isChecked, true);
    }

    public void setChecked(boolean isChecked, boolean withAnimation) {
        switcher.setChecked(isChecked, withAnimation);
    }

}

