package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.ScaleAlphaAnimator;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;
import com.zpj.fragmentation.dialog.impl.CenterListDialogFragment;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.widget.DownloadButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUrlCenterListDialogFragment extends CenterListDialogFragment<AppDetailInfo.AppUrlInfo> {

    private final Map<Integer, ExpansionLayout.Listener> listenerMap = new HashMap<>();
    private final SparseBooleanArray expandedArray = new SparseBooleanArray();

    private View anchorView;

    private AppDetailInfo detailInfo;

    public static AppUrlCenterListDialogFragment with(Context context) {
        return new AppUrlCenterListDialogFragment();
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
    protected int getItemRes() {
        return R.layout.item_app_url;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
//        getContentView().setBackgroundResource(R.drawable.bg_center_dialog);
        tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<AppDetailInfo.AppUrlInfo> list, int position, List<Object> payloads) {
        AppDetailInfo.AppUrlInfo appUrlInfo = list.get(position);
        holder.setText(R.id.tv_title, appUrlInfo.getUrlName());
        String content = "文件Md5:" + appUrlInfo.getMd5() + "\n";
        holder.setText(R.id.tv_content,content +  appUrlInfo.getMore());
        ExpansionLayout expansionLayout = holder.getView(R.id.layout_expansion);

        for (ExpansionLayout.Listener oldListener : listenerMap.values()) {
            expansionLayout.removeListener(oldListener);
        }

        // TODO 以下写法是不得已而为之，建议将ExpansionPanel克隆下来修改并引入项目或者自己实现一个类似的控件
        boolean expanded = expandedArray.get(position, false);
        if (expanded) {
            expansionLayout.expand(false);
        } else {
            expansionLayout.collapse(false);
        }
        ExpansionLayout.Listener listener;
        if (listenerMap.containsKey(position)) {
            listener = listenerMap.get(position);
        } else {
            listener = (expansionLayout1, expanded1) -> {
                expandedArray.put(position, expanded1);
            };
            listenerMap.put(position, listener);
        }
        expansionLayout.addListener(listener);
        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        String id = appUrlInfo.getUrlAdress().substring(appUrlInfo.getUrlAdress().lastIndexOf("id=") + 3);
//        detailInfo.getId()
        downloadButton.bindApp(id, detailInfo.getName(),
                detailInfo.getPackageName(), detailInfo.getAppType(),
                detailInfo.getIconUrl(), appUrlInfo.getYunUrl());
//        holder.setOnClickListener(R.id.tv_download, v -> {
//            if (TextUtils.isEmpty(appUrlInfo.getYunUrl())) {
//                dismiss();
//                ZToast.normal("TODO下载" + appUrlInfo.getUrlAdress());
//            } else {
//                dismiss();
//                WebFragment.start(appUrlInfo.getYunUrl());
//            }
//
//        });
    }

    public AppUrlCenterListDialogFragment setAnchorView(View anchorView) {
        this.anchorView = anchorView;
        return this;
    }

    public AppUrlCenterListDialogFragment setAppDetailInfo(AppDetailInfo info) {
        setTitle(info.getName());
        setData(info.getAppUrlInfoList());
        this.detailInfo = info;
        return this;
    }

}
