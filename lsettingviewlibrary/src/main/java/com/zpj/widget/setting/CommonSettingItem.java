package com.zpj.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.widget.R;

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

    @Override
    public void inflateLeftIcon(ViewStub viewStub) {
        if (mLeftIcon != null) {
            viewStub.setLayoutResource(R.layout.z_setting_left_icon);
            viewStub.setInflatedId(R.id.iv_left_icon);
            viewStub.inflate();
        }
    }

    @Override
    public void inflateRightContainer(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.z_setting_right_container_arrow);
        viewStub.setInflatedId(R.id.iv_right_icon);
        ImageView view = (ImageView) viewStub.inflate();
        if (mRightIcon != null) {
            view.setImageDrawable(mRightIcon);
        }
    }

    @Override
    public void inflateInfoButton(ViewStub viewStub) {
        if (showInfoButton) {
            viewStub.setLayoutResource(R.layout.z_setting_info_btn);
            viewStub.setInflatedId(R.id.iv_info_btn);
            viewStub.inflate();
        }
    }

    @Override
    public void inflateRightText(ViewStub viewStub) {
        if (showRightText) {
            viewStub.setLayoutResource(R.layout.z_setting_right_text);
            viewStub.setInflatedId(R.id.tv_right_text);
            TextView view = (TextView) viewStub.inflate();
            view.setText(mRightText);
        }
    }

}

