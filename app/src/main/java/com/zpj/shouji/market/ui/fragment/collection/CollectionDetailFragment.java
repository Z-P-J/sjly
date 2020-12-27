package com.zpj.shouji.market.ui.fragment.collection;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.transformations.CircleWithBorderTransformation;
import com.zpj.shouji.market.glide.transformations.blur.BlurTransformation;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.StateSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommentDialogFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.DrawableTintTextView;
import com.zpj.shouji.market.ui.widget.emoji.EmojiExpandableTextView;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.toast.ZToast;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;

public class CollectionDetailFragment extends StateSwipeBackFragment
        implements View.OnClickListener {

    private final String[] TAB_TITLES = {"应用", "评论"};

    private CollapsingToolbarLayout toolbarLayout;
//    private StateLayout stateLayout;
    //    private ImageView ivHeader;
    private ImageView ivIcon;
    private ImageView ivAvatar;
    private TextView tvTitle;
    private TextView tvUserName;
    private TextView tvTime;
    private EmojiExpandableTextView tvDesc;
    private DrawableTintTextView tvSupport;
    private DrawableTintTextView tvFavorite;
    private DrawableTintTextView tvView;
    private DrawableTintTextView tvDownload;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private View buttonBarLayout;
    private ImageView ivToolbarAvater;

    private FloatingActionButton fabComment;

//    private CommentPopup commentPopup;

    private CommentDialogFragment commentDialogFragment;

    private String backgroundUrl;
    private String time;
    private String userAvatarUrl;
    private boolean isFav;
    private boolean isLike;

    private CollectionInfo item;

    public static void start(CollectionInfo item) {
        Bundle args = new Bundle();
        CollectionDetailFragment fragment = new CollectionDetailFragment();
        fragment.setAppCollectionItem(item);
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collection_detail;
    }

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//    }

    @Override
    protected void initStatusBar() {
        if (isLazyInit()) {
            lightStatusBar();
        } else {
            super.initStatusBar();
        }
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (item == null) {
            pop();
            return;
        }

//        stateLayout = view.findViewById(R.id.state_layout);
//        stateLayout.showLoadingView();

        toolbarLayout = view.findViewById(R.id.collapsingToolbar);

        buttonBarLayout = toolbar.getCenterCustomView();
        buttonBarLayout.setAlpha(0);
        ivToolbarAvater = toolbar.findViewById(R.id.toolbar_avatar);
        TextView tvToolbarName = toolbar.findViewById(R.id.toolbar_name);

        View header = view.findViewById(R.id.layout_header);
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, i) -> {
            float alpha = (float) Math.abs(i) / appBarLayout1.getTotalScrollRange();
            alpha = Math.min(1f, alpha);
            buttonBarLayout.setAlpha(alpha);
            if (alpha >= 1f) {
                header.setAlpha(0f);
            } else {
                header.setAlpha(1f);
            }
//                int color = alphaColor(Color.WHITE, alpha);
//                toolbar.setBackgroundColor(color);
        });

//        ivHeader = view.findViewById(R.id.iv_header);


        ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle = view.findViewById(R.id.tv_title);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        ivAvatar.setOnClickListener(this);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserName.setOnClickListener(this);
        tvTime = view.findViewById(R.id.tv_time);
        tvDesc = view.findViewById(R.id.tv_desc);
        tvSupport = view.findViewById(R.id.tv_support);
        tvSupport.setOnClickListener(this);
        tvFavorite = view.findViewById(R.id.tv_favorite);
        tvFavorite.setTag(false);
        tvFavorite.setOnClickListener(this);
        tvView = view.findViewById(R.id.tv_view);
//        tvView.setOnClickListener(this);
        tvDownload = view.findViewById(R.id.tv_download);
        tvDownload.setOnClickListener(this);
//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        toolbar.setTitle(item.getTitle());

        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);

        //        TAB_TITLES[0] = TAB_TITLES[0] + "(" + item.getSize() + ")";
//        TAB_TITLES[1] = TAB_TITLES[1] + "(" + item.getReplyCount() + ")";

        setToolbarTitle(item.getTitle());

        tvTitle.setText(item.getTitle());
        tvToolbarName.setText(item.getTitle());
        tvUserName.setText(item.getNickName());
        tvDesc.setContent(item.getComment());
