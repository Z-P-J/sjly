package com.zpj.sjly.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpj.sjly.R;
import com.zpj.sjly.fragment.AppItem;
import com.zpj.sjly.view.PosterItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * 观看历史，点击是继续观看，不用进详情页
 * Created by huangyong on 2018/1/26.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {
    private Context context;
    private List<AppItem> info;

    public BannerAdapter(Context context, List<AppItem> recentUpdate) {
        this.context = context;
        this.info = recentUpdate;
    }

    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_item,parent,false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        holder.iv.setBackgroundResource(R.mipmap.ic_launcher);
        holder.title.setText(info.get(position).getAppTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    public int getItemCount() {
        if (info==null){
            return 0;
        }
        return info.size();
    }

    class BannerHolder extends RecyclerView.ViewHolder {

        PosterItemView iv;
        TextView title;

        BannerHolder(View view) {
            super(view);
            iv = view.findViewById(R.id.banner);
            title = view.findViewById(R.id.movtitle);
        }
    }


}
