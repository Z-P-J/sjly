package com.zpj.shouji.market.ui.fragment.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.toast.ZToast;
import com.zpj.fragmentation.dialog.impl.ArrowMenuDialogFragment;
import com.zpj.fragmentation.dialog.model.OptionMenu;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.shouji.market.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class UpdateManagerFragment extends RecyclerLayoutFragment<AppUpdateInfo>
        implements AppUpdateManager.CheckUpdateListener {

    private static final List<OptionMenu> optionMenus = new ArrayList<>();
    static {
        optionMenus.add(new OptionMenu("忽略更新"));
        optionMenus.add(new OptionMenu("详细信息"));
        optionMenus.add(new OptionMenu("卸载"));
        optionMenus.add(new OptionMenu("打开"));
    }

    private RelativeLayout topLayout;
    private TextView updateInfo;

    private boolean showToolbar = false;

    public static UpdateManagerFragment newInstance(boolean showToolbar) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, showToolbar);
        UpdateManagerFragment fragment = new UpdateManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(boolean showToolbar) {
        start(UpdateManagerFragment.newInstance(showToolbar));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_update;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_app_update;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

//    @Override
//    protected void initStatusBar() {
//        if (showToolbar) {
//            ThemeUtils.initStatusBar(this);
//        }
//    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        showToolbar = getArguments() != null && getArguments().getBoolean(Keys.SHOW_TOOLBAR, false);
        if (showToolbar) {
            toolbar.setVisibility(View.VISIBLE);
//            findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
            setToolbarTitle("应用更新");
        } else {
            setSwipeBackEnable(false);
        }

        topLayout = view.findViewById(R.id.layout_top);
        TextView updateAll = view.findViewById(R.id.update_all);
        updateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo update all apps
                ZToast.normal("updateAll");
            }
        });
        updateInfo = view.findViewById(R.id.update_info);
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<AppUpdateInfo> recyclerLayout) {
        recyclerLayout.setEnableSwipeRefresh(false);
    }

    @Override
    public void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList) {
        data.clear();
        data.addAll(updateInfoList);
        recyclerLayout.notifyDataSetChanged();
        if (updateInfoList.isEmpty()) {
            topLayout.setVisibility(View.GONE);
            recyclerLayout.showEmptyView("所有应用均为最新版");
        } else {
            topLayout.setVisibility(View.VISIBLE);
            updateInfo.setText(updateInfoList.size() + "款应用可更新");
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e == null) {
            AppUpdateManager.getInstance().checkUpdate(context);
            return;
        }
        recyclerLayout.showErrorView(e.getMessage());
        topLayout.setVisibility(View.GONE);
//        errorText.setVisibility(View.VISIBLE);
        ZToast.error("检查更新失败！" + e.getMessage());
        e.printStackTrace();
    }

    public void onMenuClicked(View view, AppUpdateInfo updateInfo) {

        new ArrowMenuDialogFragment()
                .setOptionMenus(optionMenus)
                .setOrientation(LinearLayout.HORIZONTAL)
                .setOnItemClickListener((position, menu) -> {
                    switch (position) {
                        case 0:
                            ZToast.normal("详细信息");
                            break;
                        case 1:
                            ZToast.normal("详细信息");
                            break;
                        case 2:
                            AppUtil.uninstallApp(_mActivity, updateInfo.getPackageName());
                            break;
                        case 3:
                            AppUtil.openApp(context, updateInfo.getPackageName());
                            break;
                        default:
                            ZToast.warning("未知操作！");
                            break;
                    }
                })
                .setAttachView(view)
                .show(context);
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, AppUpdateInfo data) {
        AppDetailFragment.start(data);
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, AppUpdateInfo data) {
        return false;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<AppUpdateInfo> list, int position, List<Object> payloads) {
        AppUpdateInfo updateInfo = list.get(position);

        ImageView iconImageView = holder.getView(R.id.iv_icon);
        TextView versionTextView = holder.getView(R.id.tv_version);
        TextView titleTextView = holder.getView(R.id.tv_name);
        TextView infoTextView = holder.getView(R.id.tv_info);
        TextView updateTextView = holder.getView(R.id.tv_update_info);
        ImageView settingBtn = holder.getView(R.id.iv_setting);
        ImageView expandBtn = holder.getView(R.id.iv_expand);
        DownloadButton downloadButton = holder.getView(R.id.tv_update);
        downloadButton.bindApp(updateInfo);

        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setTempInstalled(true);
        appInfo.setPackageName(updateInfo.getPackageName());
        GlideApp.with(context).load(appInfo).into(iconImageView);

        titleTextView.setText(updateInfo.getAppName());
        versionTextView.setText(getVersionText(updateInfo));
        infoTextView.setText(updateInfo.getNewSize() + " | " + updateInfo.getUpdateTimeInfo());
        updateTextView.setText(updateInfo.getUpdateInfo());


        if (updateTextView.getLayout() != null && updateTextView.getLayout().getEllipsisCount(updateTextView.getLineCount() - 1) > 0) {
            expandBtn.setVisibility(View.VISIBLE);
        } else {
            expandBtn.setVisibility(View.GONE);
        }

        expandBtn.setTag(false);
        expandBtn.setOnClickListener(v -> {
            boolean tag = (boolean) expandBtn.getTag();
            expandBtn.setImageResource(tag ? R.drawable.ic_expand_more_black_24dp : R.drawable.ic_expand_less_black_24dp);
            updateTextView.setMaxLines(tag ? 1 : 0);
            updateTextView.setText(updateInfo.getUpdateInfo());
            expandBtn.setTag(!tag);
        });

        settingBtn.setOnClickListener(v -> onMenuClicked(v, updateInfo));


    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        if (data.isEmpty()) {
            postOnEnterAnimationEnd(new Runnable() {
                @Override
                public void run() {
                    AppUpdateManager.getInstance().addCheckUpdateListener(UpdateManagerFragment.this);
                }
            });
            return true;
        }
        return false;
    }

    private SpannableString getVersionText(AppUpdateInfo updateInfo) {
        SpannableString spannableString = new SpannableString(updateInfo.getOldVersionName() + "  " + updateInfo.getNewVersionName());
        spannableString.setSpan(
                new StrikethroughSpan(),
                0,
                updateInfo.getOldVersionName().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannableString;
    }
}
