package com.zpj.fragmentation.dialog.imagetrans.listener;

import com.zpj.fragmentation.dialog.imagetrans.ImageItemView;

/**
 * Created by liuting on 18/3/14.
 */

public interface SourceImageViewGet<T> {

//    ImageView getImageView(int pos);

    public void updateImageView(ImageItemView<T> imageItemView, int pos, boolean isCurrent);
}