//        tvFavorite.setText(item.getFavCount() + "");
//        tvSupport.setText(item.getSupportCount() + "");
//        tvView.setText(item.getViewCount() + "");

        fabComment = view.findViewById(R.id.fab_comment);
        fabComment.setOnClickListener(this);


        getCollectionInfo();

    }

    private void setAppCollectionItem(CollectionInfo item) {
        this.item = item;
    }

    private void getCollectionInfo() {
        darkStatusBar();
        Log.d("getCollectionInfo", "start id=" + item.getId());
        HttpApi.get("http://tt.shouji.com.cn/androidv3/yyj_info_xml.jsp?reviewid=" + item.getId())
                .onSuccess(doc -> {
                    postOnEnterAnimationEnd(() -> {
                        Log.d("getCollectionInfo", "doc=" + doc.toString());
//                collectionInfo.collectionId = doc.selectFirst("yyjid").text();
                        isFav = "1".equals(doc.selectFirst("isfav").text());
                        isLike = "1".equals(doc.selectFirst("islike").text());
                        backgroundUrl = doc.selectFirst("memberBackGround").text();
                        time = doc.selectFirst("time").text();
                        tvTime.setText(time);
                        userAvatarUrl = doc.selectFirst("memberAvatar").text();
                        tvFavorite.setText(doc.selectFirst("favcount").text());
                        tvSupport.setText(doc.selectFirst("supportcount").text());
                        tvDownload.setText(doc.selectFirst("size").text());
//                    boolean isFav = "1".equals(doc.selectFirst("isfav").text());
//                    boolean isLike = "1".equals(doc.selectFirst("islike").text());
                        if (isFav) {
                            tvFavorite.setDrawableTintColor(Color.RED);
                            tvFavorite.setTag(true);
                        }
                        if (isLike) {
                            tvSupport.setDrawableTintColor(Color.RED);
                            tvSupport.setTag(true);
                        }
                        tvView.setText(doc.selectFirst("viewcount").text());
//                        RequestOptions options = new RequestOptions()
//                                .centerCrop()
//                                .transform(new CircleWithBorderTransformation(0.5f, Color.GRAY))
//                                .error(R.mipmap.ic_launcher)
//                                .placeholder(R.mipmap.ic_launcher);
                        Glide.with(context)
                                .load(backgroundUrl)
                                .apply(GlideRequestOptions.getDefaultIconOption())
                                .into(ivIcon);
                        Glide.with(context)
                                .load(userAvatarUrl)
                                .apply(
                                        GlideRequestOptions.with()
                                                .addTransformation(new CircleWithBorderTransformation(0.5f, Color.LTGRAY))
                                                .get()
                                                .error(R.mipmap.ic_launcher)
                                                .placeholder(R.mipmap.ic_launcher)
                                )
//                                .apply(options.clone().transform(new CircleWithBorderTransformation(0.5f, Color.GRAY)))
                                .into(ivAvatar);
                        Glide.with(context)
                                .load(backgroundUrl)
                                .apply(
                                        GlideRequestOptions.with()
                                                .addTransformation(new CircleWithBorderTransformation(0.5f, Color.LTGRAY))
                                                .get()
                                                .error(R.mipmap.ic_launcher)
                                                .placeholder(R.mipmap.ic_launcher)
                                )
//                                .apply(options.clone().transform(new CircleWithBorderTransformation(0.5f, Color.GRAY)))
                                .into(ivToolbarAvater);
//                    Glide.with(context).asBitmap().load(backgroundUrl).apply(options).into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            ivIcon.setImageBitmap(resource);
//                            Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
//                                Bitmap bitmap = Blurred.with(resource)
//                                        .percent(0.1f)
//                                        .scale(0.1f)
//                                        .blur();
//                                emitter.onNext(bitmap);
//                                emitter.onComplete();
//                            })
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .doOnNext(bitmap -> {
//                                        getColor(bitmap);
//                                    })
//                                    .subscribe();
//                        }
//                    });
//                    Glide.with(context).load(backgroundUrl)
//                            .apply(new RequestOptions()
//                                    .error(R.drawable.bg_member_default)
//                                    .placeholder(R.drawable.bg_member_default)
//                            )
//                            .apply(RequestOptions.bitmapTransform(new BlurTransformation2(0.2f, 0.3f)))
//                            .into(ivHeader);

                        Glide.with(context)
                                .asDrawable()
                                .load(backgroundUrl)
                                .apply(
                                        RequestOptions
                                                .bitmapTransform(new BlurTransformation(25, 4))
                                                .error(R.drawable.bg_member_default)
                                                .placeholder(R.drawable.bg_member_default)
                                )
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        toolbarLayout.setBackground(resource);
                                    }
                                });

                        ArrayList<Fragment> list = new ArrayList<>();
                        CollectionAppListFragment appListFragment = findChildFragment(CollectionAppListFragment.class);
                        if (appListFragment == null) {
                            appListFragment = CollectionAppListFragment.newInstance(item.getId());
                        }
                        CollectionCommentFragment commentFragment = findChildFragment(CollectionCommentFragment.class);
                        if (commentFragment == null) {
                            commentFragment = CollectionCommentFragment.newInstance(item.getId());
                        }
                        list.add(appListFragment);
                        list.add(commentFragment);


                        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);

                        viewPager.setAdapter(adapter);
                        viewPager.setOffscreenPageLimit(list.size());
                        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);

//                        stateLayout.showContentView();
                        postOnEnterAnimationEnd(() -> {
                            showContent();
                            lightStatusBar();
                        });
                    });
                })
                .onError(throwable -> {
//                    stateLayout.showErrorView(throwable.getMessage());
                    showError(throwable.getMessage());
                })
                .subscribe();
    }

