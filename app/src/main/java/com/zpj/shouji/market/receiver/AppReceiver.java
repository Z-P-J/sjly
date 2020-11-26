package com.zpj.shouji.market.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.felix.atoast.library.AToast;
import com.zpj.notification.ZNotify;

public class AppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 接受更新安装广播
        if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
            AToast.warning("安装了:" + packageName + "包名的程序");
        }
        //接收安装广播
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
            AToast.warning("安装了:" + packageName + "包名的程序");
            ZNotify.with(context)
                    .buildNotify()
                    .setId(hashCode())
                    .setContentTitle("安装成功")
                    .setContentText("应用" + packageName + "安装成功")
                    .show();
        }
        //接收卸载广播
        if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
            String packageName = intent.getDataString();
            AToast.warning("卸载了:" + packageName + "包名的程序");
        }
    }
}
