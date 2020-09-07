package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.cb.ratingbar.CBRatingBar;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.FabEvent;
import com.zpj.shouji.market.event.RefreshEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.widget.popup.AppRatingPopup;
import com.zpj.shouji.market.utils.Callback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AppDetailCommentFragment extends ThemeListFragment {

    private AppDetailInfo info;

    private String userRatingValue;

//    public static AppDetailCommentFragment newInstance(String id, String type) {
//        AppDetailCommentFragment fragment = new AppDetailCommentFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/app/comment_index_xml_v5.jsp?type=" + type + "&id=" + id);
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    public static AppDetailCommentFragment newInstance(AppDetailInfo info) {
        AppDetailCommentFragment fragment = new AppDetailCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/app/comment_index_xml_v5.jsp?type=" + info.getAppType() + "&id=" + info.getId());
        fragment.setArguments(bundle);
        fragment.info = info;
        return fragment;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        recyclerLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mScrollThreshold = 0;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    FabEvent.post(dy < 0);
                }
            }
        });

        if (UserManager.getInstance().isLogin() && info.isScoreState()) {
            findDetailMemberInfo();
        }
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<DiscoverInfo> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout.setHeaderView(R.layout.item_app_rating_header, new IEasy.OnBindHeaderListener() {
            @Override
            public void onBindHeader(EasyViewHolder holder) {
                holder.setText(R.id.tv_score, info.getScoreInfo());
                holder.setText(R.id.tv_info, info.getRatingValue());
                CBRatingBar ratingBar = holder.getView(R.id.rating_bar);

                float score = Float.parseFloat(info.getScoreInfo());
                ratingBar.setStarProgress(score * 20);

                RelativeLayout rlMyScore = holder.getView(R.id.rl_my_score);
                if (UserManager.getInstance().isLogin()) {
                    CBRatingBar myRating = holder.getView(R.id.my_rating);

                    rlMyScore.setVisibility(View.VISIBLE);

                    Glide.with(context)
                            .load(UserManager.getInstance().getMemberInfo().getMemberAvatar())
                            .into(holder.getImageView(R.id.iv_icon));
                    if (info.isScoreState()) {
                        holder.setText(R.id.tv_text, "我的打分");
                        Log.d("onBindHeader", "userRatingValue=" + userRatingValue);
                        if (TextUtils.isEmpty(userRatingValue)) {
                            findDetailMemberInfo();
                        } else {
                            myRating.setStarProgress(Float.parseFloat(userRatingValue) * 20);
                        }
                    } else {
                        holder.setText(R.id.tv_text, "快来评分吧");
                        myRating.setStarProgress(0);
                    }
                    rlMyScore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppRatingPopup.with(context)
                                    .setAppDetailInfo(info)
                                    .setStarProgress(myRating.getStarProgress())
                                    .setCallback(progress -> {
                                        holder.setText(R.id.tv_text, "我的打分");
                                        myRating.setStarProgress(progress);
                                    })
                                    .show();
                        }
                    });
                } else {
                    rlMyScore.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void findDetailMemberInfo() {
        HttpApi.findDetailMemberInfoApi(info.getId(), info.getAppType(), UserManager.getInstance().getUserId())
                .onSuccess(data -> {
                    Log.d("findDetailMemberInfoApi", "data=" + data);
                    userRatingValue = data.selectFirst("scoreValue").text();
//                                        myRating.setStarProgress(Float.parseFloat(userRatingValue) * 20);
                    recyclerLayout.notifyItemChanged(0);
                })
                .subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        if (isSupportVisible()) {
            onRefresh();
        }
    }


}
