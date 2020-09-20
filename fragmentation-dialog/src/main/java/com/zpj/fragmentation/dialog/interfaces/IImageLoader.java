package com.zpj.fragmentation.dialog.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

public interface IImageLoader<T>{
    void loadImage(int position, @NonNull T uri, @NonNull ImageView imageView, Runnable runnable);

    /**
     * 获取图片对应的文件
     * @param context
     * @param uri
     * @return
     */
    File getImageFile(@NonNull Context context, @NonNull T uri);
}
