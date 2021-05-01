package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zpj.fragmentation.dialog.base.CardDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.CheckLayout;
import com.zpj.utils.BrightnessUtils;

public class BrightnessDialogFragment extends CardDialogFragment<BrightnessDialogFragment> {

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
        checkLayout.setChecked(BrightnessUtils.isSystemBrightness());
        checkLayout.setOnItemClickListener(item -> {
            BrightnessUtils.setSystemBrightness(item.isChecked());
            seekBar.setEnabled(!item.isChecked());
            if (item.isChecked()) {
                BrightnessUtils.setAutoBrightness(_mActivity);
            } else {
                seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
            }
        });


        seekBar.setEnabled(!checkLayout.isChecked());
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                if (seekBar.isEnabled()) {
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

        seekBar.setProgress(BrightnessUtils.getAppBrightness(context));
    }

}
