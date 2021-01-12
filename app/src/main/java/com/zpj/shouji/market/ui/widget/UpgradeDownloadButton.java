package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.download.AppDownloadMission;

import java.util.Locale;

public class UpgradeDownloadButton extends DownloadButton {

    public UpgradeDownloadButton(Context context) {
        super(context);
    }

    public UpgradeDownloadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpgradeDownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onBindMission(AppDownloadMission mission) {
        if (mission.isIniting()) {
            setText(R.string.text_preparing);
        } else if (mission.isRunning()) {
            if (mission.getProgress() < 10) {
                setText(mission.getProgressStr());
            } else {
                setText(String.format(Locale.US, "%.1f%%", mission.getProgress()));
            }
        } else if (mission.isFinished()) {
            if (mission.getFile().exists()) {
                if (mission.isUpgrade()) {
                    setText(R.string.text_upgrade);
                } else if (mission.isInstalled()) {
                    setText(R.string.text_open);
                } else {
                    setText(R.string.text_install);
                }
            } else {
                mission.delete();
                onDelete();
            }
        } else if (mission.isWaiting()) {
            setText(R.string.text_waiting);
        } else {
            setText(R.string.text_continue);
        }
    }
}
