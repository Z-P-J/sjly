//package com.zpj.downloader.util.notification;
//
//import android.app.Activity;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//import com.zpj.downloader.util.notification.builder.ProgressBuilder;
//
//import static android.support.v4.app.NotificationCompat.VISIBILITY_SECRET;
//
///**
// * Created by Administrator on 2017/2/13 0013.
// */
//
//public class NotifyUtil {
//
//    public static final String CHANNEL_ID = "1";
//    private static final String CHANNEL_NAME = "mmDefault_Channel";
//
//    public static Context context;
//
//    public static NotificationManager getNm() {
//        return nm;
//    }
//
//    private static NotificationManager nm;
//
//    public static void init(Context context1) {
//        context = context1;
//        nm = (NotificationManager) context1
//                .getSystemService(Activity.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_MIN);
////            channel.canBypassDnd();//是否绕过请勿打扰模式
//            channel.enableLights(false);//闪光灯
//            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
////            channel.setLightColor(Color.RED);//闪关灯的灯光颜色
////            channel.canShowBadge();//桌面launcher的消息角标
//            channel.setShowBadge(true);
//            channel.enableVibration(false);//是否允许震动
////            channel.getAudioAttributes();//获取系统通知响铃声音的配置
////            channel.getGroup();//获取通知取到组
//            channel.setBypassDnd(false);//设置可绕过 请勿打扰模式
////            channel.setVibrationPattern(new long[]{100, 100, 200});//设置震动模式
////            channel.shouldShowLights();//是否会有灯光
//            channel.setSound(null, null);
//            channel.setVibrationPattern(new long[]{0});
//            nm.createNotificationChannel(channel);
//        }
//    }
//
////    public static SingleLineBuilder buildSimple(int id,int smallIcon,CharSequence contentTitle ,CharSequence contentText,PendingIntent contentIntent){
////        SingleLineBuilder builder = new SingleLineBuilder();
////        builder.setBase(smallIcon,contentTitle,contentText)
////                .setId(id)
////                .setContentIntent(contentIntent);
////        return builder;
////    }
//
////    @Deprecated
////    public static ProgressBuilder buildProgress(int id,int smallIcon,CharSequence contentTitle,int progress,int max){
////        ProgressBuilder builder = new ProgressBuilder();
////        builder.setBase(smallIcon,contentTitle,progress+"/"+max)
////                .setId(id);
////        builder.setProgress(max,progress,false);
////        return builder;
////    }
//
//    public static ProgressBuilder buildProgress(int id, int smallIcon, CharSequence contentTitle, float progress, boolean isRunning) {
//        ProgressBuilder progressBuilder;
//        progressBuilder = new ProgressBuilder();
//        progressBuilder.setBase(smallIcon, contentTitle, progress + "/" + 100)
//                .setId(id);
//        progressBuilder.setProgressAndFormat(progress, false, "");
////        progressBuilder.setForgroundService(isRunning);
//        return progressBuilder;
//    }
//
//    public static void notify(int id, Notification notification) {
////        Log.d("Notification", "id = " + id);
////        Log.d("Notification", "notification = " + notification);
//        nm.notify(id, notification);
//    }
//
//    public static PendingIntent buildIntent(Class clazz) {
//        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
//        Intent intent = new Intent(NotifyUtil.context, clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pi = PendingIntent.getActivity(NotifyUtil.context, 0, intent, flags);
//        return pi;
//    }
//
//    public static void cancel(int id) {
//        if (nm != null) {
//            nm.cancel(id);
//        }
//    }
//
//    public static void cancelAll() {
//        if (nm != null) {
//            nm.cancelAll();
//        }
//    }
//
//
//}

package com.zpj.downloader.util.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.zpj.downloader.util.notification.builder.BaseBuilder;
import com.zpj.downloader.util.notification.builder.ProgressBuilder;

import static android.support.v4.app.NotificationCompat.VISIBILITY_SECRET;

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public class NotifyUtil {

    public static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Default_Channel";

    private final Context context;

    public static NotificationManager getNm() {
        return nm;
    }

    private static NotificationManager nm;

    public static void init(Context context1){
//        context = context1;
        nm = (NotificationManager) context1
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context1.getPackageName(),
                    NotificationManager.IMPORTANCE_DEFAULT);
//            channel.canBypassDnd();//是否绕过请勿打扰模式
            channel.enableLights(false);//闪光灯
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
//            channel.setLightColor(Color.RED);//闪关灯的灯光颜色
//            channel.canShowBadge();//桌面launcher的消息角标
            channel.setShowBadge(true);
            channel.enableVibration(false);//是否允许震动
//            channel.getAudioAttributes();//获取系统通知响铃声音的配置
//            channel.getGroup();//获取通知取到组
            channel.setBypassDnd(false);//设置可绕过 请勿打扰模式
//            channel.setVibrationPattern(new long[]{100, 100, 200});//设置震动模式
//            channel.shouldShowLights();//是否会有灯光
            channel.setSound(null, null);
            channel.setVibrationPattern(new long[]{0});
            nm.createNotificationChannel(channel);
        }
    }

    private NotifyUtil(Context context) {
        this.context = context;
    }

    public static NotifyUtil with(Context context) {
        return new NotifyUtil(context);
    }

//    public static SingleLineBuilder buildSimple(int id,int smallIcon,CharSequence contentTitle ,CharSequence contentText,PendingIntent contentIntent){
//        SingleLineBuilder builder = new SingleLineBuilder();
//        builder.setBase(smallIcon,contentTitle,contentText)
//                .setId(id)
//                .setContentIntent(contentIntent);
//        return builder;
//    }

//    @Deprecated
//    public static ProgressBuilder buildProgress(int id,int smallIcon,CharSequence contentTitle,int progress,int max){
//        ProgressBuilder builder = new ProgressBuilder();
//        builder.setBase(smallIcon,contentTitle,progress+"/"+max)
//                .setId(id);
//        builder.setProgress(max,progress,false);
//        return builder;
//    }

//    public static ProgressBuilder buildProgress(int id, int smallIcon, CharSequence contentTitle, float progress, boolean isRunning){
//        ProgressBuilder progressBuilder;
//        progressBuilder = new ProgressBuilder();
//        progressBuilder.setBase(smallIcon, contentTitle,progress+"/"+100)
//                .setId(id);
//        progressBuilder.setProgressAndFormat(progress,false, "");
////        progressBuilder.setForgroundService(isRunning);
//        return progressBuilder;
//    }

    public ProgressBuilder buildProgressNotify(){
        return new ProgressBuilder(context);
    }

    public BaseBuilder buildNotify(){
        return new BaseBuilder(context);
    }

    public static void notify(int id, Notification notification){
//        Log.d("Notification", "id = " + id);
//        Log.d("Notification", "notification = " + notification);
        nm.notify(id, notification);
    }

//    public static PendingIntent buildIntent(Class clazz){
//        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
//        Intent intent = new Intent(NotifyUtil.context, clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pi = PendingIntent.getActivity(NotifyUtil.context, 0, intent, flags);
//        return pi;
//    }

    public static void cancel(int id){
        if(nm!=null){
            nm.cancel(id);
        }
    }

    public static void cancelAll(){
        if(nm!=null){
            nm.cancelAll();
        }
    }


}

