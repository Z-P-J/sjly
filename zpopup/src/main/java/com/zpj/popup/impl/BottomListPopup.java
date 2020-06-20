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
import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.core.BottomPopup;
import com.zpj.popup.widget.CheckView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 底部的列表对话框
 * Create by dance, at 2018/12/16
 */
public class BottomListPopup<T> extends BottomPopup<BottomListPopup<T>> {

    RecyclerView recyclerView;
    TextView tv_title;
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    private final List<T> list = new ArrayList<>();
    private OnSelectListener<T> selectListener;
    private int checkedPosition = -1;
    private int selectedPosition = -1;

    public BottomListPopup(@NonNull Context context) {
        super(context);
    }

    /**
     * 传入自定义的布局，对布局中的id有要求
     *
     * @param layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @return
     */
    public BottomListPopup bindLayout(int layoutId) {
        this.bindLayoutId = layoutId;
        return this;
    }

    /**
     * 传入自定义的 item布局
     *
     * @param itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     * @return
     */
    public BottomListPopup bindItemLayout(int itemLayoutId) {
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
        recyclerView = findViewById(R.id.recyclerView);
        tv_title = findViewById(R.id.tv_title);

        if (tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
                findViewById(R.id.xpopup_divider).setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        final EasyAdapter<T> adapter = new EasyAdapter<T>(list, bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId) {
            @Override
            protected void bind(@NonNull ViewHolder holder, @NonNull T s, int position) {
                holder.setText(R.id.tv_text, s.toString());
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
                    holder.<TextView>getView(R.id.tv_text).setTextColor(
                            getResources().getColor(position == checkedPosition ?
                                    R.color._xpopup_text_major_color : R.color._xpopup_text_normal_color));
                } else if (selectedPosition != -1) {
                    holder.<TextView>getView(R.id.tv_text).setTextColor(
                            getResources().getColor(position == selectedPosition ?
                                    R.color._xpopup_text_major_color : R.color._xpopup_text_normal_color));
                }
                if (position == (list.size() - 1)) {
                    holder.getView(R.id.xpopup_divider).setVisibility(INVISIBLE);
                }
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (selectListener != null) {
                    selectListener.onSelect(BottomListPopup.this, position, adapter.getData().get(position));
                }
                if (checkedPosition != -1) {
                    checkedPosition = position;
                    adapter.notifyDataSetChanged();
                } else if (selectedPosition != -1) {
                    selectedPosition = position;
                    adapter.notifyDataSetChanged();
                }
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (popupInfo.autoDismiss) dismiss();
//                    }
//                }, 100);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    String title;
    int[] iconIds;

    public BottomListPopup<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public BottomListPopup<T> setData(List<T> list) {
        this.list.addAll(list);
        return this;
    }

    public BottomListPopup<T> setIconIds(int[] iconIds) {
        this.iconIds = iconIds;
        return this;
    }

    public BottomListPopup<T> setOnSelectListener(OnSelectListener<T> selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public BottomListPopup<T> setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }

    public BottomListPopup<T> setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        return this;
    }

    public interface OnSelectListener<T> {
        void onSelect(BottomListPopup<T> popup, int position, T item);
    }


}
