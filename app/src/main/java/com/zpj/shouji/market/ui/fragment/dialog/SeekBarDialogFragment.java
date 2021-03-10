package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zpj.fragmentation.dialog.base.CardDialogFragment;
import com.zpj.shouji.market.R;

public class SeekBarDialogFragment extends CardDialogFragment {

    private String title;
    private int max;
    private int min;
    private int progress;
    private OnSeekProgressChangeListener onSeekProgressChangeListener;

    public static SeekBarDialogFragment with(Context context) {
        return new SeekBarDialogFragment();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_seek_bar;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);

        IndicatorSeekBar seekBar = findViewById(R.id.seek_bar);

        seekBar.setMin(min);
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        seekBar.setTickCount(max - min + 1);
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                if (onSeekProgressChangeListener != null) {
                    onSeekProgressChangeListener.onSeek(seekParams.progress);
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
    }

    public SeekBarDialogFragment setTitle(String title) {
        this.title = title;
        return this;
    }

    public SeekBarDialogFragment setMax(int max) {
        this.max = max;
        return this;
    }

    public SeekBarDialogFragment setMin(int min) {
        this.min = min;
        return this;
    }

    public SeekBarDialogFragment setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public SeekBarDialogFragment setOnSeekProgressChangeListener(OnSeekProgressChangeListener onSeekProgressChangeListener) {
        this.onSeekProgressChangeListener = onSeekProgressChangeListener;
        return this;
    }

    public interface OnSeekProgressChangeListener {
        public void onSeek(int progress);
    }

}
