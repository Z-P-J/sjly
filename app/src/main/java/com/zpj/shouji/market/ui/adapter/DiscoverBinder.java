package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.glide.transformations.CircleWithBorderTransformation;
import com.zpj.shouji.market.glide.transformations.blur.CropBlurTransformation;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.model.SupportUserInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommonImageViewerDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.SupportUserListDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ThemeAppDownloadDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ThemeMoreDialogFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeDetailFragment;
import com.zpj.shouji.market.ui.fragment.theme.TopicThemeListFragment;
import com.zpj.shouji.market.ui.widget.CombineImageView;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
import com.zpj.shouji.market.ui.widget.count.IconCountView;
import com.zpj.shouji.market.ui.widget.emoji.EmojiExpandableTextView;
import com.zpj.shouji.market.ui.widget.expandabletextview.ExpandableTextView;
import com.zpj.shouji.market.ui.widget.expandabletextview.app.LinkType;
import com.zpj.shouji.market.ui.widget.ninegrid.NineGridView;
import com.zpj.toast.ZToast;
import com.zpj.utils.NetUtils;
import com.zpj.utils.ScreenUtils;
import com.zpj.skin.SkinEngine;

import java.util.List;

public class DiscoverBinder
        implements IEasy.OnBindViewHolderListener<DiscoverInfo>,
        ExpandableTextView.OnLinkClickListener {

    private static final RequestOptions BLUR_OPTIONS = RequestOptions.bitmapTransform(new CropBlurTransformation(18, 0.6f));


    private final boolean showComment;
    private final boolean expanable;

    public DiscoverBinder(boolean showComment) {
        this.showComment = showComment;
        this.expanable = true;
    }

    public DiscoverBinder(boolean showComment, boolean expanable) {
        this.showComment = showComment;
        this.expanable = expanable;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<DiscoverInfo> list, int position, List<Object> payloads) {
        Context context = holder.getItemView().getContext();
        final DiscoverInfo discoverInfo = list.get(position);

        Glide.with(context)
                .load(discoverInfo.getIcon())
                .apply(RequestOptions.bitmapTransform(new CircleWithBorderTransformation(0.5f, Color.LTGRAY)))
                .into(holder.getImageView(R.id.item_icon));
        Log.d("DiscoverBinder", "nickName=" + discoverInfo.getNickName() + " getIcon=" + discoverInfo.getIcon());


        TextView shareInfo = holder.getTextView(R.id.share_info);
        View appLayout = holder.getView(R.id.layout_app);


        holder.setVisible(R.id.collection_layout, false);
        EmojiExpandableTextView tvContent = holder.getView(R.id.tv_content);
        NineGridView nineGridImageView = holder.getView(R.id.nine_grid_view);
        if (!discoverInfo.getSpics().isEmpty()) {
            tvContent.setVisibility(View.VISIBLE);
            shareInfo.setText("分享乐图:");
            nineGridImageView.setVisibility(View.VISIBLE);
            nineGridImageView.setUrls(discoverInfo.getSpics());
            nineGridImageView.setCallback(new NineGridCallback(nineGridImageView, discoverInfo));
        } else if (!discoverInfo.getSharePics().isEmpty()) {
            tvContent.setVisibility(View.VISIBLE);
            shareInfo.setText("分享应用集:");
            nineGridImageView.setVisibility(View.GONE);

            holder.setVisible(R.id.collection_layout, true);

            CombineImageView ivIcon = holder.getView(R.id.iv_icon);
            ivIcon.setUrls(discoverInfo.getSharePics());


            Glide.with(context)
                    .load(discoverInfo.getSharePics().get(0))
//                    .apply(RequestOptions.bitmapTransform(new CropBlurTransformation(18, 0.8f)))
                    .apply(BLUR_OPTIONS)
                    .into(holder.getImageView(R.id.img_bg));

            holder.getTextView(R.id.tv_title).setText(discoverInfo.getShareTitle());
            holder.setText(R.id.tv_info, "共" + discoverInfo.getShareCount() + "个应用");
//            holder.getTextView(R.id.tv_desc).setText("简介：" + discoverInfo.getContent());
        } else {
            tvContent.setVisibility(View.VISIBLE);
            shareInfo.setText("分享动态:");
            nineGridImageView.setVisibility(View.GONE);
        }

        DownloadButton tvDownload = holder.getView(R.id.tv_download);
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
                    ZToast.warning("应用未收录");
                } else {
                    AppDetailFragment.start(discoverInfo.getAppType(), discoverInfo.getSoftId());
//                    ZToast.normal("appId=" + discoverInfo.getAppId() + " softId=" + discoverInfo.getSoftId());
                }
            });
            if (discoverInfo.isApkExist()) {
                tvDownload.setVisibility(View.VISIBLE);
                tvDownload.setOnClickListener(v -> {
                    new ThemeAppDownloadDialogFragment()
                            .setId(discoverInfo.getAppId())
                            .setDiscoverInfo(discoverInfo)
                            .show(context);
                });
            } else {
                tvDownload.setVisibility(View.GONE);
                tvDownload.setOnClickListener(null);
            }

        } else {
            appLayout.setVisibility(View.GONE);
            appLayout.setOnClickListener(null);
        }

        ExpandableTextView tvSupportUsers = holder.getView(R.id.tv_support_users);
        if (!expanable || discoverInfo.getSupportUserInfoList().isEmpty()) {
            tvSupportUsers.setVisibility(View.GONE);
            tvSupportUsers.setLinkClickListener(null);
        } else {
            tvSupportUsers.setVisibility(View.VISIBLE);
            int size = discoverInfo.getSupportUserInfoList().size();
            StringBuilder supportUsers = new StringBuilder("[" + discoverInfo.getSupportCount() + "人赞过](support_users)：");
            for (int i = 0; i < size; i++) {
                SupportUserInfo userInfo = discoverInfo.getSupportUserInfoList().get(i);
                if (i != 0) {
                    supportUsers.append("，");
                }
                supportUsers.append(String.format("[%s](%s)", userInfo.getNickName(), userInfo.getUserId()));
            }
            tvSupportUsers.setContent(supportUsers.toString());
            tvSupportUsers.setLinkClickListener((type, title, content) -> {
                if (type == LinkType.SELF) {
                    if ("support_users".equals(content)) {
                        SupportUserListDialogFragment.start(discoverInfo.getContentType(), discoverInfo.getId());
                    } else {
                        ProfileFragment.start(content, false);
                    }
                }
            });
        }

        LinearLayout commentLayout = holder.getView(R.id.layout_comment);
        commentLayout.removeAllViews();
        if (showComment) {
            if (discoverInfo.getChildren().isEmpty()) {
                commentLayout.setVisibility(View.GONE);
            } else {
                commentLayout.setVisibility(View.VISIBLE);
                int i = 0;

                for (DiscoverInfo child : discoverInfo.getChildren()) {

                    TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
                    Drawable background = typedArray.getDrawable(0);
                    typedArray.recycle();
                    LinearLayout container = new LinearLayout(context);
                    container.setOrientation(LinearLayout.VERTICAL);
                    container.setBackground(background);
                    commentLayout.addView(container);

                    container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            ZToast.normal("hhhhhhh");
                            holder.performClick();
                        }
                    });

                    if (i >= 3) {
                        container.setOnLongClickListener(v -> true);
                        int total = discoverInfo.getChildren().size();
                        if (total > 3) {
                            DrawableTintTextView textView = new DrawableTintTextView(context);
                            textView.setText("共" + total + "条回复");
                            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_enter_bak);
                            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
                            textView.setCompoundDrawablePadding(8);
                            textView.setGravity(Gravity.CENTER_VERTICAL);
                            int color = context.getResources().getColor(R.color.colorPrimary);
                            textView.setTextColor(color);
                            textView.setDrawableTintColor(color);
//                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            container.addView(textView);
                        }
                        break;
                    }

                    container.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            new ThemeMoreDialogFragment()
                                    .setDiscoverInfo(child)
                                    .show(context);
                            return true;
                        }
                    });

                    EmojiExpandableTextView textView = new EmojiExpandableTextView(context);

                    textView.setNeedExpend(false);
                    textView.setNeedConvertUrl(false);
                    textView.setShowRedDot(child.isNewMessage());
                    SkinEngine.setTextColor(textView, R.attr.textColorMajor);
                    textView.setLimitLines(8);
                    textView.setContent(getComment2(child));
                    textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