//    public void getColor(Bitmap bitmap) {
//        Palette.from(bitmap)
//                .generate(palette -> {
//                    if (palette != null) {
//                        Palette.Swatch s = palette.getDominantSwatch();//独特的一种
//                        if (s != null) {
//                            post(() -> {
//                                boolean isDark = ColorUtils.calculateLuminance(s.getRgb()) <= 0.5;
//                                if (isDark) {
//                                    lightStatusBar();
//                                } else {
//                                    darkStatusBar();
//                                }
//                                int color = getResources().getColor(isDark ? R.color.white : R.color.color_text_major);
//                                tvTitle.setTextColor(color);
//                                tvUserName.setTextColor(color);
//                                tvDesc.setTextColor(color);
//                                tvFavorite.setTextColor(color);
//                                tvSupport.setTextColor(color);
//                                tvView.setTextColor(color);
//                                toolbar.setLightStyle(isDark);
//                            });
//
//                        }
//                    }
//                });
//    }

//    public static int alphaColor(int color, float alpha) {
//        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
//        int rgb = 0x00ffffff & color;
//        return a + rgb;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_comment:

                if (!UserManager.getInstance().isLogin()) {
                    ZToast.warning(R.string.text_msg_not_login);
                    LoginFragment.start();
                    return;
                }

                viewPager.setCurrentItem(1);
                fabComment.hide();

                if (commentDialogFragment == null) {
                    commentDialogFragment = CommentDialogFragment.with(
                            context,
                            item.getId(),
                            item.getNickName(),
                            item.getContentType(),
                            () -> {
                                commentDialogFragment = null;
                                EventBus.sendRefreshEvent();
                            });
                    commentDialogFragment.setOnDismissListener(() -> fabComment.show());
                }
//                else {
//                    _mActivity.showHideFragment(commentDialogFragment);
//                }
                commentDialogFragment.show(context);

//                if (commentPopup == null) {
//                    commentPopup = CommentPopup.with(
//                            context,
//                            item.getId(),
//                            item.getNickName(),
//                            item.getContentType(),
//                            () -> {
//                                commentPopup = null;
//                                RefreshEvent.postEvent();
//                            })
//                            .setDecorView(Objects.requireNonNull(getView()).findViewById(R.id.fl_container))
//                            .setOnDismissListener(() -> fabComment.show())
//                            .show();
//                }
//                commentPopup.show();
                break;
            case R.id.iv_avatar:
            case R.id.tv_user_name:
                ProfileFragment.start(item.getMemberId(), false);
                break;
            case R.id.tv_support:
                if (!UserManager.getInstance().isLogin()) {
                    ZToast.warning(R.string.text_msg_not_login);
                    LoginFragment.start();
                    return;
                }
                HttpApi.likeApi("discuss", item.getId())
                        .onSuccess(data -> {
                            String info = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
                                boolean isLike = "点赞成功".equals(info);
                                tvSupport.setDrawableTintColor(isLike ? Color.RED : Color.WHITE);
                                tvSupport.setText(data.selectFirst("flower").text());
                            } else {
                                ZToast.error(info);
                            }
                        })
                        .onError(throwable -> {
                            ZToast.error("点赞失败！" + throwable.getMessage());
                        })
                        .subscribe();
                break;
            case R.id.tv_favorite:
                if (!UserManager.getInstance().isLogin()) {
                    ZToast.warning(R.string.text_msg_not_login);
                    LoginFragment.start();
                    return;
                }
                if ((boolean) tvFavorite.getTag()) {
                    HttpApi.delFavCollectionApi(item.getId(), "discuss")
                            .onSuccess(data -> {
                                String info = data.selectFirst("info").text();
                                if ("success".equals(data.selectFirst("result").text())) {
                                    int count = Integer.parseInt(tvFavorite.getText().toString());
                                    tvFavorite.setDrawableTintColor(Color.WHITE);
                                    tvFavorite.setText(String.valueOf(count - 1));
                                    tvFavorite.setTag(false);
                                    ZToast.success("取消收藏成功！");
                                } else {
                                    ZToast.error(info);
                                }
                            })
                            .onError(throwable -> {
                                ZToast.error("取消收藏失败！" + throwable.getMessage());
                            })
                            .subscribe();
                } else {
                    HttpApi.addFavCollectionApi(item.getId(), "discuss")
                            .onSuccess(data -> {
                                String info = data.selectFirst("info").text();
                                if ("success".equals(data.selectFirst("result").text())) {
                                    int count = Integer.parseInt(tvFavorite.getText().toString());
                                    tvFavorite.setDrawableTintColor(Color.RED);
                                    tvFavorite.setText(String.valueOf(count + 1));
                                    tvFavorite.setTag(true);
                                    ZToast.success("收藏成功！");
                                } else {
                                    ZToast.error(info);
                                }
                            })
                            .onError(throwable -> {
                                ZToast.error("收藏失败！" + throwable.getMessage());
                            })
                            .subscribe();
                }
                break;
//            case R.id.tv_view:
//
//                break;
            case R.id.tv_download:
                ZToast.normal("TODO 应用集下载");
                break;
        }
    }

}
