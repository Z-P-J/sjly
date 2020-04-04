package com.zpj.matisse.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.zpj.matisse.R;
import com.zpj.matisse.entity.IncapableCause;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.popup.core.ImageViewerPopup;
import com.zpj.popup.interfaces.XPopupImageLoader;
import com.zpj.widget.toolbar.ZToolBar;

import java.io.File;

public class CustomImageViewerPopup extends ImageViewerPopup<Item>
        implements XPopupImageLoader<Item> {

    protected final SelectedItemManager mSelectedCollection;
    protected final SelectionSpec mSpec;
    private ZToolBar titleBar;
    protected CheckView mCheckView;
    protected TextView mButtonApply;
    protected TextView tvIndicator;

    public static CustomImageViewerPopup with(@NonNull Context context) {
        return new CustomImageViewerPopup(context);
    }

    private CustomImageViewerPopup(@NonNull Context context) {
        super(context);
        isShowIndicator(false);
        isShowPlaceholder(false);
        isShowSaveButton(false);
        setImageLoader(this);
        mSelectedCollection = SelectedItemManager.getInstance();
        mSpec = SelectionSpec.getInstance();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.matisse_custom_image_viewer_popup;
    }

    @Override
    protected void initPopupContent() {
        titleBar = findViewById(R.id.tool_bar);
        mCheckView = findViewById(R.id.check_view);
        mCheckView.setCountable(mSpec.countable);
        tvIndicator = findViewById(R.id.tv_indicator);
        mButtonApply = findViewById(R.id.button_apply);
        super.initPopupContent();
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                int posi = isInfinite ? position % urls.size() : position;
                titleBar.getCenterTextView().setText(urls.get(posi).getFile(getContext()).getName());
                Log.d("CustomImageViewerPopup", "posi=" + posi + " position=" + position);
                Item item = urls.get(posi);
                if (mSpec.countable) {
                    int checkedNum = mSelectedCollection.checkedNumOf(item);
                    mCheckView.setCheckedNum(checkedNum);
                    if (checkedNum > 0) {
                        mCheckView.setEnabled(true);
                    } else {
                        mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                    }
                } else {
                    boolean checked = mSelectedCollection.isSelected(item);
                    mCheckView.setChecked(checked);
                    if (checked) {
                        mCheckView.setEnabled(true);
                    } else {
                        mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                    }
                }
                tvIndicator.setText(urls.size() + "/" + (posi + 1));
            }
        });
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(urls.get(pager.getCurrentItem())));
        titleBar.getCenterTextView().setText(urls.get(pager.getCurrentItem()).getFile(getContext()).getName());


        tvIndicator.setText(urls.size() + "/" + (pager.getCurrentItem() + 1));

        mCheckView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Item item = urls.get(pager.getCurrentItem());
                if (mSelectedCollection.isSelected(item)) {
                    mSelectedCollection.remove(item);
                    if (mSpec.countable) {
                        mCheckView.setCheckedNum(CheckView.UNCHECKED);
                    } else {
                        mCheckView.setChecked(false);
                    }
                } else {
                    if (assertAddSelection(item)) {
                        mSelectedCollection.add(item);
                        if (mSpec.countable) {
                            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
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

    private void updateApplyButton() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonApply.setText(R.string.button_sure_default);
            mButtonApply.setEnabled(false);
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonApply.setText(R.string.button_sure_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getContext().getString(R.string.button_sure, selectedCount));
        }
    }

    @Override
    public void loadImage(int position, @NonNull Item item, @NonNull ImageView imageView) {
        Glide.with(imageView).load(item.uri)
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL))
                .into(imageView);
    }

    @Override
    public File getImageFile(@NonNull Context context, @NonNull Item item) {
        try {
            return Glide.with(context).downloadOnly().load(item.uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean assertAddSelection(Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(getContext(), item);
        IncapableCause.handleCause(getContext(), cause);
        return cause == null;
    }

    @Override
    protected void onShow() {
        super.onShow();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }
}
