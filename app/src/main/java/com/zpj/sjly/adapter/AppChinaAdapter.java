package com.zpj.sjly.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.sjly.R;
import com.zpj.sjly.fragment.AppChinaItem;

import java.util.ArrayList;
import java.util.List;

public class AppChinaAdapter extends RecyclerView.Adapter<AppChinaAdapter.ViewHolder> {

    private List<AppChinaItem> appChinaItemList = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;


    public AppChinaAdapter(List<AppChinaItem> appChinaItemList){
        this.appChinaItemList = appChinaItemList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView app_icon;
        private TextView app_name;
        private TextView app_type;
        private TextView app_result;
        private TextView app_count;
        private TextView app_update_time;
        private TextView app_introduce;
        private CardView app_item;

        public ViewHolder(View view){
            super(view);
            app_icon = (ImageView)view.findViewById(R.id.app_icon);
            app_name = (TextView)view.findViewById(R.id.app_name);
            app_type = (TextView)view.findViewById(R.id.app_type);
            app_result = (TextView)view.findViewById(R.id.app_result);
            app_count = (TextView)view.findViewById(R.id.app_count);
            app_update_time = (TextView)view.findViewById(R.id.app_update_time);
            app_introduce = (TextView)view.findViewById(R.id.app_introduce);
            app_item = (CardView)view.findViewById(R.id.app_item);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appchina_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick((Integer) v.getTag());
                }
            }
        });
         return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(appChinaItemList.get(position).getApp_img_url()).into(holder.app_icon);
        holder.app_name.setText(appChinaItemList.get(position).getApp_name());
        holder.app_result.setText(appChinaItemList.get(position).getApp_result());
        holder.app_count.setText(appChinaItemList.get(position).getApp_count());
        holder.app_update_time.setText(appChinaItemList.get(position).getApp_update_time());
        holder.app_introduce.setText(appChinaItemList.get(position).getApp_introduce());
        if (appChinaItemList.get(position).getApp_result().equals("已收录")) {
            holder.app_result.setBackgroundColor(Color.GREEN);
        } else if (appChinaItemList.get(position).getApp_result().equals("待更新")) {
            holder.app_result.setBackgroundColor(Color.YELLOW);
        } else if (appChinaItemList.get(position).getApp_result().equals("未收录")){
            holder.app_result.setBackgroundColor(Color.RED);
        }
        String app_type = appChinaItemList.get(position).getApp_type();
        holder.app_type.setText(app_type);
        if (app_type.equals("应用")) {
            holder.app_type.setBackgroundColor(Color.WHITE);
            holder.app_type.setTextColor(Color.GRAY);
        } else if (app_type.equals("游戏")){
            holder.app_type.setBackgroundColor(Color.GRAY);
            holder.app_type.setTextColor(Color.WHITE);
        }
        holder.app_item.setTag(position);
    }

    @Override
    public int getItemCount() {
        return appChinaItemList.size();
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
