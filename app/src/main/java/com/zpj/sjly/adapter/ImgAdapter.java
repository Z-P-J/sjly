package com.zpj.sjly.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zpj.sjly.R;
import com.zpj.sjly.fragment.ImgItem;

import java.util.ArrayList;
import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.ViewHolder> {

    private Context context;
    private List<ImgItem> imgItemList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img_view;

        public ViewHolder(View itemView) {
            super(itemView);
            img_view = (ImageView)itemView.findViewById(R.id.img_view);
        }
    }

    public ImgAdapter(List<ImgItem> imgItemList){
        this.imgItemList = imgItemList;
    }


    @NonNull
    @Override
    public ImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_view, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImgAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(imgItemList.get(position).getImg_site()).into(holder.img_view);
    }

    @Override
    public int getItemCount() {
        return imgItemList.size();
    }
}
