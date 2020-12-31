package com.zpj.shouji.market.ui.fragment.wallpaper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.github.zagum.expandicon.ExpandIconView;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.fragmentation.dialog.imagetrans.ImageItemView;
import com.zpj.fragmentation.dialog.imagetrans.listener.SourceImageViewGet;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment3;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.glide.GlideUtils;
import com.zpj.shouji.market.glide.ImageViewDrawableTarget;
import com.zpj.shouji.market.model.WallpaperInfo;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.WallpaperViewerDialogFragment2;
import com.zpj.shouji.market.ui.fragment.dialog.WallpaperViewerDialogFragment3;
import com.zpj.shouji.market.ui.widget.emoji.EmojiExpandableTextView;
import com.zpj.toast.ZToast;
import com.zpj.utils.NetUtils;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class WallpaperListFragment extends NextUrlFragment<WallpaperInfo> {

    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/appv3/bizhi_list.jsp";

    private String id;
    private String tag;
    @IntRange(from = 0, to = 2)
    private int sortPosition = 0;

    public static WallpaperListFragment newInstance(WallpaperTag tag) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, tag.getId());
        args.putString(Keys.TAG, tag.getName());
        WallpaperListFragment fragment = new WallpaperListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultUrl = DEFAULT_URL;
        nextUrl = DEFAULT_URL;
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        id = arguments.getString(Keys.ID, "1");
        tag = arguments.getString(Keys.TAG, "全部");
        initNextUrl();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_wallpaper;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        return layoutManager;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<WallpaperInfo> recyclerLayout) {
        if (getHeaderLayout() > 0) {
            recyclerLayout.setHeaderView(getHeaderLayout(), holder -> holder.setOnItemClickListener((this::showSortPupWindow)));
        }
        recyclerLayout.setFooterView(LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false));
        recyclerLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
                }
            }
        });
        int dp4 = ScreenUtils.dp2pxInt(context, 4);
        recyclerLayout.getRecyclerView().setPadding(dp4, 0, dp4, 0);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<WallpaperInfo> list, int position, List<Object> payloads) {
        WallpaperInfo info = list.get(position);
        ImageView wallpaper = holder.getImageView(R.id.iv_wallpaper);
        wallpaper.setTag(position);
        Glide.with(context)
                .load(list.get(position).getSpic())
                .apply(GlideUtils.REQUEST_OPTIONS)
                .into(new ImageViewDrawableTarget(wallpaper) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();
                        float p = (float) height / width;
                        if (p > 2.5f) {
                            p = 2.5f;
                        }
                        height = (int) (wallpaper.getMeasuredWidth() * p);
                        ViewGroup.LayoutParams layoutParams = wallpaper.getLayoutParams();
                        layoutParams.height = height;
                        super.onResourceReady(resource, transition);
                    }
                });

        Glide.with(context).load(info.getMemberIcon())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.getImageView(R.id.iv_icon));
        EmojiExpandableTextView tvContent = holder.getView(R.id.tv_content);
        tvContent.setContent(info.getContent());
        holder.getTextView(R.id.tv_name).setText(info.getNickName());
        IconCountView countView = holder.getView(R.id.support_view);
        countView.setCount(info.getSupportCount());
        countView.setState(info.isLike());
        countView.setOnStateChangedListener(new IconCountView.OnSelectedStateChangedListener() {
            @Override
            public void select(boolean isSelected) {
                HttpApi.likeApi("wallpaper", info.getId())
                        .onSuccess(data -> {
                            String result = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
//                                ZToast.success(result);
                                info.setSupportCount(info.getSupportCount() + (isSelected ? 1 : -1));
                                info.setLike(isSelected);
                            } else {
                                ZToast.error(result);
                                countView.setState(!isSelected);
                            }
                        })
                        .onError(throwable -> {
                            ZToast.error("点赞失败！" + throwable.getMessage());
                            countView.setState(!isSelected);
                        })
                        .subscribe();
            }
        });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, WallpaperInfo data) {
        ImageView wallpaper = holder.getImageView(R.id.iv_wallpaper);

        List<String> objects = new ArrayList<>();
        objects.add(data.getSpic());
        List<String> original = new ArrayList<>();
        original.add(data.getPic());

        Log.d("WallpaperListFragment", " width / height = " + (Float.parseFloat(data.getWidth()) / Float.parseFloat(data.getHeight())));
        Log.d("WallpaperListFragment2", " width / height = " + (wallpaper.getWidth() / wallpaper.getHeight()));
//        new WallpaperViewerDialogFragment2()
//                .setWallpaperInfo(data)
//                .setOriginalImageList(original)
//                .setImageList(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(context) ? original : objects)
//                .setNowIndex(0)
//                .setSourceImageView(new SourceImageViewGet<String>() {
//                    @Override
//                    public void updateImageView(ImageItemView<String> imageItemView, int pos, boolean isCurrent) {
//                        imageItemView.update(wallpaper);
//                    }
//                })
////                .setOnDismissListener(() -> StatusBarEvent.post(false))
//                .show(context);
        new WallpaperViewerDialogFragment3()
                .setWallpaperInfo(data)
                .setOriginalImageList(original)
                .setImageUrls(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(context) ? original : objects)
                .setSrcView(wallpaper, 0)
                .setSrcViewUpdateListener(new ImageViewerDialogFragment3.OnSrcViewUpdateListener<String>() {
                    @Override
                    public void onSrcViewUpdate(@NonNull ImageViewerDialogFragment3<String> popup, int position) {
                        popup.updateSrcView(wallpaper);
                    }
                })
//                .setImageList(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(context) ? original : objects)
//                .setNowIndex(0)
//                .setSourceImageView(new SourceImageViewGet<String>() {
//                    @Override
//                    public void updateImageView(ImageItemView<String> imageItemView, int pos, boolean isCurrent) {
//                        imageItemView.update(wallpaper);
//                    }
//                })
                .show(context);
    }

    @Override
    public WallpaperInfo createData(Element element) {
        if ("wallpaper".equals(element.selectFirst("type").text())) {
            return WallpaperInfo.create(element);
        }
        return null;
    }

    @Override
    public void onSuccess(Document doc) throws Exception {
        Log.d("getData", "doc=" + doc);
        nextUrl = doc.selectFirst("nextUrl").text();
        if (refresh) {
            data.clear();
        }
        int start = data.size();
        for (Element element : doc.select("item")) {
            WallpaperInfo item = createData(element);
            if (item == null) {
                continue;
            }
            data.add(item);
        }
        int end = data.size();
        if (data.size() == 0) {
            recyclerLayout.showEmpty();
        } else {
            recyclerLayout.showContent();
        }
        if (start < end) {
            // 这里加1是因为我们给RecyclerView添加了一个header View
            recyclerLayout.notifyItemRangeChanged(start + 1, end - start);
        }
        refresh = false;
    }

    @Override
    public void onRefresh() {
//        data.clear();
        initNextUrl();
//        refresh = true;
//        recyclerLayout.notifyDataSetChanged();

        if (data.isEmpty()) {
//            refresh = false;
            recyclerLayout.showLoading();
//            recyclerLayout.showEmpty();
        } else {
//            refresh = true;
//            getData();
        }
        refresh = true;
        getData();
    }

    protected int getHeaderLayout() {
        return R.layout.item_header_wallpaper;
    }

    private void initNextUrl() {
        nextUrl = DEFAULT_URL;
        if (!TextUtils.isEmpty(tag) && !"全部".equals(tag)) {
            nextUrl = nextUrl + "?tag=" + tag;
        }
        nextUrl += nextUrl.equals(DEFAULT_URL) ? "?" : "&";
        if (sortPosition == 1) {
            nextUrl = nextUrl + "sort=time";
        } else if (sortPosition == 2) {
            nextUrl = nextUrl + "sort=user";
        }
    }

    private void showSortPupWindow(View v) {
        ExpandIconView expandIconView = v.findViewById(R.id.expand_icon);

        expandIconView.switchState();
        new RecyclerPartShadowDialogFragment()
                .addItems("默认排序", "时间排序", "人气排序")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    TextView titleText = v.findViewById(R.id.tv_title);
                    titleText.setText(title);
                    onRefresh();
                })
                .setAttachView(v)
                .setOnDismissListener(expandIconView::switchState)
                .show(context);
    }
}
