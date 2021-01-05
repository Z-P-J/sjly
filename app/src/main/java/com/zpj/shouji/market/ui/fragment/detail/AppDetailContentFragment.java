package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.ctetin.expandabletextviewlibrary.app.LinkType;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.multidata.AppGridListMultiData;
import com.zpj.shouji.market.ui.multidata.BaseHeaderMultiData;
import com.zpj.shouji.market.ui.multidata.ScreenShootMultiData;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class AppDetailContentFragment extends SkinFragment {


    private MultiRecyclerViewWrapper wrapper;

    private float screenWidth;
    private float screenHeight;
    private float ratio;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onGetAppInfoEvent(this, new RxBus.SingleConsumer<AppDetailInfo>() {
            @Override
            public void onAccept(AppDetailInfo info) throws Exception {
                Log.d("AppDetailInfoFragment", "onGetAppDetailInfo info=" + info);

                List<MultiData<?>> list = new ArrayList<>();


                addItem(list, "特别说明", info.getSpecialStatement());
                addItem(list, "小编评论", info.getEditorComment());
                list.add(new ScreenShootMultiData("软件截图", info.getImgUrlList()));
                addItem(list, "应用简介", info.getAppIntroduceContent());
                addItem(list, "新版特性", info.getUpdateContent());
                addItem(list, "详细信息", info.getAppInfo());
                addItem(list, "权限信息", info.getPermissionContent());

                if (info.getOtherAppList() != null) {
                    list.add(new AppGridListMultiData("开发者其他应用", info.getOtherAppList()) {
                        @Override
                        public void onHeaderClick() {
                            ToolBarAppListFragment.start(info.getOtherAppUrl(), "开发者其他应用");
                        }

                        @Override
                        public int getMaxColumnCount() {
                            return 3;
                        }
                    });
                }

                wrapper.setData(list)
                        .setFooterView(LayoutInflater.from(context).inflate(R.layout.item_footer_normal, null, false))
                        .build();
                wrapper.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        this.screenHeight = ScreenUtils.getScreenHeight(context);
        this.screenWidth = ScreenUtils.getScreenWidth(context);
        this.ratio = screenHeight / screenWidth;


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        wrapper = new MultiRecyclerViewWrapper(recyclerView);
    }

    private void addItem(List<MultiData<?>> list, String title, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        list.add(new DetailInfoMultiData(title, text));
    }

    public static class DetailInfoMultiData extends BaseHeaderMultiData<String>
            implements ExpandableTextView.OnLinkClickListener {

        public DetailInfoMultiData(String title, String content) {
            super(title);
            list.add(content);
            hasMore = false;
            isLoaded = true;
        }

        @Override
        public int getChildLayoutId(int viewType) {
            return R.layout.item_app_info_text;
        }

        @Override
        public int getChildViewType(int position) {
            return R.layout.item_app_info_text;
        }

        @Override
        public boolean hasChildViewType(int viewType) {
            return viewType == R.layout.item_app_info_text;
        }

        @Override
        public boolean loadData(MultiAdapter adapter) {
            return false;
        }

        @Override
        protected boolean showMoreButton() {
            return false;
        }

        @Override
        public void onBindChild(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
            ExpandableTextView tvContent = holder.getView(R.id.tv_content);
            tvContent.setTextIsSelectable(true);
            tvContent.setLinkClickListener(this);
            tvContent.setContent(list.get(position));
        }

        @Override
        public void onLinkClickListener(LinkType type, String content, String selfContent) {
            if (type == LinkType.MENTION_TYPE) {
                ProfileFragment.start(content.replace("@", "").trim());
            }
        }
    }
}
