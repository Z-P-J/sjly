package com.zpj.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;

public class CheckSettingItem extends CheckableSettingItem {

    public CheckSettingItem(Context context) {
        this(context, null);
    }

    public CheckSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflateRightContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.z_setting_right_container_check);
        viewStub.setInflatedId(R.id.check);
        viewStub.inflate();
    }
}

