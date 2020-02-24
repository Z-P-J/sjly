package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.PopupImageLoader;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.ViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private List<String> imageUrlList;
    private final float screenWidth;
    private final float screenHeight;
//    private int defaultHeight;

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
        this.screenHeight = ScreenUtils.getScreenHeight(recyclerView.getContext());
        this.screenWidth = ScreenUtils.getScreenWidth(recyclerView.getContext());
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
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();

                        FrameLayout.LayoutParams params; //  = (FrameLayout.LayoutParams) holder.img_view.getLayoutParams();
                        if (width > height) {
//                            params.height = screenHeight / 3;
//                            params.width = screenWidth / 3;
                            params = new FrameLayout.LayoutParams((int) ((screenHeight / screenWidth) * screenHeight / 4), (int) (screenHeight / 4));
                        } else {
                            params = new FrameLayout.LayoutParams((int) (screenWidth / 4), (int) (screenHeight / 4));
                        }
                        holder.img_view.setLayoutParams(params);
                        holder.img_view.setImageDrawable(resource);
                    }
                });
        holder.img_view.setTag(position);
        holder.img_view.setOnClickListener(v -> {
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
                    }, new PopupImageLoader())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }
}
