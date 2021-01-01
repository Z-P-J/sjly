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
package com.zpj.imagepicker.entity;

import com.zpj.imagepicker.ImagePicker;
import com.zpj.imagepicker.MimeType;
import com.zpj.imagepicker.engine.ImageEngine;
import com.zpj.imagepicker.engine.impl.GlideEngine;

import java.util.List;
import java.util.Set;

public final class SelectionSpec {

    public Set<MimeType> mimeTypeSet;
    public boolean mediaTypeExclusive;
    public boolean showSingleMediaType = true;
    public boolean countable = true;
    public int maxSelectable = 9;
    public int maxImageSelectable;
    public int maxVideoSelectable;
    public int spanCount;
    public float thumbnailScale;
    public ImageEngine imageEngine;
    public boolean hasInited;
    public ImagePicker.OnSelectedListener onSelectedListener;
    public boolean isCrop;//是否进行裁剪
    public List<Item> selectedList;

    private SelectionSpec() {
    }

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectionSpec getCleanInstance() {
        SelectionSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private void reset() {
        mimeTypeSet = null;
        mediaTypeExclusive = true;
        showSingleMediaType = true;
        countable = true;
        maxSelectable = 9;
        maxImageSelectable = 0;
        maxVideoSelectable = 0;
        spanCount = 3;
        thumbnailScale = 0.5f;
        imageEngine = new GlideEngine();
        hasInited = true;
        isCrop = false;
        this.selectedList = null;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && (maxSelectable == 1 || (maxImageSelectable == 1 && maxVideoSelectable == 1));
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && MimeType.ofImage().containsAll(mimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && MimeType.ofVideo().containsAll(mimeTypeSet);
    }

    private static final class InstanceHolder {
        private static final SelectionSpec INSTANCE = new SelectionSpec();
    }
}
