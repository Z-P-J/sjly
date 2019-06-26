package com.zpj.sjly.utils;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadApksTask extends AsyncTask<Void, Void, List<String>> {

    private WeakReference<Fragment> fragmentWeakReference;

    private CallBack callBack;

    private LoadApksTask(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    public static LoadApksTask with(Fragment fragment) {
        return new LoadApksTask(fragment);
    }

    public LoadApksTask setCallBack(CallBack callBack) {
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
