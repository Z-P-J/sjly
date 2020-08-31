package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.PickedGameInfo;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyCollectionFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;

import java.util.List;

public class PickedGameFragment extends NextUrlFragment<PickedGameInfo> {

    public static void start() {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.tljpxm.com/androidv3/app_index_xml.jsp?index=1");
        PickedGameFragment fragment = new PickedGameFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
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

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<PickedGameInfo> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout.addItemDecoration(new Y_DividerItemDecoration(context) {
            @Override
            public Y_Divider getDivider(int itemPosition) {
                Y_DividerBuilder builder = null;
                if (itemPosition == 0) {
                    builder = new Y_DividerBuilder()
                            .setTopSideLine(true, Color.WHITE, 12, 0, 0)
                            .setBottomSideLine(true, Color.WHITE, 4, 0, 0);
                } else if (itemPosition == data.size() - 1) {
                    builder = new Y_DividerBuilder()
//                            .setTopSideLine(true, Color.WHITE, 4, 0, 0)
                            .setBottomSideLine(true, Color.WHITE, 12, 0, 0);
                } else {
                    builder = new Y_DividerBuilder()
                            .setTopSideLine(true, Color.WHITE, 4, 0, 0)
                            .setBottomSideLine(true, Color.WHITE, 4, 0, 0);
                }
                return builder
                        .setLeftSideLine(true, Color.WHITE, 12, 0, 0)
                        .setRightSideLine(true, Color.WHITE, 12, 0, 0)
                        .create();
            }
        });
    }

    @Override
    public PickedGameInfo createData(Element element) {
        return PickedGameInfo.from(element);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<PickedGameInfo> list, int position, List<Object> payloads) {
        PickedGameInfo info = list.get(position);
        Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_bg));
        Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
        holder.setText(R.id.tv_name, info.getAppName());
        holder.setText(R.id.tv_size, info.getAppSize());
        holder.setText(R.id.tv_content, info.getComment());
        holder.setText(R.id.tv_info,  String.format("%s浏览 | %s评论 | %s收藏", info.getViewCount(), info.getReviewCount(), info.getFavCount()));
        Glide.with(context).load(info.getMemberAvatar()).into(holder.getImageView(R.id.iv_avatar));
        holder.setText(R.id.tv_editor, info.getNickname() + " 推荐");
        holder.setText(R.id.tv_time, info.getTime());
        holder.setOnClickListener(R.id.ll_editor_info, v -> {
            ProfileFragment.start(info.getNickname());
        });
        holder.setOnClickListener(R.id.tv_download, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AToast.normal("TODO");
            }
        });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, PickedGameInfo data) {
        AppDetailFragment.start(data.getAppType(), data.getAppId());
    }
}
