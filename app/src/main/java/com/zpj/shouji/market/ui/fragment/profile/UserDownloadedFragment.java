package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.loadmore.LoadMoreWrapper;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.adapter.UserDownloadedAdapter;
import com.zpj.shouji.market.ui.fragment.base.LoadMoreFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDownloadedFragment extends LoadMoreFragment<UserDownloadedAppInfo> {

    private static final String KEY_ID = "key_id";
    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/view_member_down_xml_v2.jsp?versioncode=198&id=5636865";

    public static UserDownloadedFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        UserDownloadedFragment fragment = new UserDownloadedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultUrl = DEFAULT_URL;
        nextUrl = DEFAULT_URL;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_user_downloaded;
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        defaultUrl = "http://tt.shouji.com.cn/app/view_member_down_xml_v2.jsp?versioncode=198&id=" + arguments.getString(KEY_ID, "5636865");
        nextUrl = defaultUrl;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, UserDownloadedAppInfo data, float x, float y) {
        _mActivity.start(AppDetailFragment.newInstance(data));
    }

    @Override
    public UserDownloadedAppInfo createData(Element element) {
        UserDownloadedAppInfo appInfo = new UserDownloadedAppInfo();
        appInfo.setId(element.selectFirst("id").text());
        appInfo.setTitle(element.selectFirst("title").text());
        appInfo.setDownId(element.selectFirst("downid").text());
        appInfo.setAppType(element.selectFirst("apptype").text());
        appInfo.setPackageName(element.selectFirst("package").text());
        appInfo.setAppSize(element.selectFirst("m").text());
        appInfo.setDownloadTime(element.selectFirst("r").text());
        return appInfo;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<UserDownloadedAppInfo> list, int position, List<Object> payloads) {
        UserDownloadedAppInfo appInfo = list.get(position);
        holder.getTextView(R.id.text_title).setText(appInfo.getTitle());
        holder.getTextView(R.id.text_package_name).setText(appInfo.getPackageName());
        holder.getTextView(R.id.text_info).setText(appInfo.getAppSize() + " | 于" + appInfo.getDownloadTime() + "下载");
    }
}
