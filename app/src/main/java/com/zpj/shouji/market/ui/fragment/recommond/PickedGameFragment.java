package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.PickedGameInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class PickedGameFragment extends NextUrlFragment<PickedGameInfo> {

    public static void start() {
        Bundle args = new Bundle();
//        http://tt.tljpxm.com
        args.putString(Keys.DEFAULT_URL, "/androidv3/app_index_xml.jsp?index=1");
        PickedGameFragment fragment = new PickedGameFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_picked_game;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("精选游戏");
    }

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        ThemeUtils.initStatusBar(this);
//    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<PickedGameInfo> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout.addItemDecoration(new Y_DividerItemDecoration(context) {
            @Override
            public Y_Divider getDivider(int itemPosition) {
                Y_DividerBuilder builder = null;
                int color = Color.TRANSPARENT;
                if (itemPosition == 0) {
                    builder = new Y_DividerBuilder()
                            .setTopSideLine(true, color, 12, 0, 0)
                            .setBottomSideLine(true, color, 4, 0, 0);
                } else if (itemPosition == data.size() - 1) {
                    builder = new Y_DividerBuilder()
//                            .setTopSideLine(true, Color.WHITE, 4, 0, 0)
                            .setBottomSideLine(true, color, 12, 0, 0);
                } else {
                    builder = new Y_DividerBuilder()
                            .setTopSideLine(true, color, 4, 0, 0)
                            .setBottomSideLine(true, color, 4, 0, 0);
                }
                return builder
                        .setLeftSideLine(true, color, 12, 0, 0)
                        .setRightSideLine(true, color, 12, 0, 0)
                        .create();
            }
        });
        recyclerLayout.getRecyclerView()
                .addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(@NonNull View view) {

                    }

                    @Override
                    public void onChildViewDetachedFromWindow(@NonNull View view) {
//                        DownloadButton tvDownload = view.findViewById(R.id.tv_download);
//                        tvDownload.onChildViewDetachedFromWindow();
                    }
                });
    }

    @Override
    public PickedGameInfo createData(Element element) {
        PickedGameInfo info = BeanUtils.createBean(element, PickedGameInfo.class);
        info.init();
        return info;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<PickedGameInfo> list, int position, List<Object> payloads) {
        PickedGameInfo info = list.get(position);
        Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_bg));
        Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
        holder.setText(R.id.tv_name, info.getAppName());
        holder.setText(R.id.tv_size, info.getAppSize());
        holder.setText(R.id.tv_content, info.getComment());
        holder.setText(R.id.tv_info, String.format("%s浏览 | %s评论 | %s收藏", info.getViewCount(), info.getReviewCount(), info.getFavCount()));
        Glide.with(context).load(info.getMemberAvatar()).into(holder.getImageView(R.id.iv_avatar));
        holder.setText(R.id.tv_editor, info.getNickname() + " 推荐");
        holder.setText(R.id.tv_time, info.getTime());
        holder.setOnClickListener(R.id.ll_editor_info, v -> {
            ProfileFragment.start(info.getNickname());
        });
        DownloadButton tvDownload = holder.getView(R.id.tv_download);
        tvDownload.bindApp(info);
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, PickedGameInfo data) {
        AppDetailFragment.start(data.getAppType(), data.getAppId());
    }
}
