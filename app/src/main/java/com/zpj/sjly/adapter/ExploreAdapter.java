package com.zpj.sjly.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.sjly.R;
import com.zpj.sjly.model.ExploreItem;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> implements BGANinePhotoLayout.Delegate {
    private List<ExploreItem> appItemList;
    private Context context;

    private RequestManager requestManager;

    private OnItemClickListener onItemClickListener;

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        Toast.makeText(context, "onClickNinePhotoItem", Toast.LENGTH_SHORT).show();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView itemIcon;
        TextView userName;
        TextView info;
        TextView contentText;

//        TextView supportCount;
//        TextView replyCount;
//        TextView starCount;
//
//        ImageView supportImg;
//        ImageView replyImg;
//        ImageView starImg;

        IconCountView supportView;
//        IconCountView commentView;
        IconCountView starView;

        ImageView appIcon;
        TextView appTitle;
        TextView appInfo;

        LinearLayout commentLayout;
        LinearLayout appLayout;
        BGANinePhotoLayout photoLayout;

        public Bitmap icon;
        ExploreItem item;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            itemIcon = view.findViewById(R.id.item_icon);
            userName = view.findViewById(R.id.user_name);
            info = view.findViewById(R.id.text_info);
            contentText = view.findViewById(R.id.text_content);

            photoLayout = view.findViewById(R.id.layout_photo);

            appLayout = view.findViewById(R.id.layout_app);
            appIcon = view.findViewById(R.id.app_icon);
            appTitle = view.findViewById(R.id.app_name);
            appInfo = view.findViewById(R.id.app_info);


//            supportCount = view.findViewById(R.id.support_count);
//            replyCount = view.findViewById(R.id.reply_count);
//            starCount = view.findViewById(R.id.star_count);
//
//            supportImg = view.findViewById(R.id.support_img);
//            replyImg = view.findViewById(R.id.reply_img);
//            starImg = view.findViewById(R.id.star_img);

            supportView = view.findViewById(R.id.support_view);
//            commentView = view.findViewById(R.id.comment_view);
            starView = view.findViewById(R.id.like_view);

            commentLayout = view.findViewById(R.id.layout_comment);

        }
    }

    public ExploreAdapter(List<ExploreItem> appItemList) {
        this.appItemList = appItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        context = parent.getContext();
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_item, parent, false);

        final ViewHolder holder = new ViewHolder(view);

        requestManager = Glide.with(parent.getContext());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExploreAdapter.ViewHolder holder, final int position) {
        final ExploreItem exploreItem = appItemList.get(position);
        holder.item = exploreItem;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder, holder.getAdapterPosition(), holder.item);
                }
            }
        });
        requestManager.load(exploreItem.getIcon()).into(holder.itemIcon);

        if (!exploreItem.getSpics().isEmpty()) {
            holder.photoLayout.setVisibility(View.VISIBLE);
            holder.photoLayout.setDelegate(ExploreAdapter.this);
            holder.photoLayout.setData((ArrayList<String>) exploreItem.getSpics());
        } else {
            holder.photoLayout.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(exploreItem.getAppName())
                && !TextUtils.isEmpty(exploreItem.getAppIcon())
                && !TextUtils.isEmpty(exploreItem.getAppPackageName())) {
            holder.appLayout.setVisibility(View.VISIBLE);
            Glide.with(holder.appIcon).load(exploreItem.getAppIcon()).into(holder.appIcon);
            holder.appTitle.setText(exploreItem.getAppName());
            holder.appInfo.setText(exploreItem.getAppSize());
        } else {
            holder.appLayout.setVisibility(View.GONE);
        }

//        if (exploreItem.getChildren().isEmpty()) {
//
//        }
        for (ExploreItem child : exploreItem.getChildren()) {
            if (holder.commentLayout.getChildCount() >= 4) {
                break;
            }
            TextView textView = new TextView(holder.commentLayout.getContext());
            if (TextUtils.isEmpty(child.getToNickName())) {
                textView.setText(child.getNickName() + "：" + child.getContent());
            } else {
                textView.setText(child.getNickName() + " 回复 " + child.getToNickName() + "：" + child.getContent());
            }

            textView.setPadding(0, 0, 0, 5);
            holder.commentLayout.addView(textView);
        }

        holder.userName.setText(exploreItem.getNickName());
        holder.info.setText(exploreItem.getTime());
        holder.contentText.setText(exploreItem.getContent());
        holder.supportView.setCount(Long.parseLong(exploreItem.getSupportCount()));
//        holder.commentView.setCount(Long.parseLong(exploreItem.getReplyCount()));
        holder.starView.setCount(0);
//        holder.supportCount.setText(exploreItem.getSupportCount());
//        holder.supportImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GoodView goodView = new GoodView(v.getContext());
//                holder.supportImg.setImageResource(R.drawable.good_checked);
//                goodView.setImage(R.drawable.good_checked);
//                goodView.setText("+1");
//                goodView.show(v);
//            }
//        });
//        holder.replyCount.setText(exploreItem.getReplyCount());
//        holder.starImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GoodView goodView = new GoodView(v.getContext());
//                holder.starImg.setImageResource(R.drawable.collection_checked);
//                goodView.setImage(R.drawable.collection_checked);
//                goodView.setText("收藏成功");
//                goodView.show(v);
//            }
//        });


//        String icon = appItemList.get(position).getAppIcon();
//        requestManager.asBitmap().load(icon).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                holder.icon = resource;
//                holder.appIcon.setImageBitmap(resource);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ViewHolder holder, int position, ExploreItem item);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
