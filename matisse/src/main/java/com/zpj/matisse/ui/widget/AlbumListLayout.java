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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.matisse.R;
import com.zpj.matisse.entity.Album;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.event.UpdateTitleEvent;
import com.zpj.matisse.model.AlbumManager;
import com.zpj.matisse.ui.fragment.MediaSelectionFragment;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class AlbumListLayout extends RecyclerView implements
        AlbumManager.AlbumCallbacks {

    private final AlbumManager mAlbumManager = new AlbumManager();
    private final List<Album> albumList = new ArrayList<>();


    private EasyRecyclerView<Album> recyclerLayout;

    private OnAlbumSelectListener onAlbumSelectListener;

    private int selectPosition;

    public AlbumListLayout(@NonNull Context context) {
        this(context, null);
    }

    public AlbumListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumListLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Drawable placeholder = new ColorDrawable(getResources().getColor(R.color.zhihu_album_dropdown_thumbnail_placeholder));
        recyclerLayout = new EasyRecyclerView<>(this);
        recyclerLayout.setItemRes(R.layout.item_album_linear)
                .setData(albumList)
//                .setLayoutManager(new GridLayoutManager(context, 2))
                .onBindViewHolder((holder, list, position, payloads) -> {
                    Album album = list.get(position);

                    holder.getItemView().setBackgroundColor(Color.TRANSPARENT);

                    holder.setText(R.id.tv_title, album.getDisplayName(context));
                    holder.setText(R.id.tv_count, "共" + album.getCount() + "张图片");

                    // do not need to load animated Gif
                    SelectionSpec.getInstance().imageEngine.loadThumbnail(
                            context,
                            getResources().getDimensionPixelSize(R.dimen.media_grid_size),
                            placeholder,
                            holder.getImageView(R.id.album_cover),
                            Uri.fromFile(new File(album.getCoverPath()))
                    );

                    if (selectPosition == position) {
                        holder.setVisible(R.id.iv_current, true);
                    } else {
                        holder.setInVisible(R.id.iv_current);
                    }

                })
                .onItemClick((holder, view1, album) -> {
                    selectPosition = holder.getRealPosition();
                    recyclerLayout.notifyVisibleItemChanged();
                    if (album.isAll() && SelectionSpec.getInstance().capture) {
                        album.addCaptureCount();
                    }
                    if (onAlbumSelectListener != null) {
                        onAlbumSelectListener.onSelect(album);
                    }
//                    onAlbumSelected(album);
                })
                .build();
//        recyclerLayout.getEasyRecyclerView().getRecyclerView().setHasFixedSize(true);
        recyclerLayout.showLoading();
    }

    public void init(FragmentActivity activity, Bundle savedInstanceState) {
        mAlbumManager.onCreate(activity, this);
        mAlbumManager.onRestoreInstanceState(savedInstanceState);
    }

    public void loadAlbums() {
        albumList.clear();
        recyclerLayout.showLoading();
        mAlbumManager.loadAlbums();
    }

    public void onSaveInstanceState(Bundle outState) {
        mAlbumManager.onSaveInstanceState(outState);
    }

    public void onDestroy() {
        mAlbumManager.onDestroy();
    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        cursor.moveToFirst();
        do {
            albumList.add(Album.valueOf(cursor));
        } while (cursor.moveToNext());
//        recyclerLayout.notifyDataSetChanged();
        if (albumList.isEmpty()) {
            recyclerLayout.showEmpty();
        } else {
            if (onAlbumSelectListener != null) {
                onAlbumSelectListener.onSelect(albumList.get(0));
            }
            selectPosition = 0;
            recyclerLayout.showContent();
        }
    }

    @Override
    public void onAlbumReset() {
        albumList.clear();
        recyclerLayout.notifyDataSetChanged();
    }

    public void setOnAlbumSelectListener(OnAlbumSelectListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public interface OnAlbumSelectListener {
        void onSelect(Album album);
    }

//    private void onAlbumSelected(Album album) {
//        if (album.isAll() && album.isEmpty()) {
//            recyclerLayout.showEmpty();
//        } else {
//            recyclerLayout.showContent();
//            start(MediaSelectionFragment.newInstance(album));
//            UpdateTitleEvent.post(album.getDisplayName(context));
//        }
//    }
}
