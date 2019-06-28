package com.zpj.sjly.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.felix.atoast.library.AToast;

public class AppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
            System.out.println();
            AToast.warning("安装了:" + packageName + "包名的程序");
        }
        //接收卸载广播
        if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
            AToast.warning("卸载了:" + packageName + "包名的程序");
        }
    }
}
