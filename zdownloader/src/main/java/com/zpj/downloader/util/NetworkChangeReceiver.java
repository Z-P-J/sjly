package com.zpj.downloader.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zpj.downloader.ZDownloader;

/**
 * @author Z-P-J
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static NetworkChangeReceiver networkChangeReceiver;

    public synchronized static NetworkChangeReceiver getInstance() {
        if (networkChangeReceiver == null) {
            synchronized (NetworkChangeReceiver.class) {
                if (networkChangeReceiver == null) {
                    networkChangeReceiver = new NetworkChangeReceiver();
                }
            }
        }
        return networkChangeReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (ZDownloader.isWaitingForInternet()) {
                ZDownloader.resumeAll();
            }
        } else {
            ZDownloader.waitingForInternet();
        }
    }

}
