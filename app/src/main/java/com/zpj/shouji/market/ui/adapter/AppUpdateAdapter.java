package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppUpdateInfo;
import com.zpj.shouji.market.utils.AppUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.util.List;

public class AppUpdateAdapter extends RecyclerView.Adapter<AppUpdateAdapter.ViewHolder> {

    private Context context;
    private final List<AppUpdateInfo> updateInfoList;
    private OnItemClickListener onItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView iconImageView;
        TextView titleTextView;
        TextView versionTextView;
        TextView infoTextView;
        TextView updateTextView;
        ImageView settingBtn;
        ImageView expandBtn;

        ViewHolder(View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.image_icon);
            versionTextView = itemView.findViewById(R.id.text_version);
            titleTextView = itemView.findViewById(R.id.text_title);
            infoTextView = itemView.findViewById(R.id.text_info);
            updateTextView = itemView.findViewById(R.id.text_update);
            settingBtn = itemView.findViewById(R.id.btn_setting);
            expandBtn = itemView.findViewById(R.id.btn_expand);
        }
    }

    public AppUpdateAdapter(List<AppUpdateInfo> updateInfoList){
        this.updateInfoList = updateInfoList;
    }


    @NonNull
    @Override
    public AppUpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_update, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppUpdateAdapter.ViewHolder holder, int position) {
        AppUpdateInfo updateInfo = updateInfoList.get(position);
        Log.d("onBindViewHolder", "getPackageName=" + updateInfo.getPackageName());
        Log.d("onBindViewHolder", "size=" + updateInfo.getNewSize());
        if (updateInfo.getIconDrawable() == null) {
            holder.iconImageView.setImageResource(R.mipmap.ic_launcher);
            ExecutorHelper.submit(() -> {
                Drawable drawable = AppUtil.getAppIcon(context, updateInfo.getPackageName());
                updateInfo.setIconDrawable(drawable);
                holder.iconImageView.post(() -> holder.iconImageView.setImageDrawable(drawable));
            });
        } else {
            holder.iconImageView.setImageDrawable(updateInfo.getIconDrawable());
        }

        holder.titleTextView.setText(updateInfo.getAppName());
        holder.versionTextView.setText(getVersionText(updateInfo));
        holder.infoTextView.setText(updateInfo.getNewSize() + " | " + updateInfo.getUpdateTimeInfo());
        holder.updateTextView.setText(updateInfo.getUpdateInfo());

        holder.updateTextView.post(new Runnable() {
            @Override
            public void run() {
                if (holder.updateTextView.getLayout().getEllipsisCount(holder.updateTextView.getLineCount() - 1) > 0) {
                    holder.expandBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.expandBtn.setVisibility(View.GONE);
                }
            }
        });

        holder.expandBtn.setTag(false);
        holder.expandBtn.setOnClickListener(v -> {
            Toast.makeText(context, "expandBtn", Toast.LENGTH_SHORT).show();
            boolean tag = (boolean) holder.expandBtn.getTag();
            holder.expandBtn.setImageResource(tag ? R.drawable.ic_expand_more_black_24dp : R.drawable.ic_expand_less_black_24dp);
            holder.updateTextView.setMaxLines(tag ? 1 : 0);
            holder.updateTextView.setText(updateInfo.getUpdateInfo());
            holder.expandBtn.setTag(!tag);
        });

        holder.settingBtn.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onMenuClicked(v, updateInfo);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder, position, updateInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return updateInfoList.size();
    }

    private SpannableString getVersionText(AppUpdateInfo updateInfo) {
        SpannableString spannableString = new SpannableString(updateInfo.getOldVersionName() + "  " + updateInfo.getNewVersionName());
        spannableString.setSpan(
                new StrikethroughSpan(),
                0,
                updateInfo.getOldVersionName().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannableString;
    }

//    private StrikethroughSpan span = new StrikethroughSpan() {
//
//        @Override
//        public void updateDrawState(@NonNull TextPaint ds) {
//            super.updateDrawState(ds);
////            ds.setColor(Color.RED);
////            ds.setStrikeThruText(true);
//        }
//
//    };

    public interface OnItemClickListener {
        void onItemClick(AppUpdateAdapter.ViewHolder holder, int position, AppUpdateInfo updateInfo);
        void onItemLongClick(AppUpdateAdapter.ViewHolder holder, int position, AppUpdateInfo updateInfo);
        void onMenuClicked(View view, AppUpdateInfo updateInfo);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
