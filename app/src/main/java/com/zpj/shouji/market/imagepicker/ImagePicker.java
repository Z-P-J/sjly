/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.shouji.market.imagepicker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpj.shouji.market.imagepicker.entity.Item;
import com.zpj.shouji.market.utils.EventBus;

import java.util.List;

/*
* 修改至Matisse
* */
public final class ImagePicker {

    public final SelectionManager mSelectionManager;

    public interface OnSelectedListener {
        void onSelected(@NonNull List<Item> itemList);
    }

    private ImagePicker() {
        mSelectionManager = SelectionManager.getInstance();
        mSelectionManager.mediaTypeExclusive = true;
    }

    public static ImagePicker with() {
        return new ImagePicker();
    }

//    public ImagePicker showSingleMediaType(boolean showSingleMediaType) {
//        mSelectionSpec.showSingleMediaType = showSingleMediaType;
//        return this;
//    }

    public ImagePicker countable(boolean countable) {
        mSelectionManager.countable = countable;
        return this;
    }

    public ImagePicker maxSelectable(int maxSelectable) {
        if (maxSelectable < 1)
            throw new IllegalArgumentException("maxSelectable must be greater than or equal to one");
        mSelectionManager.maxSelectable = maxSelectable;
        countable(maxSelectable > 1);
        return this;
    }

//    public ImagePicker maxSelectablePerMediaType(int maxImageSelectable, int maxVideoSelectable) {
//        if (maxImageSelectable < 1 || maxVideoSelectable < 1)
//            throw new IllegalArgumentException(("max selectable must be greater than or equal to one"));
//        mSelectionSpec.maxSelectable = -1;
//        mSelectionSpec.maxImageSelectable = maxImageSelectable;
//        mSelectionSpec.maxVideoSelectable = maxVideoSelectable;
//        return this;
//    }

//    public ImagePicker spanCount(int spanCount) {
//        if (spanCount > 0) {
//            mSelectionSpec.spanCount = spanCount;
//        }
//        return this;
//    }

//    public ImagePicker thumbnailScale(float scale) {
//        if (scale <= 0f || scale > 1f)
//            throw new IllegalArgumentException("Thumbnail scale must be between (0.0, 1.0]");
//        mSelectionSpec.thumbnailScale = scale;
//        return this;
//    }

//    public ImagePicker imageEngine(ImageEngine imageEngine) {
//        mSelectionSpec.imageEngine = imageEngine;
//        return this;
//    }

    public ImagePicker isCrop(boolean isCrop) {
        mSelectionManager.isCrop = isCrop;
        mSelectionManager.isCropAvatar = true;
        return this;
    }

    public ImagePicker isCrop(boolean isCrop, boolean isCropAvatar) {
        mSelectionManager.isCrop = isCrop;
        mSelectionManager.isCropAvatar = isCropAvatar;
        return this;
    }

    public ImagePicker setSelectedList(List<Item> list) {
//        mSelectionSpec.selectedList = list;
        mSelectionManager.setSelectedList(list);
//        SelectedItemManager.getInstance().setDefaultSelection(list);
        return this;
    }

    public ImagePicker setOnSelectedListener(@Nullable ImagePicker.OnSelectedListener listener) {
        mSelectionManager.onSelectedListener = listener;
        return this;
    }

    public void start() {
        EventBus.post(new ImagePickerFragment());
    }

    public static void startCrop(boolean isCropAvatar) {
        ImagePicker.with()
                .maxSelectable(1)
                .isCrop(true, isCropAvatar)
                .start();
    }

}
