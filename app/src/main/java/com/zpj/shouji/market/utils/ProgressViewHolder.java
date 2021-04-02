package com.zpj.shouji.market.utils;

import android.content.Context;
import android.graphics.Color;

import com.zpj.fragmentation.dialog.interfaces.IProgressViewHolder;
import com.zpj.progressbar.ZProgressBar;

public class ProgressViewHolder implements IProgressViewHolder<ZProgressBar> {

    @Override
    public ZProgressBar createProgressView(Context context) {
        ZProgressBar progressBar = new ZProgressBar(context);
        progressBar.setProgressBarColor(Color.parseColor("#2ad181"));
        return progressBar;
    }

    @Override
    public void onProgressChanged(ZProgressBar progressView, float progress) {
        if (progressView.isIntermediateMode()) {
            progressView.setIsIntermediateMode(false);
//            progressView.setBorderColor(Color.parseColor("#c0f2d9"));
        }
        progressView.setProgress(progress * progressView.getMaxProgress());
    }

}
