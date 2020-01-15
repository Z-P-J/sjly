package it.liuting.imagetrans.listener;

import android.view.View;

/**
 * Created by liuting on 18/3/14.
 */

public interface OnActionListener {
    boolean onClick(View v);

    void onPullRange(float range);

    void onPullCancel();

    void onStartClose();
}
