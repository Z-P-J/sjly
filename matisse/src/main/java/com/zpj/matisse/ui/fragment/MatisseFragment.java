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

import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.matisse.R;
import com.zpj.matisse.event.UpdateTitleEvent;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.matisse.ui.widget.CheckRadioView;
import com.zpj.matisse.ui.widget.CustomImageViewerPopup;
import com.zpj.matisse.ui.widget.CustomImageViewerPopup2;
import com.zpj.matisse.ui.widget.IncapableDialog;
import com.zpj.matisse.utils.MediaStoreCompat;
import com.zpj.matisse.utils.PhotoMetadataUtils;
import com.yalantis.ucrop.UCrop;
import com.zpj.fragmentation.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class MatisseFragment extends BaseFragment implements
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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_matisse;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
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


        AlbumFragment albumFragment = findChildFragment(AlbumFragment.class);
        if (albumFragment == null) {
            albumFragment = new AlbumFragment();
            loadRootFragment(R.id.container, albumFragment);
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            popChild();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mSelectedCollection.removeOnCheckStateListener(this);
        mSpec.onCheckedListener = null;
        mSpec.onSelectedListener = null;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    //    ArrayList<Uri> selectedUris = new ArrayList<>();
//    ArrayList<String> selectedPaths = new ArrayList<>();

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK)
//            return;
//
//        if (requestCode == MatisseConst.REQUEST_CODE_PREVIEW) {
//            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
//            ArrayList<Item> selected = resultBundle.getParcelableArrayList(SelectedItemManager.STATE_SELECTION);
//            mOriginalEnable = data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
//            int collectionType = resultBundle.getInt(SelectedItemManager.STATE_COLLECTION_TYPE,
//                    SelectedItemManager.COLLECTION_UNDEFINED);
//            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
//                Intent result = new Intent();
//                selectedUris.clear();
//                selectedPaths.clear();
//                if (selected != null) {
//                    for (Item item : selected) {
//                        selectedUris.add(item.getContentUri());
//                        selectedPaths.add(PathUtils.getPath(context, item.getContentUri()));
//                    }
//                }
//                result.putParcelableArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION, selectedUris);
//                result.putStringArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION_PATH, selectedPaths);
//                result.putExtra(MatisseConst.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
//                if (mSpec.isCrop && selected != null && selected.size() == 1 && selected.get(0).isImage()) {
//                    //开启裁剪
//                    startCrop(_mActivity, selected.get(0).uri);
//                    return;
//                }
////                setFragmentResult(RESULT_OK, result);
//                pop();
//            } else {
//                mSelectedCollection.overwrite(selected, collectionType);
//                Fragment mediaSelectionFragment = getChildFragmentManager().findFragmentByTag(
//                        MediaSelectionFragment.class.getSimpleName());
//                if (mediaSelectionFragment instanceof MediaSelectionFragment) {
//                    ((MediaSelectionFragment) mediaSelectionFragment).refreshMediaGrid();
//                }
//                updateBottomToolbar();
//            }
//        } else if (requestCode == MatisseConst.REQUEST_CODE_CAPTURE) {
//            // Just pass the data back to previous calling Activity.
////            setResult(RESULT_OK, data);
////            finish();
//            pop();
//        } else if (requestCode == UCrop.REQUEST_CROP) {
////            final Uri resultUri = UCrop.getOutput(data);
////            if (resultUri != null) {
////                //finish with result.
////                Intent result = getIntent();
////                result.putExtra(MatisseConst.EXTRA_RESULT_CROP_PATH, resultUri.getPath());
////                ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
////                result.putParcelableArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION, selectedUris);
////                ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
////                result.putStringArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION_PATH, selectedPaths);
////                setResult(RESULT_OK, result);
////                finish();
////            } else {
////                Log.e("Matisse", "ucrop occur error: " + UCrop.getError(data).toString());
////            }
//        } else if (resultCode == UCrop.RESULT_ERROR) {
//            Log.e("Matisse", "ucrop occur error: " + UCrop.getError(data).toString());
//        }
//    }

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
//            CustomImageViewerPopup.with(context)
//                    .setImageUrls(mSelectedCollection.asList())
//                    .show();

            CustomImageViewerPopup2.with(context)
                    .setImageList(mSelectedCollection.asList())
                    .show();
        } else if (v.getId() == R.id.button_apply) {

            List<Uri> selectedUris =  mSelectedCollection.asListOfUri();
            List<String> selectedPaths = mSelectedCollection.asListOfString(context);
            if (mSpec.isCrop && selectedPaths.size() == 1 && mSelectedCollection.asList().get(0).isImage()) {
                //start crop
                startCrop(_mActivity, selectedUris.get(0));
            } else {
                if (mSpec.onSelectedListener != null) {
                    mSpec.onSelectedListener.onSelected(mSelectedCollection.asList());
                }
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

    @Subscribe
    public void onUpdateTitleEvent(UpdateTitleEvent event) {
        setToolbarTitle(event.getTitle());
        setSwipeBackEnable(TITLE.equals(event.getTitle()));
    }

//    @SuppressLint("WrongConstant")
//    @Override
//    public void capture() {
////        if (mMediaStoreCompat != null) {
////            mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
////        }
//
//
//        String[] permissions = mSpec.captureMode == CaptureMode.Image ? new String[]{PermissionConstants.CAMERA}
//                : new String[]{PermissionConstants.CAMERA, PermissionConstants.MICROPHONE};
//
//        XPermission.create(context, permissions).callback(new XPermission.SimpleCallback() {
//            @Override
//            public void onGranted() {
//                Intent intent = new Intent(context, CameraActivity.class);
//                startActivityForResult(intent, MatisseConst.REQUEST_CODE_CAPTURE);
//            }
//
//            @Override
//            public void onDenied() {
//                AToast.warning("没有权限，无法使用该功能");
//            }
//        }).request();
//    }
}
