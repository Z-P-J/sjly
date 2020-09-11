package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.cb.ratingbar.CBRatingBar;
import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.utils.Callback;

public class AppRatingDialogFragment extends CenterDialogFragment {

    private float starProgress = 60;
    private AppDetailInfo appDetailInfo;
    private Callback<Float> callback;

    public static AppRatingDialogFragment with(Context context) {
        return new AppRatingDialogFragment();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_popup_rating;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

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

    public AppRatingDialogFragment setStarProgress(float starProgress) {
        this.starProgress = starProgress;
        return this;
    }

    public AppRatingDialogFragment setAppDetailInfo(AppDetailInfo appDetailInfo) {
        this.appDetailInfo = appDetailInfo;
        return this;
    }

    public AppRatingDialogFragment setCallback(Callback<Float> callback) {
        this.callback = callback;
        return this;
    }
}
