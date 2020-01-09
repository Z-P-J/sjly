package com.zpj.market.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpj.market.R;
import com.zpj.market.bean.UserDownloadedAppInfo;

import java.util.List;

public class UserDownloadedAdapter extends RecyclerView.Adapter<UserDownloadedAdapter.ViewHolder> {

    private final List<UserDownloadedAppInfo> appInfoList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public UserDownloadedAdapter(List<UserDownloadedAppInfo> appInfoList) {
        this.appInfoList = appInfoList;
    }

    public interface OnItemClickListener{
        void onItemClick(ViewHolder holder, int position, UserDownloadedAppInfo item);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_downloaded,null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        UserDownloadedAppInfo appInfo = appInfoList.get(i);
        holder.titleText.setText(appInfo.getTitle());
        holder.packageNameText.setText(appInfo.getPackageName());
        holder.infoText.setText(appInfo.getAppSize() + " | 于" + appInfo.getDownloadTime() + "下载");
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder, i, appInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView packageNameText;
        TextView infoText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_title);
            packageNameText = itemView.findViewById(R.id.text_package_name);
            infoText = itemView.findViewById(R.id.text_info);
        }
    }

}
