package com.zpj.popup.impl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.zpj.popup.core.AttachPopup;
import com.zpj.popup.core.BasePopup;
import com.zpj.popup.widget.VerticalRecyclerView;
import com.zpj.popup.R;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.widget.tinted.TintedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: Attach类型的列表弹窗
 * Create by dance, at 2018/12/12
 */
public class AttachListPopup<T> extends AttachPopup<AttachListPopup<T>> {

    protected VerticalRecyclerView recyclerView;
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    protected int tintColor = -1;
    protected int textColor;

//    private View atView;

    private final List<T> items = new ArrayList<>();
    private final List<Integer> iconIds = new ArrayList<>();

//    public AttachListPopup(@NonNull View view) {
//        this(view.getContext());
////        atView = view;
//        popupInfo.atView = view;
//        popupInfo.hasShadowBg = false;
//    }

    public AttachListPopup(@NonNull Context context) {
        super(context);
//        popupInfo.atView = view;
        popupInfo.hasShadowBg = false;
        textColor = context.getResources().getColor(R.color._xpopup_text_major_color);
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public AttachListPopup<T> bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public AttachListPopup<T> bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_attach_impl_list : bindLayoutId;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setupDivider();

        EasyRecyclerView<T> easyRecyclerView = new EasyRecyclerView<>(recyclerView);
        easyRecyclerView.setData(items)
                .setItemRes(bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId)
                .onBindViewHolder((holder, list, position, payloads) -> {
                    TextView tvText = holder.getView(R.id.tv_text);
                    tvText.setText(list.get(position).toString());
                    tvText.setTextColor(textColor);

                    TintedImageView ivImage = holder.getView(R.id.iv_image);
                    if (iconIds.size() > position) {
                        ivImage.setVisibility(VISIBLE);
                        ivImage.setImageResource(iconIds.get(position));
//                        ivImage.setImageDrawable(context.getResources().getDrawable(iconIds.get(position)));
                        if (tintColor != -1) {
                            ivImage.setTint(ColorStateList.valueOf(tintColor));
                        }
                    } else {
                        ivImage.setVisibility(GONE);
                    }
                    holder.getView(R.id.xpopup_divider).setVisibility(GONE);
                })
                .onItemClick((holder, view, data) -> {
                    if (popupInfo.autoDismiss) dismiss();
                    if (selectListener != null) {
                        selectListener.onSelect(holder.getAdapterPosition(), data);
                    }

                })
                .build();
    }

    @Override
    public AttachListPopup<T> show() {
        if (popupInfo.atView == null && popupInfo.touchPoint == null) {
            popupInfo.touchPoint = new PointF(0, 0);
        }
        return super.show();
    }

    public void show(View atView) {
        popupInfo.atView = atView;
        show();
    }

    public void show(float x, float y) {
        popupInfo.touchPoint = new PointF(x, y);
        show();
    }

    public AttachListPopup<T> setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public AttachListPopup<T> setIconTintColor(int tintColor) {
        this.tintColor = tintColor;
        return this;
    }

    public AttachListPopup<T> setItems(List<T> items) {
        this.items.clear();
        this.items.addAll(items);
        return this;
    }

    public AttachListPopup<T> addItems(List<T> items) {
        this.items.addAll(items);
        return this;
    }

    public AttachListPopup<T> addItems(T...items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public AttachListPopup<T> addItemsIf(boolean flag, T...items) {
        if (flag) {
            this.items.addAll(Arrays.asList(items));
        }
        return this;
    }

    public AttachListPopup<T> addItem(T item) {
        this.items.add(item);
        return this;
    }

    public AttachListPopup<T> addItemIf(boolean flag, T item) {
        if (flag) {
            this.items.add(item);
        }
        return this;
    }

    public AttachListPopup<T> setIconIds(List<Integer> iconIds) {
        this.iconIds.clear();
        this.iconIds.addAll(iconIds);
        return this;
    }

    public AttachListPopup<T> addIconId(int iconId) {
        this.iconIds.add(iconId);
        return this;
    }

    public AttachListPopup<T> addIconIds(Integer...ids) {
        this.iconIds.addAll(Arrays.asList(ids));
        return this;
    }

    public AttachListPopup<T> setOffsetXAndY(int offsetX, int offsetY) {
        this.defaultOffsetX += offsetX;
        this.defaultOffsetY += offsetY;
        return this;
    }

    private OnSelectListener<T> selectListener;

    public AttachListPopup<T> setOnSelectListener(OnSelectListener<T> selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public interface OnSelectListener<T> {
        void onSelect(int position, T text);
    }

}
