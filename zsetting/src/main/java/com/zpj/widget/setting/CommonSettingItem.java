package com.zpj.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.widget.setting.R;

public class CommonSettingItem extends ZSettingItem<CommonSettingItem> {

    public CommonSettingItem(Context context) {
        this(context, null);
    }

    public CommonSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}

