package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.image.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.ViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private List<String> imageUrlList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img_view;

        public ViewHolder(View itemView) {
            super(itemView);
            img_view = itemView.findViewById(R.id.img_view);
        }
    }

    public ImgAdapter(RecyclerView recyclerView, List<String> imageUrlList){
        this.recyclerView = recyclerView;
        this.imageUrlList = imageUrlList;
    }


    @NonNull
    @Override
    public ImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImgAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUrlList.get(position))
                .into(holder.img_view);
        holder.img_view.setTag(position);
        holder.img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView img = (ImageView) v;
                List<Object> objects = new ArrayList<>(imageUrlList);
                new XPopup.Builder(context)
                        .asImageViewer(holder.img_view, (int)v.getTag(), objects, new OnSrcViewUpdateListener() {
                            @Override
                            public void onSrcViewUpdate(ImageViewerPopupView popupView, int pos) {
                                int layoutPos = recyclerView.indexOfChild(holder.itemView);
                                View view = recyclerView.getChildAt(layoutPos + pos - position);
                                ImageView imageView;
                                if (view != null) {
                                    imageView = view.findViewById(R.id.img_view);
                                } else {
                                    imageView = holder.img_view;
                                }
                                popupView.updateSrcView(imageView);
                            }
                        }, new ImageLoader())
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }
}
