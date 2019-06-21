package com.zpj.sjly.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.zpj.sjly.R;
import com.zpj.sjly.fragment.AppItem;
import com.zpj.sjly.fragment.CoolApkItem;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private List<AppItem> appItemList;
    private Context context;

    private RequestManager requestManager;

    private OnItemClickListener onItemClickListener;


    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView appIcon;
        TextView appTitle;
        TextView appInfo;
        TextView appDesc;

        public ViewHolder(View view){
            super(view);
            itemView = view;
            appIcon = view.findViewById(R.id.item_icon);
            appTitle = view.findViewById(R.id.item_title);
            appInfo = view.findViewById(R.id.item_info);
            appDesc = view.findViewById(R.id.item_desc);

        }
    }

    public AppAdapter(List<AppItem> appItemList){
        this.appItemList = appItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        context = parent.getContext();
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item,parent,false);

        final ViewHolder holder = new ViewHolder(view);

        requestManager = Glide.with(parent.getContext());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AppAdapter.ViewHolder holder, final int position) {
        AppItem appItem = appItemList.get(position);
        holder.appTitle.setText(appItem.getAppTitle());
        holder.appInfo.setText(appItem.getAppSize() + " | " + appItem.getAppInfo());
        holder.appDesc.setText(appItem.getAppComment());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });

        String icon = appItemList.get(position).getAppIcon();
        requestManager.load(icon).into(holder.appIcon);
    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


}
