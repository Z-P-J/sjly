package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.zpj.popup.impl.PartShadowPopup;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerPopup extends PartShadowPopup<RecyclerPopup> {


    public interface OnItemClickListener {
        void onItemClick(View view, String title, int position);
    }

    private OnItemClickListener onItemClickListener;
    private int selectPosition = 0;
    private final List<String> items = new ArrayList<>();

    public static RecyclerPopup with(Context context) {
        return new RecyclerPopup(context);
    }

    private RecyclerPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_recycler_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        EasyRecyclerView<String> recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        recyclerView.setData(items)
                .setItemRes(R.layout.item_text)
                .onBindViewHolder((holder, list, position, payloads) -> {
                    TextView title = holder.getTextView(R.id.tv_title);
                    title.setText(list.get(position));
                    title.setTextColor(getContext().getResources().getColor(position == selectPosition ? R.color.colorPrimary : R.color.text_gray));
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

    public RecyclerPopup setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public RecyclerPopup setSelectedItem(int position) {
        this.selectPosition = position;
        return this;
    }

    public RecyclerPopup addItem(String item) {
        this.items.add(item);
        return this;
    }

    public RecyclerPopup addItems(String...items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public RecyclerPopup show(View view) {
        popupInfo.atView = view;
        show();
        return this;
    }

}
