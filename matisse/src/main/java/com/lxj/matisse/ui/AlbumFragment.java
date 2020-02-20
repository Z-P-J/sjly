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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.lxj.matisse.CaptureMode;
import com.lxj.matisse.MatisseConst;
import com.lxj.matisse.R;
import com.lxj.matisse.internal.entity.Album;
import com.lxj.matisse.internal.entity.Item;
import com.lxj.matisse.internal.entity.SelectionSpec;
import com.lxj.matisse.internal.model.AlbumCollection;
import com.lxj.matisse.internal.model.SelectedItemCollection;
import com.lxj.matisse.internal.ui.AlbumPreviewActivity;
import com.lxj.matisse.internal.ui.BasePreviewActivity;
import com.lxj.matisse.internal.ui.MediaSelectionFragment;
import com.lxj.matisse.internal.ui.SelectedPreviewActivity;
import com.lxj.matisse.internal.ui.adapter.AlbumMediaAdapter;
import com.lxj.matisse.internal.ui.adapter.AlbumsAdapter;
import com.lxj.matisse.internal.ui.widget.CheckRadioView;
import com.lxj.matisse.internal.ui.widget.IncapableDialog;
import com.lxj.matisse.internal.utils.MediaStoreCompat;
import com.lxj.matisse.internal.utils.PathUtils;
import com.lxj.matisse.internal.utils.PhotoMetadataUtils;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.yalantis.ucrop.UCrop;
import com.zpj.fragmentation.BaseFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class AlbumFragment extends BaseFragment implements
        AlbumCollection.AlbumCallbacks, AdapterView.OnItemClickListener {

    private final AlbumCollection mAlbumCollection = new AlbumCollection();

    private MatisseFragment matisseFragment;

    private AlbumsAdapter mAlbumsAdapter;
    private View mEmptyView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {


        mEmptyView = view.findViewById(R.id.empty_view);

        mAlbumsAdapter = new AlbumsAdapter(context, null, false);
        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(this.mAlbumsAdapter);
        listView.setOnItemClickListener(this);

        mAlbumCollection.onCreate(_mActivity, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
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

    //相册条目点击
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mAlbumCollection.setStateCurrentSelection(position);
        mAlbumsAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mAlbumsAdapter.getCursor());
        if (album.isAll() && SelectionSpec.getInstance().capture) {
            album.addCaptureCount();
        }
        onAlbumSelected(album);
    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        mAlbumsAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumReset() {
        mAlbumsAdapter.swapCursor(null);
    }

    private void onAlbumSelected(Album album) {
        mAlbumsAdapter.updateSelection(album.getId());
        if (album.isAll() && album.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
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
