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
import com.zpj.shouji.market.utils.BrightnessUtils;
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

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        IndicatorSeekBar seekBar = findViewById(R.id.seek_bar);

        CheckLayout checkLayout = findViewById(R.id.check_layout);
        checkLayout.setChecked(PrefsHelper.with().getBoolean(Keys.SYSTEM_BRIGHTNESS, false));
        checkLayout.setOnItemClickListener(item -> {
            PrefsHelper.with().putBoolean(Keys.SYSTEM_BRIGHTNESS, item.isChecked());
            seekBar.setEnabled(!item.isChecked());
            if (item.isChecked()) {
                getActivity(new Callback<MainActivity>() {
                    @Override
                    public void onCallback(MainActivity activity) {
                        BrightnessUtils.setAutoBrightness(activity);
                    }
                });
//                if (BrightnessUtils.getBrightnessMode(context) == 1) {
//                    BrightnessUtils.setAutoBrightness();
//                } else {
//                    float brightness = BrightnessUtils.getSystemBrightness(context);
//                    seekBar.setProgress(brightness);
//                    PrefsHelper.with().putFloat(Keys.APP_BRIGHTNESS, brightness);
//                }
            } else {
                seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
            }
        });


        seekBar.setEnabled(!checkLayout.isChecked());
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                if (seekBar.isEnabled()) {
                    getActivity(activity -> {
                        BrightnessUtils.setBrightness(activity, seekParams.progressFloat);
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        getActivity(activity -> {
//            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//            seekBar.setProgress(lp.screenBrightness * 100);
//            if (checkLayout.isChecked()) {
//                seekBar.setProgress(BrightnessUtils.getSystemBrightness(context));
//            } else {
//                seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
//            }
            seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
        });

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

}
