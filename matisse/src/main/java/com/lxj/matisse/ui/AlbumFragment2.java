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
package com.lxj.matisse.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lxj.matisse.R;
import com.lxj.matisse.internal.entity.Album;
import com.lxj.matisse.internal.entity.SelectionSpec;
import com.lxj.matisse.internal.model.AlbumCollection;
import com.lxj.matisse.internal.ui.MediaSelectionFragment;
import com.lxj.matisse.internal.ui.adapter.AlbumsAdapter;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class AlbumFragment2 extends BaseFragment implements
        AlbumCollection.AlbumCallbacks {

    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private final List<Album> albumList = new ArrayList<>();

    private MatisseFragment matisseFragment;

    private EasyRecyclerLayout<Album> recyclerLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album2;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        Drawable placeholder = new ColorDrawable(getResources().getColor(R.color.zhihu_album_dropdown_thumbnail_placeholder));
        recyclerLayout = view.findViewById(R.id.recycler_layout);
        recyclerLayout.setItemRes(R.layout.album_list_item)
                .setData(albumList)
                .setEnableLoadMore(true)
                .setEnableSwipeRefresh(false)
                .onLoadMore((enabled, currentPage) -> {
                    recyclerLayout.showLoading();
                    postOnEnterAnimationEnd(mAlbumCollection::loadAlbums);
                    return false;
                })
                .onBindViewHolder((holder, list, position, payloads) -> {
                    Album album = list.get(position);
                    holder.getItemView().setBackgroundColor(Color.TRANSPARENT);

                    holder.getTextView(R.id.album_name).setText(album.getDisplayName(context));
                    holder.getTextView(R.id.album_media_count).setText(String.valueOf(album.getCount()));

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

        mAlbumCollection.onCreate(_mActivity, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
    }

//    //相册条目点击
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//        mAlbumCollection.setStateCurrentSelection(position);
//        mAlbumsAdapter.getCursor().moveToPosition(position);
//        Album album = Album.valueOf(mAlbumsAdapter.getCursor());
//        if (album.isAll() && SelectionSpec.getInstance().capture) {
//            album.addCaptureCount();
//        }
//        onAlbumSelected(album);
//    }

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
//        mAlbumsAdapter.swapCursor(null);
    }

    private void onAlbumSelected(Album album) {
//        mAlbumsAdapter.updateSelection(album.getId());
        if (album.isAll() && album.isEmpty()) {
            recyclerLayout.showEmpty();
        } else {
            recyclerLayout.showContent();
            start(MediaSelectionFragment.newInstance(album)
                    .setSelectionProvider(matisseFragment)
                    .setCheckStateListener(matisseFragment)
                    .setOnMediaClickListener(matisseFragment)
            );
        }
    }

    public void setMatisseFragment(MatisseFragment matisseFragment) {
        this.matisseFragment = matisseFragment;
    }
}
