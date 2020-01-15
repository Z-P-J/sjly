package it.liuting.imagetrans.listener;

import android.content.Context;
import android.view.View;

/**
 * Created by liuting on 18/3/19.
 */

public interface ProgressViewGet<T extends View> {

    T getProgress(Context context);

    void onProgressChange(T view, float progress);
}
