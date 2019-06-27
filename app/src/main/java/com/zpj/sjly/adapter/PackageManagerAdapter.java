//package com.zpj.sjly.adapter;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.zpj.sjly.R;
//import com.zpj.sjly.bean.InstalledAppInfo;
//import com.zpj.sjly.utils.AppUtil;
//import com.zpj.sjly.utils.AppUpdateHelper;
//import com.zpj.sjly.utils.ExecutorHelper;
//
//import java.util.List;
//
//public class PackageManagerAdapter extends RecyclerView.Adapter<PackageManagerAdapter.ViewHolder> {
//
//    private Context context;
//    private List<InstalledAppInfo> installedAppInfoList;
//
//    private IconLoadListener listener;
//
//    static class ViewHolder extends RecyclerView.ViewHolder{
//
//        ImageView appIcon;
//        TextView appName;
//        TextView appInfo;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            appIcon = itemView.findViewById(R.id.icon_app);
//            appName = itemView.findViewById(R.id.text_name);
//            appInfo = itemView.findViewById(R.id.text_info);
//        }
//    }
//
//    public PackageManagerAdapter(List<InstalledAppInfo> installedAppInfoList){
//        this.installedAppInfoList = installedAppInfoList;
//    }
//
//
//    @NonNull
//    @Override
//    public PackageManagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_installed_app, parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final PackageManagerAdapter.ViewHolder holder, int position) {
//        InstalledAppInfo appInfo = installedAppInfoList.get(position);
//        Log.d("onBindViewHolder", "name=" + appInfo.getName());
//        Log.d("onBindViewHolder", "size=" + appInfo.getFileLength());
//        if (appInfo.getIconDrawable() == null) {
//            holder.appIcon.setImageResource(R.mipmap.ic_launcher);
//            ExecutorHelper.submit(() -> {
//                final Drawable drawable;
//                if (appInfo.isTempInstalled()) {
//                    drawable = AppUtil.getAppIcon(context, appInfo.getPackageName());
//                } else if (appInfo.isTempXPK()){
//                    drawable = AppUtil.readApkIcon(context, appInfo.getApkFilePath());
//                } else {
//                    return;
//                }
//                appInfo.setIconDrawable(drawable);
//                if (listener != null && !listener.canLoadIcon(holder.getAdapterPosition())) {
//                    return;
//                }
//                holder.appIcon.post(() -> holder.appIcon.setImageDrawable(drawable));
//            });
//        } else {
//            holder.appIcon.setImageDrawable(appInfo.getIconDrawable());
//        }
//
//        holder.appName.setText(appInfo.getName());
//        String idStr = AppUpdateHelper.getInstance().getAppIdAndType(appInfo.getPackageName());
//        String info;
//        if (idStr == null) {
//            info = "未收录";
//        } else {
//            info = "已收录";
//        }
//        holder.appInfo.setText(appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize() + " | " + info);
//    }
//
//    @Override
//    public int getItemCount() {
//        return installedAppInfoList.size();
//    }
//
//    public void setIconLoadListener(IconLoadListener listener) {
//        this.listener = listener;
//    }
//
//    public interface IconLoadListener {
//        boolean canLoadIcon(int position);
//    }
//
//}
