package com.zpj.shouji.market.ui.fragment.main.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.WallpaperInfo;
import com.zpj.shouji.market.image.MyImageLoad;
import com.zpj.shouji.market.image.MyImageTransAdapter;
import com.zpj.shouji.market.image.MyProgressBarGet;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.ConnectUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.utils.ClickHelper;
import com.zpj.utils.ScreenUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.liuting.imagetrans.ImageTrans;
import it.liuting.imagetrans.listener.SourceImageViewGet;

public class WallpaperFragment extends BaseFragment implements IEasy.OnBindViewHolderCallback<WallpaperInfo>, IEasy.OnLoadMoreListener {

    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/bizhi_list.jsp?versioncode=198";
    private String nextUrl = DEFAULT_URL;

    private List<WallpaperInfo> wallpaperInfoList = new ArrayList<>();
    private EasyRecyclerLayout<WallpaperInfo> recyclerLayout;
    private int screenWidth;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wallpaper;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
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
                .setOnRefreshListener(() -> {
                    wallpaperInfoList.clear();
                    nextUrl = DEFAULT_URL;
                    recyclerLayout.notifyDataSetChanged();
                })
                .setHeaderView(R.layout.item_recommend_header, new IEasy.OnCreateHeaderCallback() {
                    @Override
                    public void onCreateHeaderView(View view) {

                    }
                })
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
                .override(layoutParams.width, layoutParams.height);
        wallpaper.setLayoutParams(layoutParams);
        Glide.with(context).load(list.get(position).getSpic()).apply(options).into(wallpaper);
        holder.setOnItemClickListener((v, x, y) -> {
            List<String> imageList = new ArrayList<>();
            imageList.add(info.getPic());
            ImageTrans.with(context)
                    .setImageList(imageList)
                    .setNowIndex(0)
                    .setSourceImageView(pos -> wallpaper)
                    .setProgressBar(new MyProgressBarGet())
                    .setImageLoad(new MyImageLoad())
                    .setAdapter(new MyImageTransAdapter())
                    .show();
        });
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        Log.d("onLoadMore", "onLoadMore");
        loadData();
        return true;
    }

    private void loadData() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = ConnectUtil.getDocument(nextUrl);
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
}
