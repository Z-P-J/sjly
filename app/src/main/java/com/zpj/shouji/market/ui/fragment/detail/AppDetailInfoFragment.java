package com.zpj.shouji.market.ui.fragment.detail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.ctetin.expandabletextviewlibrary.app.LinkType;
import com.felix.atoast.library.AToast;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.dialog.imagetrans.ImageItemView;
import com.zpj.fragmentation.dialog.imagetrans.listener.SourceImageViewGet;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommonImageViewerDialogFragment2;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class AppDetailInfoFragment extends BaseFragment
        implements IEasy.OnBindViewHolderListener<String>,
        ExpandableTextView.OnLinkClickListener {

    private EasyRecyclerView<String> recyclerView;
    private LinearLayout content;

    private float screenWidth;
    private float screenHeight;
    private float ratio;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail_info;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSupportInvisible() {
        getSupportDelegate().onSupportInvisible();
    }

    @Override
    public void onSupportVisible() {
        getSupportDelegate().onSupportVisible();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        this.screenHeight = ScreenUtils.getScreenHeight(context);
        this.screenWidth = ScreenUtils.getScreenWidth(context);
        this.ratio = screenHeight / screenWidth;
        content = view.findViewById(R.id.content);
        recyclerView = new EasyRecyclerView<>(view.findViewById(R.id.recycler_view));
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setItemViewCacheSize(100);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
        ImageView img = holder.getView(R.id.iv_img);

        Glide.with(context)
                .load(list.get(position))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.bga_pp_ic_holder_light)
                        .error(R.drawable.bga_pp_ic_holder_light))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) img.getLayoutParams();
                        params.height = (int) (screenWidth / 2f);

                        if (width > height) {
                            params.width = (int) (params.height * ratio);
                        } else {
                            params.width = (int) (params.height / ratio);
                        }
                        img.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) img.getLayoutParams();
                        params.height = (int) (screenWidth / 2f);
                        params.width = (int) (params.height / ratio);
                        img.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        this.onLoadStarted(errorDrawable);
                    }
                });
        img.setTag(position);
        img.setOnClickListener(v -> {

            new CommonImageViewerDialogFragment2()
                    .setImageList(list)
                    .setNowIndex(position)
                    .setSourceImageView(new SourceImageViewGet<String>() {
                        private boolean flag = true;
                        @Override
                        public void updateImageView(ImageItemView<String> imageItemView, int pos, boolean isCurrent) {
                            if (flag) {
                                flag = false;
                            } else if (isCurrent){
                                recyclerView.getRecyclerView().scrollToPosition(pos);
                            }
                            Log.d("updateImageView", "updateImageViewupdateImageView");

                            ImageView imageView = recyclerView.getRecyclerView().findViewWithTag(pos);
                            if (imageView == null) {
                                imageView = img;
                            }
                            imageItemView.update(imageView);
                            postDelayed(() -> {
//                                int layoutPos = recyclerView.getRecyclerView().indexOfChild(holder.getItemView());
//                                View view = recyclerView.getRecyclerView().getChildAt(layoutPos + pos - position);
//                                ImageView imageView;
//                                if (view != null) {
//                                    imageView = view.findViewById(R.id.iv_img);
//                                } else {
//                                    imageView = img;
//                                }
                                ImageView imageView2 = recyclerView.getRecyclerView().findViewWithTag(pos);
                                if (imageView2 == null) {
                                    imageView2 = img;
                                }
                                imageItemView.update(imageView2);
                            }, 150);
                        }
                    })
                    .show(context);

//            new CommonImageViewerDialogFragment()
//                    .setImageUrls(list)
//                    .setSrcView(img, position)
//                    .setSrcViewUpdateListener(new ImageViewerDialogFragment.OnSrcViewUpdateListener<String>() {
//                        private boolean flag = true;
//                        @Override
//                        public void onSrcViewUpdate(@NonNull ImageViewerDialogFragment<String> popup, int pos) {
//                            if (flag) {
//                                flag = false;
//                            } else {
//                                recyclerView.getRecyclerView().scrollToPosition(pos);
//                            }
//
//                            postDelayed(() -> {
////                                int layoutPos = recyclerView.getRecyclerView().indexOfChild(holder.getItemView());
////                                View view = recyclerView.getRecyclerView().getChildAt(layoutPos + pos - position);
////                                ImageView imageView;
////                                if (view != null) {
////                                    imageView = view.findViewById(R.id.iv_img);
////                                } else {
////                                    imageView = img;
////                                }
//                                ImageView imageView = recyclerView.getRecyclerView().findViewWithTag(pos);
//                                if (imageView == null) {
//                                    imageView = img;
//                                }
//                                popup.updateSrcView(imageView, pos);
//                            }, 100);
//                        }
//                    })
//                    .show(context);

//            CommonImageViewerPopup.with(context)
//                    .setImageUrls(list)
//                    .setSrcView(img, position)
//                    .setSrcViewUpdateListener(new ImageViewerPopup.OnSrcViewUpdateListener<String>() {
//                        private boolean flag = true;
//                        @Override
//                        public void onSrcViewUpdate(@NonNull ImageViewerPopup<String> popup, int pos) {
//                            if (flag) {
//                                flag = false;
//                            } else {
//                                recyclerView.getRecyclerView().scrollToPosition(pos);
//                            }
//
//                            postDelayed(() -> {
////                                int layoutPos = recyclerView.getRecyclerView().indexOfChild(holder.getItemView());
////                                View view = recyclerView.getRecyclerView().getChildAt(layoutPos + pos - position);
////                                ImageView imageView;
////                                if (view != null) {
////                                    imageView = view.findViewById(R.id.iv_img);
////                                } else {
////                                    imageView = img;
////                                }
//                                ImageView imageView = recyclerView.getRecyclerView().findViewWithTag(pos);
//                                if (imageView == null) {
//                                    imageView = img;
//                                }
//                                popup.updateSrcView(imageView, pos);
//                            }, 100);
//                        }
//                    })
//                    .show();
        });
    }

    @Subscribe
    public void onGetAppDetailInfo(AppDetailInfo info) {
        EventBus.getDefault().unregister(this);
        Log.d("AppDetailInfoFragment", "onGetAppDetailInfo info=" + info);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setItemRes(R.layout.item_image)
                .setData(info.getImgUrlList())
                .addItemDecoration(new DividerItemDecoration(context, info.getImgUrlList().size()))
                .setLayoutManager(layoutManager)
                .onBindViewHolder(this)
                .build();
        recyclerView.notifyDataSetChanged();
        addItem("小编评论", info.getEditorComment(), 0);
        addItem("特别说明", info.getSpecialStatement(), 0);
        addItem("应用简介", info.getAppIntroduceContent());
        addItem("新版特性", info.getUpdateContent());
        addItem("详细信息", info.getAppInfo());
        addItem("权限信息", info.getPermissionContent());

        if (info.getOtherAppList() != null) {
            View view = getLayoutInflater().inflate(R.layout.item_app_info_other, null, false);
            content.addView(view);
            view.findViewById(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolBarAppListFragment.start(info.getOtherAppUrl(), "开发者其他应用");
                }
            });
            EasyRecyclerView<AppInfo> recyclerView = new EasyRecyclerView<>(view.findViewById(R.id.recycler_view));
            recyclerView.setData(info.getOtherAppList())
                    .setItemRes(R.layout.item_app_grid)
                    .setLayoutManager(new GridLayoutManager(context, 3) {
                        @Override
                        public boolean canScrollHorizontally() {
                            return false;
                        }

                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    })
                    .onBindViewHolder((holder, list, position, payloads) -> {
                        AppInfo info1 = list.get(position);
                        holder.getTextView(R.id.item_title).setText(info1.getAppTitle());
                        holder.getTextView(R.id.item_info).setText(info1.getAppSize());
                        Glide.with(context).load(info1.getAppIcon()).into(holder.getImageView(R.id.item_icon));
                        holder.getView(R.id.tv_download).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AToast.normal("TODO Download");
                            }
                        });
                    })
                    .onItemClick((holder, view1, data) -> AppDetailFragment.start(data))
                    .build();
        }
    }

    private void addItem(String title, String text) {
//        if (TextUtils.isEmpty(text)) {
//            return;
//        }
//        View view = getLayoutInflater().inflate(R.layout.item_app_info_text, null, false);
//        content.addView(view);
//        TextView tvTitle = view.findViewById(R.id.tv_title);
//        TextView tvContent = view.findViewById(R.id.tv_content);
//        tvTitle.setText(title);
//        tvContent.setText(text);

        addItem(title, text, content.getChildCount());
    }

    private void addItem(String title, String text, int index) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.item_app_info_text, null, false);
        content.addView(view, index);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        ExpandableTextView tvContent = view.findViewById(R.id.tv_content);
        tvContent.setLinkClickListener(this);
        tvTitle.setText(title);
        tvContent.setContent(text);
    }

    @Override
    public void onLinkClickListener(LinkType type, String content, String selfContent) {
        if (type == LinkType.MENTION_TYPE) {
            ProfileFragment.start(content.replace("@", "").trim());
        }
    }


    private static class DividerItemDecoration extends Y_DividerItemDecoration {

        private final int total;

        private DividerItemDecoration(Context context, int total) {
            super(context);
            this.total = total;
        }

        @Override
        public Y_Divider getDivider(int itemPosition) {
            Y_DividerBuilder builder = null;
            if (itemPosition == 0) {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, Color.WHITE, 16, 0, 0)
                        .setRightSideLine(true, Color.WHITE, 4, 0, 0);
            } else if (itemPosition == total - 1) {
                builder = new Y_DividerBuilder()
                        .setRightSideLine(true, Color.WHITE, 16, 0, 0)
                        .setLeftSideLine(true, Color.WHITE, 4, 0, 0);
            } else {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, Color.WHITE, 4, 0, 0)
                        .setRightSideLine(true, Color.WHITE, 4, 0, 0);
            }
            return builder.setTopSideLine(true, Color.WHITE, 4, 0, 0)
                    .setBottomSideLine(true, Color.WHITE, 4, 0, 0)
                    .create();
        }
    }

}
