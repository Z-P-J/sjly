package com.zpj.matisse.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.imagetrans.ImageLoad;
import com.zpj.fragmentation.dialog.imagetrans.MyImageLoad2;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment3;
import com.zpj.fragmentation.dialog.widget.ImageViewContainer;
import com.zpj.matisse.R;
import com.zpj.matisse.entity.IncapableCause;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.listener.OnSelectedListener;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.matisse.ui.widget.CheckView;
import com.zpj.widget.toolbar.ZToolBar;

import java.util.ArrayList;
import java.util.List;

public class LocalImageViewer extends ImageViewerDialogFragment3<Item> {

    protected SelectedItemManager mSelectedCollection;
    private ZToolBar titleBar;
    protected CheckView mCheckView;
    protected TextView mButtonApply;
    protected TextView tvIndicator;
    protected boolean countable = true;
    protected boolean singleSelectionModeEnabled;
    protected List<Item> selectedList;
    protected OnSelectedListener onSelectListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new MyImageLoad2<Item>() {
            @Override
            public void loadImage(Item url, ImageLoad.LoadCallback callback, ImageViewContainer imageView, String unique) {
                addLoadCallback(unique, callback);
                loadImageFromLocal(url.getPath(context), unique, imageView);
            }

            @Override
            public boolean isCached(Item url) {
                return true;
            }

            @Override
            public void cancel(Item url, String unique) {
                removeLoadCallback(unique);
            }
        };
    }

    @Override
    protected int getCustomLayoutId() {
        return R.layout.matisse_custom_image_viewer_popup;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        lightStatusBar();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        if (mSelectedCollection == null) {
            selectedList = new ArrayList<>(urls);
        }
        titleBar = findViewById(R.id.tool_bar);
        mCheckView = findViewById(R.id.check_view);
        mCheckView.setCountable(countable);
        tvIndicator = findViewById(R.id.tv_indicator);
        mButtonApply = findViewById(R.id.button_apply);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
//                int posi = isInfinite ? position % urls.size() : position;
                int posi = position;
                titleBar.getCenterTextView().setText(urls.get(posi).getFile(getContext()).getName());
                Log.d("CustomImageViewerPopup", "posi=" + posi + " position=" + position);
                Item item = urls.get(posi);
                if (countable) {
                    int checkedNum = checkedNumOf(item);
                    mCheckView.setCheckedNum(checkedNum);
                    if (checkedNum > 0) {
                        mCheckView.setEnabled(true);
                    } else {
                        mCheckView.setEnabled(!maxSelectableReached());
                    }
                } else {
                    boolean checked = isSelected(item);
                    mCheckView.setChecked(checked);
                    if (checked) {
                        mCheckView.setEnabled(true);
                    } else {
                        mCheckView.setEnabled(!maxSelectableReached());
                    }
                }
                tvIndicator.setText(urls.size() + "/" + (posi + 1));
            }
        });

        mCheckView.setCheckedNum(checkedNumOf(urls.get(position)));
        titleBar.getCenterTextView().setText(urls.get(position).getFile(getContext()).getName());


        tvIndicator.setText(urls.size() + "/" + (position + 1));

        mCheckView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Item item = urls.get(position);
                if (isSelected(item)) {
                    removeItem(item);
                    if (countable) {
                        mCheckView.setCheckedNum(CheckView.UNCHECKED);
                    } else {
                        mCheckView.setChecked(false);
                    }
                } else {
                    if (assertAddSelection(item)) {
                        addItem(item);
                        if (countable) {
                            mCheckView.setCheckedNum(checkedNumOf(item));
                        } else {
                            mCheckView.setChecked(true);
                        }
                    }
                }
                updateApplyButton();
            }
        });

        updateApplyButton();

    }

    public LocalImageViewer setSelectedItemManager(SelectedItemManager mSelectedCollection) {
        this.mSelectedCollection = mSelectedCollection;
        return this;
    }

    public LocalImageViewer setCountable(boolean countable) {
        this.countable = countable;
        return this;
    }

    public LocalImageViewer setSingleSelectionModeEnabled(boolean singleSelectionModeEnabled) {
        this.singleSelectionModeEnabled = singleSelectionModeEnabled;
        return this;
    }

    public LocalImageViewer setOnSelectedListener(OnSelectedListener onSelectListener) {
        this.onSelectListener = onSelectListener;
        return this;
    }

    private void updateApplyButton() {
        int selectedCount = selectedCount();
        if (selectedCount == 0) {
            mButtonApply.setText(R.string.button_sure_default);
            mButtonApply.setEnabled(false);
        } else if (selectedCount == 1 && singleSelectionModeEnabled) {
            mButtonApply.setText(R.string.button_sure_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getContext().getString(R.string.button_sure, selectedCount));
        }
    }

//    @Override
//    public void loadImage(int position, @NonNull Item item, @NonNull ImageView imageView) {
//        Glide.with(imageView).load(item.uri)
////                .apply(new RequestOptions()
////                        .override(Target.SIZE_ORIGINAL))
//                .into(imageView);
//    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        if (selectedList != null && onSelectListener != null) {
            onSelectListener.onSelected(selectedList);
        }
    }

    private boolean assertAddSelection(Item item) {
        if (mSelectedCollection == null) {
            return true;
        }
        IncapableCause cause = mSelectedCollection.isAcceptable(getContext(), item);
        IncapableCause.handleCause(getContext(), cause);
        return cause == null;
    }

    private int checkedNumOf(Item item) {
        if (mSelectedCollection == null) {
//            int index = urls.indexOf(item);
            int index = selectedList.indexOf(item);
            return index == -1 ? CheckView.UNCHECKED : index + 1;
        } else {
            return mSelectedCollection.checkedNumOf(item);
        }
    }

    private boolean maxSelectableReached() {
//        if (mSelectedCollection == null) {
//            return false;
//        } else {
//            return mSelectedCollection.maxSelectableReached();
//        }
        return mSelectedCollection != null && mSelectedCollection.maxSelectableReached();
    }

    private boolean isSelected(Item item) {
        if (mSelectedCollection == null) {
            return selectedList.contains(item);
//            return urls.contains(item);
        } else {
            return mSelectedCollection.isSelected(item);
        }
    }

    private void removeItem(Item item) {
        if (mSelectedCollection == null) {
//            urls.remove(item);
            selectedList.remove(item);
        } else {
            mSelectedCollection.remove(item);
        }
    }

    private void addItem(Item item) {
        if (mSelectedCollection == null) {
//            urls.add(item);
            selectedList.add(item);
        } else {
            mSelectedCollection.add(item);
        }
    }

    private int selectedCount() {
        if (mSelectedCollection == null) {
//            return urls.size();
            return selectedList.size();
        } else {
            return mSelectedCollection.count();
        }
    }

}
