package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.adapter.ImgAdapter;
import com.zpj.fragmentation.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppInfoFragment extends BaseFragment {

    private RecyclerView recyclerView;

    private LinearLayout content;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail_info;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        content = view.findViewById(R.id.content);
        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setItemViewCacheSize(100);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Subscribe
    public void onGetAppDetailInfo(AppDetailInfo info) {
        postOnEnterAnimationEnd(() -> {
            ImgAdapter imgAdapter = new ImgAdapter(recyclerView, info.getImgUrlList());
            recyclerView.setAdapter(imgAdapter);
            addItem("应用简介", info.getAppIntroduceContent());
            addItem("新版特性", info.getUpdateContent());
            addItem("详细信息", info.getAppInfo());
            addItem("权限信息", info.getPermissionContent());
        });
    }

    private void addItem(String title, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.item_app_info_text, null, false);
        content.addView(view);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        tvTitle.setText(title);
        tvContent.setText(text);
    }

}
