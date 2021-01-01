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
package com.zpj.matisse.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment3;
import com.zpj.matisse.R;
import com.zpj.matisse.entity.Album;
import com.zpj.matisse.entity.IncapableCause;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.model.AlbumMediaManager;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.matisse.ui.fragment.LocalImageViewer;
import com.zpj.matisse.utils.UIUtils;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.util.ArrayList;
import java.util.List;

public class MediaSelectionLayout extends EasyRecyclerLayout<Item> implements
        AlbumMediaManager.AlbumMediaCallbacks,
        IEasy.OnBindViewHolderListener<Item>,
        MediaGrid.OnMediaGridClickListener,
        SelectedItemManager.OnCheckStateListener {

    public static final String EXTRA_ALBUM = "extra_album";
    private static final String UPDATE_CHECK_STATUS = "update_check_status";

    private final AlbumMediaManager mAlbumMediaManager = new AlbumMediaManager();
    private final List<Item> itemList = new ArrayList<>();
    protected final SelectedItemManager mSelectedCollection;
    protected final SelectionSpec mSpec;

    private Drawable placeholder;

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
        placeholder = new ColorDrawable(context.getResources().getColor(R.color.zhihu_item_placeholder));

        int spanCount;

        if (mSpec.gridExpectedSize > 0) {
            spanCount = UIUtils.spanCount(context, mSpec.gridExpectedSize);
        } else {
            spanCount = mSpec.spanCount;
        }
        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        layoutManager = new GridLayoutManager(getContext(), spanCount);
        setItemRes(R.layout.media_grid_item)
                .setData(itemList)
                .addItemDecoration(new MediaGridInset(spanCount, spacing, false))
                .setEnableLoadMore(false)
                .setLayoutManager(layoutManager)
                .onBindViewHolder(this)
                .build();
        showLoading();
    }

    public void init(FragmentActivity activity) {
        mAlbumMediaManager.onCreate(activity, MediaSelectionLayout.this);
    }

    public void loadAlbum(FragmentActivity activity, Album album) {
        itemList.clear();
        notifyDataSetChanged();
        showLoading();
        mAlbumMediaManager.onCreate(activity, MediaSelectionLayout.this);
        mAlbumMediaManager.load(album);
    }

    public void onDestroy() {
        mAlbumMediaManager.onDestroy();
//        EventBus.getDefault().post(new UpdateTitleEvent(MatisseFragment.TITLE));
        mSelectedCollection.removeOnCheckStateListener(this);
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        itemList.clear();
        cursor.moveToFirst();
        do {
            itemList.add(Item.valueOf(cursor));
        } while (cursor.moveToNext());
        notifyDataSetChanged();
        mAlbumMediaManager.onDestroy();
    }

    @Override
    public void onAlbumMediaReset() {
        itemList.clear();
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
                getImageResize(getContext()),
                placeholder,
                mSpec.countable,
                holder
        ));
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
                        ImageView imageView = mediaGrid.getThumbnail();
                        if (imageView == null) {
                            imageView = thumbnail;
                        }
                        popup.updateSrcView(imageView);
                    }
                })
//                .setImageList(itemList)
//                .setNowIndex(holder.getAdapterPosition())
//                .setSourceImageView((imageItemView, pos, isCurrent) -> {
//                    RecyclerView recyclerView = getEasyRecyclerView().getRecyclerView();
//                    int layoutPos = recyclerView.indexOfChild(holder.getItemView());
//                    View view = recyclerView.getChildAt(layoutPos + pos - position);
//                    ImageView imageView;
//                    if (view != null) {
//                        imageView = view.findViewById(R.id.media_thumbnail);
//                    } else {
//                        imageView = thumbnail;
//                    }
//                    imageItemView.update(imageView);
//                })
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
        IncapableCause cause = mSelectedCollection.isAcceptable(getContext(), item);
        IncapableCause.handleCause(getContext(), cause);
        return cause == null;
    }

    private void notifyCheckStateChanged() {
        notifyDataSetChanged();
    }

    @Override
    public void onUpdate() {
        notifyVisibleItemChanged(UPDATE_CHECK_STATUS);
//        int first = layoutManager.findFirstVisibleItemPosition();
//        int last = layoutManager.findLastVisibleItemPosition();
//        Log.d("MediaSelectionFragment", "first=" + first + " last=" + last);
//        if (first == -1 || last == -1) {
//            return;
//        }
//
//        for (int i = first; i <= last; i++) {
//            notifyItemChanged(i, UPDATE_CHECK_STATUS);
////            RecyclerView.ViewHolder holder = recyclerLayout.getEasyRecyclerView().getRecyclerView().findViewHolderForAdapterPosition(first);
////            if (holder instanceof EasyViewHolder) {
////                Log.d("MediaSelectionFragment", "setCheckStatus");
////                setCheckStatus(itemList.get(i), ((EasyViewHolder) holder).getView(R.id.media_grid));
////            }
//        }
    }
}
