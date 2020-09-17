package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.dialog.base.PartShadowDialogFragment;
import com.zpj.fragmentation.dialog.enums.PopupPosition;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class WallpaperTagDialogFragment extends PartShadowDialogFragment {

    private final List<WallpaperTag> tags = new ArrayList<>();
    private FlowLayout.OnItemClickListener onItemClickListener;

    private int selectedPosition = -1;

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_wallpaper_tags;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupPosition = PopupPosition.Bottom;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        FlowLayout flowLayout = findViewById(R.id.flow_layout);
        flowLayout.setOnItemClickListener((index, v, text) -> {
            dismiss();
            if (onItemClickListener != null) {
                onItemClickListener.onClick(index, v, text);
            }
        });
        flowLayout.setSpace(ScreenUtils.dp2pxInt(context, 8));
        flowLayout.setSelectedPosition(selectedPosition);
        for (WallpaperTag tag : tags) {
            flowLayout.addItem(tag.getName());
        }
    }

    public WallpaperTagDialogFragment setSelectedPosition(int position) {
        this.selectedPosition = position;
        return this;
    }

    public WallpaperTagDialogFragment setLabels(List<WallpaperTag> labels) {
        tags.addAll(labels);
        return this;
    }

    public WallpaperTagDialogFragment setOnItemClickListener(FlowLayout.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public WallpaperTagDialogFragment show(View view) {
        this.attachView = view;
        show(view.getContext());
        return this;
    }

}
