package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.popup.ZPopup;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
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
                ZPopup.imageViewer(context)
                        .setSrcView(imageView, index)
                        .setImageUrls(list)
                        .setSrcViewUpdateListener((popupView, position1) -> {
                            ImageView view = (ImageView) nineGridImageView.getChildAt(position1);
                            popupView.updateSrcView(view);
                        })
                        .setImageLoader(new PopupImageLoader())
                        .show();
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
            if (discoverInfo.getChildren().isEmpty()) {
                commentLayout.setVisibility(View.GONE);
            } else {
                commentLayout.setVisibility(View.VISIBLE);
                for (DiscoverInfo child : discoverInfo.getChildren()) {

                    if (commentLayout.getChildCount() >= 3) {
                        int total = discoverInfo.getChildren().size();
                        if (total > 3) {
                            DrawableTintTextView textView = new DrawableTintTextView(context);
                            textView.setText("共" + total + "条回复");
                            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_black_24dp);
                            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
                            textView.setCompoundDrawablePadding(16);
                            int color = context.getResources().getColor(R.color.colorPrimary);
                            textView.setTextColor(color);
                            textView.setDrawableTintColor(color);
                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            commentLayout.addView(textView, params);
                        }
                        break;
                    }
                    TextView textView = new TextView(context);

                    textView.setTextColor(context.getResources().getColor(R.color.color_text_major));
                    textView.setText(getComment(child));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());

                    textView.setPadding(0, 0, 0, 8);
                    commentLayout.addView(textView);
                }
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

    private SpannableString getComment(DiscoverInfo info) {
        String nickName = info.getNickName();
        String toNickName = info.getToNickName();
        String content = info.getContent();
        SpannableString sp;
        if (TextUtils.isEmpty(toNickName)) {
            sp = new SpannableString(nickName + "：" + content);
        } else {
            sp = new SpannableString(nickName + " 回复 " + toNickName
                    + "：" + content);
            sp.setSpan(new ClickableSpan() {
                           @Override
                           public void onClick(View widget) {
                               AToast.normal(toNickName);
                           }

                           @Override
                           public void updateDrawState(@NonNull TextPaint ds) {
                               super.updateDrawState(ds);
                               ds.setUnderlineText(false);
                           }
                       },
                    nickName.length() + 4,
                    nickName.length() + 4 + toNickName.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        sp.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                AToast.normal(nickName);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
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
