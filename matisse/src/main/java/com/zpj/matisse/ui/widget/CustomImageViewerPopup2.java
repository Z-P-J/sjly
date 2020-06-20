package com.zpj.matisse.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.matisse.R;
import com.zpj.matisse.entity.IncapableCause;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.entity.SelectionSpec;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.popup.core.ImageViewerPopup2;
import com.zpj.popup.imagetrans.ImageLoad;
import com.zpj.popup.imagetrans.ImageTransAdapter;
import com.zpj.popup.impl.FullScreenPopup;
import com.zpj.popup.interfaces.IImageLoader;
import com.zpj.widget.toolbar.ZToolBar;

import java.io.File;

public class CustomImageViewerPopup2 extends ImageViewerPopup2<Item>
        implements IImageLoader<Item> {

    protected final SelectedItemManager mSelectedCollection;
    protected final SelectionSpec mSpec;
    private ZToolBar titleBar;
    protected CheckView mCheckView;
    protected TextView mButtonApply;
    protected TextView tvIndicator;

    public static CustomImageViewerPopup2 with(@NonNull Context context) {
        return new CustomImageViewerPopup2(context);
    }

    private CustomImageViewerPopup2(@NonNull Context context) {
        super(context);
        setImageLoad(new ImageLoad<Item>() {
            @Override
            public void loadImage(Item url, LoadCallback callback, ImageView imageView, String uniqueStr) {
                Glide.with(context).asDrawable().load(url.getContentUri()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        callback.loadFinish(resource);
                    }
                });
            }

            @Override
            public boolean isCached(Item url) {
                return false;
            }

            @Override
            public void cancel(Item url, String unique) {

            }
        });
        mSelectedCollection = SelectedItemManager.getInstance();
        mSpec = SelectionSpec.getInstance();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.matisse_custom_image_viewer_popup;
    }

    @Override
    protected void initPopupContent() {

        super.initPopupContent();

    }

    @Override
    protected void onCreate() {
        super.onCreate();



        View customView  = LayoutInflater.from(context).inflate(R.layout.matisse_custom_image_viewer_popup, centerPopupContainer, false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        params.gravity = Gravity.CENTER;
        centerPopupContainer.addView(customView, params);

        customView.setAlpha(0);
        setAdapter(new ImageTransAdapter() {
            @Override
            protected View onCreateView(View parent, ViewPager viewPager, FullScreenPopup dialogInterface) {
                return null;
            }

            @Override
            protected void onTransform(float ratio) {
                super.onTransform(ratio);
                Log.d("CustomImageViewerPopup2", "onTransform ratio=" + ratio);
                customView.setAlpha(ratio);
            }
        });

        titleBar = findViewById(R.id.tool_bar);
        mCheckView = findViewById(R.id.check_view);
        mCheckView.setCountable(mSpec.countable);
        tvIndicator = findViewById(R.id.tv_indicator);
        mButtonApply = findViewById(R.id.button_apply);
        dialogView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                int posi = position;
                titleBar.getCenterTextView().setText(build.imageList.get(posi).getFile(getContext()).getName());
//                Log.d("CustomImageViewerPopup", "posi=" + posi + " position=" + position);
                Item item = build.imageList.get(posi);
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
                tvIndicator.setText(build.imageList.size() + "/" + (posi + 1));
            }
        });








        mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(build.imageList.get(dialogView.getCurrentItem())));
        titleBar.getCenterTextView().setText(build.imageList.get(dialogView.getCurrentItem()).getFile(getContext()).getName());
        titleBar.getLeftImageButton().setOnClickListener(v -> onBackPressed());


        tvIndicator.setText(build.imageList.size() + "/" + (dialogView.getCurrentItem() + 1));

        mCheckView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Item item = build.imageList.get(dialogView.getCurrentItem());
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

    @Override
    protected void onDismiss() {
        super.onDismiss();
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

}
