package com.zpj.shouji.market.ui.fragment.backup;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CloudBackupApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.CloudBackupAppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.utils.ThemeUtils;

import java.util.List;

public class BackupDetailFragment extends NextUrlFragment<CloudBackupAppInfo> {

    public static void start(String id) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, CloudBackupApi.getBackupDetailApi(id));
        BackupDetailFragment fragment = new BackupDetailFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_linear;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "备份列表详情";
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ThemeUtils.initStatusBar(this);
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, CloudBackupAppInfo data) {
        AppDetailFragment.start(data.getAppType(), data.getAppId());
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<CloudBackupAppInfo> list, int position, List<Object> payloads) {
        final CloudBackupAppInfo appInfo = list.get(position);
        holder.getTextView(R.id.tv_title).setText(appInfo.getName());
        holder.getTextView(R.id.tv_info).setText(appInfo.getPackageName());
        holder.getTextView(R.id.tv_desc).setText(appInfo.getComment());
        Glide.with(context).load(appInfo.getIcon()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public CloudBackupAppInfo createData(Element element) {
        if ("yunapp".equals(element.selectFirst("viewtype").text())) {
            return CloudBackupAppInfo.from(element);
        }
        return null;
    }

}
