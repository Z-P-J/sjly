package com.zpj.shouji.market.imagepicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.imagepicker.entity.Album;
import com.zpj.shouji.market.imagepicker.entity.Item;
import com.zpj.shouji.market.imagepicker.widget.AlbumListLayout;
import com.zpj.shouji.market.imagepicker.widget.ImageSelectionLayout;
import com.zpj.shouji.market.ui.fragment.CropImageFragment;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.utils.ScreenUtils;
import com.zxy.skin.sdk.SkinEngine;

import java.util.List;

public class ImagePickerFragment extends SkinFragment implements
        View.OnClickListener,
        SelectionManager.OnCheckStateListener {

    public static final String TITLE = "选择图片";
    private SelectionManager mSpec;

    private TextView mButtonPreview;
    private TextView mButtonApply;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ImageSelectionLayout imageSelectionLayout;
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
        mSpec = SelectionManager.getInstance();
        mSpec.addOnCheckStateListener(this);

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
        slidingUpPanelLayout.setAnchorPoint(0.68f);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset <= 0.68) {
                    bottomBar.setAlpha(1 - slideOffset / 0.68f);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    setToolbarTitle("选择相册");
//                    bottomBar.setVisibility(View.INVISIBLE);
                    bottomBar.setAlpha(0f);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    setToolbarTitle("选择相册");
//                    bottomBar.setVisibility(View.INVISIBLE);
                    bottomBar.setAlpha(0f);
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


        imageSelectionLayout = findViewById(R.id.layout_media_selection);
        albumListLayout = findViewById(R.id.gallery_recycler_view);
        albumListLayout.init(_mActivity, savedInstanceState);
        albumListLayout.setOnAlbumSelectListener(new AlbumListLayout.OnAlbumSelectListener() {
            @Override
            public void onSelect(Album album) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                if (album.isAll() && album.isEmpty()) {
                    imageSelectionLayout.showEmpty();
                } else {
                    Log.d("MediaSelectionLayout", "onSelect album=" + album);
                    imageSelectionLayout.loadAlbum(_mActivity, album);
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
        mSpec.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {

        if (popToSelect) {
            if (mSpec.onSelectedListener != null) {
                if (mSpec.selectedList != null) {
                    mSpec.selectedList.clear();
                    mSpec.selectedList.addAll(mSpec.asList());
                    mSpec.onSelectedListener.onSelected(mSpec.selectedList);
                } else {
                    mSpec.onSelectedListener.onSelected(mSpec.asList());
                }
            }
        }
        popToSelect = false;

        albumListLayout.onDestroy();
        imageSelectionLayout.onDestroy();
        mSpec.removeOnCheckStateListener(this);
        mSpec.onDestroy();
        super.onDestroy();
    }

    private void updateBottomToolbar() {
        int selectedCount = mSpec.count();
        if (selectedCount == 0) {
            mButtonPreview.setEnabled(false);
            mButtonApply.setEnabled(false);
            mButtonApply.setText(getString(R.string.button_sure_default));

            int color = SkinEngine.getColor(context, R.attr.textColorMinor);
            mButtonApply.setTextColor(color);
            mButtonPreview.setTextColor(color);
            return;
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonPreview.setEnabled(true);
            mButtonApply.setEnabled(true);
            mButtonApply.setText(R.string.button_sure_default);
        } else {
            mButtonPreview.setEnabled(true);
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getString(R.string.button_sure, selectedCount));
        }
        mButtonPreview.setTextColor(SkinEngine.getColor(context, R.attr.textColorMajor));
        mButtonApply.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_preview) {
            new LocalImageViewer()
                    .setCountable(mSpec.countable)
                    .setSingleSelectionModeEnabled(mSpec.singleSelectionModeEnabled())
                    .setSelectedItemManager(mSpec)
                    .setImageUrls(mSpec.asList())
                    .show(context);
        } else if (v.getId() == R.id.button_apply) {
            List<Item> items = mSpec.asList();
            if (mSpec.isCrop && items.size() == 1) {
                startWithPop(CropImageFragment.newInstance(items.get(0), mSpec.isCropAvatar));
            } else {
                popToSelect = true;
                pop();
            }
        }
    }

    @Override
    public void onUpdate() {
        updateBottomToolbar();
    }

}
