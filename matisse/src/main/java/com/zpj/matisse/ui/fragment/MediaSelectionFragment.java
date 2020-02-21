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
package com.zpj.matisse.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.zpj.matisse.R;
import com.zpj.matisse.entity.Album;
import com.zpj.matisse.entity.IncapableCause;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.event.UpdateTitleEvent;
import com.zpj.matisse.model.AlbumMediaManager;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.matisse.ui.widget.CheckView;
import com.zpj.matisse.ui.widget.CustomImageViewerPopup;
import com.zpj.matisse.ui.widget.MediaGrid;
import com.zpj.matisse.ui.widget.MediaGridInset;
import com.zpj.matisse.utils.UIUtils;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MediaSelectionFragment extends BaseFragment implements
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

    private EasyRecyclerLayout<Item> recyclerLayout;

    private Album album;

    private int mImageResize;

    public static MediaSelectionFragment newInstance(Album album) {
        MediaSelectionFragment fragment = new MediaSelectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    public MediaSelectionFragment() {
        mSelectedCollection = SelectedItemManager.getInstance();
        mSelectedCollection.addOnCheckStateListener(this);
        mSpec = SelectionSpec.getInstance();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_media_selection;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        album = getArguments().getParcelable(EXTRA_ALBUM);
        placeholder = new ColorDrawable(context.getResources().getColor(R.color.zhihu_item_placeholder));

        int spanCount;

        if (mSpec.gridExpectedSize > 0) {
            spanCount = UIUtils.spanCount(context, mSpec.gridExpectedSize);
        } else {
            spanCount = mSpec.spanCount;
        }
        recyclerLayout = view.findViewById(R.id.recycler_layout);
        recyclerLayout.setItemRes(R.layout.media_grid_item)
                .setData(itemList)
                .setEnableLoadMore(false)
                .setLayoutManager(new GridLayoutManager(getContext(), spanCount))
                .onBindViewHolder(this)
                .build();
        recyclerLayout.getEasyRecyclerView().getRecyclerView().setHasFixedSize(true);
        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        recyclerLayout.getEasyRecyclerView().getRecyclerView().addItemDecoration(new MediaGridInset(spanCount, spacing, false));
        recyclerLayout.showLoading();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        mAlbumMediaManager.onCreate(_mActivity, MediaSelectionFragment.this);
        mAlbumMediaManager.load(album);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumMediaManager.onDestroy();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().post(new UpdateTitleEvent("选择图片"));
        mSelectedCollection.removeOnCheckStateListener(this);
        super.onDestroy();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        itemList.clear();
        cursor.moveToFirst();
        do {
            itemList.add(Item.valueOf(cursor));
        } while (cursor.moveToNext());
        recyclerLayout.notifyDataSetChanged();
    }

    @Override
    public void onAlbumMediaReset() {
        itemList.clear();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<Item> list, int position, List<Object> payloads) {
        Item item = list.get(position);
        MediaGrid mMediaGrid = holder.getView(R.id.media_grid);
        for (Object payload : payloads) {
            if (UPDATE_CHECK_STATUS.equals(payload)) {
                setCheckStatus(item, mMediaGrid);
                return;
            }
        }


        mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                getImageResize(context),
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
        final int position = holder.getAdapterPosition();
        CustomImageViewerPopup.with(thumbnail.getContext())
//                .setCheckStateListener(this::notifyCheckStateChanged)
                .setImageUrls(itemList)
                .setSrcView(thumbnail, holder.getAdapterPosition())
                .setSrcViewUpdateListener((popupView, pos) -> {
                    RecyclerView recyclerView = recyclerLayout.getEasyRecyclerView().getRecyclerView();
                    int layoutPos = recyclerView.indexOfChild(holder.getItemView());
                    View view = recyclerView.getChildAt(layoutPos + pos - position);
                    ImageView imageView;
                    if (view != null) {
                        imageView = view.findViewById(R.id.media_thumbnail);
                    } else {
                        imageView = thumbnail;
                    }
                    popupView.updateSrcView(imageView);
                })
                .show();
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
            RecyclerView.LayoutManager lm = recyclerLayout.getEasyRecyclerView().getRecyclerView().getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
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
        IncapableCause cause = mSelectedCollection.isAcceptable(context, item);
        IncapableCause.handleCause(getContext(), cause);
        return cause == null;
    }

    private void notifyCheckStateChanged() {
        recyclerLayout.notifyDataSetChanged();
    }

    @Override
    public void onUpdate() {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerLayout.getEasyRecyclerView().getRecyclerView().getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        Log.d("MediaSelectionFragment", "first=" + first + " last=" + last);
        if (first == -1 || last == -1) {
            return;
        }
        for (int i = first; i <= last; i++) {
            recyclerLayout.notifyItemChanged(i, UPDATE_CHECK_STATUS);
//            RecyclerView.ViewHolder holder = recyclerLayout.getEasyRecyclerView().getRecyclerView().findViewHolderForAdapterPosition(first);
//            if (holder instanceof EasyViewHolder) {
//                Log.d("MediaSelectionFragment", "setCheckStatus");
//                setCheckStatus(itemList.get(i), ((EasyViewHolder) holder).getView(R.id.media_grid));
//            }
        }
    }
}
