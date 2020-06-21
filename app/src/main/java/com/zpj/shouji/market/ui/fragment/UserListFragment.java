package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.UserInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;

import java.util.List;

public class UserListFragment extends NextUrlFragment<UserInfo>
        implements SearchResultFragment.KeywordObserver {

    public static UserListFragment newInstance(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, defaultUrl);
        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_user;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, UserInfo data) {
        Log.d("UserListFragment", "userInfo=" + data);
        _mActivity.start(ProfileFragment.newInstance(data.getId(), true));
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<UserInfo> list, int position, List<Object> payloads) {
        final UserInfo appItem = list.get(position);
        holder.getTextView(R.id.tv_title).setText(appItem.getNickName());
        holder.getTextView(R.id.tv_info).setText("在线：" + appItem.isOnline() + " | " + appItem.getSignature());
        Glide.with(context).load(appItem.getAvatarUrl()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void updateKeyword(String key) {
        defaultUrl = "http://tt.shouji.com.cn/androidv3/app_search_user_xml.jsp?versioncode=26&s=" + key;
        if (isLazyInit) {
            onRefresh();
        }
    }

    @Override
    public UserInfo createData(Element element) {
        return UserInfo.from(element);
    }

}
