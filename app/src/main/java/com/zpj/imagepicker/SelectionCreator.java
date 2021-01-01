/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.imagepicker;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.imagepicker.engine.ImageEngine;
import com.zpj.imagepicker.entity.Item;
import com.zpj.imagepicker.entity.SelectionSpec;
import com.zpj.imagepicker.model.SelectedItemManager;
import com.zpj.imagepicker.ui.fragment.ImagePickerFragment;
import com.zpj.toast.ZToast;

import java.util.List;
import java.util.Set;

/**
 * Fluent API for building media select specification.
 */
@SuppressWarnings("unused")
public final class SelectionCreator {
    private final ImagePicker mImagePicker;
    public final SelectionSpec mSelectionSpec;

    /**
     * Constructs a new specification builder on the context.
     *
     * @param imagePicker   a requester context wrapper.
     * @param mimeTypes MIME type set to select.
     */
    SelectionCreator(ImagePicker imagePicker, @NonNull Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        mImagePicker = imagePicker;
        mSelectionSpec = SelectionSpec.getCleanInstance();
        mSelectionSpec.mimeTypeSet = mimeTypes;
        mSelectionSpec.mediaTypeExclusive = mediaTypeExclusive;
    }

    SelectionCreator(ImagePicker imagePicker) {
        mImagePicker = imagePicker;
        mSelectionSpec = SelectionSpec.getCleanInstance();
    }

    /**
     * Whether to show only one media type if choosing medias are only images or videos.
     *
     * @param showSingleMediaType whether to show only one media type, either images or videos.
     * @return {@link SelectionCreator} for fluent API.
     * @see SelectionSpec#onlyShowImages()
     * @see SelectionSpec#onlyShowVideos()
     */
    public SelectionCreator showSingleMediaType(boolean showSingleMediaType) {
        mSelectionSpec.showSingleMediaType = showSingleMediaType;
        return this;
    }

    /**
     * Show a auto-increased number or a check mark when user select media.
     *
     * @param countable true for a auto-increased number from 1, false for a check mark. Default
     *                  value is false.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator countable(boolean countable) {
        mSelectionSpec.countable = countable;
        return this;
    }

    /**
     * Maximum selectable count.
     *
     * @param maxSelectable Maximum selectable count. Default value is 1.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator maxSelectable(int maxSelectable) {
        if (maxSelectable < 1)
            throw new IllegalArgumentException("maxSelectable must be greater than or equal to one");
        if (mSelectionSpec.maxImageSelectable > 0 || mSelectionSpec.maxVideoSelectable > 0)
            throw new IllegalStateException("already set maxImageSelectable and maxVideoSelectable");
        mSelectionSpec.maxSelectable = maxSelectable;
        countable(maxSelectable > 1);
        return this;
    }

    /**
     * Only useful when {@link SelectionSpec#mediaTypeExclusive} set true and you want to set different maximum
     * selectable files for image and video media types.
     *
     * @param maxImageSelectable Maximum selectable count for image.
     * @param maxVideoSelectable Maximum selectable count for video.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator maxSelectablePerMediaType(int maxImageSelectable, int maxVideoSelectable) {
        if (maxImageSelectable < 1 || maxVideoSelectable < 1)
            throw new IllegalArgumentException(("max selectable must be greater than or equal to one"));
        mSelectionSpec.maxSelectable = -1;
        mSelectionSpec.maxImageSelectable = maxImageSelectable;
        mSelectionSpec.maxVideoSelectable = maxVideoSelectable;
        return this;
    }

    public SelectionCreator spanCount(int spanCount) {
        if (spanCount > 0) {
            mSelectionSpec.spanCount = spanCount;
        }
        return this;
    }

    /**
     * Photo thumbnail's scale compared to the View's size. It should be a float value in (0.0,
     * 1.0].
     *
     * @param scale Thumbnail's scale in (0.0, 1.0]. Default value is 0.5.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator thumbnailScale(float scale) {
        if (scale <= 0f || scale > 1f)
            throw new IllegalArgumentException("Thumbnail scale must be between (0.0, 1.0]");
        mSelectionSpec.thumbnailScale = scale;
        return this;
    }

    public SelectionCreator imageEngine(ImageEngine imageEngine) {
        mSelectionSpec.imageEngine = imageEngine;
        return this;
    }

    public SelectionCreator setOnSelectedListener(@Nullable ImagePicker.OnSelectedListener listener) {
        mSelectionSpec.onSelectedListener = listener;
        return this;
    }

    /**
     * Start to select media and wait for result.
     *
     */
    @SuppressLint("WrongConstant")
    public void start() {
        final SupportActivity activity = mImagePicker.getActivity();
        if (activity == null) {
            return;
        }

        //自动进行权限检查
        XPermission xPermission = XPermission.create(activity, PermissionConstants.STORAGE);
        xPermission.callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        SupportFragment fragment = mImagePicker.getFragment();
                        if (fragment != null) {
                            fragment.start(new ImagePickerFragment());
                            return;
                        }
                        activity.start(new ImagePickerFragment());
                    }
                    @Override
                    public void onDenied() {
                        ZToast.warning("没有权限，无法使用该功能");
                    }
                }).request();


    }

    /**
     * 是否裁剪，只有选择一张图片时才有用
     *
     * @param isCrop
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator isCrop(boolean isCrop) {
        mSelectionSpec.isCrop = isCrop;
        return this;
    }

    public SelectionCreator setDefaultSelection(List<Item> list) {
        mSelectionSpec.selectedList = list;
        SelectedItemManager.getInstance().setDefaultSelection(list);
        return this;
    }

}
