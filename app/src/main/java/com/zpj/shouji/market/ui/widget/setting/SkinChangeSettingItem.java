package com.zpj.shouji.market.ui.widget.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.SkinChangeAnimation;
import com.zpj.widget.setting.SwitchSettingItem;
import com.zxy.skin.sdk.SkinEngine;

public class SkinChangeSettingItem extends SwitchSettingItem
        implements View.OnTouchListener {

    private float lastX;
    private float lastY;

    public SkinChangeSettingItem(Context context) {
        this(context, null);
    }

    public SkinChangeSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinChangeSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        setOnLongClickListener(v -> true);
    }

    @Override
    public void onItemClick() {
        super.onItemClick();
        SkinChangeAnimation.with(getContext())
                .setStartPosition(lastX, lastY)
                .setStartRadius(0)
                .setDuration(500)
                .start();
        SkinEngine.changeSkin(AppConfig.isNightMode() ? R.style.DayTheme : R.style.NightTheme);
        AppConfig.toggleThemeMode();
//        SkinChangeEvent.post(AppConfig.isNightMode());
        EventBus.sendSkinChangeEvent();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getRawX();
            lastY = event.getRawY();
        }
        return false;
    }

}