//                    textView.setLinkClickListener(this);

                    textView.setPadding(0, 0, 0, 8);
                    container.addView(textView);

                    if (!child.getSpics().isEmpty()) {
                        NineGridView nineGridView = (NineGridView) LayoutInflater.from(context).inflate(R.layout.layout_nine_grid, null, false);
                        nineGridView.setId(View.generateViewId());
                        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(nineGridView.getLayoutParams());
                        int margin = ScreenUtils.dp2pxInt(context, 4);
                        params.bottomMargin = margin;
                        params.topMargin = margin;
                        params.leftMargin = margin;
                        params.rightMargin = margin;
                        nineGridView.setLayoutParams(params);
//                        nineGridView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        nineGridView.setUrls(child.getSpics());
                        nineGridView.setCallback(new NineGridCallback(nineGridView, child));
//                        nineGridView.setPadding(0, 0, 0, 8);
                        container.addView(nineGridView);
                    }
                    i++;
                }
            }
        } else {
            commentLayout.setVisibility(View.GONE);
        }

        holder.setVisible(R.id.ll_action_contaienr, expanable);

        holder.setText(R.id.tv_state, discoverInfo.getIconState());
        holder.setText(R.id.phone_type, discoverInfo.getPhone());
        holder.setText(R.id.user_name, discoverInfo.getNickName());
        holder.setText(R.id.text_info, discoverInfo.getTime());
        if (tvContent.getVisibility() == View.VISIBLE) {
            tvContent.setNeedExpend(expanable);
            tvContent.setNeedContract(expanable);
            tvContent.setContent(discoverInfo.getContent());
//            tvContent.setLinkClickListener(this);
        }

