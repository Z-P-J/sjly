package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.ctetin.expandabletextviewlibrary.app.LinkType;
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
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.glide.GlideUtils;
import com.zpj.shouji.market.glide.blur.CropBlurTransformation;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.model.SupportUserInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.theme.TopicThemeListFragment;
import com.zpj.shouji.market.ui.widget.CombineImageView;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
import com.zpj.shouji.market.ui.widget.emoji.EmojiExpandableTextView;
import com.zpj.shouji.market.ui.widget.popup.CommonImageViewerPopup;
import com.zpj.shouji.market.ui.widget.popup.SupportUserListPopup;
import com.zpj.shouji.market.ui.widget.popup.ThemeAppDownloadPopup;
import com.zpj.shouji.market.ui.widget.popup.ThemeMorePopupMenu;
import com.zpj.shouji.market.utils.TextUrlUtil;
import com.zpj.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class DiscoverBinder
        implements IEasy.OnBindViewHolderListener<DiscoverInfo>,
        TextUrlUtil.OnClickString, ExpandableTextView.OnLinkClickListener {


    private static NineGridImageLoader imageLoader;

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
                .into(holder.getImageView(R.id.item_icon));


        TextView shareInfo = holder.getTextView(R.id.share_info);
        View appLayout = holder.getView(R.id.layout_app);


        holder.setVisible(R.id.collection_layout, false);
        EmojiExpandableTextView tvContent = holder.getView(R.id.tv_content);
        NineGridView nineGridImageView = holder.getView(R.id.nine_grid_image_view);
        if (!discoverInfo.getSpics().isEmpty()) {
            tvContent.setVisibility(View.VISIBLE);
            shareInfo.setText("分享乐图:");
            List<NineGridBean> gridList = new ArrayList<>();
            for (String url : discoverInfo.getSpics()) {
                gridList.add(new NineGridBean(url));
            }
            nineGridImageView.setVisibility(View.VISIBLE);
            nineGridImageView.setImageLoader(getImageLoader());
            nineGridImageView.setOnItemClickListener(new OnNineGridImageViewItemClickListener(nineGridImageView, discoverInfo));
            nineGridImageView.setDataList(gridList);
        } else if (!discoverInfo.getSharePics().isEmpty()) {
            tvContent.setVisibility(View.VISIBLE);
            shareInfo.setText("分享应用集:");
            nineGridImageView.setVisibility(View.GONE);

            holder.setVisible(R.id.collection_layout, true);
//            NineGridView gridImageView = holder.getView(R.id.grid_image_view);
//            gridImageView.setImageLoader(getImageLoader());
//            List<NineGridBean> gridList = new ArrayList<>();
//            for (String url : discoverInfo.getSharePics()) {
//                gridList.add(new NineGridBean(url));
//                if (gridList.size() >= 4) {
//                    break;
//                }
//            }
//            gridImageView.setDataList(gridList);

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
                tvDownload.setOnClickListener(v -> {
                    AToast.normal("TODO Download");
                    ThemeAppDownloadPopup.with(context)
                            .setId(discoverInfo.getAppId())
                            .setDiscoverInfo(discoverInfo)
                            .show();
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
                        SupportUserListPopup.with(context)
                                .setThemeId(discoverInfo.getId())
                                .show();
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
//                            AToast.normal("hhhhhhh");
                            holder.performClick();
                        }
                    });

                    if (i >= 3) {
                        container.setOnLongClickListener(v -> true);
                        int total = discoverInfo.getChildren().size();
                        if (total > 3) {
                            DrawableTintTextView textView = new DrawableTintTextView(context);
                            textView.setText("共" + total + "条回复");
                            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_black_24dp);
                            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
                            textView.setCompoundDrawablePadding(8);
                            textView.setGravity(Gravity.CENTER_VERTICAL);
                            int color = context.getResources().getColor(R.color.colorPrimary);
                            textView.setTextColor(color);
                            textView.setDrawableTintColor(color);
                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            container.addView(textView);
                        }
                        break;
                    }

                    container.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ThemeMorePopupMenu.with(context)
                                    .setDiscoverInfo(child)
                                    .show();
                            return true;
                        }
                    });

                    EmojiExpandableTextView textView = new EmojiExpandableTextView(context);

                    textView.setNeedExpend(false);
                    textView.setNeedConvertUrl(false);
                    textView.setTextColor(context.getResources().getColor(R.color.color_text_major));
                    textView.setLimitLines(8);
                    textView.setContent(getComment2(child));
                    textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
