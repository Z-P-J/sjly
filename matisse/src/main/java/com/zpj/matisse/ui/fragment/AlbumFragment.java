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

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.matisse.R;
import com.zpj.matisse.event.UpdateTitleEvent;
import com.zpj.matisse.entity.Album;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.model.AlbumManager;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.matisse.ui.widget.MediaGridInset;
import com.zpj.matisse.utils.UIUtils;
import com.zpj.recyclerview.EasyRecyclerLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class AlbumFragment extends BaseFragment implements
        AlbumManager.AlbumCallbacks {

    private final AlbumManager mAlbumManager = new AlbumManager();
    private final List<Album> albumList = new ArrayList<>();

    private EasyRecyclerLayout<Album> recyclerLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        Drawable placeholder = new ColorDrawable(getResources().getColor(R.color.zhihu_album_dropdown_thumbnail_placeholder));
        recyclerLayout = view.findViewById(R.id.recycler_layout);
        int spanCount;
        SelectionSpec mSpec = SelectionSpec.getInstance();
        if (mSpec.gridExpectedSize > 0) {
            spanCount = UIUtils.spanCount(context, mSpec.gridExpectedSize);
        } else {
            spanCount = mSpec.spanCount;
        }
//        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
//        recyclerLayout.getEasyRecyclerView().getRecyclerView().addItemDecoration(new MediaGridInset(spanCount, spacing, false));
        recyclerLayout.setItemRes(R.layout.item_album_grid)
                .setData(albumList)
                .setEnableLoadMore(false)
                .setEnableSwipeRefresh(false)
                .setLayoutManager(new GridLayoutManager(getContext(), spanCount))
                .onBindViewHolder((holder, list, position, payloads) -> {
                    Album album = list.get(position);

                    holder.getItemView().setBackgroundColor(Color.TRANSPARENT);

                    holder.getTextView(R.id.tv_title).setText(album.getDisplayName(context));
                    holder.getTextView(R.id.tv_count).setText("共" + album.getCount() + "张图片");

                    // do not need to load animated Gif
                    SelectionSpec.getInstance().imageEngine.loadThumbnail(
                            context,
                            getResources().getDimensionPixelSize(R.dimen.media_grid_size),
                            placeholder,
                            holder.getImageView(R.id.album_cover),
                            Uri.fromFile(new File(album.getCoverPath()))
                    );
                })
                .onItemClick((holder, view1, album) -> {
                    if (album.isAll() && SelectionSpec.getInstance().capture) {
                        album.addCaptureCount();
                    }
                    onAlbumSelected(album);
                })
                .build();
        recyclerLayout.getEasyRecyclerView().getRecyclerView().setHasFixedSize(true);
        recyclerLayout.showLoading();
        postDelayed(mAlbumManager::loadAlbums, 250);


        mAlbumManager.onCreate(_mActivity, this);
        mAlbumManager.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAlbumManager.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlbumManager.onDestroy();
    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        cursor.moveToFirst();
        do {
            albumList.add(Album.valueOf(cursor));
        } while (cursor.moveToNext());
        recyclerLayout.notifyDataSetChanged();
    }

    @Override
    public void onAlbumReset() {
        albumList.clear();
        recyclerLayout.notifyDataSetChanged();
    }

    private void onAlbumSelected(Album album) {
        if (album.isAll() && album.isEmpty()) {
            recyclerLayout.showEmpty();
        } else {
            recyclerLayout.showContent();
            start(MediaSelectionFragment.newInstance(album));
            EventBus.getDefault().post(new UpdateTitleEvent(album.getDisplayName(context)));
        }
    }
}
