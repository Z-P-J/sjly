package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.ui.widget.CheckLayout;
import com.zpj.shouji.market.utils.BrightnessUtils;
import com.zpj.utils.PrefsHelper;

public class BrightnessDialogFragment extends CenterDialogFragment {

//    private MainActivity activity;

    public static BrightnessDialogFragment with(Context context) {
        return new BrightnessDialogFragment();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_brightness;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        IndicatorSeekBar seekBar = findViewById(R.id.seek_bar);

        CheckLayout checkLayout = findViewById(R.id.check_layout);
        checkLayout.setChecked(PrefsHelper.with().getBoolean(Keys.SYSTEM_BRIGHTNESS, false));
        checkLayout.setOnItemClickListener(item -> {
            PrefsHelper.with().putBoolean(Keys.SYSTEM_BRIGHTNESS, item.isChecked());
            seekBar.setEnabled(!item.isChecked());
            if (item.isChecked()) {
//                getActivity(new Callback<MainActivity>() {
//                    @Override
//                    public void onCallback(MainActivity activity) {
//                        BrightnessUtils.setAutoBrightness(activity);
//                    }
//                });
                BrightnessUtils.setAutoBrightness(_mActivity);
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
//                    getActivity(activity -> {
//                        BrightnessUtils.setBrightness(activity, seekParams.progressFloat);
//                    });
                    BrightnessUtils.setBrightness(_mActivity, seekParams.progressFloat);
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

//        getActivity(activity -> {
////            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
////            seekBar.setProgress(lp.screenBrightness * 100);
////            if (checkLayout.isChecked()) {
////                seekBar.setProgress(BrightnessUtils.getSystemBrightness(context));
////            } else {
////                seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
////            }
//            seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
//        });
        seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
    }

//    private void getActivity(Callback<MainActivity> callback) {
//        if (activity != null) {
//            if (callback != null) {
//                callback.onCallback(activity);
//            }
//            return;
//        }
//        GetMainActivityEvent.post(obj -> {
//            activity = obj;
//            if (callback != null) {
//                callback.onCallback(obj);
//            }
//        });
//    }

}
