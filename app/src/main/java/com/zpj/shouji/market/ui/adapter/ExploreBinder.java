package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.ExploreItem;
import com.zpj.shouji.market.image.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ExploreBinder implements IEasy.OnBindViewHolderListener<ExploreItem> {

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<ExploreItem> list, int position, List<Object> payloads) {
        Context context = holder.getItemView().getContext();
        final ExploreItem exploreItem = list.get(position);

        Glide.with(context)
                .load(exploreItem.getIcon())
                .into(holder.getImageView(R.id.item_icon));

        NineGridImageView<String> nineGridImageView = holder.getView(R.id.nine_grid_image_view);
        TextView shareInfo = holder.getTextView(R.id.share_info);
        View appLayout = holder.getView(R.id.layout_app);




        nineGridImageView.setAdapter(new ZNineGridImageViewAdapter());
        nineGridImageView.setItemImageClickListener(new ItemImageClickListener<String>() {
            @Override
            public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
                List<Object> objects = new ArrayList<>(list);
                new XPopup.Builder(context)
                        .asImageViewer(imageView, index, objects, new OnSrcViewUpdateListener() {
                            @Override
                            public void onSrcViewUpdate(ImageViewerPopupView popupView, int position) {
                                ImageView view = (ImageView) nineGridImageView.getChildAt(position);
                                popupView.updateSrcView(view);
                            }
                        }, new ImageLoader())
                        .show();

//                ImageTrans.with(context)
//                        .setImageList(list)
//                        .setNowIndex(index)
//                        .setSourceImageView(pos -> {
//                            View itemView = nineGridImageView.getChildAt(pos);
//                            if (itemView != null) {
//                                return (ImageView) itemView;
//                            }
//                            return null;
//                        })
//                        .setProgressBar(new MyProgressBarGet())
//                        .setImageLoad(new MyImageLoad())
//                        .setAdapter(new MyImageTransAdapter())
//                        .show();
            }
        });
        nineGridImageView.setVisibility(View.VISIBLE);
        if (!exploreItem.getSpics().isEmpty()) {
            shareInfo.setText("分享乐图:");
            nineGridImageView.setImagesData(exploreItem.getSpics());
        } else if (!exploreItem.getSharePics().isEmpty()) {
            shareInfo.setText("分享应用集:");
            nineGridImageView.setImagesData(exploreItem.getSharePics());
        } else {
            shareInfo.setText("分享动态:");
            nineGridImageView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(exploreItem.getAppName())
                && !TextUtils.isEmpty(exploreItem.getAppIcon())
                && !TextUtils.isEmpty(exploreItem.getAppPackageName())) {
            shareInfo.setText("分享应用:");
            appLayout.setVisibility(View.VISIBLE);
            Glide.with(context).load(exploreItem.getAppIcon()).into(holder.getImageView(R.id.app_icon));
            holder.getTextView(R.id.app_name).setText(exploreItem.getAppName());
            holder.getTextView(R.id.app_info).setText(exploreItem.getAppSize());
        } else {
            appLayout.setVisibility(View.GONE);
        }

        LinearLayout commentLayout = holder.getView(R.id.layout_comment);
        commentLayout.removeAllViews();
        for (ExploreItem child : exploreItem.getChildren()) {
            if (commentLayout.getChildCount() >= 4) {
                break;
            }
            TextView textView = new TextView(context);
            if (TextUtils.isEmpty(child.getToNickName())) {
                textView.setText(child.getNickName() + "：" + child.getContent());
            } else {
                textView.setText(child.getNickName() + " 回复 " + child.getToNickName() + "：" + child.getContent());
            }

            textView.setPadding(0, 0, 0, 5);
            commentLayout.addView(textView);
        }

        holder.getTextView(R.id.text_state).setText(exploreItem.getIconState());
        holder.getTextView(R.id.phone_type).setText(exploreItem.getPhone());
        holder.getTextView(R.id.user_name).setText(exploreItem.getNickName());
        holder.getTextView(R.id.text_info).setText(exploreItem.getTime());
        holder.getTextView(R.id.tv_content).setText(exploreItem.getContent());
        IconCountView supportView = holder.getView(R.id.support_view);
        IconCountView starView = holder.getView(R.id.like_view);
        supportView.setCount(Long.parseLong(exploreItem.getSupportCount()));
        starView.setCount(0);
    }

}
