package com.zpj.shouji.market.ui.fragment.recommond;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.zpj.http.ZHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.MultiData;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.booking.LatestBookingFragment;
import com.zpj.shouji.market.ui.fragment.homepage.multi.AppInfoMultiData;
import com.zpj.shouji.market.ui.fragment.homepage.multi.GameBookingMultiData;
import com.zpj.shouji.market.ui.fragment.homepage.multi.TutorialMultiData;
import com.zpj.toast.ZToast;

import java.util.ArrayList;
import java.util.List;

public class GameRecommendFragment extends BaseRecommendFragment implements View.OnClickListener {

    private static final String TAG = "RecommendFragment";
    private static final String[] TITLES = {"游戏快递", "游戏评测", "游戏攻略", "游戏新闻", "游戏周刊", "游戏公告"};

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.layout_header_recommend_game;
    }

    @Override
    public void toolbarLeftTextView(@NonNull TextView view) {
        super.toolbarLeftTextView(view);
        view.setText(R.string.title_game);
    }

    @Override
    public void initHeader(EasyViewHolder holder) {
        holder.setOnClickListener(R.id.tv_booking, this);
        holder.setOnClickListener(R.id.tv_handpick, this);
        holder.setOnClickListener(R.id.tv_rank, this);
        holder.setOnClickListener(R.id.tv_classification, this);

        ZHttp.get("https://soft.shouji.com.cn/")
                .toHtml()
                .onSuccess(data -> {

                    List<AppInfo> list = new ArrayList<>();
                    Elements recommends = data.selectFirst("body > div:nth-child(5) > div.boutique.fl > ul").select("li");
                    for (Element recommend : recommends) {
                        Element a = recommend.selectFirst("a");
                        AppInfo info = new AppInfo();
                        info.setAppId(a.attr("href")
                                .replace("/game/", "")
                                .replace(".html", ""));
                        info.setAppIcon(a.selectFirst("img").attr("src"));
                        info.setAppTitle(a.attr("title"));
                        info.setAppType("game");
                        info.setAppSize(a.selectFirst("p").text().replace("大小：", ""));
                        list.add(info);
                    }
                    initData(list);
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    ZToast.error("出错了！" + throwable.getMessage());
                })
                .subscribe();

    }

    @Override
    protected void initMultiData(List<MultiData> list) {

        list.add(new AppInfoMultiData("最近更新") {
            @Override
            public void onHeaderClick() {
                ToolBarAppListFragment.startUpdateGameList();
            }

            @Override
            public PreloadApi getKey() {
                return PreloadApi.UPDATE_GAME;
            }
        });

        list.add(new AppInfoMultiData("游戏推荐") {
            @Override
            public void onHeaderClick() {
                ToolBarAppListFragment.startRecommendGameList();
            }

            @Override
            public PreloadApi getKey() {
                return PreloadApi.HOME_GAME;
            }
        });

        list.add(new GameBookingMultiData());

        list.add(new AppInfoMultiData("热门网游") {
            @Override
            public void onHeaderClick() {
                ToolBarAppListFragment.startNetGameList();
            }

            @Override
            public PreloadApi getKey() {
                return PreloadApi.NET_GAME;
            }
        });

        for (int i = 0; i < TITLES.length; i++) {
            list.add(new TutorialMultiData(TITLES[i], "game", i + 1));
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_booking:
                LatestBookingFragment.start();
                break;
            case R.id.tv_handpick:
                ZToast.normal("TODO");
                PickedGameFragment.start();
                break;
            case R.id.tv_rank:
                AppRankFragment.startGame();
                break;
            case R.id.tv_classification:
                AppClassificationFragment.startGame();
                break;
        }
    }


}
