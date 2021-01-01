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
package com.zpj.imagepicker.ui.fragment;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.ucrop.UCrop;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.imagepicker.entity.Album;
import com.zpj.imagepicker.entity.SelectionSpec;
import com.zpj.imagepicker.model.SelectedItemManager;
import com.zpj.imagepicker.ui.widget.AlbumListLayout;
import com.zpj.imagepicker.ui.widget.MediaSelectionLayout;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class ImagePickerFragment extends SkinFragment implements
        View.OnClickListener,
        SelectedItemManager.OnCheckStateListener {

    public static final String TITLE = "选择图片";
    private SelectedItemManager mSelectedCollection;
    private SelectionSpec mSpec;

    private TextView mButtonPreview;
    private TextView mButtonApply;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private MediaSelectionLayout mediaSelectionLayout;
    private AlbumListLayout albumListLayout;

    private boolean popToSelect;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image_picker;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        mSelectedCollection = SelectedItemManager.getInstance();
        mSelectedCollection.addOnCheckStateListener(this);
        mSpec = SelectionSpec.getInstance();
        if (!mSpec.hasInited) {
            pop();
            return;
        }

        setToolbarTitle(TITLE);

        FrameLayout bottomBar = findViewById(R.id.bottom_bar);
        mButtonPreview = view.findViewById(R.id.button_preview);
        mButtonApply = view.findViewById(R.id.button_apply);
        mButtonPreview.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
        LinearLayout dragView = findViewById(R.id.dragView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) dragView.getLayoutParams();
        params.topMargin = ScreenUtils.getStatusBarHeight(context);

        updateBottomToolbar();

        slidingUpPanelLayout = this.findViewById(R.id.slidingUpPanelLayout);
        slidingUpPanelLayout.setAnchorPoint(0.5f);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset <= 0.5) {
                    bottomBar.setAlpha(1 - 2 * slideOffset);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    setToolbarTitle("选择相册");
//                    bottomBar.setVisibility(View.INVISIBLE);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    setToolbarTitle("选择相册");
//                    bottomBar.setVisibility(View.INVISIBLE);
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    bottomBar.setVisibility(View.VISIBLE);
                    setToolbarTitle(albumListLayout.getCurrentAlbum().getDisplayName(context));
                }
            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        mediaSelectionLayout = findViewById(R.id.layout_media_selection);
        albumListLayout = findViewById(R.id.gallery_recycler_view);
        albumListLayout.init(_mActivity, savedInstanceState);
        albumListLayout.setOnAlbumSelectListener(new AlbumListLayout.OnAlbumSelectListener() {
            @Override
            public void onSelect(Album album) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                if (album.isAll() && album.isEmpty()) {
                    mediaSelectionLayout.showEmpty();
                } else {
                    Log.d("MediaSelectionLayout", "onSelect album=" + album);
                    mediaSelectionLayout.loadAlbum(_mActivity, album);
                    setToolbarTitle(album.getDisplayName(context));
                    setToolbarSubTitle("共" + album.getCount() + "张图片");
                }
            }
        });
        postOnEnterAnimationEnd(new Runnable() {
            @Override
            public void run() {
                albumListLayout.loadAlbums();
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {

        if (popToSelect) {
            if (mSpec.onSelectedListener != null) {
                if (mSpec.selectedList != null) {
                    mSpec.selectedList.clear();
                    mSpec.selectedList.addAll(mSelectedCollection.asList());
                    mSpec.onSelectedListener.onSelected(mSpec.selectedList);
                } else {
                    mSpec.onSelectedListener.onSelected(mSelectedCollection.asList());
                }
            }
        }
        popToSelect = false;

        albumListLayout.onDestroy();
        mediaSelectionLayout.onDestroy();
        mSelectedCollection.removeOnCheckStateListener(this);
        mSelectedCollection.onDestroy();
//        mSpec.onCheckedListener = null;
        mSpec.onSelectedListener = null;
        super.onDestroy();
    }

    private void updateBottomToolbar() {

        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonPreview.setEnabled(false);
            mButtonApply.setEnabled(false);
            mButtonApply.setText(getString(R.string.button_sure_default));
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonPreview.setEnabled(true);
            mButtonApply.setText(R.string.button_sure_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonPreview.setEnabled(true);
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getString(R.string.button_sure, selectedCount));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_preview) {
            new LocalImageViewer()
                    .setCountable(mSpec.countable)
                    .setSingleSelectionModeEnabled(mSpec.singleSelectionModeEnabled())
                    .setSelectedItemManager(mSelectedCollection)
                    .setImageUrls(mSelectedCollection.asList())
//                    .setImageList(mSelectedCollection.asList())
                    .show(context);
        } else if (v.getId() == R.id.button_apply) {

            List<Uri> selectedUris =  mSelectedCollection.asListOfUri();
            List<String> selectedPaths = mSelectedCollection.asListOfString(context);
            if (mSpec.isCrop && selectedPaths.size() == 1 && mSelectedCollection.asList().get(0).isImage()) {
                //start crop
                startCrop(_mActivity, selectedUris.get(0));
            } else {
                popToSelect = true;
                pop();
            }
        }
    }

    public static void startCrop(Activity context, Uri source) {
        String destinationFileName = System.nanoTime() + "_crop.jpg";
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(90);
        // Color palette
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.colorPrimary,
                        R.attr.colorPrimaryDark});
        int primaryColor = ta.getColor(0, Color.TRANSPARENT);
        options.setToolbarColor(primaryColor);
        options.setStatusBarColor(ta.getColor(1, Color.TRANSPARENT));
        options.setActiveWidgetColor(primaryColor);
        ta.recycle();
        File cacheFile = new File(context.getCacheDir(), destinationFileName);
        UCrop.of(source, Uri.fromFile(cacheFile))
                .withAspectRatio(1, 1)
                .withOptions(options)
                .start(context);
    }

    @Override
    public void onUpdate() {
        updateBottomToolbar();
    }

}
