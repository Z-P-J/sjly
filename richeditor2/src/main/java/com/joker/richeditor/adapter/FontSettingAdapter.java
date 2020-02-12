package com.joker.richeditor.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joker.richeditor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Font Setting Adapter
 * Created by even.wu on 9/8/17.
 */

public class FontSettingAdapter extends RecyclerView.Adapter<FontSettingAdapter.FontSettingHolder> {
    @NonNull
    private List<String> list;
    private OnItemClickListener itemClickListener;

    public FontSettingAdapter(@Nullable List<String> data) {
        if (data != null) {
            list = data;
        } else {
            list = new ArrayList<>();
        }
    }

    @Override
    public FontSettingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_font_setting, null);
        return new FontSettingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FontSettingHolder holder, int position) {
        holder.tvContent.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(FontSettingAdapter adapter, View view, int position);
    }

    class FontSettingHolder extends RecyclerView.ViewHolder {
        TextView tvContent;

        private FontSettingHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(FontSettingAdapter.this, v, getAdapterPosition());
                    }
                }
            });
        }
    }
}
