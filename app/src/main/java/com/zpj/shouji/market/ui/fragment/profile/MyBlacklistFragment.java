package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.BlacklistInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.widget.popup.BottomListPopupMenu;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class MyBlacklistFragment extends NextUrlFragment<BlacklistInfo> {

    public static void start() {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, "http://tt.tljpxm.com/androidv3/user_blacklist_xml.jsp");
        MyBlacklistFragment fragment = new MyBlacklistFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_user;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("我的黑名单");
    }

    @Override
    public BlacklistInfo createData(Element element) {
        return BeanUtils.createBean(element, BlacklistInfo.class);
//        return BlacklistInfo.from(element);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<BlacklistInfo> list, int position, List<Object> payloads) {
        final BlacklistInfo appItem = list.get(position);
        holder.setText(R.id.tv_title, appItem.getNickName());
        holder.setText(R.id.tv_info, "在线：" + appItem.isOnline());
        Glide.with(context).load(appItem.getAvatarUrl()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, BlacklistInfo data) {
        ProfileFragment.start(data.getMemberId(), false);
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, BlacklistInfo data) {
        BottomListPopupMenu.with(context)
                .setMenu(R.menu.menu_blacklist)
                .onItemClick((menu, view1, data1) -> {
                    switch (data1.getItemId()) {
                        case R.id.remove:
                            HttpApi.removeBlacklistApi(data.getMemberId())
                                    .onSuccess(doc -> {
                                        String info = doc.selectFirst("info").text();
                                        if ("success".equals(doc.selectFirst("result").text())) {
                                            AToast.success(info);
                                            onRefresh();
                                        } else {
                                            AToast.error(info);
                                        }
                                    })
                                    .onError(throwable -> AToast.error(throwable.getMessage()))
                                    .subscribe();
                            break;
                        case R.id.report:
                            AToast.normal("TODO 举报");
                            break;
                        case R.id.share:
                            WebFragment.shareHomepage(data.getMemberId());
                            break;
                    }
                    menu.dismiss();
                })
                .show();
        return true;
    }
}
