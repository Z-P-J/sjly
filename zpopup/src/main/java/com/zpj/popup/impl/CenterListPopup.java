package com.zpj.popup.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.MultiItemTypeAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.zpj.popup.XPopup;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.interfaces.OnSelectListener;
import com.zpj.popup.widget.CheckView;
import com.zpj.popup.R;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: 在中间的列表对话框
 * Create by dance, at 2018/12/16
 */
public class CenterListPopup<T extends CenterListPopup> extends CenterPopup<T>
        implements IEasy.OnBindViewHolderListener<String>,
        IEasy.OnItemClickListener<String> {

    private final List<String> data = new ArrayList<>();
    private String title;
    private int[] iconIds;

    EasyRecyclerView<String> recyclerView;
    TextView tv_title;

    protected int bindItemLayoutId = R.layout._xpopup_adapter_text;

    public CenterListPopup(@NonNull Context context) {
        super(context);
    }

//    /**
//     * 传入自定义的布局，对布局中的id有要求
//     *
//     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
//     * @return
//     */
//    public CenterListPopup bindLayout(int layoutId) {
//        this.bindLayoutId = layoutId;
//        return this;
//    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public CenterListPopup bindItemLayout(int itemLayoutId) {
        this.bindItemLayoutId = itemLayoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_center_impl_list : bindLayoutId;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        tv_title = findViewById(R.id.tv_title);

        if (tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
                findViewById(R.id.xpopup_divider).setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recyclerView));
        recyclerView.setData(data)
                .setItemRes(bindItemLayoutId)
                .onBindViewHolder(this)
                .onItemClick(this)
                .build();

//        final EasyAdapter<String> adapter = new EasyAdapter<String>(Arrays.asList(data), bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId) {
//            @Override
//            protected void bind(@NonNull ViewHolder holder, @NonNull String s, int position) {
//                holder.setText(R.id.tv_text, s);
//                if (iconIds != null && iconIds.length > position) {
//                    holder.getView(R.id.iv_image).setVisibility(VISIBLE);
//                    holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
//                } else {
//                    holder.getView(R.id.iv_image).setVisibility(GONE);
//                }
//
//                // 对勾View
//                if (checkedPosition != -1) {
//                    if (holder.getView(R.id.check_view) != null) {
//                        holder.getView(R.id.check_view).setVisibility(position == checkedPosition ? VISIBLE : GONE);
//                        holder.<CheckView>getView(R.id.check_view).setColor(XPopup.getPrimaryColor());
//                    }
//                    holder.<TextView>getView(R.id.tv_text).setTextColor(position == checkedPosition ?
//                            XPopup.getPrimaryColor() : getResources().getColor(R.color._xpopup_title_color));
//                }
//                if (position == (data.length - 1)) {
//                    holder.getView(R.id.xpopup_divider).setVisibility(INVISIBLE);
//                }
//            }
//        };
//        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
//            @Override
//            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                if (selectListener != null) {
//                    if (position >= 0 && position < adapter.getData().size())
//                        selectListener.onSelect(position, adapter.getData().get(position));
//                }
//                if (checkedPosition != -1) {
//                    checkedPosition = position;
//                    adapter.notifyDataSetChanged();
//                }
//                if (popupInfo.autoDismiss) dismiss();
//            }
//        });
//        recyclerView.setAdapter(adapter);
    }

    public CenterListPopup setStringData(String title, String[] data, int[] iconIds) {
        this.title = title;
        this.data.addAll(Arrays.asList(data));
        this.iconIds = iconIds;
        return this;
    }

    private OnSelectListener selectListener;

    public CenterListPopup setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    int checkedPosition = -1;

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public CenterListPopup setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }

    @Override
    protected int getMaxWidth() {
        return popupInfo.maxWidth == 0 ? (int) (super.getMaxWidth() * .8f)
                : popupInfo.maxWidth;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
        if (bindItemLayoutId == R.layout._xpopup_adapter_text) {
            holder.setText(R.id.tv_text, list.get(position));
            if (iconIds != null && iconIds.length > position) {
                holder.getView(R.id.iv_image).setVisibility(VISIBLE);
                holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
            } else {
                holder.getView(R.id.iv_image).setVisibility(GONE);
            }

            // 对勾View
            if (checkedPosition != -1) {
                if (holder.getView(R.id.check_view) != null) {
                    holder.getView(R.id.check_view).setVisibility(position == checkedPosition ? VISIBLE : GONE);
                    holder.<CheckView>getView(R.id.check_view).setColor(XPopup.getPrimaryColor());
                }
                holder.<TextView>getView(R.id.tv_text).setTextColor(position == checkedPosition ?
                        XPopup.getPrimaryColor() : getResources().getColor(R.color._xpopup_title_color));
            }
            if (position == (data.size() - 1)) {
                holder.getView(R.id.xpopup_divider).setVisibility(INVISIBLE);
            }
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, String str) {
        int position = holder.getRealPosition();
        if (selectListener != null) {
            if (position >= 0 && position < data.size())
                selectListener.onSelect(position, data.get(position));
        }
        if (checkedPosition != -1) {
            checkedPosition = position;
            recyclerView.notifyDataSetChanged();
        }
        if (popupInfo.autoDismiss) dismiss();
    }
}
