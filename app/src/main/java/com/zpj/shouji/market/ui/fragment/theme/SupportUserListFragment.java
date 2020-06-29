package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.SupportUserInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;

import java.util.List;

public class SupportUserListFragment extends NextUrlFragment<SupportUserInfo> {


    public void setData(List<SupportUserInfo> list) {
        this.data.clear();
        data.addAll(list);
        if (isSupportVisible()) {
            recyclerLayout.notifyDataSetChanged();
//            if (data.isEmpty()) {
//                recyclerLayout.showEmpty();
//            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        recyclerLayout.showLoading();
    }

    @Override
    public SupportUserInfo createData(Element element) {
        return null;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_user;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<SupportUserInfo> list, int position, List<Object> payloads) {
        final SupportUserInfo info = list.get(position);
        holder.getTextView(R.id.tv_title).setText(info.getNickName());
        holder.setVisible(R.id.tv_info, false);
        holder.setVisible(R.id.tv_follow, false);
        Glide.with(context).load(info.getUserLogo()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, SupportUserInfo data) {
        ProfileFragment.start(data.getUserId(), false);
    }
}
