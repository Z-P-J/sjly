package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cb.ratingbar.CBRatingBar;
import com.felix.atoast.library.AToast;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.interfaces.OnDismissListener;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.GetMainActivityEvent;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.activity.MainActivity;
import com.zpj.shouji.market.ui.widget.CheckLayout;
import com.zpj.shouji.market.utils.BrightnessUtils;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.utils.PrefsHelper;
import com.zpj.widget.toolbar.ZToolBar;

public class AppRatingPopup extends CenterPopup<AppRatingPopup> {

    private float starProgress = 60;
    private AppDetailInfo appDetailInfo;
    private Callback<Float> callback;

    public static AppRatingPopup with(Context context) {
        return new AppRatingPopup(context);
    }

    public AppRatingPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_rating;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        CBRatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setStarProgress(starProgress);
        ratingBar.setOnStarTouchListener(new CBRatingBar.OnStarTouchListener() {
            @Override
            public void onStarTouch(int i) {
                ratingBar.setStarProgress(i * 20);
            }
        });

        findViewById(R.id.tv_submit).setOnClickListener(v -> {
            ShowLoadingEvent.post("评分中...");
            HttpApi.appRatingApi(appDetailInfo.getId(), String.valueOf((int) ratingBar.getStarProgress() / 20), appDetailInfo.getAppType(), appDetailInfo.getPackageName(), appDetailInfo.getVersion())
                    .onSuccess(doc -> {
                        Log.d("AppRatingPopup", "doc=" + doc);
                        if ("success".equals(doc.selectFirst("result").text())) {
                            if (callback != null) {
                                callback.onCallback(ratingBar.getStarProgress());
                            }
                            AToast.success("评分成功！");
                            HideLoadingEvent.post(500, this::dismiss);
                        } else {
                            AToast.success("评分失败！");
                        }
                    })
                    .onError(throwable -> AToast.error("出错了！" + throwable.getMessage()))
                    .subscribe();
        });

    }

    public AppRatingPopup setStarProgress(float starProgress) {
        this.starProgress = starProgress;
        return self();
    }

    public AppRatingPopup setAppDetailInfo(AppDetailInfo appDetailInfo) {
        this.appDetailInfo = appDetailInfo;
        return self();
    }

    public AppRatingPopup setCallback(Callback<Float> callback) {
        this.callback = callback;
        return self();
    }
}
