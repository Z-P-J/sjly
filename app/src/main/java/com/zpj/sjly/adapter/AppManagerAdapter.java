package com.zpj.sjly.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.sjly.R;
import com.zpj.sjly.bean.InstalledAppInfo;
import com.zpj.sjly.utils.ApkUtil;
import com.zpj.sjly.utils.ExecutorHelper;

import java.util.List;

public class AppManagerAdapter extends RecyclerView.Adapter<AppManagerAdapter.ViewHolder> {

    private Context context;
    private List<InstalledAppInfo> installedAppInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView appIcon;
        TextView appName;
        TextView appInfo;

        ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.icon_app);
            appName = itemView.findViewById(R.id.text_name);
            appInfo = itemView.findViewById(R.id.text_info);
        }
    }

    public AppManagerAdapter(List<InstalledAppInfo> installedAppInfoList){
        this.installedAppInfoList = installedAppInfoList;
    }


    @NonNull
    @Override
    public AppManagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_installed_app, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppManagerAdapter.ViewHolder holder, int position) {
        InstalledAppInfo appInfo = installedAppInfoList.get(position);
        Log.d("onBindViewHolder", "name=" + appInfo.getName());
        Log.d("onBindViewHolder", "size=" + appInfo.getFileLength());
        if (appInfo.getIconDrawable() == null) {
            holder.appIcon.setImageResource(R.mipmap.ic_launcher);
            if (appInfo.isTempInstalled()) {
                ExecutorHelper.submit(() -> {
                    Drawable drawable = ApkUtil.getAppIcon(context, appInfo.getPackageName());
                    appInfo.setIconDrawable(drawable);
                    holder.appIcon.post(() -> holder.appIcon.setImageDrawable(drawable));
                });
            } else {
                ExecutorHelper.submit(() -> {
                    Drawable drawable = ApkUtil.readApkIcon(context, appInfo.getApkFilePath());
                    appInfo.setIconDrawable(drawable);
                    holder.appIcon.post(() -> holder.appIcon.setImageDrawable(drawable));
                });
            }
        } else {
            holder.appIcon.setImageDrawable(appInfo.getIconDrawable());
        }

        holder.appName.setText(appInfo.getName());
        holder.appInfo.setText(appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize());
    }

    @Override
    public int getItemCount() {
        return installedAppInfoList.size();
    }
}
