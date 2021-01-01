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
package com.zpj.imagepicker.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment3;
import com.zpj.imagepicker.entity.Album;
import com.zpj.imagepicker.entity.Item;
import com.zpj.imagepicker.entity.SelectionSpec;
import com.zpj.imagepicker.loader.AlbumMediaLoader;
import com.zpj.imagepicker.model.SelectedItemManager;
import com.zpj.imagepicker.ui.fragment.LocalImageViewer;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.toast.ZToast;

import java.util.ArrayList;
import java.util.List;

public class MediaSelectionLayout extends EasyRecyclerLayout<Item> implements
        IEasy.OnBindViewHolderListener<Item>,
        MediaGrid.OnMediaGridClickListener,
        SelectedItemManager.OnCheckStateListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 2;

    public static final String EXTRA_ALBUM = "extra_album";
    private static final String ARGS_ALBUM = "args_album";
    private static final String UPDATE_CHECK_STATUS = "update_check_status";

    private final List<Item> itemList = new ArrayList<>();
    protected final SelectedItemManager mSelectedCollection;
    protected final SelectionSpec mSpec;

    private LoaderManager mLoaderManager;

    private GridLayoutManager layoutManager;

    private int mImageResize;

    public MediaSelectionLayout(@NonNull Context context) {
        this(context, null);
    }

    public MediaSelectionLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaSelectionLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSelectedCollection = SelectedItemManager.getInstance();
        mSelectedCollection.addOnCheckStateListener(this);
        mSpec = SelectionSpec.getInstance();
        initView(context);
    }

    protected void initView(Context context) {
        int spanCount;

        spanCount = Math.max(1, mSpec.spanCount);
        layoutManager = new GridLayoutManager(getContext(), spanCount);
        setItemRes(R.layout.matisse_media_grid_item)
                .setData(itemList)
                .addItemDecoration(new MediaGridInset(spanCount, context.getResources().getDimensionPixelSize(
                        R.dimen.media_grid_spacing), true))
                .setEnableLoadMore(false)
                .setLayoutManager(layoutManager)
                .onBindViewHolder(this)
                .build();
        showLoading();
    }

    public void loadAlbum(FragmentActivity activity, Album album) {
        itemList.clear();
        notifyDataSetChanged();
        showLoading();
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mLoaderManager = LoaderManager.getInstance(activity);
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, album);
        mLoaderManager.initLoader(LOADER_ID, args, this);
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mSelectedCollection.removeOnCheckStateListener(this);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<Item> list, int position, List<Object> payloads) {
        Item item = list.get(position);
        MediaGrid mMediaGrid = holder.getView(R.id.media_grid);
        mMediaGrid.setTag(position);
        for (Object payload : payloads) {
            if (UPDATE_CHECK_STATUS.equals(payload)) {
                setCheckStatus(item, mMediaGrid);
                return;
            }
        }


        mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                mSpec.countable,
                holder));
        Log.d("MediaSelectionLayout", "onBindViewHolder mMediaGrid.bindMedia");
        mMediaGrid.bindMedia(item);
        mMediaGrid.setOnMediaGridClickListener(this);
        setCheckStatus(item, mMediaGrid);
    }

    @Override
    public void onThumbnailClicked(ImageView thumbnail, Item item, EasyViewHolder holder) {
        new LocalImageViewer()
                .setSelectedItemManager(mSelectedCollection)
                .setCountable(mSpec.countable)
                .setSingleSelectionModeEnabled(mSpec.singleSelectionModeEnabled())
                .setImageUrls(itemList)
                .setSrcView(thumbnail, holder.getAdapterPosition())
                .setSrcViewUpdateListener(new ImageViewerDialogFragment3.OnSrcViewUpdateListener<Item>() {
                    private boolean isFirst = true;

                    @Override
                    public void onSrcViewUpdate(@NonNull ImageViewerDialogFragment3<Item> popup, int pos) {
                        RecyclerView recyclerView = getEasyRecyclerView().getRecyclerView();
                        if (!isFirst) {
                            recyclerView.scrollToPosition(pos);
                        }
                        isFirst = false;
                        MediaGrid mediaGrid = recyclerView.findViewWithTag(pos);
                        ImageView imageView;
                        if (mediaGrid == null) {
                            imageView = null;
                        } else {
                            imageView = mediaGrid.getThumbnail();
                        }
                        popup.updateSrcView(imageView);
                    }
                })
                .show(getContext());
    }

    @Override
    public void onCheckViewClicked(CheckView checkView, Item item, EasyViewHolder holder) {
        if (mSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            } else {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            }
        } else {
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged();
            } else {
                if (assertAddSelection(item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged();
                }
            }
        }
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            int spanCount = layoutManager.getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.media_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * mSpec.thumbnailScale);
        }
        return mImageResize;
    }

    private void setCheckStatus(Item item, MediaGrid mediaGrid) {
        if (mSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setCheckedNum(checkedNum);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setCheckedNum(checkedNum);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setChecked(true);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setChecked(false);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setChecked(false);
                }
            }
        }
    }

    private boolean assertAddSelection(Item item) {
        String cause = mSelectedCollection.isAcceptable(getContext(), item);
        if (cause != null) {
            ZToast.warning(cause);
        }
        return cause == null;
    }

    private void notifyCheckStateChanged() {
        notifyVisibleItemChanged(UPDATE_CHECK_STATUS);
    }

    @Override
    public void onUpdate() {
        notifyVisibleItemChanged(UPDATE_CHECK_STATUS);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Album album = bundle.getParcelable(ARGS_ALBUM);
        if (album == null) {
            return null;
        }
        return AlbumMediaLoader.newInstance(getContext(), album, false);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        itemList.clear();
        cursor.moveToFirst();
        do {
            itemList.add(Item.valueOf(cursor));
        } while (cursor.moveToNext());
        Log.d("MediaSelectionLayout", "onLoadFinished notifyDataSetChanged");
        notifyDataSetChanged();
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        itemList.clear();
    }
}
