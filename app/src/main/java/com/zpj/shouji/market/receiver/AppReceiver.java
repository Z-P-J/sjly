package com.zpj.shouji.market.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.zpj.notification.ZNotify;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.toast.ZToast;

import java.util.concurrent.atomic.AtomicBoolean;

public class AppReceiver extends BroadcastReceiver {

    private final AtomicBoolean isRegistered = new AtomicBoolean(false);
    private static AppReceiver APP_RECEIVER;

    public static AppReceiver getInstance() {
        if (APP_RECEIVER == null) {
            synchronized (AppReceiver.class) {
                if (APP_RECEIVER == null) {
                    APP_RECEIVER = new AppReceiver();
                }
            }
        }
        return APP_RECEIVER;
    }

    public static void register(Context context) {
        if (!getInstance().isRegistered()) {
            getInstance().setRegistered(true);
            APP_RECEIVER = null;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            context.registerReceiver(getInstance(), intentFilter);
        }
    }

    public static void unregister(Context context) {
        if (getInstance().isRegistered()) {
            context.unregisterReceiver(getInstance());
            getInstance().setRegistered(false);
            APP_RECEIVER = null;
        }
    }

    public boolean isRegistered() {
        return isRegistered.get();
    }

    public void setRegistered(boolean value) {
        isRegistered.set(value);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        // 接受更新安装广播
//        if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {
//            String packageName = intent.getDataString();
//            ZToast.warning("安装了:" + packageName + "包名的程序");
//        }
//        //接收安装广播
//        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
//            String packageName = intent.getDataString();
//            ZToast.warning("安装了:" + packageName + "包名的程序");
//            ZNotify.with(context)
//                    .buildNotify()
//                    .setId(hashCode())
//                    .setContentTitle("安装成功")
//                    .setContentText("应用" + packageName + "安装成功")
//                    .show();
//        }
//        //接收卸载广播
//        if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
//            String packageName = intent.getDataString();
//            ZToast.warning("卸载了:" + packageName + "包名的程序");
//        }

        if (intent != null) {
            if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())
                    || Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())
                    || Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
                RxBus.post(intent.getData().getSchemeSpecificPart(), intent.getAction());
                Log.d("AppReceiver", "安装的app的包名是-------->" + intent.getDataString() + " intent.getData().getSchemeSpecificPart()=" + intent.getData().getSchemeSpecificPart());
//                if (TextUtils.equals(packageName, intent.getData().getSchemeSpecificPart())) {
//
//                }
            }
        }
    }
}
