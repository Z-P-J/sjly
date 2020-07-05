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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.felix.atoast.library.AToast;
import com.lwkandroid.widget.ninegridview.INineGridImageLoader;
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.glide.GlideApp;
import com.zpj.shouji.market.glide.blur.BlurTransformation2;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeDetailFragment;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
import com.zpj.shouji.market.ui.widget.popup.BottomListPopupMenu;
import com.zpj.shouji.market.ui.widget.popup.ImageViewer;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

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

        NineGridView nineGridImageView = holder.getView(R.id.nine_grid_image_view);
        nineGridImageView.setImageLoader(new GlideImageLoader());
        nineGridImageView.setOnItemClickListener(new NineGridView.onItemClickListener() {
            @Override
            public void onNineGirdAddMoreClick(int dValue) {

            }

            @Override
            public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {
                ImageViewer.with(context)
                        .setImageList(discoverInfo.getSpics())
                        .setNowIndex(position)
                        .setSourceImageView(pos -> {
                            NineGirdImageContainer view = (NineGirdImageContainer) nineGridImageView.getChildAt(pos);
                            return view.getImageView();
                        })
                        .show();
            }

            @Override
            public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

            }
        });
        TextView shareInfo = holder.getTextView(R.id.share_info);
        View appLayout = holder.getView(R.id.layout_app);


        holder.setVisible(R.id.collection_layout, false);
        nineGridImageView.setVisibility(View.VISIBLE);
        holder.setVisible(R.id.tv_content, true);
        if (!discoverInfo.getSpics().isEmpty()) {
            shareInfo.setText("分享乐图:");
            List<NineGridBean> gridList = new ArrayList<>();
            for (String url : discoverInfo.getSpics()) {
                gridList.add(new NineGridBean(url));
            }
            nineGridImageView.setDataList(gridList);
//            nineGridImageView.setImagesData(discoverInfo.getSpics());
        } else if (!discoverInfo.getSharePics().isEmpty()) {
            shareInfo.setText("分享应用集:");
            nineGridImageView.setVisibility(View.GONE);
//            nineGridImageView.setImagesData(discoverInfo.getSharePics());

            holder.setVisible(R.id.collection_layout, true);
            NineGridView gridImageView = holder.getView(R.id.grid_image_view);
            gridImageView.setImageLoader(new GlideImageLoader());
            List<NineGridBean> gridList = new ArrayList<>();
            for (String url : discoverInfo.getSharePics()) {
                gridList.add(new NineGridBean(url));
            }
            gridImageView.setDataList(gridList);
//            gridImageView.setAdapter(new NineGridImageAdapter());
//            int size = Math.min(discoverInfo.getSharePics().size(), 4);
//            gridImageView.setImagesData(discoverInfo.getSharePics().subList(0, size));

            Glide.with(context)
                    .load(discoverInfo.getSharePics().get(0))
//                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(26, 6)))
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation2(0.1f, 1 / 4f)))
//                    .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                    .into(holder.getImageView(R.id.img_bg));

            holder.getTextView(R.id.tv_title).setText(discoverInfo.getShareTitle());
            holder.setText(R.id.tv_info, "共" + discoverInfo.getShareCount() + "个应用");
            holder.getTextView(R.id.tv_desc).setText("简介：" + discoverInfo.getContent());
            holder.setVisible(R.id.tv_content, false);

        } else {
            shareInfo.setText("分享动态:");
            nineGridImageView.setVisibility(View.GONE);
        }

        TextView tvDownload = holder.getView(R.id.tv_download);
        if (!TextUtils.isEmpty(discoverInfo.getAppName())
                && !TextUtils.isEmpty(discoverInfo.getAppIcon())
                && !TextUtils.isEmpty(discoverInfo.getAppPackageName())) {
            shareInfo.setText("分享应用:");
            appLayout.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(
                            discoverInfo.getAppIcon()
                                    .replaceAll("img.shouji.com.cn", "imgo.tljpxm.com")
                    )
                    .into(holder.getImageView(R.id.app_icon));
            holder.getTextView(R.id.app_name).setText(discoverInfo.getAppName());
            holder.getTextView(R.id.app_info).setText(discoverInfo.getAppPackageName());
            appLayout.setOnClickListener(v -> {
                if ("0".equals(discoverInfo.getSoftId())) {
                    AToast.warning("应用未收录");
                } else {
                    AppDetailFragment.start(discoverInfo.getAppType(), discoverInfo.getSoftId());
//                    AToast.normal("appId=" + discoverInfo.getAppId() + " softId=" + discoverInfo.getSoftId());
                }
            });
            if (discoverInfo.isApkExist()) {
                tvDownload.setVisibility(View.VISIBLE);
                tvDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AToast.normal("TODO Download");
                    }
                });
            } else {
                tvDownload.setVisibility(View.GONE);
                tvDownload.setOnClickListener(null);
            }

        } else {
            appLayout.setVisibility(View.GONE);
            appLayout.setOnClickListener(null);
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


        holder.setText(R.id.tv_state, discoverInfo.getIconState());
        holder.getTextView(R.id.phone_type).setText(discoverInfo.getPhone());
        holder.getTextView(R.id.user_name).setText(discoverInfo.getNickName());
        holder.getTextView(R.id.text_info).setText(discoverInfo.getTime());
        holder.getTextView(R.id.tv_content).setText(discoverInfo.getContent());

        TextView tvFollow = holder.getView(R.id.tv_follow);
        tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HttpApi.followApi(discoverInfo.getMemberId())
//                        .onSuccess(data -> {
//                            if ("success".equals(data.selectFirst("result").text())) {
//                                AToast.success("关注成功");
//                            } else {
//                                String result = data.selectFirst("info").text();
//                                AToast.error(result);
//                            }
//                        })
//                        .onError(throwable -> {
//                            AToast.error("关注失败！" + throwable.getMessage());
//                        })
//                        .subscribe();
                AToast.normal("TODO");
            }
        });

        IconCountView supportView = holder.getView(R.id.support_view);
        supportView.setCount(Long.parseLong(discoverInfo.getSupportCount()));
        supportView.setOnStateChangedListener(new IconCountView.OnSelectedStateChangedListener() {
            @Override
            public void select(boolean isSelected) {

                HttpApi.likeApi(discoverInfo.getContentType(), discoverInfo.getId())
                        .onSuccess(data -> {
                            String result = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
                                AToast.success(result);
                                String count = String.valueOf(Long.parseLong(discoverInfo.getSupportCount()) + (isSelected ? 1 : -1));
                                discoverInfo.setSupportCount(count);
                                discoverInfo.setLike(isSelected);
//                                if (isSelected) {
//                                    SupportUserInfo userInfo = new SupportUserInfo();
//                                    userInfo.setUserId(discoverInfo.getMemberId());
//                                    userInfo.setNickName(discoverInfo.getNickName());
//                                    userInfo.setUserLogo(discoverInfo.getIcon());
//                                    discoverInfo.getSupportUserInfoList().add(userInfo);
//                                } else {
//
//                                }
                            } else {
                                AToast.error(result);
                                supportView.setState(!isSelected);
                            }
                        })
                        .onError(throwable -> {
                            AToast.error("点赞失败！" + throwable.getMessage());
                            supportView.setState(!isSelected);
                        })
                        .subscribe();
            }
        });
        supportView.setState(discoverInfo.isLike());

        IconCountView starView = holder.getView(R.id.like_view);
        starView.setCount(0);

        holder.getView(R.id.comment_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AToast.normal("回复");
//                CommentPopup.with(context, discoverInfo.getId()).show();
                ThemeDetailFragment.start(discoverInfo, true);
            }
        });


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
            sp.setSpan(
                    new MyClickableSpan(info.getToMemberId()),
                    nickName.length() + 4,
                    nickName.length() + 4 + toNickName.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        sp.setSpan(
                new MyClickableSpan(info.getMemberId()),
                0,
                nickName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
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

    public static class GlideImageLoader implements INineGridImageLoader {

        @Override
        public void displayNineGridImage(Context context, String url, ImageView imageView) {
            if (url.toLowerCase().endsWith(".gif")) {
                Log.d("PopupImageLoader", "gif");
                GlideApp.with(context).asGif().load(url)
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.bga_pp_ic_holder_light).error(R.drawable.bga_pp_ic_holder_light).override(Target.SIZE_ORIGINAL)).into(imageView);
            } else {
                Log.d("PopupImageLoader", "png");
                Glide.with(context).load(url).apply(new RequestOptions().centerCrop().placeholder(R.drawable.bga_pp_ic_holder_light).error(R.drawable.bga_pp_ic_holder_light).override(Target.SIZE_ORIGINAL).dontAnimate()).into(imageView);
            }
        }

        @Override
        public void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height) {
            displayNineGridImage(context, url, imageView);
        }
    }

//    public static class NineGridImageAdapter extends NineGridImageViewAdapter<String> {
//
//        @Override
//        protected void onDisplayImage(Context context, ImageView imageView, String s) {
//            if (s.toLowerCase().endsWith(".gif")) {
//                Log.d("PopupImageLoader", "gif");
//                GlideApp.with(context).asGif().load(s)
//                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.bga_pp_ic_holder_light).error(R.drawable.bga_pp_ic_holder_light).override(Target.SIZE_ORIGINAL)).into(imageView);
//            } else {
//                Log.d("PopupImageLoader", "png");
//                Glide.with(context).load(s).apply(new RequestOptions().centerCrop().placeholder(R.drawable.bga_pp_ic_holder_light).error(R.drawable.bga_pp_ic_holder_light).override(Target.SIZE_ORIGINAL).dontAnimate()).into(imageView);
//            }
//        }
//
//        @Override
//        protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
//            super.onItemImageClick(context, imageView, index, list);
//        }
//    }

    public static class MyClickableSpan extends ClickableSpan {

        private final String memberId;

        public MyClickableSpan(String memberId) {
            this.memberId = memberId;
        }

        @Override
        public void onClick(View widget) {
            ProfileFragment.start(memberId, false);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

}
