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
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.popup.ZPopup;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.widget.popup.BottomListPopupMenu;
import com.zpj.shouji.market.utils.PopupImageLoader;

import java.util.List;

public class DiscoverBinder implements IEasy.OnBindViewHolderListener<DiscoverInfo> {


    private final boolean showComment;

    public DiscoverBinder(boolean showComment) {
        this.showComment = showComment;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<DiscoverInfo> list, int position, List<Object> payloads) {
        Context context = holder.getItemView().getContext();
        final DiscoverInfo discoverInfo = list.get(position);

        Glide.with(context)
                .load(discoverInfo.getIcon())
                .into(holder.getImageView(R.id.item_icon));

        NineGridImageView<String> nineGridImageView = holder.getView(R.id.nine_grid_image_view);
        TextView shareInfo = holder.getTextView(R.id.share_info);
        View appLayout = holder.getView(R.id.layout_app);

        nineGridImageView.setAdapter(new NineGridImageAdapter());
        nineGridImageView.setItemImageClickListener(new ItemImageClickListener<String>() {
            @Override
            public void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
//                List<Object> objects = new ArrayList<>(list);
                ZPopup.imageViewer(context, String.class)
                        .setSrcView(imageView, index)
                        .setImageUrls(list)
                        .setSrcViewUpdateListener((popupView, position1) -> {
                            ImageView view = (ImageView) nineGridImageView.getChildAt(position1);
                            popupView.updateSrcView(view);
                        })
                        .setImageLoader(new PopupImageLoader())
                        .show();
//                new XPopup.Builder(context)
//                        .asImageViewer(imageView, index, objects, new OnSrcViewUpdateListener() {
//                            @Override
//                            public void onSrcViewUpdate(ImageViewerPopupView popupView, int position) {
//                                ImageView view = (ImageView) nineGridImageView.getChildAt(position);
//                                popupView.updateSrcView(view);
//                            }
//                        }, new PopupImageLoader())
//                        .show();

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
        if (!discoverInfo.getSpics().isEmpty()) {
            shareInfo.setText("分享乐图:");
            nineGridImageView.setImagesData(discoverInfo.getSpics());
        } else if (!discoverInfo.getSharePics().isEmpty()) {
            shareInfo.setText("分享应用集:");
            nineGridImageView.setImagesData(discoverInfo.getSharePics());
        } else {
            shareInfo.setText("分享动态:");
            nineGridImageView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(discoverInfo.getAppName())
                && !TextUtils.isEmpty(discoverInfo.getAppIcon())
                && !TextUtils.isEmpty(discoverInfo.getAppPackageName())) {
            shareInfo.setText("分享应用:");
            appLayout.setVisibility(View.VISIBLE);
            Glide.with(context).load(discoverInfo.getAppIcon()).into(holder.getImageView(R.id.app_icon));
            holder.getTextView(R.id.app_name).setText(discoverInfo.getAppName());
            holder.getTextView(R.id.app_info).setText(discoverInfo.getAppPackageName());
        } else {
            appLayout.setVisibility(View.GONE);
        }

        LinearLayout commentLayout = holder.getView(R.id.layout_comment);
        commentLayout.removeAllViews();
        if (showComment) {
            commentLayout.setVisibility(View.VISIBLE);
            for (DiscoverInfo child : discoverInfo.getChildren()) {
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
        } else {
            commentLayout.setVisibility(View.GONE);
        }


        holder.getTextView(R.id.text_state).setText(discoverInfo.getIconState());
        holder.getTextView(R.id.phone_type).setText(discoverInfo.getPhone());
        holder.getTextView(R.id.user_name).setText(discoverInfo.getNickName());
        holder.getTextView(R.id.text_info).setText(discoverInfo.getTime());
        holder.getTextView(R.id.tv_content).setText(discoverInfo.getContent());
        IconCountView supportView = holder.getView(R.id.support_view);
        IconCountView starView = holder.getView(R.id.like_view);
        supportView.setCount(Long.parseLong(discoverInfo.getSupportCount()));
        starView.setCount(0);
    }

    public static void showMenu(Context context, DiscoverInfo data) {
        BottomListPopupMenu.with(context)
                .setMenu(R.menu.menu_tools)
                .onItemClick((menu, view, data1) -> {
                    switch (data1.getItemId()) {
                        case R.id.copy:

                            break;
                        case R.id.share:

                            break;
                        case R.id.collect:

                            break;
                        case R.id.delete:

                            break;
                        case R.id.report:

                            break;
                        case R.id.black_list:
                            break;
                    }
                    menu.dismiss();
                })
                .show();
    }

}
