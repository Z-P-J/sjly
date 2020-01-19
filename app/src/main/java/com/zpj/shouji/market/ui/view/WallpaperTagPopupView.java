package com.zpj.shouji.market.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.PartShadowPopupView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.WallpaperTag;

import java.util.ArrayList;
import java.util.List;

public class WallpaperTagPopupView extends PartShadowPopupView implements OnLabelClickListener {

    private final Context context;
    private List<String> labels;
    private String selectLabel;
    private OnLabelClickListener onLabelClickListener;

    public static WallpaperTagPopupView with(Context context) {
        return new WallpaperTagPopupView(context);
    }

    private WallpaperTagPopupView(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_wallpaper_tags;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        StackLabel stackLabel = findViewById(R.id.stack_label);
        if (labels == null || selectLabel == null) {
            dismiss();
            return;
        }
        stackLabel.setLabels(labels);
        List<String> selectLabels = new ArrayList<>();
        selectLabels.add(selectLabel);
        stackLabel.setSelectMode(true, selectLabels);
        stackLabel.setOnLabelClickListener(this);
    }

    public WallpaperTagPopupView setSelectLabel(String whichIsSelected) {
        this.selectLabel = whichIsSelected;
        return this;
    }

//    public WallpaperTagPopupView setLabels(List<String> labels) {
//        this.labels = labels;
//        return this;
//    }

    public WallpaperTagPopupView setLabels(List<WallpaperTag> labels) {
        this.labels = new ArrayList<>();
        for (WallpaperTag tag : labels) {
            this.labels.add(tag.getName());
        }
        return this;
    }

    public WallpaperTagPopupView setOnLabelClickListener(OnLabelClickListener onLabelClickListener) {
        this.onLabelClickListener = onLabelClickListener;
        return this;
    }

    public WallpaperTagPopupView show(View view) {
        new XPopup.Builder(context)
                .atView(view)
                .asCustom(this)
                .show();
        return this;
    }

    @Override
    public void onClick(int index, View v, String s) {
        dismiss();
        if (onLabelClickListener != null) {
//            if (labels.get(index).equals(selectLabel)) {
//                return;
//            }
            onLabelClickListener.onClick(index, v, s);
        }
    }
}
