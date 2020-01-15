package com.zpj.shouji.market.image;

import android.content.Context;
import android.view.ViewGroup;

import com.mingle.widget.ShapeLoadingView;
import com.zpj.utils.ScreenUtil;

import it.liuting.imagetrans.listener.ProgressViewGet;

/**
 * Created by liuting on 18/3/19.
 */

public class MyProgressBarGet implements ProgressViewGet<ShapeLoadingView> {
    @Override
    public ShapeLoadingView getProgress(Context context) {
        ShapeLoadingView loadingView = new ShapeLoadingView(context);
//        RingLoadingView view = new RingLoadingView(context);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ScreenUtil.dp2pxInt(context, 50), ScreenUtil.dp2pxInt(context, 50)));
        return loadingView;
    }

    @Override
    public void onProgressChange(ShapeLoadingView view, float progress) {
//        view.setProgress(progress);
    }
}
