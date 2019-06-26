package com.zpj.sjly.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zpj.popupmenuview.OptionMenu;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.popupmenuview.PopupMenuView;
import com.zpj.sjly.DetailActivity;
import com.zpj.sjly.R;
import com.zpj.sjly.adapter.AppUpdateAdapter;
import com.zpj.sjly.bean.AppUpdateInfo;
import com.zpj.sjly.utils.AppUpdateHelper;

import java.util.ArrayList;
import java.util.List;

public class AppUpdateFragment extends Fragment implements AppUpdateHelper.CheckUpdateListener, AppUpdateAdapter.OnItemClickListener {

    private AppUpdateAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, null, false);
        initView(view);
        AppUpdateHelper.getInstance().addCheckUpdateListener(this);
        return view;
    }

    private void initView(View view) {
        adapter = new AppUpdateAdapter(AppUpdateHelper.getInstance().getUpdateAppList());
        adapter.setItemClickListener(this);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onItemClick(AppUpdateAdapter.ViewHolder holder, int position, AppUpdateInfo updateInfo) {
        Toast.makeText(getContext(), updateInfo.getAppName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        if ("game".equals(updateInfo.getAppType())) {
            intent.putExtra("app_site", "sjly:http://tt.shouji.com.cn/androidv3/game_show.jsp?id=" + updateInfo.getId());
        } else {
            intent.putExtra("app_site", "sjly:http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=" + updateInfo.getId());
        }
        getActivity().startActivity(intent);
    }

    @Override
    public void onMenuClicked(View view, AppUpdateInfo updateInfo) {
        List<OptionMenu> optionMenus = new ArrayList<>();
        optionMenus.add(new OptionMenu("打开"));
        optionMenus.add(new OptionMenu("卸载"));
        optionMenus.add(new OptionMenu("详细信息"));
        optionMenus.add(new OptionMenu("忽略更新"));
        PopupMenuView popupMenuView = new PopupMenuView(getContext());
        popupMenuView.setOrientation(LinearLayout.HORIZONTAL)
                .setMenuItems(optionMenus)
                .setBackgroundAlpha(getActivity(), 0.9f, 500)
                .setBackgroundColor(Color.WHITE)
                .setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
                    @Override
                    public boolean onOptionMenuClick(int position, OptionMenu menu) {
                        Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                        popupMenuView.dismiss();
                        return true;
                    }
                }).show(view);
    }
}
