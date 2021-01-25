package com.zpj.shouji.market.ui.fragment.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zpj.downloader.BaseMission;
import com.zpj.downloader.DownloadManager;
import com.zpj.downloader.ZDownloader;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.recyclerview.StickyHeaderItemDecoration;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.multidata.DownloadMultiData;

import java.util.ArrayList;
import java.util.List;

public class DownloadManagerFragment extends BaseSwipeBackFragment
        implements DownloadManager.DownloadManagerListener {

    private final List<MultiData<?>> downloadMultiDataList = new ArrayList<>();

    private MultiRecyclerViewWrapper recyclerViewWrapper;

    public static DownloadManagerFragment newInstance(boolean showToolbar) {
        Bundle args = new Bundle();
        args.putBoolean(Keys.SHOW_TOOLBAR, showToolbar);
        DownloadManagerFragment fragment = new DownloadManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(boolean showToolbar) {
        start(DownloadManagerFragment.newInstance(showToolbar));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        boolean showToolbar = getArguments() != null
                && getArguments().getBoolean(Keys.SHOW_TOOLBAR, false);
        if (showToolbar) {
            toolbar.setVisibility(View.VISIBLE);
//            findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
            setToolbarTitle("下载管理");
        } else {
            setSwipeBackEnable(false);
        }

        ZDownloader.getDownloadManager().addDownloadManagerListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_layout);
        recyclerViewWrapper = MultiRecyclerViewWrapper.with(recyclerView)
                .setData(downloadMultiDataList)
                .addItemDecoration(new StickyHeaderItemDecoration())
                .build();
        recyclerViewWrapper.showLoading();
        postOnEnterAnimationEnd(this::loadDownloadMissions);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        ZDownloader.getDownloadManager().removeDownloadManagerListener(this);
        super.onDestroy();
    }

    @Override
    public void onMissionAdd(BaseMission<?> mission) {
        loadDownloadMissions();
    }

    @Override
    public void onMissionDelete(BaseMission<?> mission) {
        loadDownloadMissions();
    }

    @Override
    public void onMissionFinished(BaseMission<?> mission) {
        loadDownloadMissions();
    }

    private void loadDownloadMissions() {
        ZDownloader.getAllMissions(AppDownloadMission.class, new DownloadManager.OnLoadMissionListener<AppDownloadMission>() {
            @Override
            public void onLoaded(List<AppDownloadMission> missions) {
                new HttpObserver<>(
                        emitter -> {
                            List<AppDownloadMission> downloadingList = new ArrayList<>();
                            List<AppDownloadMission> downloadedList = new ArrayList<>();

                            for (AppDownloadMission mission : missions) {
                                if (mission.isFinished()) {
                                    downloadedList.add(mission);
                                } else {
                                    downloadingList.add(mission);
                                }
                            }

                            downloadMultiDataList.add(new DownloadMultiData("下载中", downloadingList));
                            downloadMultiDataList.add(new DownloadMultiData("已完成", downloadedList));
                            emitter.onComplete();
                        })
                        .onComplete(new IHttp.OnCompleteListener() {
                            @Override
                            public void onComplete() throws Exception {
                                recyclerViewWrapper.showContent();
                            }
                        })
                        .subscribe();
            }
        });
    }

}
