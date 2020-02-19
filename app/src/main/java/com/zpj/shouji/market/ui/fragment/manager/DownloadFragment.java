package com.zpj.shouji.market.ui.fragment.manager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.sunfusheng.ExpandableGroupRecyclerViewAdapter;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.StickyHeaderDecoration;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.constant.Error;
import com.zpj.downloader.core.DownloadManager;
import com.zpj.downloader.core.DownloadMission;
import com.zpj.downloader.util.FileUtil;
import com.zpj.shouji.market.R;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.utils.HttpApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DownloadFragment extends BaseFragment implements DownloadManager.DownloadManagerListener, GroupRecyclerViewAdapter.OnItemClickListener<DownloadFragment.DownloadWrapper> {

    private final List<List<DownloadWrapper>> downloadTaskList = new ArrayList<>();

    private ExpandCollapseGroupAdapter expandableAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        ZDownloader.getDownloadManager().setDownloadManagerListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expandableAdapter = new ExpandCollapseGroupAdapter(getContext(), downloadTaskList);
        recyclerView.setAdapter(expandableAdapter);
        recyclerView.addItemDecoration(new StickyHeaderDecoration());
        expandableAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        loadDownloadMissions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(GroupRecyclerViewAdapter<DownloadWrapper> adapter, GroupViewHolder holder, DownloadWrapper data, int groupPosition, int childPosition) {
        if (adapter.isHeader(groupPosition, childPosition)) {
            List<DownloadWrapper> list = adapter.getGroupItemsWithoutHeaderFooter(groupPosition);
            if (list.size() == 1 && list.get(0).getMission() == null) {
                return;
            }
            if (expandableAdapter.isExpand(groupPosition)) {
                expandableAdapter.collapseGroup(groupPosition, true);
            } else {
                expandableAdapter.expandGroup(groupPosition, true);
            }
            expandableAdapter.updateItem(groupPosition, childPosition, expandableAdapter.getItem(groupPosition, childPosition));
        } else if (data.getMission() != null) {
            if (data.getMission().isFinished()) {
                data.getMission().openFile(getContext());
            }
        } else {
            // TODO onclick
            AToast.normal("TODO onClick");
        }
    }

    @Override
    public void onMissionAdd(DownloadMission mission) {

    }

    @Override
    public void onMissionDelete(DownloadMission mission) {

    }

    @Override
    public void onMissionFinished(DownloadMission mission) {

    }

    private void loadDownloadMissions() {
        HttpApi.with(() -> {
            downloadTaskList.clear();
            List<DownloadWrapper> downloadingList = new ArrayList<>();
            List<DownloadWrapper> downloadedList = new ArrayList<>();
            downloadTaskList.add(downloadingList);
            downloadTaskList.add(downloadedList);
            downloadingList.add(new DownloadWrapper("下载中"));
            downloadedList.add(new DownloadWrapper("已完成"));
            for (DownloadMission mission : ZDownloader.getAllMissions(true)) {
                downloadingList.add(new DownloadWrapper(mission));
            }
            for (DownloadMission mission : ZDownloader.getAllMissions(false)) {
                downloadedList.add(new DownloadWrapper(mission));
            }
            if (downloadingList.size() == 1) {
                downloadingList.add(new DownloadWrapper());
            }
            if (downloadedList.size() == 1) {
                downloadedList.add(new DownloadWrapper());
            }
        })
                .onSuccess(data -> expandableAdapter.notifyDataSetChanged())
                .subscribe();
//        ExecutorHelper.submit(() -> {
//            for (DownloadMission mission : ZDownloader.getAllMissions(true)) {
//                downloadingList.add(new DownloadWrapper(mission));
//            }
//            for (DownloadMission mission : ZDownloader.getAllMissions(false)) {
//                downloadedList.add(new DownloadWrapper(mission));
//            }
//            if (downloadingList.size() == 1) {
//                downloadingList.add(new DownloadWrapper());
//            }
//            if (downloadedList.size() == 1) {
//                downloadedList.add(new DownloadWrapper());
//            }
//            post(() -> expandableAdapter.notifyDataSetChanged());
//        });
    }

    public class DownloadWrapper {

        private DownloadMission mission;
        private String title;

        DownloadWrapper() {

        }

        DownloadWrapper(DownloadMission mission) {
            this.mission = mission;
        }

        DownloadWrapper(String title) {
            this.title = title;
        }

        public DownloadMission getMission() {
            return mission;
        }

        public String getTitle() {
            return title;
        }
    }

    public class ExpandCollapseGroupAdapter extends ExpandableGroupRecyclerViewAdapter<DownloadWrapper> {

        private static final int TYPE_CHILD = 111;
        private static final int TYPE_EMPTY = 222;
        private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        private final Map<DownloadMission, MissionObserver> map = new HashMap<>();

        public ExpandCollapseGroupAdapter(Context context, List<List<DownloadWrapper>> groups) {
            super(context, groups);
        }

        @Override
        public int getChildItemViewType(int groupPosition, int childPosition) {
            List<DownloadWrapper> list = getGroupItemsWithoutHeaderFooter(groupPosition);
            if (list.size() == 1 && list.get(0).getMission() == null) {
                return TYPE_EMPTY;
            }
            return TYPE_CHILD;
        }

        @Override
        public boolean showHeader() {
            return true;
        }

        @Override
        public boolean showFooter() {
            return false;
        }

        @Override
        public int getHeaderLayoutId(int viewType) {
            return R.layout.item_download_header;
        }

        @Override
        public int getChildLayoutId(int viewType) {
            if (viewType == TYPE_CHILD) {
                return R.layout.item_download;
            } else if (viewType == TYPE_EMPTY) {
                return R.layout.item_empty;
            } else {
                return 0;
            }
        }

        @Override
        public int getFooterLayoutId(int viewType) {
            return 0;
        }

        @Override
        public void onBindHeaderViewHolder(GroupViewHolder holder, DownloadWrapper item, int groupPosition) {
            ((TextView) holder.get(R.id.text_title)).setText(item.getTitle());
            holder.setImageResource(R.id.img_arrow, isExpand(groupPosition) ? R.drawable.ic_keyboard_arrow_down_black_24dp : R.drawable.ic_keyboard_arrow_right_black_24dp);
            if (groupPosition == 0) {
                holder.setImageResource(R.id.img_icon, R.drawable.ic_downloading);
            } else {
                holder.setImageResource(R.id.img_icon, R.drawable.ic_downloaded);
            }
        }

        @Override
        public void onBindChildViewHolder(GroupViewHolder holder, DownloadWrapper item, int groupPosition, int childPosition) {
            int viewType = getChildItemViewType(groupPosition, childPosition);
            if (viewType == TYPE_CHILD) {
                DownloadMission mission = item.getMission();
                holder.setImageResource(R.id.item_icon, FileUtil.getFileTypeIconId(mission.getTaskName()));
                holder.setText(R.id.item_name, mission.getTaskName());
                holder.get(R.id.btn_download).setOnClickListener(v -> {
                    AToast.normal("onClick");
                    if (mission.isPause() || mission.isError()) {
                        mission.start();
                    } else {
                        mission.pause();
                    }
                });
                updateStatus(holder, mission);
            } else if (viewType == TYPE_EMPTY) {

            }
        }

        @Override
        public void onBindFooterViewHolder(GroupViewHolder holder, DownloadWrapper item, int groupPosition) {

        }

        private void updateStatus(GroupViewHolder holder, DownloadMission mission) {
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
            ProgressBar progressBar = holder.get(R.id.progress_bar);
            progressBar.setMax(100);
            if (isFinished) {
                progressBar.setVisibility(View.GONE);
                holder.itemView.setBackground(new ColorDrawable(Color.WHITE));
                map.remove(mission);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) mission.getProgress());

                if (mission.isPause()) {
                    holder.setText(R.id.item_status, mission.getStatus().toString());
                    holder.setImageResource(R.id.btn_download, R.drawable.download_item_resume_icon_style2);
                } else if (mission.isError()) {
                    holder.setText(R.id.item_status, mission.getStatus().toString() + ":" + mission.getErrCode());
                    holder.setImageResource(R.id.btn_download, R.drawable.download_item_retry_icon_style2);
                } else {
                    holder.setText(R.id.item_status, mission.getProgressStr());
                    holder.setImageResource(R.id.btn_download, R.drawable.download_item_pause_icon_style2);
                }

                MissionObserver missionListener = map.get(mission);
                if (missionListener == null) {
                    missionListener = new MissionObserver(holder, mission);
                    map.put(mission, missionListener);
                    mission.addListener(missionListener);
                } else {
                    missionListener.setHolder(holder);
                }
            }
        }

        class MissionObserver implements DownloadMission.MissionListener {

            private GroupViewHolder holder;
            private DownloadMission mission;

            MissionObserver(GroupViewHolder holder, DownloadMission mission) {
                this.holder = holder;
                this.mission = mission;
            }

            public void setHolder(GroupViewHolder holder) {
                this.holder = holder;
            }

            @Override
            public void onInit() {
                updateStatus(holder, mission);
            }

            @Override
            public void onStart() {
                updateStatus(holder, mission);
            }

            @Override
            public void onPause() {
                updateStatus(holder, mission);
            }

            @Override
            public void onWaiting() {
                updateStatus(holder, mission);
            }

            @Override
            public void onRetry() {
                updateStatus(holder, mission);
            }

            @Override
            public void onProgress(DownloadMission.UpdateInfo update) {
                holder.setText(R.id.item_size, update.getFileSizeStr() + "/" + update.getDownloadedSizeStr());
            }

            @Override
            public void onFinish() {
                updateStatus(holder, mission);
//                expandableAdapter.removeItem(0, 0, true);
//                expandableAdapter.insertItem(1, 0, new DownloadWrapper(mission), true);
            }

            @Override
            public void onError(Error error) {
                updateStatus(holder, mission);
            }
        }

    }

}
