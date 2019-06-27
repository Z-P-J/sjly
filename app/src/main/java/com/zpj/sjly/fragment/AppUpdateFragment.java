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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felix.atoast.library.AToast;
import com.zpj.popupmenuview.OptionMenu;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.popupmenuview.PopupMenuView;
import com.zpj.sjly.DetailActivity;
import com.zpj.sjly.R;
import com.zpj.sjly.adapter.AppUpdateAdapter;
import com.zpj.sjly.bean.AppUpdateInfo;
import com.zpj.sjly.utils.AppUpdateHelper;
import com.zpj.sjly.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class AppUpdateFragment extends Fragment implements AppUpdateHelper.CheckUpdateListener, AppUpdateAdapter.OnItemClickListener {

    private static final List<OptionMenu> optionMenus = new ArrayList<>();
    static {
        optionMenus.add(new OptionMenu("忽略更新"));
        optionMenus.add(new OptionMenu("详细信息"));
        optionMenus.add(new OptionMenu("卸载"));
        optionMenus.add(new OptionMenu("打开"));
    }

    private AppUpdateAdapter adapter;
    private RecyclerView recyclerView;
    private RelativeLayout topLayout;
    private TextView updateInfo;
    private TextView emptyText;
    private TextView errorText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_update, null, false);
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

        topLayout = view.findViewById(R.id.layout_top);
        TextView updateAll = view.findViewById(R.id.update_all);
        updateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo update all apps
                Toast.makeText(getContext(), "updateAll", Toast.LENGTH_SHORT).show();
            }
        });
        updateInfo = view.findViewById(R.id.update_info);
        emptyText = view.findViewById(R.id.text_empty);
        errorText = view.findViewById(R.id.text_error);
    }

    @Override
    public void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList) {
        adapter.notifyDataSetChanged();
        errorText.setVisibility(View.GONE);
        if (updateInfoList.isEmpty()) {
            topLayout.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            topLayout.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            updateInfo.setText(updateInfoList.size() + "款应用可更新");
        }
    }

    @Override
    public void onError(Exception e) {
        topLayout.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        if (e == null) {
            AToast.error("检查更新失败！");
            return;
        }
        AToast.error("检查更新失败！" + e.getMessage());
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
        PopupMenuView popupMenuView = new PopupMenuView(getContext());
        popupMenuView.setOrientation(LinearLayout.HORIZONTAL)
                .setMenuItems(optionMenus)
                .setBackgroundAlpha(getActivity(), 0.9f, 500)
                .setBackgroundColor(Color.WHITE)
                .setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
                    @Override
                    public boolean onOptionMenuClick(int position, OptionMenu menu) {
                        popupMenuView.dismiss();
                        switch (position) {
                            case 0:
                                AToast.normal("详细信息");
                                break;
                            case 1:
                                AToast.normal("详细信息");
                                break;
                            case 2:
                                AppUtil.uninstallApp(getActivity(), updateInfo.getPackageName());
                                break;
                            case 3:
                                AppUtil.openApp(getContext(), updateInfo.getPackageName());
                                break;
                            default:
                                AToast.warning("未知操作！");
                                break;
                        }
                        return true;
                    }
                }).show(view);
    }
}
