package com.zpj.shouji.market.ui.fragment.manager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.ZDownloader;
import com.zpj.fragmentation.dialog.ZDialog;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.rxlife.RxLife;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.model.IgnoredUpdateInfo;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.shouji.market.ui.widget.ExpandIcon;
import com.zpj.shouji.market.utils.PinyinComparator;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpdateManagerFragment extends RecyclerLayoutFragment<AppUpdateInfo>
        implements AppUpdateManager.CheckUpdateListener {

    private final List<IgnoredUpdateInfo> ignoredUpdateInfoList = new ArrayList<>();

    private RelativeLayout topLayout;
    private TextView tvIgnoreUpdate;
    private TextView tvUpdateAll;

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

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        boolean showToolbar = getArguments() != null && getArguments().getBoolean(Keys.SHOW_TOOLBAR, false);
        if (showToolbar) {
            toolbar.setVisibility(View.VISIBLE);
            setToolbarTitle("应用更新");
        } else {
            setSwipeBackEnable(false);
        }

        topLayout = view.findViewById(R.id.layout_top);
        topLayout.setVisibility(View.GONE);
        tvUpdateAll = view.findViewById(R.id.tv_update_all);
        tvUpdateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZDialog.alert()
                        .setTitle(R.string.text_update_all)
                        .setContent("确认全部更新所有应用？")
                        .setPositiveButton((fragment, which) -> updateAll())
                        .show(context);
            }
        });
        tvIgnoreUpdate = findViewById(R.id.tv_ignore_update);
        tvIgnoreUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZDialog.select(IgnoredUpdateInfo.class)
                        .onBindIcon((icon, item, position) -> {
                            InstalledAppInfo appInfo = new InstalledAppInfo();
                            appInfo.setTempInstalled(true);
                            appInfo.setPackageName(item.getPackageName());
                            GlideApp.with(context).load(appInfo).into(icon);
                        })
                        .onBindTitle((titleView, item, position) -> titleView.setText(item.getAppName()))
                        .onBindSubtitle((view1, item, position) -> view1.setText(item.getPackageName()))
                        .onMultiSelect((fragment, selected, list) -> {
                            for (int i : selected) {
                                IgnoredUpdateInfo info = list.get(i);
                                if (info.getUpdateInfo() != null) {
                                    data.add(info.getUpdateInfo());
                                }
                                ignoredUpdateInfoList.remove(info);
                                info.delete();
                            }
                            Collections.sort(data, new PinyinComparator());
                            recyclerLayout.notifyDataSetChanged();
                            updateTopBar();
                        })
                        .setPositiveText("移除")
                        .setTitle("忽略更新(" + ignoredUpdateInfoList.size() + ")")
                        .setData(ignoredUpdateInfoList)
                        .show(context);
            }
        });
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<AppUpdateInfo> recyclerLayout) {
        recyclerLayout.setEnableSwipeRefresh(false);
    }

    @Override
    public void onCheckUpdateFinish(List<AppUpdateInfo> updateInfoList, List<IgnoredUpdateInfo> ignoredUpdateInfoList) {
        this.ignoredUpdateInfoList.clear();
        this.ignoredUpdateInfoList.addAll(ignoredUpdateInfoList);
        data.clear();
        data.addAll(updateInfoList);

        recyclerLayout.notifyDataSetChanged();
        if (data.isEmpty()) {
            topLayout.setVisibility(View.GONE);
            recyclerLayout.showEmptyView("所有应用均为最新版");
        } else {
            topLayout.setVisibility(View.VISIBLE);
            updateTopBar();
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
        ZToast.error("检查更新失败！" + e.getMessage());
        e.printStackTrace();
    }

    private void updateTopBar() {
        tvIgnoreUpdate.setText("已忽略(" + ignoredUpdateInfoList.size() + ")");
        tvIgnoreUpdate.setVisibility(ignoredUpdateInfoList.size() == 0 ? View.GONE : View.VISIBLE);
        tvUpdateAll.setText("全部更新(" + data.size() + ")");
    }

    private void updateAll() {
        Observable.create(
                new ObservableOnSubscribe<AppDownloadMission>() {
                    @Override
                    public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<AppDownloadMission> emitter) throws Exception {
                        ZDownloader.getAllMissions(AppDownloadMission.class, missions -> {
                            for (AppUpdateInfo updateInfo : data) {
                                AppDownloadMission downloadMission = null;
                                for (AppDownloadMission mission : missions) {
                                    if (TextUtils.equals(updateInfo.getId(), mission.getAppId())
                                            && TextUtils.equals(updateInfo.getPackageName(), mission.getPackageName())) {
                                        downloadMission = mission;
                                        break;
                                    }
                                }
                                if (downloadMission == null) {
                                    downloadMission = AppDownloadMission.create(
                                            updateInfo.getId(),
                                            updateInfo.getAppName(),
                                            updateInfo.getPackageName(),
                                            updateInfo.getAppType(),
                                            false
                                    );
                                } else {
                                    if (downloadMission.isFinished()) {
                                        continue;
                                    }
                                }
                                emitter.onNext(downloadMission);
                            }
                            emitter.onComplete();
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(BaseMission::start)
                .doOnComplete(() -> recyclerLayout.notifyDataSetChanged())
                .compose(RxLife.bindLifeOwner(this))
                .subscribe();
    }

    public void onMenuClicked(View view, AppUpdateInfo updateInfo) {
        ZDialog.arrowMenu()
                .setOptionMenus(R.array.update_actions)
                .setOrientation(LinearLayout.HORIZONTAL)
                .setOnItemClickListener((position, menu) -> {
                    switch (position) {
                        case 0:
                            IgnoredUpdateInfo ignoredUpdateInfo = new IgnoredUpdateInfo();
                            ignoredUpdateInfo.setAppName(updateInfo.getAppName());
                            ignoredUpdateInfo.setPackageName(updateInfo.getPackageName());
                            ignoredUpdateInfo.insert();
                            ignoredUpdateInfo.setUpdateInfo(updateInfo);
                            ignoredUpdateInfoList.add(ignoredUpdateInfo);
                            data.remove(updateInfo);
                            recyclerLayout.notifyDataSetChanged();
                            updateTopBar();
                            break;
                        case 1:
                            ZToast.normal("详细信息");
                            break;
                        case 2:
                            AppUtils.uninstallApk(_mActivity, updateInfo.getPackageName());
                            break;
                        case 3:
                            AppUtils.runApp(context, updateInfo.getPackageName());
                            break;
                        default:
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
//        ImageView expandBtn = holder.getView(R.id.iv_expand);
        ExpandIcon expandBtn = holder.getView(R.id.iv_expand);
        DownloadButton downloadButton = holder.getView(R.id.tv_update);
        downloadButton.bindApp(updateInfo);

        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setTempInstalled(true);
        appInfo.setPackageName(updateInfo.getPackageName());
        GlideApp.with(context).load(appInfo).into(iconImageView);

        titleTextView.setText(updateInfo.getAppName());
        versionTextView.setText(getVersionText(updateInfo));
        infoTextView.setText(updateInfo.getNewSize() + " | " + updateInfo.getUpdateTimeInfo());

        updateTextView.setMaxLines(updateInfo.isExpand() ? Integer.MAX_VALUE : 1);
        updateTextView.setText(updateInfo.getUpdateInfo());

        expandBtn.setTag(updateInfo);
        expandBtn.setState(updateInfo.isExpand() ? ExpandIcon.LESS : ExpandIcon.MORE, false);
        expandBtn.setOnClickListener(v -> {
            AppUpdateInfo info = (AppUpdateInfo) v.getTag();
            boolean isExpand = info.isExpand();
            info.setExpand(!isExpand);

            updateTextView.setMaxLines(isExpand ? 1 : Integer.MAX_VALUE);
            updateTextView.setText(updateInfo.getUpdateInfo());

            expandBtn.switchState();
        });

        settingBtn.setOnClickListener(v -> onMenuClicked(v, updateInfo));

        updateTextView.post(new Runnable() {
            @Override
            public void run() {
                if (updateInfo.isExpand() || (updateTextView.getLayout() != null &&
                        updateTextView.getLayout().getEllipsisCount(updateTextView.getLineCount() - 1) > 0)) {
                    expandBtn.setVisibility(View.VISIBLE);
                } else {
                    expandBtn.setVisibility(View.GONE);
                }
            }
        });


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
        SpannableString spannableString = new SpannableString(updateInfo.getOldVersionName() + "\t--->\t" + updateInfo.getNewVersionName());
        spannableString.setSpan(
                new StrikethroughSpan() {
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#ff5c5d"));
                    }
                },
                0,
                updateInfo.getOldVersionName().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableString.setSpan(
                new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)),
                spannableString.length() - updateInfo.getNewVersionName().length(),
                spannableString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannableString;
    }
}
