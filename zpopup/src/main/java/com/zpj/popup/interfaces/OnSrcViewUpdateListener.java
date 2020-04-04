package com.zpj.popup.interfaces;

import android.support.annotation.NonNull;

import com.zpj.popup.core.ImageViewerPopupView;

/**
 * Description:
 * Create by dance, at 2019/1/29
 */
public interface OnSrcViewUpdateListener {
    void onSrcViewUpdate(@NonNull ImageViewerPopupView popupView, int position);
}
