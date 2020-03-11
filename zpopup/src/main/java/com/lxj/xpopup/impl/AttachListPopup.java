package com.lxj.xpopup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.MultiItemTypeAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.widget.VerticalRecyclerView;
import com.zpj.popupmenuview.R;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: Attach类型的列表弹窗
 * Create by dance, at 2018/12/12
 */
public class AttachListPopup extends AttachPopupView {

    protected VerticalRecyclerView recyclerView;
    protected int bindLayoutId;
    protected int bindItemLayoutId;

    private final List<String> items = new ArrayList<>();
    private final List<Integer> iconIds = new ArrayList<>();

    public AttachListPopup(@NonNull Context context) {
        super(context);
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public AttachListPopup bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public AttachListPopup bindItemLayout(int itemLayoutId) {
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

        EasyRecyclerView<String> easyRecyclerView = new EasyRecyclerView<>(recyclerView);
        easyRecyclerView.setData(items)
                .setItemRes(bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId)
                .onBindViewHolder((holder, list, position, payloads) -> {
                    holder.setText(R.id.tv_text, list.get(position));
                    if (iconIds.size() > position) {
                        holder.getView(R.id.iv_image).setVisibility(VISIBLE);
                        holder.getView(R.id.iv_image).setBackgroundResource(iconIds.get(position));
                    } else {
                        holder.getView(R.id.iv_image).setVisibility(GONE);
                    }
                    holder.getView(R.id.xpopup_divider).setVisibility(GONE);
                })
                .onItemClick((holder, view, data) -> {
                    if (selectListener != null) {
                        selectListener.onSelect(holder.getAdapterPosition(), data);
                    }
                    if (popupInfo.autoDismiss) dismiss();
                })
                .build();

//        final EasyAdapter<String> adapter = new EasyAdapter<String>(items, bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId) {
//            @Override
//            protected void bind(@NonNull ViewHolder holder, @NonNull String s, int position) {
//                holder.setText(R.id.tv_text, s);
//                if (iconIds.size() > position) {
//                    holder.getView(R.id.iv_image).setVisibility(VISIBLE);
//                    holder.getView(R.id.iv_image).setBackgroundResource(iconIds.get(position));
//                } else {
//                    holder.getView(R.id.iv_image).setVisibility(GONE);
//                }
//                holder.getView(R.id.xpopup_divider).setVisibility(GONE);
//            }
//        };
//        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
//            @Override
//            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                if (selectListener != null) {
//                    selectListener.onSelect(position, adapter.getData().get(position));
//                }
//                if (popupInfo.autoDismiss) dismiss();
//            }
//        });
//        recyclerView.setAdapter(adapter);
    }

    public AttachListPopup setItems(List<String> items) {
        this.items.clear();
        this.items.addAll(items);
        return this;
    }

    public AttachListPopup addItems(List<String> items) {
        this.items.addAll(items);
        return this;
    }

    public AttachListPopup addItems(String...items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public AttachListPopup addItem(String item) {
        this.items.add(item);
        return this;
    }

    public AttachListPopup setIconIds(List<Integer> iconIds) {
        this.iconIds.clear();
        this.iconIds.addAll(iconIds);
        return this;
    }

    public AttachListPopup addIconId(int iconId) {
        this.iconIds.add(iconId);
        return this;
    }

    public AttachListPopup setOffsetXAndY(int offsetX, int offsetY) {
        this.defaultOffsetX += offsetX;
        this.defaultOffsetY += offsetY;
        return this;
    }

    private OnSelectListener selectListener;

    public AttachListPopup setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }
}
