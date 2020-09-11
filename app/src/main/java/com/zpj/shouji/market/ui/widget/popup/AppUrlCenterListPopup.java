//package com.zpj.shouji.market.ui.widget.popup;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//import android.util.SparseBooleanArray;
//import android.view.View;
//
//import com.felix.atoast.library.AToast;
//import com.github.florent37.expansionpanel.ExpansionLayout;
//import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;
//import com.zpj.popup.animator.PopupAnimator;
//import com.zpj.popup.animator.ScaleAlphaAnimator;
//import com.zpj.popup.animator.ScrollScaleAnimator;
//import com.zpj.popup.enums.PopupAnimation;
//import com.zpj.popup.impl.CenterListPopup2;
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.model.AppDetailInfo;
//import com.zpj.shouji.market.ui.fragment.WebFragment;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class AppUrlCenterListPopup extends CenterListPopup2<AppDetailInfo.AppUrlInfo> {
//
//    private final Map<Integer, ExpansionLayout.Listener> listenerMap = new HashMap<>();
//    private final SparseBooleanArray expandedArray = new SparseBooleanArray();
//
//    public static AppUrlCenterListPopup with(Context context) {
//        return new AppUrlCenterListPopup(context);
//    }
//
//    public AppUrlCenterListPopup(@NonNull Context context) {
//        super(context);
//        bindItemLayoutId = R.layout.item_app_url;
//    }
//
//    @Override
//    protected void initPopupContent() {
//        super.initPopupContent();
//        tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
//    }
//
//    @Override
//    public void onBindViewHolder(EasyViewHolder holder, List<AppDetailInfo.AppUrlInfo> list, int position, List<Object> payloads) {
//        AppDetailInfo.AppUrlInfo appUrlInfo = list.get(position);
//        holder.setText(R.id.tv_title, appUrlInfo.getUrlName());
//        String content = "文件Md5:" + appUrlInfo.getMd5() + "\n";
//        holder.setText(R.id.tv_content,content +  appUrlInfo.getMore());
//        ExpansionLayout expansionLayout = holder.getView(R.id.layout_expansion);
//
//        for (ExpansionLayout.Listener oldListener : listenerMap.values()) {
//            expansionLayout.removeListener(oldListener);
//        }
//
//        // TODO 以下写法是不得已而为之，建议将ExpansionPanel克隆下来修改并引入项目
//        boolean expanded = expandedArray.get(position, false);
//        if (expanded) {
//            expansionLayout.expand(false);
//        } else {
//            expansionLayout.collapse(false);
//        }
//        ExpansionLayout.Listener listener;
//        if (listenerMap.containsKey(position)) {
//            listener = listenerMap.get(position);
//        } else {
//            listener = (expansionLayout1, expanded1) -> {
//                expandedArray.put(position, expanded1);
//            };
//            listenerMap.put(position, listener);
//        }
//        expansionLayout.addListener(listener);
//        holder.setOnClickListener(R.id.tv_download, v -> {
//            if (TextUtils.isEmpty(appUrlInfo.getYunUrl())) {
//                dismiss();
//                AToast.normal("TODO下载" + appUrlInfo.getUrlAdress());
//            } else {
//                dismiss();
//                WebFragment.start(appUrlInfo.getYunUrl());
//            }
//
//        });
//    }
//
//    @Override
//    protected PopupAnimator getPopupAnimator() {
//        return new ScaleAlphaAnimator(getPopupContentView(), PopupAnimation.ScaleAlphaFromRightBottom);
//    }
//
//    public AppUrlCenterListPopup setAppDetailInfo(AppDetailInfo info) {
//        setTitle(info.getName());
//        setData(info.getAppUrlInfoList());
//        return this;
//    }
//
//}
