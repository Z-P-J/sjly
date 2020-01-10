package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.QianQianItem;

import java.util.ArrayList;
import java.util.List;

public class QianQianAdapter extends RecyclerView.Adapter<QianQianAdapter.ViewHolder> {

    private Context context;
    private List<QianQianItem> qianQianItemList = new ArrayList<>();
    private RequestManager requestManager;
    private String app_img_site = "";


    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView app_title;
        private TextView app_description;
        private TextView app_type;
        private TextView app_info;
        private ImageView app_icon;
        private CardView app_item;


        public ViewHolder(View itemView) {
            super(itemView);
            app_title = itemView.findViewById(R.id.app_title);
            app_description = itemView.findViewById(R.id.app_description);
            app_type = itemView.findViewById(R.id.app_type);
            app_info = itemView.findViewById(R.id.app_info);
            app_icon = itemView.findViewById(R.id.app_icon);
            app_item = itemView.findViewById(R.id.app_item);
        }
    }

    public QianQianAdapter(List<QianQianItem> qianQianItemList) {
        this.qianQianItemList = qianQianItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qianqian_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        requestManager = Glide.with(parent.getContext());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.app_title.setText(qianQianItemList.get(position).getApp_title());
        holder.app_description.setText(qianQianItemList.get(position).getApp_description());
        holder.app_type.setText(qianQianItemList.get(position).getApp_type());
        holder.app_info.setText(qianQianItemList.get(position).getApp_info());
        app_img_site = qianQianItemList.get(position).getApp_img_site();
        Log.d("qianqian_app_img_site",""+app_img_site);
        requestManager.load(app_img_site).into(holder.app_icon);
        holder.app_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(qianQianItemList.get(position).getApp_site());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return qianQianItemList.size();
    }


}
