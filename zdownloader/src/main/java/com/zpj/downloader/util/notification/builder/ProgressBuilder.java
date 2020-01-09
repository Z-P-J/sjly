//package com.zpj.downloader.util.notification.builder;
//
//import android.app.Notification;
//import android.support.v4.app.NotificationCompat;
//import android.text.TextUtils;
//
///**
// * Created by Administrator on 2017/2/13 0013.
// */
//
//public class ProgressBuilder extends BaseBuilder {
//
//    private int max;
//    private float progress;
//    private boolean interminate = false;
//
//    @Deprecated
//    public ProgressBuilder setProgress(int max, int progress, boolean interminate){
//        setProgressAndFormat(progress, interminate, "%d/%d");
//        return this;
//    }
//
//    public ProgressBuilder setProgressAndFormat(float progress, boolean interminate, String format){
//        this.max = 100;
//        this.progress = progress;
//        this.interminate = interminate;
//
//        //contenttext的显示
//        if(TextUtils.isEmpty(format) ){
//            format = "进度:%.2f%%";
//            setContentText(String.format(format, progress));
//        }else {
//            if(format.contains("%%")){//百分比类型
//                int progressf = (int) (progress);
//                setContentText(String.format(format, progressf));
//            }else {
//                setContentText(String.format(format, progress, 100));
//            }
//        }
//
//        return this;
//    }
//
//    @Override
//    public void build(NotificationCompat.Builder builder) {
//        super.build(builder);
//        if (!isPause) {
//            builder.setProgress(max, (int) progress, interminate);
//        }
//
////        builder.setDefaults(0);
//        builder.setPriority(NotificationCompat.PRIORITY_LOW);
//    }
//
//    @Override
//    public void build(Notification.Builder builder) {
//        super.build(builder);
//        if (!isPause) {
//            builder.setProgress(max, (int) progress, interminate);
//        }
////        builder.setDefaults(0);
//        builder.setPriority(Notification.PRIORITY_DEFAULT);
//    }
//}


package com.zpj.downloader.util.notification.builder;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017/2/13 0013.
 */

public class ProgressBuilder extends BaseBuilder {
    public int max;
    public float progress;
    public boolean interminate = false;

    public ProgressBuilder(Context context) {
        super(context);
    }

    @Deprecated
    public ProgressBuilder setProgress(int max, int progress, boolean interminate){
        setProgressAndFormat(progress, interminate, "%d/%d");
        return this;
    }

    public ProgressBuilder setProgressAndFormat(float progress, boolean interminate, String format){
        this.max = 100;
        this.progress = progress;
        this.interminate = interminate;

        //contenttext的显示
        if(TextUtils.isEmpty(format) ){
            format = "进度:%.2f%%";
            setContentText(String.format(format, progress));
        }else {
            if(format.contains("%%")){//百分比类型
                int progressf = (int) (progress);
                setContentText(String.format(format, progressf));
            }else {
                setContentText(String.format(format, progress, 100));
            }
        }

        return this;
    }

    @Override
    public void build(NotificationCompat.Builder builder) {
        super.build(builder);
        builder.setProgress(max, (int) progress, interminate);

//        builder.setDefaults(0);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
    }

    @Override
    public void build(Notification.Builder builder) {
        super.build(builder);
        builder.setProgress(max, (int) progress, interminate);
//        builder.setDefaults(0);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
    }
}

