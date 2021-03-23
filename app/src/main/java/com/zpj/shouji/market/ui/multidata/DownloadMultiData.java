package com.zpj.shouji.market.ui.multidata;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.zpj.downloader.BaseMission;
import com.zpj.downloader.DownloadMission;
import com.zpj.downloader.constant.Error;
import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.CheckDialogFragment;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.ExpandableMultiData;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadedActionButton;
import com.zpj.statemanager.State;
import com.zpj.toast.ZToast;
import com.zpj.utils.ClickHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DownloadMultiData extends ExpandableMultiData<AppDownloadMission> {

    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    private final Map<BaseMission<?>, MissionObserver> map = new HashMap<>();

    private final String title;

    public DownloadMultiData(String title) {
        super(new ArrayList<>());
        this.title = title;
    }

    public void setData(List<AppDownloadMission> list) {
        this.list.clear();
        this.list.addAll(list);
        hasMore = false;
        if (list.isEmpty()) {
            state = State.STATE_EMPTY;
        }
    }

    public void addMission(BaseMission<?> mission) {
        if (mission instanceof AppDownloadMission) {
            this.list.add(0, (AppDownloadMission) mission);
            if (this.list.size() == 1) {
                showContent();
            } else {
                notifyItemRangeInserted(1, 1);
                notifyItemChanged(0);
            }
        }
    }

    public void removeMission(BaseMission<?> mission) {
        if (mission instanceof AppDownloadMission) {
            int pos = this.list.indexOf(mission);
            if (pos >= 0) {
                this.list.remove(mission);
                if (this.list.isEmpty()) {
                    showEmpty();
                } else {
                    notifyItemRemoved(pos + 1);
                    notifyItemChanged(0);
                }
            }
        }
    }

    @Override
    public int getHeaderLayoutId() {
        return R.layout.item_download_header;
    }

    @Override
    public int getChildViewType(int position) {
        return R.layout.item_download;
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.item_download;
    }

    @Override
    public void onBindHeader(EasyViewHolder holder, List<Object> payloads) {
        holder.setText(R.id.text_title, title + "(" + list.size() + ")");
        updateIcon(holder);
        if (getMultiDataPosition() == 0) {
            holder.setImageResource(R.id.img_icon, R.drawable.ic_downloading);
        } else {
            holder.setImageResource(R.id.img_icon, R.drawable.ic_downloaded);
        }
        holder.setOnItemClickListener(v -> {
            if (state == State.STATE_CONTENT) {
                if (isExpand()) {
                    collapse();
                } else {
                    expand();
                }
                updateIcon(holder);
                scrollToPosition(0);
            }
        });
    }

    private void updateIcon(EasyViewHolder holder) {
        ImageView ivArrow = holder.getView(R.id.iv_arrow);
        ivArrow.setImageResource(R.drawable.ic_enter_bak);
        if (isExpand()) {
            ivArrow.setRotation(90);
        } else {
            ivArrow.setRotation(0);
        }
    }

    @Override
    public void onBindChild(EasyViewHolder holder, List<AppDownloadMission> list, int position, List<Object> payloads) {
        Context context = holder.getContext();
        AppDownloadMission mission = list.get(position);
        ImageView ivIcon = holder.getView(R.id.item_icon);
        Glide.with(holder.getContext()).load(mission).into(ivIcon);

        holder.setText(R.id.item_name, mission.getAppName());
        holder.setOnClickListener(R.id.btn_download, v -> {
            if (mission.canStart()) {
                mission.start();
            } else if (mission.canPause()) {
                mission.pause();
            }
        });
        updateStatus(holder, mission);

        ClickHelper.with(holder.getItemView())
                .setOnClickListener((v, x, y) -> {
                    if (mission.isFinished()) {
                        mission.install();
                    } else {
                        AppDetailFragment.start(mission.getAppType(), mission.getAppId());
                    }
                })
                .setOnLongClickListener((v, x, y) -> {
                    ArrayList<String> titleList = new ArrayList<>();
                    if (!mission.isFinished()) {
                        if (mission.canPause()) {
                            titleList.add("暂停");
                        } else if (mission.canStart()) {
                            titleList.add("开始");

                        }
                        titleList.add("删除");
                        titleList.add("复制链接");
                        titleList.add("任务详情");
                    } else {
                        titleList.add("安装");
                        titleList.add("删除");
                        titleList.add("复制链接");
                        titleList.add("任务详情");
                        titleList.add("分享");
                    }
                    new AttachListDialogFragment<String>()
                            .addItems(titleList)
                            .setOnSelectListener((fragment, p, text) -> {
                                fragment.dismiss();
                                switch (text) {
                                    case "开始":
                                        mission.start();
                                        break;
                                    case "暂停":
                                        mission.pause();
                                        break;
                                    case "安装":
//                                                    mission.openFile(context);
                                        mission.install();
                                        break;
                                    case "删除":
                                        new CheckDialogFragment()
                                                .setChecked(true)
                                                .setCheckTitle("删除已下载的文件")
                                                .setTitle("确定删除？")
                                                .setContent("你将删除下载任务：" + mission.getTaskName())
                                                .setPositiveButton((fragment1, which) -> {
                                                    mission.delete();
                                                    int pos = list.indexOf(mission) + 1;
                                                    list.remove(mission);
                                                    if (list.isEmpty()) {
                                                        showEmpty();
                                                    } else {
                                                        notifyItemRemoved(pos);
                                                    }
                                                })
                                                .show(context);
                                        break;
                                    case "复制链接":
                                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        cm.setPrimaryClip(ClipData.newPlainText(null, mission.getOriginUrl()));
                                        ZToast.success("已复制到粘贴板");
                                        break;
                                    case "任务详情":
                                        ZToast.normal("TODO");
                                        break;
                                    case "分享":
                                        ZToast.normal("分享");
                                        break;
                                    case "检验":
                                        ZToast.normal("检验");
                                        break;
                                }
                            })
                            .setTouchPoint(x, y)
                            .show(context);
                    return true;
                });
    }

    @Override
    public boolean loadData() {
        if (list.isEmpty()) {
            showEmpty();
        } else {
            showContent();
        }
        return false;
    }

    private void updateStatus(EasyViewHolder holder, AppDownloadMission mission) {
        boolean isFinished = mission.isFinished();

        String size = mission.getFileSizeStr();
        if (isFinished) {
            size += (" " + formatter.format(mission.getCreateTime()));
        } else {
            size += ("/" + mission.getDownloadedSizeStr());
        }
        holder.setText(R.id.item_size, size);
        holder.setVisible(R.id.item_status, !isFinished);
        holder.setVisible(R.id.btn_download, !isFinished);
        DownloadedActionButton tvAction = holder.getView(R.id.tv_action);
        tvAction.bindMission(mission);

        ProgressBar progressBar = holder.getView(R.id.progress_bar);
        progressBar.setMax(100);
        if (isFinished) {
            progressBar.setVisibility(View.GONE);
            map.remove(mission);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress((int) mission.getProgress());

            if (mission.isIniting()) {
                holder.setText(R.id.item_status, mission.getStatus().toString());
                holder.setImageResource(R.id.btn_download, R.drawable.download_item_pause_icon_style2);
            } else if (mission.isPause()) {
                holder.setText(R.id.item_status, mission.getStatus().toString());
                holder.setImageResource(R.id.btn_download,  R.drawable.download_item_resume_icon_style2);
            } else if (mission.isError()) {
//                    holder.setText(R.id.item_status, mission.getStatus().toString() + ":" + mission.getErrCode());
                holder.setText(R.id.item_status, mission.getStatus().toString());
                holder.setImageResource(R.id.btn_download, R.drawable.download_item_retry_icon_style2);
            } else if (mission.isWaiting()) {
                holder.setText(R.id.item_status, mission.getStatus().toString());
                holder.setImageResource(R.id.btn_download, R.drawable.download_item_pause_icon_style2);
            } else {
                holder.setText(R.id.item_status, mission.getProgressStr());
                holder.setImageResource(R.id.btn_download, R.drawable.download_item_pause_icon_style2);
            }


        }
        MissionObserver missionListener = map.get(mission);
        if (missionListener == null) {
            missionListener = new MissionObserver(this, holder, mission);
            map.put(mission, missionListener);
            mission.addListener(missionListener);
        } else {
            missionListener.updateHolder(holder);
        }
    }

    private static class MissionObserver implements AppDownloadMission.AppMissionListener {

        private final DownloadMultiData multiData;
        private EasyViewHolder holder;
        private final AppDownloadMission mission;

        private MissionObserver(DownloadMultiData multiData, EasyViewHolder holder, AppDownloadMission mission) {
            this.multiData = multiData;
            this.holder = holder;
            this.mission = mission;
        }

        public void updateHolder(EasyViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onInit() {
            multiData.updateStatus(holder, mission);
        }

        @Override
        public void onStart() {
            multiData.updateStatus(holder, mission);
        }

        @Override
        public void onPause() {
            multiData.updateStatus(holder, mission);
        }

        @Override
        public void onWaiting() {
            multiData.updateStatus(holder, mission);
        }

        @Override
        public void onRetry() {
            multiData.updateStatus(holder, mission);
        }

        @Override
        public void onProgress(DownloadMission.ProgressInfo update) {
            holder.setText(R.id.item_size, update.getFileSizeStr() + "/" + update.getDownloadedSizeStr());
            holder.setText(R.id.item_status, mission.getProgressStr());
            ProgressBar progressBar = holder.getView(R.id.progress_bar);
            progressBar.setProgress((int) mission.getProgress());
        }

        @Override
        public void onFinish() {
            multiData.updateStatus(holder, mission);
        }

        @Override
        public void onError(Error error) {
            multiData.updateStatus(holder, mission);
        }

        @Override
        public void onDelete() {

        }

        @Override
        public void onClear() {

        }

        @Override
        public void onInstalled() {
            holder.setText(R.id.tv_action, R.string.text_open);
        }

        @Override
        public void onUninstalled() {
            holder.setText(R.id.tv_action, R.string.text_install);
        }
    }

}
