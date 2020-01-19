package com.zpj.shouji.market.ui.fragment.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.ImageViewerPopupView;
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.WallpaperInfo;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.image.ImageLoader;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.widget.RecyclerPopup;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.utils.ScreenUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends BaseFragment implements IEasy.OnBindViewHolderListener<WallpaperInfo>, IEasy.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_ID = "id";
    private static final String KEY_TAG = "tag";
    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/bizhi_list.jsp?versioncode=198";
    private String nextUrl = DEFAULT_URL;

    private String id;
    private String tag;
    private int sortPosition = 0;

    private List<WallpaperInfo> wallpaperInfoList = new ArrayList<>();
    private EasyRecyclerLayout<WallpaperInfo> recyclerLayout;
    private int screenWidth;

    public static ImageFragment newInstance(WallpaperTag tag) {
        Bundle args = new Bundle();
        args.putString(KEY_ID, tag.getId());
        args.putString(KEY_TAG, tag.getName());
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            id = getArguments().getString(KEY_ID, "1");
            tag = getArguments().getString(KEY_TAG, "全部");
        }
        initNextUrl();
        recyclerLayout = view.findViewById(R.id.recycler_layout);
        screenWidth = ScreenUtil.getScreenWidth(context);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        recyclerLayout.setItemRes(R.layout.item_wallpaper)
                .setData(wallpaperInfoList)
                .setItemAnimator(null)
                .setEnableLoadMore(true)
                .setEnableSwipeRefresh(true)
                .setOnRefreshListener(this)
                .setHeaderView(R.layout.item_image_header, holder -> holder.setOnItemClickListener((v, x, y) -> showSortPupWindow(v)))
                .setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
                .onBindViewHolder(this)
                .onLoadMore(this)
                .build();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<WallpaperInfo> list, int position, List<Object> payloads) {
        WallpaperInfo info = list.get(position);
        ImageView wallpaper = holder.getImageView(R.id.iv_wallpaper);
        ViewGroup.LayoutParams layoutParams = wallpaper.getLayoutParams();
        float width = Float.parseFloat(info.getWidth());
        float height = Float.parseFloat(info.getHeight());
        layoutParams.width = screenWidth / 2;
        layoutParams.height = (int) (height / width * layoutParams.width);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.bga_pp_ic_holder_light)
                .error(R.drawable.bga_pp_ic_holder_light)
//                .override(layoutParams.width, layoutParams.height)
                .override(Target.SIZE_ORIGINAL);
        wallpaper.setLayoutParams(layoutParams);
        Glide.with(context)
                .load(list.get(position).getSpic())
                .apply(options).into(wallpaper);
        holder.setOnItemClickListener((v, x, y) -> {
            List<String> imageList = new ArrayList<>();
            imageList.add(info.getPic());
            List<Object> objects = new ArrayList<>(imageList);
            new XPopup.Builder(context)
                    .asImageViewer(wallpaper, 0, objects, new OnSrcViewUpdateListener() {
                        @Override
                        public void onSrcViewUpdate(ImageViewerPopupView popupView, int position) {
                            popupView.updateSrcView(wallpaper);
                        }
                    }, new ImageLoader())
                    .show();
        });

        Glide.with(context).load(info.getMemberIcon()).into(holder.getImageView(R.id.iv_icon));
        holder.getTextView(R.id.tv_content).setText(info.getContent());
        holder.getTextView(R.id.tv_name).setText(info.getNickName());
        IconCountView countView = holder.getView(R.id.support_view);
        countView.setCount(info.getSupportCount());
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        Log.d("onLoadMore", "onLoadMore");
        loadData();
        return true;
    }

    @Override
    public void onRefresh() {
        wallpaperInfoList.clear();
        initNextUrl();
        recyclerLayout.notifyDataSetChanged();
    }

    private void initNextUrl() {
        nextUrl = DEFAULT_URL;
        if (!TextUtils.isEmpty(tag) && !"全部".equals(tag)) {
            nextUrl = nextUrl + "&tag=" + tag;
        }
        if (sortPosition == 1) {
            nextUrl = nextUrl + "&sort=time";
        } else if (sortPosition == 2) {
            nextUrl = nextUrl + "&sort=user";
        }
    }

    private void loadData() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument(nextUrl);
                nextUrl = doc.selectFirst("nextUrl").text();
                Elements elements = doc.select("item");
                for (int i = 1; i < elements.size(); i++) {
                    wallpaperInfoList.add(WallpaperInfo.create(elements.get(i)));
                }
                Log.d("onLoadMore", "size=" + wallpaperInfoList.size());
                post(() -> recyclerLayout.notifyDataSetChanged());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showSortPupWindow(View v) {
        RecyclerPopup.with(context)
                .addItems("默认排序", "时间排序", "人气排序")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    TextView titleText = v.findViewById(R.id.tv_title);
                    titleText.setText(title);
                    onRefresh();
                })
                .show(v);
    }
}
