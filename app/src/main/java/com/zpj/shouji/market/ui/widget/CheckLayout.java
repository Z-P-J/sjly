package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.zpj.shouji.market.R;
import com.zpj.widget.setting.SwitchSettingItem;
import com.zpj.widget.switcher.BaseSwitcher;

public class CheckLayout extends SwitchSettingItem {

    public CheckLayout(Context context) {
        this(context, null);
    }

    public CheckLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int padding = getResources().getDimensionPixelSize(R.dimen.z_setting_item_default_padding);
        setPadding(padding, padding, padding, padding);
        setBackground(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void inflateLeftIcon(ViewStub viewStub) {
        viewStub.setLayoutResource(R.layout.z_setting_right_container_switch);
        viewStub.setInflatedId(R.id.switcher);
        viewStub.inflate();
    }

    @Override
    public void inflateRightContainer(ViewStub viewStub) {

    }

    @Override
    public void onInflate(ViewStub stub, View inflated) {
        super.onInflate(stub, inflated);
        if (stub == vsLeftIcon && inflated instanceof BaseSwitcher) {
            switcher = (BaseSwitcher) inflated;
            switcher.setOnClickListener(v -> onItemClick());
            switcher.setOnColor(getResources().getColor(R.color.switcher_checked_color));
            switcher.setOffColor(getResources().getColor(R.color.switcher_normal_color));
            switcher.setEnabled(isEnabled());
        }
    }
}
