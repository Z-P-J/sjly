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

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.ucrop.UCrop;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.matisse.R;
import com.zpj.matisse.entity.Album;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.matisse.ui.widget.AlbumListLayout;
import com.zpj.matisse.ui.widget.CheckRadioView;
import com.zpj.matisse.ui.widget.IncapableDialog;
import com.zpj.matisse.ui.widget.MediaSelectionLayout;
import com.zpj.matisse.utils.MediaStoreCompat;
import com.zpj.matisse.utils.PhotoMetadataUtils;

import java.io.File;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class MatisseFragment2 extends BaseFragment implements
        View.OnClickListener,
        SelectedItemManager.OnCheckStateListener {

    public static final String CHECK_STATE = "checkState";
    public static final String TITLE = "选择图片";
    private MediaStoreCompat mMediaStoreCompat;
    private SelectedItemManager mSelectedCollection;
    private SelectionSpec mSpec;

    private TextView mButtonPreview;
    private TextView mButtonApply;

    private LinearLayout mOriginalLayout;
    private CheckRadioView mOriginal;
    private boolean mOriginalEnable;


    private SlidingUpPanelLayout slidingUpPanelLayout;
    private MediaSelectionLayout mediaSelectionLayout;
    private AlbumListLayout albumListLayout;

    private boolean popToSelect;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_matisse2;
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

        if (mSpec.needOrientationRestriction()) {
            _mActivity.setRequestedOrientation(mSpec.orientation);
        }

        if (mSpec.capture) {
            mMediaStoreCompat = new MediaStoreCompat(_mActivity);
            if (mSpec.captureStrategy == null)
                throw new RuntimeException("Don't forget to set CaptureStrategy.");
            mMediaStoreCompat.setCaptureStrategy(mSpec.captureStrategy);
        }

        mButtonPreview = view.findViewById(R.id.button_preview);
        mButtonApply = view.findViewById(R.id.button_apply);
        mButtonPreview.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
        mOriginalLayout = view.findViewById(R.id.originalLayout);
        mOriginal = view.findViewById(R.id.original);
        mOriginalLayout.setOnClickListener(this);

        mSelectedCollection.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }
        updateBottomToolbar();

        slidingUpPanelLayout = this.findViewById(R.id.slidingUpPanelLayout);
        slidingUpPanelLayout.setAnchorPoint(0.5f);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
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
//                postOnEnterAnimationEnd(() -> {
//
//                });

                if (album.isAll() && album.isEmpty()) {
                    mediaSelectionLayout.showEmpty();
                } else {
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
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        albumListLayout.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
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
        mSpec.onCheckedListener = null;
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


        if (mSpec.originalable) {
            mOriginalLayout.setVisibility(View.VISIBLE);
            updateOriginalState();
        } else {
            mOriginalLayout.setVisibility(View.INVISIBLE);
        }


    }

    private void updateOriginalState() {
        mOriginal.setChecked(mOriginalEnable);
        if (countOverMaxSize() > 0) {

            if (mOriginalEnable) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_size, mSpec.originalMaxSize));
                incapableDialog.show(getChildFragmentManager(),
                        IncapableDialog.class.getName());

                mOriginal.setChecked(false);
                mOriginalEnable = false;
            }
        }
    }


    private int countOverMaxSize() {
        int count = 0;
        int selectedCount = mSelectedCollection.count();
        for (int i = 0; i < selectedCount; i++) {
            Item item = mSelectedCollection.asList().get(i);

            if (item.isImage()) {
                float size = PhotoMetadataUtils.getSizeInMB(item.size);
                if (size > mSpec.originalMaxSize) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_preview) {
//            new CustomImageViewerDialogFragment()
//                    .setCountable(mSpec.countable)
//                    .setSingleSelectionModeEnabled(mSpec.singleSelectionModeEnabled())
//                    .setSelectedItemManager(mSelectedCollection)
//                    .setImageUrls(mSelectedCollection.asList())
//                    .show(context);

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
        } else if (v.getId() == R.id.originalLayout) {
            int count = countOverMaxSize();
            if (count > 0) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_count, count, mSpec.originalMaxSize));
                incapableDialog.show(getChildFragmentManager(),
                        IncapableDialog.class.getName());
                return;
            }

            mOriginalEnable = !mOriginalEnable;
            mOriginal.setChecked(mOriginalEnable);

            if (mSpec.onCheckedListener != null) {
                mSpec.onCheckedListener.onCheck(mOriginalEnable);
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
