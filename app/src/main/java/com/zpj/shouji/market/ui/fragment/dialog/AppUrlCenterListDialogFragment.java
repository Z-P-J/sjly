package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.ScaleAlphaAnimator;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;
import com.zpj.fragmentation.dialog.impl.ListDialogFragment;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.multidata.AppUrlMultiData;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class AppUrlCenterListDialogFragment extends ListDialogFragment<MultiData<?>> {

    private View anchorView;

    public static AppUrlCenterListDialogFragment with() {
        return new AppUrlCenterListDialogFragment();
    }
    
    public AppUrlCenterListDialogFragment() {
        setMaxWidth(MATCH_PARENT);
        if (!isDragDialog()) {
            float screenWidth = ScreenUtils.getScreenWidth();
            float screenHeight = ScreenUtils.getScreenHeight();
            int marginHorizontal = (int) (screenWidth * 0.08f);
            setMarginHorizontal(marginHorizontal);
            int marginVertical = (int) (screenHeight * 0.12f);
            setMarginVertical(marginVertical);
        }
    }

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        if (anchorView == null) {
            return new ScaleAlphaAnimator(contentView, PopupAnimation.ScaleAlphaFromRightBottom);
        }
        int[] contentLocation = new int[2];
        contentView.getLocationInWindow(contentLocation);

        int[] anchorLocation = new int[2];
        anchorView.getLocationInWindow(anchorLocation);

        float pivotX = anchorLocation[0] + anchorView.getMeasuredWidth() / 2f - contentLocation[0];
        float pivotY = anchorLocation[1] + anchorView.getMeasuredHeight() / 2f - contentLocation[1];
        return new ScaleAlphaAnimator(contentView, pivotX, pivotY);

    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void initRecyclerView(RecyclerView recyclerView, List<MultiData<?>> list) {
        MultiRecyclerViewWrapper.with(recyclerView)
                .setData(list)
                .build();
    }

    public AppUrlCenterListDialogFragment setAnchorView(View anchorView) {
        this.anchorView = anchorView;
        return this;
    }

    public AppUrlCenterListDialogFragment setAppDetailInfo(AppDetailInfo info) {
        setTitle(info.getName());
        List<MultiData<?>> multiDataList = new ArrayList<>();
        for (AppDetailInfo.AppUrlInfo appUrlInfo : info.getAppUrlInfoList()) {
            AppUrlMultiData data = new AppUrlMultiData(info, appUrlInfo);
            if (info.getAppUrlInfoList().size() == 1) {
                data.setExpand(true);
            }
            multiDataList.add(data);
        }
        setData(multiDataList);
        return this;
    }

}
