package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zpj.popup.core.CenterPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.GetMainActivityEvent;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.shouji.market.ui.widget.CheckLayout;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.utils.PrefsHelper;
import com.zpj.widget.toolbar.ZToolBar;

public class BrightnessPopup extends CenterPopup<BrightnessPopup> {

    private MainActivity activity;

    public static BrightnessPopup with(Context context) {
        return new BrightnessPopup(context);
    }

    public BrightnessPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_brightness;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        ZToolBar toolBar = findViewById(R.id.tool_bar);
        toolBar.setRightButtonTint(getResources().getColor(R.color.light_gray_10));
        toolBar.getRightImageButton().setOnClickListener(v -> dismiss());

        IndicatorSeekBar seekBar = findViewById(R.id.seek_bar);

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                getActivity(activity -> {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.screenBrightness = seekParams.progressFloat / 100;
                    PrefsHelper.with().putFloat(Keys.APP_BRIGHTNESS, seekParams.progressFloat);
                    activity.getWindow().setAttributes(lp);
                });
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        CheckLayout checkLayout = findViewById(R.id.check_layout);
        checkLayout.setChecked(PrefsHelper.with().getBoolean(Keys.SYSTEM_BRIGHTNESS, false));
        checkLayout.setOnItemClickListener(item -> {
            PrefsHelper.with().putBoolean(Keys.SYSTEM_BRIGHTNESS, item.isChecked());
            seekBar.setEnabled(!item.isChecked());
            if (item.isChecked()) {
                float brightness = getSystemBrightness();
                seekBar.setProgress(brightness);
                PrefsHelper.with().putFloat(Keys.APP_BRIGHTNESS, brightness);
            }
        });

        getActivity(activity -> {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            seekBar.setProgress(lp.screenBrightness * 100);
            if (checkLayout.isChecked()) {
                seekBar.setProgress(getSystemBrightness());
            } else {
                seekBar.setProgress(PrefsHelper.with().getFloat(Keys.APP_BRIGHTNESS, getSystemBrightness()));
            }
        });

        seekBar.setEnabled(!checkLayout.isChecked());

    }

    private void getActivity(Callback<MainActivity> callback) {
        if (activity != null) {
            if (callback != null) {
                callback.onCallback(activity);
            }
            return;
        }
        GetMainActivityEvent.post(obj -> {
            activity = obj;
            if (callback != null) {
                callback.onCallback(obj);
            }
        });
    }

    private float getSystemBrightness(){
        int screenBrightness=255;
        try{
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e){
            e.printStackTrace();
        }
        return (float) screenBrightness / 255 * 100;
    }

}
