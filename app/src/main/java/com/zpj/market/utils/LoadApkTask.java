package com.zpj.market.utils;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadApkTask extends AsyncTask<Void, Void, List<String>> {

    private WeakReference<Fragment> fragmentWeakReference;

    private CallBack callBack;

    private LoadApkTask(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    public static LoadApkTask with(Fragment fragment) {
        return new LoadApkTask(fragment);
    }

    public LoadApkTask setCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        return FileUtils.getAllAvailableSdcardPath(fragmentWeakReference.get().getContext());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callBack != null) {
            callBack.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(List<String> installedAppInfos) {
        if (callBack != null) {
            callBack.onPostExecute(installedAppInfos);
        }
    }

    public interface CallBack {
        void onPreExecute();
        void onPostExecute(List<String> installedAppInfos);
    }

}
