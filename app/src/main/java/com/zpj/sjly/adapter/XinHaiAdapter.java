package com.zpj.sjly.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.zpj.sjly.R;
import com.zpj.sjly.model.XinHaiItem;

import java.util.ArrayList;
import java.util.List;

public class XinHaiAdapter extends RecyclerView.Adapter<XinHaiAdapter.ViewHolder> {

    private Context context;
    private List<XinHaiItem> xinHaiItemList = new ArrayList<>();
    private RequestManager requestManager;
    private String app_img_site = "";


    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView app_title;
        private TextView app_description;
        private TextView app_info;
        private ImageView app_icon;
        private CardView app_item;

        public ViewHolder(View view){
            super(view);
            app_title = (TextView)view.findViewById(R.id.app_title);
            app_description = (TextView)view.findViewById(R.id.app_description);
            app_info = (TextView)view.findViewById(R.id.app_info);
            app_icon = (ImageView)view.findViewById(R.id.app_icon);
            app_item = (CardView)view.findViewById(R.id.app_item);
        }
    }

    public XinHaiAdapter(List<XinHaiItem> xinHaiItemList){
        this.xinHaiItemList = xinHaiItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xinhai_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        requestManager = Glide.with(parent.getContext());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        app_img_site = xinHaiItemList.get(position).getAppImgSite();
        holder.app_title.setText(xinHaiItemList.get(position).getAppTitle());
        holder.app_info.setText(xinHaiItemList.get(position).getAppInfo());
        holder.app_description.setText(xinHaiItemList.get(position).getAppDescription());
        requestManager.load(app_img_site).into(holder.app_icon);
        holder.app_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(xinHaiItemList.get(holder.getAdapterPosition()).getAppSite());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return xinHaiItemList.size();
    }
}
