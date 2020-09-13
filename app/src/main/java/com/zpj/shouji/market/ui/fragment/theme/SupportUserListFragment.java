package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.DiscoverInfo;
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

    private String themeId;

    public static SupportUserListFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        args.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/app/flower_show_xml_v2.jsp?type=discuss&id=" + id);
        SupportUserListFragment fragment = new SupportUserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        super.handleArguments(arguments);
        themeId = arguments.getString(Keys.ID);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
//        if (TextUtils.isEmpty(themeId)) {
//            recyclerLayout.showEmpty();
//        }
    }

    @Override
    protected void onGetDocument(Document doc) throws Exception {
        Elements users = doc.select("fuser");
        if (users.isEmpty()) {
            nextUrl = "";
        } else {
            for (Element element : doc.select("fuser")) {
                data.add(createData(element));
            }
        }
    }

    @Override
    public SupportUserInfo createData(Element element) {
        SupportUserInfo userInfo = new SupportUserInfo();
        userInfo.setNickName(element.selectFirst("fname").text());
        userInfo.setUserId(element.selectFirst("fid").text());
        userInfo.setUserLogo(element.selectFirst("avatar").text());
        return userInfo;
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
