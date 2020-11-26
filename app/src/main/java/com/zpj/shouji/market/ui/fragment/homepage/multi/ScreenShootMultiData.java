package com.zpj.shouji.market.ui.fragment.homepage.multi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.fragmentation.dialog.imagetrans.ImageItemView;
import com.zpj.fragmentation.dialog.imagetrans.listener.SourceImageViewGet;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.recyclerview.MultiData;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailInfoFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommonImageViewerDialogFragment2;
import com.zpj.shouji.market.ui.fragment.subject.SubjectDetailFragment;
import com.zpj.shouji.market.ui.fragment.subject.SubjectRecommendListFragment;
import com.zpj.shouji.market.ui.widget.ZViewPager;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.utils.ScreenUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ScreenShootMultiData extends RecyclerMultiData<String> {

    private List<String> urls;

    public ScreenShootMultiData(String title, List<String> urls) {
        super(title);
        this.urls = urls;
        list.addAll(urls);
        try {
            hasMore = false;
            Field isLoaded = MultiData.class.getDeclaredField("isLoaded");
            isLoaded.setAccessible(true);
            isLoaded.set(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getHeaderSpanCount() {
        return 3;
    }

    @Override
    public int getChildSpanCount(int viewType) {
        return 3;
    }

    @Override
    public boolean loadData(final MultiAdapter adapter) {
//        list.addAll(urls);
//        adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public int getItemRes() {
        return R.layout.item_image;
    }

    @Override
    public void buildRecyclerView(EasyRecyclerView<String> recyclerView) {
        Context context = recyclerView.getRecyclerView().getContext();
        float screenHeight = ScreenUtils.getScreenHeight(context);
        float screenWidth = ScreenUtils.getScreenWidth(context);
        float ratio = screenHeight / screenWidth;

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView
                .setLayoutManager(layoutManager)
                .addItemDecoration(new DividerItemDecoration(context, urls.size()))
                .onBindViewHolder((holder, list, position, payloads) -> {
                    ImageView ivImg = holder.getView(R.id.iv_img);
                    ivImg.setTag(position);
                    Glide.with(ivImg)
                            .load(list.get(position))
                            .apply(GlideRequestOptions.with()
                                    .roundedCorners(8)
                                    .get()
                                    .placeholder(R.drawable.bga_pp_ic_holder_light)
                                    .error(R.drawable.bga_pp_ic_holder_light))
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    int width = resource.getIntrinsicWidth();
                                    int height = resource.getIntrinsicHeight();

                                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ivImg.getLayoutParams();
                                    params.height = (int) (screenWidth / 2f);

                                    if (width > height) {
                                        params.width = (int) (params.height * ratio);
                                    } else {
                                        params.width = (int) (params.height / ratio);
                                    }
                                    ivImg.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadStarted(@Nullable Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ivImg.getLayoutParams();
                                    params.height = (int) (screenWidth / 2f);
                                    params.width = (int) (params.height / ratio);
                                    ivImg.setImageDrawable(placeholder);
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    this.onLoadStarted(errorDrawable);
                                }
                            });

                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageViewer(ivImg, position);
                        }
                    });
                });
    }

    @Override
    public void onHeaderClick() {

    }

    private void showImageViewer(ImageView ivImg, int position) {
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
                            imageView = ivImg;
                        }
                        imageItemView.update(imageView);
                        ivImg.postDelayed(() -> {
                            ImageView imageView2 = recyclerView.getRecyclerView().findViewWithTag(pos);
                            if (imageView2 == null) {
                                imageView2 = ivImg;
                            }
                            imageItemView.update(imageView2);
                        }, 150);
                    }
                })
                .show(ivImg.getContext());
    }

    private static class DividerItemDecoration extends Y_DividerItemDecoration {

        private final int total;
        private final int color = Color.TRANSPARENT; // Color.WHITE

        private DividerItemDecoration(Context context, int total) {
            super(context);
            this.total = total;
        }

        @Override
        public Y_Divider getDivider(int itemPosition) {
            Y_DividerBuilder builder = null;
            if (itemPosition == 0) {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, color, 16, 0, 0)
                        .setRightSideLine(true, color, 4, 0, 0);
            } else if (itemPosition == total - 1) {
                builder = new Y_DividerBuilder()
                        .setRightSideLine(true, color, 16, 0, 0)
                        .setLeftSideLine(true, color, 4, 0, 0);
            } else {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, color, 4, 0, 0)
                        .setRightSideLine(true, color, 4, 0, 0);
            }
            return builder.setTopSideLine(true, color, 4, 0, 0)
                    .setBottomSideLine(true, color, 4, 0, 0)
                    .create();
        }
    }

}
