package com.zpj.shouji.market.ui.fragment.manager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felix.atoast.library.AToast;
import com.zpj.popupmenuview.OptionMenu;
import com.zpj.popupmenuview.OptionMenuView;
import com.zpj.popupmenuview.PopupMenuView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.AppUpdateInfo;
import com.zpj.shouji.market.ui.adapter.AppUpdateAdapter;
import com.zpj.shouji.market.ui.fragment.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.AppUpdateHelper;
import com.zpj.shouji.market.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

public class UpdateFragment extends BaseFragment
        implements AppUpdateHelper.CheckUpdateListener,
        AppUpdateAdapter.OnItemClickListener {

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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_update;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        AppUpdateHelper.getInstance().addCheckUpdateListener(this);
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
                AToast.normal("updateAll");
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
        if (e == null) {
            AppUpdateHelper.getInstance().checkUpdate(getContext());
            return;
        }
        topLayout.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        AToast.error("检查更新失败！" + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onItemClick(AppUpdateAdapter.ViewHolder holder, int position, AppUpdateInfo updateInfo) {
        if (getActivity() instanceof SupportActivity) {
            _mActivity.start(AppDetailFragment.newInstance(updateInfo));
        }
//        findFragment(MainFragment.class).start(AppDetailFragment.newInstance(updateInfo));
    }

    @Override
    public void onItemLongClick(AppUpdateAdapter.ViewHolder holder, int position, AppUpdateInfo updateInfo) {

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