//                    textView.setLinkClickListener(this);

                    textView.setPadding(0, 0, 0, 8);
                    container.addView(textView);

                    if (!child.getSpics().isEmpty()) {
                        NineGridView nineGridView = new NineGridView(context);
                        nineGridView.setImageLoader(getImageLoader());
                        nineGridView.setOnItemClickListener(new OnNineGridImageViewItemClickListener(nineGridView, child));
                        List<NineGridBean> gridList = new ArrayList<>();
                        for (String url : child.getSpics()) {
                            gridList.add(new NineGridBean(url));
                        }
                        nineGridView.addDataList(gridList);
                        nineGridView.setPadding(0, 0, 0, 8);
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
                if (!UserManager.getInstance().isLogin()) {
                    supportView.setState(!isSelected);
                    AToast.warning(R.string.text_msg_not_login);
                    LoginFragment.start();
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

//        IconCountView starView = holder.getView(R.id.like_view);
//        starView.setCount(0);

//        holder.setOnClickListener(R.id.comment_view, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThemeDetailFragment.start(discoverInfo, true);
//            }
//        });

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

//    private SpannableString getComment(DiscoverInfo info) {
//        String nickName = info.getNickName();
//        String toNickName = info.getToNickName();
//        String content = info.getContent();
//        SpannableString sp;
//        if (TextUtils.isEmpty(toNickName)) {
//            sp = new SpannableString(nickName + "：" + content);
//        } else {
//            sp = new SpannableString(nickName + " 回复 " + toNickName
//                    + "：" + content);
//            sp.setSpan(
//                    new MyClickableSpan(info.getToMemberId()),
//                    nickName.length() + 4,
//                    nickName.length() + 4 + toNickName.length(),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            );
//        }
//        sp.setSpan(
//                new MyClickableSpan(info.getMemberId()),
//                0,
//                nickName.length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        );
//        return sp;
//    }

    @Override
    public void onLinkClick(String link) {
        WebFragment.start(link);
    }

    @Override
    public void onAtClick(String at) {
        AToast.success(at);
    }

    @Override
    public void onTopicClick(String topic) {
        AToast.success(topic);
    }

    @Override
    public void onViewClick(View view) {
//        if (view.getTag() instanceof ViewGroup) {
//            ((ViewGroup) view.getTag()).performClick();
//        }
    }

    public static INineGridImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = new NineGridImageLoader();
        }
        return imageLoader;
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

    public static class NineGridImageLoader implements INineGridImageLoader {

        @Override
        public void displayNineGridImage(Context context, String url, ImageView imageView) {
//            if (url.toLowerCase().endsWith(".gif")) {
//                Log.d("PopupImageLoader", "gif");
//                GlideApp.with(context)
//                        .asGif()
//                        .load(url)
//                        .apply(
////                                new RequestOptions()
////                                        .centerCrop()
////                                        .placeholder(R.drawable.bga_pp_ic_holder_light)
////                                        .error(R.drawable.bga_pp_ic_holder_light)
////                                        .override(Target.SIZE_ORIGINAL)
//                                REQUEST_OPTIONS
//
//                        )
//                        .into(imageView);
//            } else {
//                Log.d("PopupImageLoader", "png");
//                Glide.with(context)
//                        .load(url)
//                        .apply(
////                                new RequestOptions()
////                                        .centerCrop()
////                                        .placeholder(R.drawable.bga_pp_ic_holder_light)
////                                        .error(R.drawable.bga_pp_ic_holder_light)
////                                        .override(Target.SIZE_ORIGINAL)
////                                        .dontAnimate()
//                                REQUEST_OPTIONS
//                        ).into(imageView);
//            }

            Glide.with(context)
                    .load(url)
                    .apply(GlideUtils.REQUEST_OPTIONS)
//                    .transition(GlideUtils.DRAWABLE_TRANSITION_OPTIONS)
                    .into(imageView);
        }

        @Override
        public void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height) {
            displayNineGridImage(context, url, imageView);
        }

    }

    public static class OnNineGridImageViewItemClickListener implements NineGridView.onItemClickListener {

        private final NineGridView nineGridView;
        private final DiscoverInfo discoverInfo;

        public OnNineGridImageViewItemClickListener(NineGridView nineGridView, DiscoverInfo discoverInfo) {
            this.nineGridView = nineGridView;
            this.discoverInfo = discoverInfo;
        }

        @Override
        public void onNineGirdAddMoreClick(int dValue) {

        }

        @Override
        public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

            CommonImageViewerPopup.with(nineGridView.getContext())
                    .setOriginalImageList(discoverInfo.getPics())
                    .setImageSizeList(discoverInfo.getPicSizes())
                    .setImageUrls(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(nineGridView.getContext()) ? discoverInfo.getPics() : discoverInfo.getSpics())
                    .setSrcView(imageContainer.getImageView(), position)
                    .setSrcViewUpdateListener((popup, pos) -> {
                        NineGirdImageContainer view = (NineGirdImageContainer) nineGridView.getChildAt(pos);
                        popup.updateSrcView(view.getImageView());
                    })
                    .show();

//            TransferConfig config = TransferConfig.build()
//                    .setOnGetImageViewListener(new TransferConfig.OnGetImageViewListener() {
//                        @Override
//                        public ImageView onGetImageView(int pos) {
//                            NineGirdImageContainer view = (NineGirdImageContainer) nineGridView.getChildAt(pos);
//                            return view.getImageView();
//                        }
//                    })
//                    .setSourceUrlList(discoverInfo.getPics())
////                    .setMissPlaceHolder(R.mipmap.ic_empty_photo) // 资源加载前的占位图
////                    .setErrorPlaceHolder(R.mipmap.ic_empty_photo) // 资源加载错误后的占位图
//                    .setProgressIndicator(new ProgressPieIndicator()) // 资源加载进度指示器, 可以实现 IProgressIndicator 扩展
//                    .setIndexIndicator(new NumberIndexIndicator()) // 资源数量索引指示器，可以实现 IIndexIndicator 扩展
//                    .setImageLoader(GlideImageLoader.with(nineGridView.getContext())) // 图片加载器，可以实现 ImageLoader 扩展
//                    .setBackgroundColor(Color.parseColor("#000000")) // 背景色
//                    .setDuration(300) // 开启、关闭、手势拖拽关闭、显示、扩散消失等动画时长
//                    .setOffscreenPageLimit(3) // 第一次初始化或者切换页面时预加载资源的数量，与 justLoadHitImage 属性冲突，默认为 1
////                    .setCustomView(customView) // 自定义视图，将放在 transferee 的面板上
//                    .setNowThumbnailIndex(position) // 缩略图在图组中的索引
//                    .enableJustLoadHitPage(false) // 是否只加载当前显示在屏幕中的的资源，默认关闭
//                    .enableDragClose(true) // 是否开启下拉手势关闭，默认开启
//                    .enableDragHide(false) // 下拉拖拽关闭时，是否先隐藏页面上除主视图以外的其他视图，默认开启
//                    .enableDragPause(false) // 下拉拖拽关闭时，如果当前是视频，是否暂停播放，默认关闭
//                    .enableHideThumb(false) // 是否开启当 transferee 打开时，隐藏缩略图, 默认关闭
//                    .enableScrollingWithPageChange(false).create(); // 是否启动列表随着页面的切换而滚动你的列表，默认关闭
////                    .bindImageView(imageContainer.getImageView(), gridBean.getOriginUrl()); // 绑定一个 ImageView, 所有绑定方法只能调用一个
////                    .bindListView(listView, R.id.iv_thumb) // 绑定一个 ListView， 所有绑定方法只能调用一个
////                    .bindRecyclerView(recyclerView, R.id.iv_thumb)  // 绑定一个 RecyclerView， 所有绑定方法只能调用一个
//
//            Transferee transfer = Transferee.getDefault(nineGridView.getContext());
//            transfer.apply(config).show();
        }

        @Override
        public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

        }

    }

//    public static class MyClickableSpan extends ClickableSpan {
//
//        private final String memberId;
//
//        public MyClickableSpan(String memberId) {
//            this.memberId = memberId;
//        }
//
//        @Override
//        public void onClick(View widget) {
//            ProfileFragment.start(memberId, false);
//        }
//
//        @Override
//        public void updateDrawState(@NonNull TextPaint ds) {
//            super.updateDrawState(ds);
//            ds.setUnderlineText(false);
//        }
//    }

}
