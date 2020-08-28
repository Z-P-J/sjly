package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.popup.impl.PartShadowPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class WallpaperTagPopup extends PartShadowPopup<WallpaperTagPopup> {

    private final List<WallpaperTag> tags = new ArrayList<>();
    private FlowLayout.OnItemClickListener onItemClickListener;

    private int selectedPosition = -1;

    public static WallpaperTagPopup with(Context context) {
        return new WallpaperTagPopup(context);
    }

    private WallpaperTagPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_wallpaper_tags;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
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

    public WallpaperTagPopup setSelectedPosition(int position) {
        this.selectedPosition = position;
        return this;
    }

    public WallpaperTagPopup setLabels(List<WallpaperTag> labels) {
        tags.addAll(labels);
        return this;
    }

    public WallpaperTagPopup setOnItemClickListener(FlowLayout.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public WallpaperTagPopup show(View view) {
        popupInfo.atView = view;
        show();
        return this;
    }

}
