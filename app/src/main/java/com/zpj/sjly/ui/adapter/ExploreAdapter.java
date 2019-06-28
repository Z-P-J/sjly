package com.zpj.sjly.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.zpj.sjly.bean.ExploreItem;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> implements BGANinePhotoLayout.Delegate {
    private List<ExploreItem> appItemList;
    private Context context;

    private RequestManager requestManager;

    private OnItemClickListener onItemClickListener;

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        Toast.makeText(context, "onClickNinePhotoItem", Toast.LENGTH_SHORT).show();

        BGAPhotoPreviewActivity.IntentBuilder intentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(view.getContext());
        Intent intent = intentBuilder.currentPosition(position).previewPhotos((ArrayList<String>) models).build();
        view.getContext().startActivity(intent);
//        MNImageBrowser.with(context)
//                //必须-当前位置
//                .setCurrentPosition(position)
//                //必须-图片加载用户自己去选择
//                .setImageEngine(new ImageEngine() {
//                    @Override
//                    public void loadImage(final Context context, final String url, final ImageView imageView, final View progressView) {
//                        Log.d("url", "url=" + url);
//                        Glide.with(context)
//                                .load(url)
////                                        .apply(options)
//                                .listener(new RequestListener<Drawable>() {
//                                    @Override
//                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                        //隐藏进度View,必须设置setCustomProgressViewLayoutID
////                                                progressView.setVisibility(View.GONE);
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                        //隐藏进度View,必须设置setCustomProgressViewLayoutID
////                                                progressView.setVisibility(View.GONE);
//                                        return false;
//                                    }
//                                })
//                                .into(imageView);
//                    }
//                })
//                //必须（setImageList和setImageUrl二选一，会覆盖）-图片集合
//                .setImageList((ArrayList<String>) models)
//                //必须（setImageList和setImageUrl二选一，会覆盖）-设置单张图片
////                            .setImageUrl("xxx")
//                //非必须-图片切换动画
//                .setTransformType(ImageBrowserConfig.TransformType.Transform_Default)
//                //非必须-指示器样式（默认文本样式：两种模式）
//                .setIndicatorType(ImageBrowserConfig.IndicatorType.Indicator_Number)
//                //设置隐藏指示器
//                .setIndicatorHide(false)
//                //设置自定义遮盖层，定制自己想要的效果，当设置遮盖层后，原本的指示器会被隐藏
//                .setCustomShadeView(null)
////                        .setCustomProgressViewLayoutID(R.layout.layout_dialog_loading)
//                //自定义ProgressView，不设置默认默认没有
////                            .setCustomProgressViewLayoutID(R.layout.layout_custom_progress_view)
//                //非必须-屏幕方向：横屏，竖屏，Both（默认：横竖屏都支持）
//                .setScreenOrientationType(ImageBrowserConfig.ScreenOrientationType.Screenorientation_Default)
////                            //非必须-图片单击监听
//                .setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(FragmentActivity activity, ImageView view, int position, String url) {
//                        Toast.makeText(activity, "position=" + position, Toast.LENGTH_SHORT).show();
//                    }
//                })
////                            //非必须-图片长按监听
////                            .setOnLongClickListener(new OnLongClickListener() {
////                                @Override
////                                public void onLongClick(FragmentActivity activity, ImageView imageView, int position, String url) {
////                                    //长按监听
////                                }
////                            })
////                            //非必须-图片滑动切换监听
////                            .setOnPageChangeListener(new OnPageChangeListener() {
////                                @Override
////                                public void onPageSelected(int position) {
////                                    //图片滑动切换监听
////                                }
////                            }
//                //全屏模式：默认非全屏模式
//                .setFullScreenMode(true)
//                //打开动画
////                                    .setActivityOpenAnime(R.anim.activity_anmie_in)
////                                    //关闭动画
////                                    .setActivityExitAnime(R.anim.activity_anmie_out)
//                //打开
//                .show(view);
    }


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
            stateText = view.findViewById(R.id.text_state);
            phoneType = view.findViewById(R.id.phone_type);
            userName = view.findViewById(R.id.user_name);
            shareInfo = view.findViewById(R.id.share_info);
            info = view.findViewById(R.id.text_info);
            contentText = view.findViewById(R.id.text_content);

            photoLayout = view.findViewById(R.id.layout_photo);

            appLayout = view.findViewById(R.id.layout_app);
            appIcon = view.findViewById(R.id.app_icon);
            appTitle = view.findViewById(R.id.app_name);
            appInfo = view.findViewById(R.id.app_info);

            moreMenu = view.findViewById(R.id.menu_more);


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

        holder.photoLayout.setVisibility(View.VISIBLE);
        holder.photoLayout.setDelegate(ExploreAdapter.this);
        if (!exploreItem.getSpics().isEmpty()) {
            holder.shareInfo.setText("分享乐图:");
            holder.photoLayout.setData((ArrayList<String>) exploreItem.getSpics());
        } else if (!exploreItem.getSharePics().isEmpty()) {
            holder.shareInfo.setText("分享应用集:");
            holder.photoLayout.setData((ArrayList<String>) exploreItem.getSharePics());
        } else {
            holder.shareInfo.setText("分享动态:");
            holder.photoLayout.setVisibility(View.GONE);
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
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