//        TextView tvFollow = holder.getView(R.id.tv_follow);
//        tvFollow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                HttpApi.followApi(discoverInfo.getMemberId())
////                        .onSuccess(data -> {
////                            if ("success".equals(data.selectFirst("result").text())) {
////                                ZToast.success("关注成功");
////                            } else {
////                                String result = data.selectFirst("info").text();
////                                ZToast.error(result);
////                            }
////                        })
////                        .onError(throwable -> {
////                            ZToast.error("关注失败！" + throwable.getMessage());
////                        })
////                        .subscribe();
//                ZToast.normal("TODO");
//            }
//        });

        IconCountView supportView = holder.getView(R.id.support_view);
        supportView.setCount(Long.parseLong(discoverInfo.getSupportCount()));
        supportView.setOnStateChangedListener(new IconCountView.OnSelectedStateChangedListener() {
            @Override
            public void select(boolean isSelected) {
                if (!UserManager.getInstance().isLogin()) {
//                    supportView.setState(!isSelected);
                    ZToast.warning(R.string.text_msg_not_login);
                    LoginFragment.start();
                    supportView.praiseFailed();
                    return;
                }

                HttpApi.likeApi(discoverInfo.getContentType(), discoverInfo.getId())
                        .onSuccess(data -> {
                            String result = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
                                String count = String.valueOf(Long.parseLong(discoverInfo.getSupportCount()) + (isSelected ? 1 : -1));
                                discoverInfo.setSupportCount(count);
                                discoverInfo.setLike(isSelected);
                            } else {
                                ZToast.error(result);
//                                supportView.setState(!isSelected);
                                supportView.praiseFailed();
                            }
                        })
                        .onError(throwable -> {
                            ZToast.error("点赞失败！" + throwable.getMessage());
//                            supportView.setState(!isSelected);
                            supportView.praiseFailed();
                        })
                        .subscribe();
            }
        });
        supportView.setState(discoverInfo.isLike());

//        IconCountView starView = holder.getView(R.id.like_view);
//        starView.setCount(0);

        holder.setOnClickListener(R.id.comment_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeDetailFragment.start(discoverInfo, true);
            }
        });

    }

    private String getComment2(DiscoverInfo info) {
        String nickName = info.getNickName();
        String toNickName = info.getToNickName();
        String model = "[%s](%s)";
        String comment = String.format(model, nickName, info.getMemberId());

        if (!TextUtils.isEmpty(toNickName)) {
            comment += (" 回复 " + String.format(model, toNickName, info.getToMemberId()));
        }
        comment += ("：" + info.getContent());
        return comment;
    }

    @Override
    public void onLinkClickListener(LinkType type, String content, String selfContent) {
        if (type == LinkType.LINK_TYPE) {
            WebFragment.start(content);
        } else if (type == LinkType.MENTION_TYPE) {
            ProfileFragment.start(content.replace("@", "").trim());
        } else if (type == LinkType.TOPIC_TYPE) {
            TopicThemeListFragment.start(content.replaceAll("#", "").trim());
        } else if (type == LinkType.SELF) {
            ProfileFragment.start(content);
        }
    }

    public static class NineGridCallback extends NineGridView.SimpleCallback {

        private final NineGridView nineGridView;
        private final DiscoverInfo discoverInfo;

        public NineGridCallback(NineGridView nineGridView, DiscoverInfo discoverInfo) {
            this.nineGridView = nineGridView;
            this.discoverInfo = discoverInfo;
        }

        @Override
        public void onImageItemClicked(int position, List<String> urls) {
            new CommonImageViewerDialogFragment()
                    .setOriginalImageList(discoverInfo.getPics())
                    .setImageSizeList(discoverInfo.getPicSizes())
                    .setSrcView(nineGridView.getImageView(position), position)
                    .setImageUrls(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(nineGridView.getContext()) ? discoverInfo.getPics() : discoverInfo.getSpics())
                    .setSrcViewUpdateListener(new ImageViewerDialogFragment.OnSrcViewUpdateListener<String>() {
                        @Override
                        public void onSrcViewUpdate(@NonNull ImageViewerDialogFragment<String> popup, int position) {
                            popup.updateSrcView(nineGridView.getImageView(position));
                        }
                    })
                    .show(nineGridView.getContext());
        }

    }

}
