package com.lxj.xpopup.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

public interface XPopupImageLoader<T>{
    void loadImage(int position, @NonNull T uri, @NonNull ImageView imageView);

    /**
     * 获取图片对应的文件
     * @param context
     * @param uri
     * @return
     */
    File getImageFile(@NonNull Context context, @NonNull T uri);
}
