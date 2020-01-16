package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.InstalledAppInfo;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.AppUpdateHelper;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.refactor.library.SmoothCheckBox;

public class AppManagerAdapter extends RecyclerView.Adapter<AppManagerAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ViewHolder holder, int position, InstalledAppInfo updateInfo);
        void onMenuClicked(View view, InstalledAppInfo updateInfo);
        void onCheckBoxClicked(int allCount, int selectCount);
        void onEnterSelectMode();
        void onExitSelectMode();
    }

    private Context context;
    private List<InstalledAppInfo> installedAppInfoList;
    private OnItemClickListener onItemClickListener;

    private boolean isSelectMode = false;

    private Set<Integer> selectedSet = new HashSet<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView appIcon;
        TextView appName;
        TextView appInfo;
        ImageView moreBtn;
        FrameLayout rightLayout;
        SmoothCheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.icon_app);
            appName = itemView.findViewById(R.id.text_name);
            appInfo = itemView.findViewById(R.id.text_info);
            moreBtn = itemView.findViewById(R.id.btn_more);
            rightLayout = itemView.findViewById(R.id.layout_right);
            checkBox = itemView.findViewById(R.id.checkbox);
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


        GlideApp.with(context).load(appInfo).into(holder.appIcon);

        holder.appName.setText(appInfo.getName());
        String idStr = AppUpdateHelper.getInstance().getAppIdAndType(appInfo.getPackageName());
        String info;
        if (idStr == null) {
            info = "未收录";
        } else {
            info = "已收录";
        }
        holder.appInfo.setText(appInfo.getVersionName() + " | " + appInfo.getFormattedAppSize() + " | " + info);

        holder.itemView.setOnClickListener(v -> {
            if (isSelectMode) {
                boolean isChecked = holder.checkBox.isChecked();
                holder.checkBox.setChecked(!isChecked, true);
            } else {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder, holder.getAdapterPosition(), appInfo);
                }
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (isSelectMode) {
                holder.itemView.performClick();
            } else {
                selectedSet.add(holder.getAdapterPosition());
                enterSelectMode();
            }
            return true;
        });

        if (isSelectMode) {
            holder.rightLayout.setClickable(false);
            boolean select = selectedSet.contains(position);
            holder.moreBtn.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(select, select);
            holder.checkBox.setClickable(false);
//            holder.checkBoxWrapper.setOnClickListener(v -> {
//                boolean isChecked = holder.checkBox.isChecked();
//                holder.checkBox.setChecked(!isChecked, !isChecked);
//            });
            holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    if (isChecked) {
                        selectedSet.add(holder.getAdapterPosition());
                    } else {
                        selectedSet.remove(holder.getAdapterPosition());
                    }
                    if (onItemClickListener != null) {
                        onItemClickListener.onCheckBoxClicked(installedAppInfoList.size(), selectedSet.size());
                    }
//                    if (selectedSet.isEmpty()) {
//                        exitSelectMode();
//                    }
                }
            });
        } else {
            holder.rightLayout.setClickable(true);
            holder.rightLayout.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onMenuClicked(v, appInfo);
                }
            });
            holder.moreBtn.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.GONE);
//            holder.moreBtn.setOnClickListener(v -> {
//                if (onItemClickListener != null) {
//                    onItemClickListener.onMenuClicked(v, appInfo);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return installedAppInfoList.size();
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public Set<Integer> getSelectedSet() {
        return selectedSet;
    }

    public void selectAll() {
        selectedSet.clear();
        for (int i = 0; i < installedAppInfoList.size(); i++) {
            selectedSet.add(i);
        }
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        selectedSet.clear();
        notifyDataSetChanged();
    }

    public void enterSelectMode() {
        isSelectMode = true;
        notifyDataSetChanged();
        if (onItemClickListener != null) {
            onItemClickListener.onEnterSelectMode();
        }
    }

    public void exitSelectMode() {
        isSelectMode = false;
        selectedSet.clear();
        notifyDataSetChanged();
        if (onItemClickListener != null) {
            onItemClickListener.onExitSelectMode();
        }
    }

    public boolean isSelectMode() {
        return isSelectMode;
    }
}
