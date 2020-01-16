package com.zpj.shouji.market.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.ExploreItem;
import com.zpj.shouji.market.image.MyImageLoad;
import com.zpj.shouji.market.image.MyImageTransAdapter;
import com.zpj.shouji.market.image.MyProgressBarGet;

import java.util.ArrayList;
import java.util.List;

import it.liuting.imagetrans.ImageTrans;
import it.liuting.imagetrans.listener.SourceImageViewGet;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {
    private List<ExploreItem> appItemList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView itemIcon;
        TextView stateText;
        TextView phoneType;
        TextView userName;
        TextView shareInfo;
        TextView info;
        TextView contentText;
        ImageView moreMenu;

        IconCountView supportView;
//        IconCountView commentView;
        IconCountView starView;

        ImageView appIcon;
        TextView appTitle;
        TextView appInfo;

        LinearLayout commentLayout;
        LinearLayout appLayout;
        NineGridImageView<String> nineGridImageView;

        public Bitmap icon;
        ExploreItem item;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            itemIcon = view.findViewById(R.id.item_icon);
            stateText = view.findViewById(R.id.text_state);
            phoneType = view.findViewById(R.id.phone_type);
            userName = view.findViewById(R.id.user_name);
            shareInfo = view.findViewById(R.id.share_info);
            info = view.findViewById(R.id.text_info);
            contentText = view.findViewById(R.id.text_content);

//            photoLayout = view.findViewById(R.id.layout_photo);
            nineGridImageView = view.findViewById(R.id.nine_grid_image_view);
            nineGridImageView.setAdapter(new ZNineGridImageViewAdapter());
            nineGridImageView.setItemImageClickListener(new ItemImageClickListener<String>() {
                @Override
                public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
                    ImageTrans.with(context)
                            .setImageList(list)
                            .setNowIndex(index)
                            .setSourceImageView(pos -> {
                                View itemView = nineGridImageView.getChildAt(pos);
                                if (itemView != null) {
                                    return (ImageView) itemView;
                                }
                                return null;
                            })
                            .setProgressBar(new MyProgressBarGet())
                            .setImageLoad(new MyImageLoad())
                            .setAdapter(new MyImageTransAdapter())
                            .show();
                }
            });

            appLayout = view.findViewById(R.id.layout_app);
            appIcon = view.findViewById(R.id.app_icon);
            appTitle = view.findViewById(R.id.app_name);
            appInfo = view.findViewById(R.id.app_info);

            moreMenu = view.findViewById(R.id.menu_more);

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
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExploreAdapter.ViewHolder holder, final int position) {
        final ExploreItem exploreItem = appItemList.get(position);
        holder.item = exploreItem;
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder, holder.getAdapterPosition(), holder.item);
            }
        });
        holder.itemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onIconClicked(v, exploreItem.getMemberId());
                }
            }
        });
        Glide.with(context).load(exploreItem.getIcon()).into(holder.itemIcon);

//        holder.photoLayout.setVisibility(View.VISIBLE);
//        holder.photoLayout.setDelegate(ExploreAdapter.this);
        holder.nineGridImageView.setVisibility(View.VISIBLE);
        if (!exploreItem.getSpics().isEmpty()) {
            holder.shareInfo.setText("分享乐图:");
//            holder.photoLayout.setData((ArrayList<String>) exploreItem.getSpics());
            holder.nineGridImageView.setImagesData(exploreItem.getSpics());
        } else if (!exploreItem.getSharePics().isEmpty()) {
            holder.shareInfo.setText("分享应用集:");
//            holder.photoLayout.setData((ArrayList<String>) exploreItem.getSharePics());
            holder.nineGridImageView.setImagesData(exploreItem.getSharePics());
        } else {
            holder.shareInfo.setText("分享动态:");
//            holder.photoLayout.setVisibility(View.GONE);
            holder.nineGridImageView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(exploreItem.getAppName())
                && !TextUtils.isEmpty(exploreItem.getAppIcon())
                && !TextUtils.isEmpty(exploreItem.getAppPackageName())) {
            holder.shareInfo.setText("分享应用:");
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
        holder.commentLayout.removeAllViews();
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

        holder.moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onMenuClicked(v, holder.item);
                }
            }
        });
        holder.stateText.setText(exploreItem.getIconState());
        holder.phoneType.setText(exploreItem.getPhone());
        holder.userName.setText(exploreItem.getNickName());
        holder.info.setText(exploreItem.getTime());
        holder.contentText.setText(exploreItem.getContent());
        holder.supportView.setCount(Long.parseLong(exploreItem.getSupportCount()));
//        holder.commentView.setCount(Long.parseLong(exploreItem.getReplyCount()));
        holder.starView.setCount(0);
    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ViewHolder holder, int position, ExploreItem item);
        void onMenuClicked(View view, ExploreItem item);
        void onIconClicked(View view, String userId);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
