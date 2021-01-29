package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.PartShadowDialogFragment;
import com.zpj.fragmentation.dialog.enums.PopupPosition;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;
import com.zxy.skin.sdk.SkinEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerPartShadowDialogFragment extends PartShadowDialogFragment {


    public interface OnItemClickListener {
        void onItemClick(View view, String title, int position);
    }

    private OnItemClickListener onItemClickListener;
    private int selectPosition = 0;
    private final List<String> items = new ArrayList<>();

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_part_shadow_recycler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupPosition = PopupPosition.Bottom;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        EasyRecyclerView<String> recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        int primaryColor = getResources().getColor(R.color.colorPrimary);
        recyclerView.setData(items)
                .setItemRes(R.layout.item_text)
                .onBindViewHolder((holder, list, position, payloads) -> {
                    TextView title = holder.getTextView(R.id.tv_title);
                    title.setText(list.get(position));
                    title.setTextColor(position == selectPosition ? primaryColor : SkinEngine.getColor(context, R.attr.textColorNormal));
                    holder.setOnItemClickListener(v -> {
                        if (position == selectPosition) {
                            return;
                        }
                        dismiss();
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(v, list.get(holder.getRealPosition()), holder.getRealPosition());
                        }
                    });
                })
                .build();
    }

    public RecyclerPartShadowDialogFragment setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public RecyclerPartShadowDialogFragment setSelectedItem(int position) {
        this.selectPosition = position;
        return this;
    }

    public RecyclerPartShadowDialogFragment addItem(String item) {
        this.items.add(item);
        return this;
    }

    public RecyclerPartShadowDialogFragment addItems(String...items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public RecyclerPartShadowDialogFragment show(View view) {
        setAttachView(view);
        show(view.getContext());
        return this;
    }

}
