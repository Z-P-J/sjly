package com.zpj.sjly.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zpj.sjly.R;
import com.zpj.sjly.adapter.AppManagerAdapter;
import com.zpj.sjly.bean.InstalledAppInfo;
import com.zpj.sjly.utils.FileScanner;
import com.zpj.sjly.utils.FileUtils;
import com.zpj.sjly.utils.LoadApksTask;
import com.zpj.sjly.utils.XpkInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public class PackageManagerFragment extends BaseFragment implements FileScanner.FileChecker, FileScanner.ScanListener {

    private static final String TAG = "PackageManagerFragment";

    private List<InstalledAppInfo> appInfoList = new ArrayList<>();
    private AppManagerAdapter adapter;
    private RecyclerView recyclerView;

    private FileScanner fileScanner;

    @Nullable
    @Override
    public View onBuildView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, null, false);
        fileScanner = new FileScanner(this, this);
        fileScanner.setDirFilter(new FileScanner.DirFilter() {
            @Override
            public boolean accept(File dir) {
                String fileNameLowerCase = dir.getName().toLowerCase();

                String keyword;
//                if (fileNameLowerCase.startsWith(keyword)) {
//                    return false;
//                }

                keyword = "tuniuapp";
                if (keyword.equalsIgnoreCase(fileNameLowerCase)) {
                    return false;
                }

                keyword = "cache";
                if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                    return false;
                }

                keyword = "log";
                if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                    return false;
                }

                keyword = "dump";
                if (keyword.equalsIgnoreCase(fileNameLowerCase) || fileNameLowerCase.endsWith(keyword)) {
                    return false;
                }
                return true;
            }
        });
        initView(view);
        loadApks();
        return view;
    }

    @Override
    public void lazyLoadData() {
//        new LoadAppsTask(this).execute();
//        Toast.makeText(getContext(), "lazyLoadData", Toast.LENGTH_SHORT).show();
//        loadApks();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new AppManagerAdapter(appInfoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadApks() {
        LoadApksTask.with(this)
                .setCallBack(new LoadApksTask.CallBack() {

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(List<String> installedAppInfos) {
                        Log.d(TAG, "installedAppInfos=" + installedAppInfos);
                        fileScanner.execute(installedAppInfos);
                    }

                })
                .execute();
    }

    @Override
    public void onStarted() {
        Log.d(TAG, "onStarted");
    }

    @Override
    public void onFindFile(FileScanner.FileItem fileItem) {
        Log.d(TAG, "onFindFile");
        if (fileItem instanceof InstalledAppInfo) {
            appInfoList.add((InstalledAppInfo) fileItem);
            adapter.notifyItemInserted(appInfoList.size() - 1);
        }
    }

    @Override
    public void onUpdateProgress(int totalLength, int completedLength) {

    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted");
    }

    @Override
    public void onCanceled() {
        Log.d(TAG, "onCanceled");
    }

    @Override
    public void onScanDir(File dir) {
        Log.d(TAG, "onScanDir dir=" + dir);
    }

    @Override
    public FileScanner.FileItem accept(File pathname) {
        Log.d(TAG, "accept pathname=" + pathname.getName());
        if (pathname.isFile()) {
            String subSuffix = FileUtils.subSuffix(pathname.getName());
            if (".apk".equalsIgnoreCase(subSuffix)) {
                return parseFromApk(getContext(), pathname);
            } else if (".xpk".equalsIgnoreCase(subSuffix)) {
                return parseFromXpk(pathname);
            }
        }
        return null;
    }

    @Override
    public void onFinished() {
        Log.d(TAG, "onFinished");
    }

    private InstalledAppInfo parseFromApk(Context context, File file) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

        if (packageInfo == null) {
            return null;
        }

        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setApkFilePath(file.getPath());

        appInfo.setName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
        appInfo.setPackageName(packageInfo.packageName);
        appInfo.setId(packageInfo.packageName);
        appInfo.setVersionName(packageInfo.versionName);
        appInfo.setVersionCode(packageInfo.versionCode);
        appInfo.setAppSize(file.length());
        appInfo.setFormattedAppSize(FileUtils.formatFileSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
        return appInfo;
    }

    private InstalledAppInfo parseFromXpk(File file) {
        XpkInfo xpkInfo;
        try {
            xpkInfo = XpkInfo.getXPKManifestDom(new ZipFile(file));
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return null;
        }
        InstalledAppInfo appInfo = new InstalledAppInfo();
        appInfo.setApkFilePath(file.getPath());
        appInfo.setName(xpkInfo.getAppName());
        appInfo.setPackageName(xpkInfo.getPackageName());
        appInfo.setId(xpkInfo.getPackageName());
        appInfo.setVersionName(xpkInfo.getVersionName());
        appInfo.setVersionCode(xpkInfo.getVersionCode());
        appInfo.setAppSize(file.length());
        appInfo.setFormattedAppSize(FileUtils.formatFileSize(file.length()));
        appInfo.setTempXPK(true);
        appInfo.setTempInstalled(false);
        return appInfo;
    }
}
