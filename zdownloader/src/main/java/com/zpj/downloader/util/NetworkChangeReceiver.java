package com.zpj.downloader.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.zpj.downloader.ZDownloader;

/**
 * @author Z-P-J
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static NetworkChangeReceiver networkChangeReceiver;


    public synchronized static NetworkChangeReceiver getInstance() {
        if (networkChangeReceiver == null) {
            networkChangeReceiver = new NetworkChangeReceiver();
        }
        return networkChangeReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(context, "正在使用移动网络", Toast.LENGTH_SHORT).show();
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "正在使用WIFI", Toast.LENGTH_SHORT).show();
            }
            if (ZDownloader.isWaitingForInternet()) {
                ZDownloader.resumeAll();
            }
        } else {
            Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
            ZDownloader.waitingForInternet();
        }
    }

}
