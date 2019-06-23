package com.zpj.sjly.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.MNImageBrowser;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.zpj.sjly.R;
import com.zpj.sjly.model.ImgItem;

import java.util.ArrayList;
import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.ViewHolder> {

    private Context context;
    private List<ImgItem> imgItemList;
    private ArrayList<String> imageUrlList = new ArrayList<>();

//    private static final RequestOptions options = new RequestOptions()
////            .placeholder(R.drawable.format_picture)	//加载成功之前占位图
////            .error(R.drawable.format_picture_broken)	//加载错误之后的错误图
//            .centerCrop();

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img_view;

        public ViewHolder(View itemView) {
            super(itemView);
            img_view = itemView.findViewById(R.id.img_view);
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
    public void onBindViewHolder(@NonNull final ImgAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(imgItemList.get(position).getImg_site()).into(holder.img_view);
        holder.img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "ttttttttt", Toast.LENGTH_SHORT).show();
                imageUrlList.clear();
                for (ImgItem item : imgItemList) {
                    imageUrlList.add(item.getImg_site());
                }
                MNImageBrowser.with(context)
                        //必须-当前位置
                        .setCurrentPosition(holder.getAdapterPosition())
                        //必须-图片加载用户自己去选择
                        .setImageEngine(new ImageEngine() {
                            @Override
                            public void loadImage(final Context context, final String url, final ImageView imageView, final View progressView) {
                                Log.d("url", "url=" + url);
                                Glide.with(context)
                                        .load(url)
//                                        .apply(options)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                //隐藏进度View,必须设置setCustomProgressViewLayoutID
//                                                progressView.setVisibility(View.GONE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                //隐藏进度View,必须设置setCustomProgressViewLayoutID
//                                                progressView.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(imageView);
                            }
                        })
                        //必须（setImageList和setImageUrl二选一，会覆盖）-图片集合
                        .setImageList(imageUrlList)
                        //必须（setImageList和setImageUrl二选一，会覆盖）-设置单张图片
//                            .setImageUrl("xxx")
                        //非必须-图片切换动画
                        .setTransformType(ImageBrowserConfig.TransformType.Transform_Default)
                        //非必须-指示器样式（默认文本样式：两种模式）
                        .setIndicatorType(ImageBrowserConfig.IndicatorType.Indicator_Number)
                        //设置隐藏指示器
                        .setIndicatorHide(false)
                        //设置自定义遮盖层，定制自己想要的效果，当设置遮盖层后，原本的指示器会被隐藏
                        .setCustomShadeView(null)
//                        .setCustomProgressViewLayoutID(R.layout.layout_dialog_loading)
                        //自定义ProgressView，不设置默认默认没有
//                            .setCustomProgressViewLayoutID(R.layout.layout_custom_progress_view)
                        //非必须-屏幕方向：横屏，竖屏，Both（默认：横竖屏都支持）
                        .setScreenOrientationType(ImageBrowserConfig.ScreenOrientationType.Screenorientation_Default)
//                            //非必须-图片单击监听
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(FragmentActivity activity, ImageView view, int position, String url) {
                                Toast.makeText(activity, "position=" + position, Toast.LENGTH_SHORT).show();
                            }
                        })
//                            //非必须-图片长按监听
//                            .setOnLongClickListener(new OnLongClickListener() {
//                                @Override
//                                public void onLongClick(FragmentActivity activity, ImageView imageView, int position, String url) {
//                                    //长按监听
//                                }
//                            })
//                            //非必须-图片滑动切换监听
//                            .setOnPageChangeListener(new OnPageChangeListener() {
//                                @Override
//                                public void onPageSelected(int position) {
//                                    //图片滑动切换监听
//                                }
//                            }
                        //全屏模式：默认非全屏模式
                        .setFullScreenMode(true)
                        //打开动画
//                                    .setActivityOpenAnime(R.anim.activity_anmie_in)
//                                    //关闭动画
//                                    .setActivityExitAnime(R.anim.activity_anmie_out)
                        //打开
                        .show(holder.img_view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgItemList.size();
    }
}
