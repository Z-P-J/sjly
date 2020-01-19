package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.adapter.ImgAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppInfoFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private ImgAdapter imgAdapter;

    private TextView tvIntroduce;
    private TextView tvUpdate;
    private TextView tvDetail;
    private TextView tvPermission;

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
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(100);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        tvIntroduce = view.findViewById(R.id.tv_introduce);
        tvUpdate = view.findViewById(R.id.tv_update);
        tvDetail = view.findViewById(R.id.tv_detail);
        tvPermission = view.findViewById(R.id.tv_permission);
    }

    @Subscribe
    public void onGetAppDetailInfo(AppDetailInfo info) {
        imgAdapter = new ImgAdapter(recyclerView, info.getImgUrlList());
        recyclerView.setAdapter(imgAdapter);
        tvIntroduce.setText(info.getAppIntroduceContent());
        tvUpdate.setText(info.getUpdateContent());
        tvDetail.setText(info.getAppInfo());
        tvPermission.setText(info.getPermissionContent());
    }

}
