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
package com.zpj.shouji.market.imagepicker;

import android.content.Context;
import android.os.Bundle;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.imagepicker.entity.Item;
import com.zpj.shouji.market.imagepicker.widget.CheckView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class SelectionManager {

    private static SelectionManager INSTANCE;

    public static final String STATE_SELECTION = "state_selection";

    private final Set<Item> mItems;
    private final List<WeakReference<OnCheckStateListener>> listeners;

    public boolean mediaTypeExclusive;
    public boolean showSingleMediaType;
    public boolean countable;
    public int maxSelectable;
    public int spanCount;
    public float thumbnailScale;
    public ImageEngine imageEngine;
    public ImagePicker.OnSelectedListener onSelectedListener;
    public boolean isCrop;
    public boolean isCropAvatar;
    public List<Item> selectedList;

    private SelectionManager() {
        mItems = new LinkedHashSet<>();
        listeners = new ArrayList<>();
        mediaTypeExclusive = true;
        showSingleMediaType = true;
        countable = true;
        maxSelectable = 9;
        spanCount = 3;
        thumbnailScale = 0.5f;
        imageEngine = new GlideEngine();
        isCrop = false;
        this.selectedList = null;
    }

    public void setSelectedList(List<Item> selectedList) {
        this.selectedList = selectedList;
        mItems.clear();
        mItems.addAll(selectedList);
        notifyOnCheckStateUpdate();
    }

    public static SelectionManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SelectionManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SelectionManager();
                }
            }
        }
        return INSTANCE;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && (maxSelectable == 1);
    }








    public void onDestroy() {
        mItems.clear();
        listeners.clear();
        this.onSelectedListener = null;
        imageEngine = null;
        INSTANCE = null;
    }

    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            mItems.clear();
            List<Item> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            if (saved != null) {
                mItems.addAll(saved);
            }
        }
        notifyOnCheckStateUpdate();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
    }

//    public Bundle getDataWithBundle() {
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
//        return bundle;
//    }

    public boolean add(Item item) {
        boolean added = mItems.add(item);
        if (added) {
            notifyOnCheckStateUpdate();
        }
        return added;
    }

    public boolean remove(Item item) {
        boolean removed = mItems.remove(item);
        if (removed) {
            notifyOnCheckStateUpdate();
        }
        return removed;
    }


    public List<Item> asList() {
        return new ArrayList<>(mItems);
    }

//    public List<Uri> asListOfUri() {
//        List<Uri> uris = new ArrayList<>();
//        for (Item item : mItems) {
//            uris.add(item.getContentUri());
//        }
//        return uris;
//    }
//
//    public List<String> asListOfString(Context context) {
//        List<String> paths = new ArrayList<>();
//        for (Item item : mItems) {
//            paths.add(item.getPath(context));
//        }
//        return paths;
//    }

    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    public boolean isSelected(Item item) {
        return mItems.contains(item);
    }

    public boolean maxSelectableReached() {
        return mItems.size() == maxSelectable;
    }

    public String isAcceptable(Context context, Item item) {
        if (maxSelectableReached()) {
            return context.getResources().getString(
                    R.string.error_over_count,
                    maxSelectable);
        }
        return null;
    }

    public int count() {
        return mItems.size();
    }

    public int checkedNumOf(Item item) {
        int index = new ArrayList<>(mItems).indexOf(item);
        return index == -1 ? CheckView.UNCHECKED : index + 1;
    }

    public void addOnCheckStateListener(OnCheckStateListener listener) {
        for (WeakReference<OnCheckStateListener> listenerWeakReference : listeners) {
            if (listenerWeakReference.get() != null && listenerWeakReference.get() == listener) {
                return;
            }
        }
        listeners.add(new WeakReference<>(listener));
    }

    public void removeOnCheckStateListener(OnCheckStateListener listener) {
        for (WeakReference<OnCheckStateListener> listenerWeakReference : listeners) {
            if (listenerWeakReference.get() != null && listenerWeakReference.get() == listener) {
                listeners.remove(listenerWeakReference);
                return;
            }
        }
    }

    public void notifyOnCheckStateUpdate() {
        for (WeakReference<OnCheckStateListener> listenerWeakReference : listeners) {
            if (listenerWeakReference.get() != null) {
                listenerWeakReference.get().onUpdate();
            }
        }
    }

    public interface OnCheckStateListener {
        void onUpdate();
    }
}
