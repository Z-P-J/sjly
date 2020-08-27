package com.zpj.shouji.market.ui.widget.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.popup.core.BottomPopup;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.model.SupportUserInfo;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.statelayout.StateLayout;

import java.util.ArrayList;
import java.util.List;

public class SupportUserListPopup extends BottomPopup<SupportUserListPopup>
         implements IEasy.OnBindViewHolderListener<SupportUserInfo> {

    private final List<SupportUserInfo> userInfoList = new ArrayList<>();

    private StateLayout stateLayout;
    private EasyRecyclerView<SupportUserInfo> recyclerView;

    private String themeId;


    private boolean isShow;
    private boolean hasInit;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stateLayout.showContentView();
            recyclerView.notifyDataSetChanged();
        }
    };

    protected SupportUserListPopup(@NonNull Context context) {
        super(context);
    }

    public static SupportUserListPopup with(Context context) {
        return new SupportUserListPopup(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_support_user_list;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        stateLayout = findViewById(R.id.state_layout);
        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        recyclerView.setData(userInfoList)
                .setItemRes(R.layout.item_menu)
                .setLayoutManager(new LinearLayoutManager(getContext()))
                .onBindViewHolder(this)
                .onItemClick((holder, view1, data) -> {
                    dismiss();
                    ProfileFragment.start(data.getUserId());
                })
                .build();
        stateLayout.showLoadingView();
        getSupportUserList();
    }

    @Override
    protected void onShow() {
        super.onShow();
        isShow = true;
        if (hasInit) {
            post(runnable);
        }
    }

    public SupportUserListPopup setThemeId(String id) {
        this.themeId = id;
        return self();
    }

    private void getSupportUserList() {
        HttpApi.getSupportUserListApi(themeId)
                .onSuccess(data -> {
                    userInfoList.clear();
                    for (Element element : data.select("fuser")) {
                        SupportUserInfo userInfo = new SupportUserInfo();
                        userInfo.setNickName(element.selectFirst("fname").text());
                        userInfo.setUserId(element.selectFirst("fid").text());
                        userInfo.setUserLogo(element.selectFirst("avatar").text());
                        userInfoList.add(userInfo);
                    }
//                    recyclerView.notifyDataSetChanged();
//                    stateLayout.showContentView();
                    if (isShow) {
                        hasInit = false;
                        post(runnable);
                    } else {
                        hasInit = true;
                    }
                })
                .onError(new IHttp.OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        stateLayout.showErrorView(throwable.getMessage());
                    }
                })
                .subscribe();
    }

//    public SupportUserListPopup setUserInfoList(List<SupportUserInfo> userInfoList) {
//        this.userInfoList.clear();
//        this.userInfoList = userInfoList;
//        return this;
//    }
//
//    public SupportUserListPopup addUserInfoList(List<SupportUserInfo> userInfoList) {
//        this.userInfoList.addAll(userInfoList);
//        return this;
//    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<SupportUserInfo> list, int position, List<Object> payloads) {
        SupportUserInfo userInfo = list.get(position);
        Glide.with(context).load(userInfo.getUserLogo()).into(holder.getImageView(R.id.iv_icon));
        holder.setText(R.id.tv_title, userInfo.getNickName());
    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(context) - ScreenUtils.dp2pxInt(context, 56) - ScreenUtils.getStatusBarHeight(context);
    }

}
