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
package com.zpj.matisse.model;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.zpj.matisse.R;
import com.zpj.matisse.entity.IncapableCause;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.ui.widget.CheckView;
import com.zpj.matisse.utils.PathUtils;
import com.zpj.matisse.utils.PhotoMetadataUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class SelectedItemManager {

    public static final String STATE_SELECTION = "state_selection";
    public static final String STATE_COLLECTION_TYPE = "state_collection_type";

    private static SelectedItemManager collection;

    /**
     * Empty collection
     */
    public static final int COLLECTION_UNDEFINED = 0x00;
    /**
     * Collection only with images
     */
    public static final int COLLECTION_IMAGE = 0x01;
    /**
     * Collection only with videos
     */
    public static final int COLLECTION_VIDEO = 0x01 << 1;
    /**
     * Collection with images and videos.
     */
    public static final int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;
//    private final Context mContext;
    private final Set<Item> mItems = new LinkedHashSet<>();
    private final List<WeakReference<OnCheckStateListener>> listeners = new ArrayList<>();
    private int mCollectionType = COLLECTION_UNDEFINED;

    public static SelectedItemManager getInstance() {
        if (collection == null) {
            synchronized(SelectedItemManager.class) {
                if (collection == null) {
                    collection = new SelectedItemManager();
                }
            }
        }
        return collection;
    }

    private SelectedItemManager() {

    }

    public void onDestroy() {
        mCollectionType = COLLECTION_UNDEFINED;
        mItems.clear();
        listeners.clear();
        collection = null;
    }

    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            mItems.clear();
            List<Item> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            if (saved != null) {
                mItems.addAll(saved);
            }
            mCollectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        }
        notifyOnCheckStateUpdate();
    }

    public void setDefaultSelection(List<Item> uris) {
        mItems.clear();
        mItems.addAll(uris);
        notifyOnCheckStateUpdate();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        outState.putInt(STATE_COLLECTION_TYPE, mCollectionType);
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        bundle.putInt(STATE_COLLECTION_TYPE, mCollectionType);
        return bundle;
    }

    public boolean add(Item item) {
        if (typeConflict(item)) {
            throw new IllegalArgumentException("Can't select images and videos at the same time.");
        }
        boolean added = mItems.add(item);
        if (added) {
            if (mCollectionType == COLLECTION_UNDEFINED) {
                if (item.isImage()) {
                    mCollectionType = COLLECTION_IMAGE;
                } else if (item.isVideo()) {
                    mCollectionType = COLLECTION_VIDEO;
                }
            } else if (mCollectionType == COLLECTION_IMAGE) {
                if (item.isVideo()) {
                    mCollectionType = COLLECTION_MIXED;
                }
            } else if (mCollectionType == COLLECTION_VIDEO) {
                if (item.isImage()) {
                    mCollectionType = COLLECTION_MIXED;
                }
            }
            notifyOnCheckStateUpdate();
        }
        return added;
    }

    public boolean remove(Item item) {
        boolean removed = mItems.remove(item);
        if (removed) {
            if (mItems.size() == 0) {
                mCollectionType = COLLECTION_UNDEFINED;
            } else {
                if (mCollectionType == COLLECTION_MIXED) {
                    refineCollectionType();
                }
            }
            notifyOnCheckStateUpdate();
        }
        return removed;
    }

    public void overwrite(ArrayList<Item> items, int collectionType) {
        if (items.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        } else {
            mCollectionType = collectionType;
        }
        mItems.clear();
        mItems.addAll(items);
        notifyOnCheckStateUpdate();
    }


    public List<Item> asList() {
        return new ArrayList<>(mItems);
    }

    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (Item item : mItems) {
            uris.add(item.getContentUri());
        }
        return uris;
    }

    public List<String> asListOfString(Context context) {
        List<String> paths = new ArrayList<>();
        for (Item item : mItems) {
            paths.add(PathUtils.getPath(context, item.getContentUri()));
        }
        return paths;
    }

    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    public boolean isSelected(Item item) {
        return mItems.contains(item);
    }

    public IncapableCause isAcceptable(Context context, Item item) {
        if (maxSelectableReached()) {
            int maxSelectable = currentMaxSelectable();
            String cause;

            try {
                cause = context.getResources().getQuantityString(
                        R.string.error_over_count,
                        maxSelectable,
                        maxSelectable
                );
            } catch (Resources.NotFoundException e) {
                cause = context.getString(
                        R.string.error_over_count,
                        maxSelectable
                );
            } catch (NoClassDefFoundError e) {
                cause = context.getString(
                        R.string.error_over_count,
                        maxSelectable
                );
            }

            return new IncapableCause(cause);
        } else if (typeConflict(item)) {
            return new IncapableCause(context.getString(R.string.error_type_conflict));
        }

        return PhotoMetadataUtils.isAcceptable(context, item);
    }

    public boolean maxSelectableReached() {
        return mItems.size() == currentMaxSelectable();
    }

    // depends
    private int currentMaxSelectable() {
        SelectionSpec spec = SelectionSpec.getInstance();
        if (spec.maxSelectable > 0) {
            return spec.maxSelectable;
        } else if (mCollectionType == COLLECTION_IMAGE) {
            return spec.maxImageSelectable;
        } else if (mCollectionType == COLLECTION_VIDEO) {
            return spec.maxVideoSelectable;
        } else {
            return spec.maxSelectable;
        }
    }

    public int getCollectionType() {
        return mCollectionType;
    }

    private void refineCollectionType() {
        boolean hasImage = false;
        boolean hasVideo = false;
        for (Item i : mItems) {
            if (i.isImage() && !hasImage) hasImage = true;
            if (i.isVideo() && !hasVideo) hasVideo = true;
        }
        if (hasImage && hasVideo) {
            mCollectionType = COLLECTION_MIXED;
        } else if (hasImage) {
            mCollectionType = COLLECTION_IMAGE;
        } else if (hasVideo) {
            mCollectionType = COLLECTION_VIDEO;
        }
    }

    /**
     * Determine whether there will be conflict media types. A user can only select images and videos at the same time
     * while {@link SelectionSpec#mediaTypeExclusive} is set to false.
     */
    public boolean typeConflict(Item item) {
        return SelectionSpec.getInstance().mediaTypeExclusive
                && ((item.isImage() && (mCollectionType == COLLECTION_VIDEO || mCollectionType == COLLECTION_MIXED))
                || (item.isVideo() && (mCollectionType == COLLECTION_IMAGE || mCollectionType == COLLECTION_MIXED)));
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
