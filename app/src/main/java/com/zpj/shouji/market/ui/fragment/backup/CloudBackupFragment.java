package com.zpj.shouji.market.ui.fragment.backup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CloudBackupApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.CloudBackupAppInfo;
import com.zpj.shouji.market.model.CloudBackupItem;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
import com.zpj.shouji.market.utils.ThemeUtils;
import com.zpj.widget.statelayout.StateLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class CloudBackupFragment extends BaseFragment
        implements IEasy.OnBindViewHolderListener<CloudBackupItem>,
        IEasy.OnItemClickListener<CloudBackupItem> {

    private final List<CloudBackupItem> backupItemList = new ArrayList<>();

    private StateLayout stateLayout;
    private TextView tvInfo;
    private TextView tvCreate;

    private EasyRecyclerView<CloudBackupItem> recyclerView;

    public static void start() {
        StartFragmentEvent.start(new CloudBackupFragment());
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_backup_cloud;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        stateLayout = findViewById(R.id.state_layout);
        stateLayout.showLoadingView();

        tvInfo = findViewById(R.id.tv_info);
        tvCreate = findViewById(R.id.tv_create);

        tvCreate.setOnClickListener(v -> CreateBackupFragment.start());

        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));

        recyclerView.setData(backupItemList)
                .setItemRes(R.layout.item_cloud_backup)
                .onBindViewHolder(this)
                .onItemClick(this)
                .build();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getData();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (isLazyInit()) {
            lightStatusBar();
        } else {
            ThemeUtils.initStatusBar(this);
        }
    }

    private void getData() {
        CloudBackupApi.backupListApi()
                .onSuccess(data -> {
                    Log.d("CloudBackupFragment", "data=" + data);
                    backupItemList.clear();
                    for (Element item : data.select("item")) {
                        String viewType = item.selectFirst("viewtype").text();
                        if ("beifentitle".equals(viewType)) {
                            tvInfo.setText(item.selectFirst("createdate").text());
                        } else if ("beifen".equals(viewType)) {
                            backupItemList.add(CloudBackupItem.from(item));
                        }
                    }
                    if (TextUtils.isEmpty(tvInfo.getText()) && backupItemList.size() == 1) {
                        tvInfo.setText(backupItemList.get(0).getCreateDate());
                    }
                    postOnEnterAnimationEnd(() -> {
                        lightStatusBar();
                        recyclerView.notifyDataSetChanged();
                        stateLayout.showContentView();
                    });
                })
                .onError(throwable -> stateLayout.showErrorView(throwable.getMessage()))
                .subscribe();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<CloudBackupItem> list, int position, List<Object> payloads) {
        CloudBackupItem item = list.get(position);
        holder.setText(R.id.tv_title, item.getTitle());
        holder.setText(R.id.tv_info, item.getComment());
        holder.setText(R.id.tv_time, "共" + item.getCount() + "个应用 | " + item.getCreateDate());
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, CloudBackupItem data) {
        BackupDetailFragment.start(data.getId());
    }

    @Subscribe
    public void onRefreshEvent(RefreshEvent event) {
        getData();
    }


}
