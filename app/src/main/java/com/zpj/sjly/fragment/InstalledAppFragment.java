package com.zpj.sjly.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zpj.sjly.R;
import com.zpj.sjly.adapter.AppManagerAdapter;
import com.zpj.sjly.bean.InstalledAppInfo;
import com.zpj.sjly.utils.ExecutorHelper;
import com.zpj.sjly.utils.LoadAppsTask;
import com.zpj.sjly.view.recyclerview.LoadMoreAdapter;
import com.zpj.sjly.view.recyclerview.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

public class InstalledAppFragment extends BaseFragment {

    private final List<InstalledAppInfo> installedAppInfos = new ArrayList<>();
    private AppManagerAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, null, false);
        initView(view);
        return view;
    }

    @Override
    public void lazyLoadData() {
        Toast.makeText(getContext(), "lazyLoadData", Toast.LENGTH_SHORT).show();
    }

    private void initView(View view) {
        adapter = new AppManagerAdapter(installedAppInfos);
        LoadMoreWrapper.with(adapter)
                .setLoadMoreEnabled(false)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 获取数据
                                Toast.makeText(getContext(), "onLoadMore", Toast.LENGTH_SHORT).show();
                                loadInstallApps();
                            }
                        }, 1);
                    }
                })
                .into(recyclerView);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadInstallApps() {
        LoadAppsTask.with(InstalledAppFragment.this)
                .setCallBack(new LoadAppsTask.CallBack() {
                    @Override
                    public void onPostExecute(List<InstalledAppInfo> appInfos) {
                        installedAppInfos.clear();
                        installedAppInfos.addAll(appInfos);
                        adapter.notifyDataSetChanged();
                    }
                })
                .execute();
    }

//    public static class LoadAppsTask extends AsyncTask<Void, Void, List<InstalledAppInfo>> {
//
//        private WeakReference<Fragment> fragmentWeakReference;
//
//        LoadAppsTask(Fragment fragment) {
//            fragmentWeakReference = new WeakReference<>(fragment);
//        }
//
//        @Override
//        protected List<InstalledAppInfo> doInBackground(Void... voids) {
//            List<InstalledAppInfo> installedAppInfoList = new ArrayList<>();
//            PackageManager manager = fragmentWeakReference.get().getContext().getPackageManager();
//            List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
//            for (PackageInfo packageInfo : packageInfoList) {
//                InstalledAppInfo installedAppInfo = new InstalledAppInfo();
//                installedAppInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
//                installedAppInfo.setPackageName(packageInfo.packageName);
//                installedAppInfo.setSortName(installedAppInfo.getName());
//                installedAppInfo.setId(packageInfo.packageName);
//                installedAppInfo.setVersionName(packageInfo.versionName);
//                installedAppInfo.setApkFilePath(packageInfo.applicationInfo.publicSourceDir);
//                installedAppInfo.setFormattedAppSize(FileUtils.formatFileSize(new File(installedAppInfo.getApkFilePath()).length()));
//                installedAppInfo.setVersionCode(packageInfo.versionCode);
//                installedAppInfoList.add(installedAppInfo);
//            }
//            Collections.sort(installedAppInfoList, new Comparator<InstalledAppInfo>() {
//                @Override
//                public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
//                    return o1.getName().compareTo(o2.getName());
//                }
//            });
//            return installedAppInfoList;
//        }
//
//        @Override
//        protected void onPostExecute(List<InstalledAppInfo> installedAppInfos) {
////            super.onPostExecute(installedAppInfos);
//            adapter = new AppManagerAdapter(installedAppInfos);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            recyclerView.setAdapter(adapter);
////            adapter.notifyDataSetChanged();
//        }
//    }

}
