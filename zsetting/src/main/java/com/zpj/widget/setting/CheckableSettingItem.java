package com.zpj.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.zpj.widget.switcher.BaseSwitcher;
import com.zpj.widget.switcher.OnCheckedChangeListener;

abstract class CheckableSettingItem extends CommonSettingItem {

    protected BaseSwitcher switcher;

    public CheckableSettingItem(Context context) {
        this(context, null);
    }

    public CheckableSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onInflate(ViewStub stub, View inflated) {
        super.onInflate(stub, inflated);
        if (stub == vsRightContainer && inflated instanceof BaseSwitcher) {
            switcher = (BaseSwitcher) inflated;
            switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onChange(boolean checked) {
                    CheckableSettingItem.super.onItemClick();
                }
            });
        }
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

