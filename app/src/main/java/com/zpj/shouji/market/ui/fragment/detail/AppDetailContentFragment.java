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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.recyclerview.MultiData;
import com.zpj.recyclerview.MultiRecyclerViewWrapper;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommonImageViewerDialogFragment2;
import com.zpj.shouji.market.ui.fragment.homepage.multi.AppGridListMultiData;
import com.zpj.shouji.market.ui.fragment.homepage.multi.BaseHeaderMultiData;
import com.zpj.shouji.market.ui.fragment.homepage.multi.ScreenShootMultiData;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initStatusBar() {

    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        this.screenHeight = ScreenUtils.getScreenHeight(context);
        this.screenWidth = ScreenUtils.getScreenWidth(context);
        this.ratio = screenHeight / screenWidth;


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        wrapper = new MultiRecyclerViewWrapper(recyclerView);
    }

    @Subscribe
    public void onGetAppDetailInfo(AppDetailInfo info) {
        EventBus.getDefault().unregister(this);
        Log.d("AppDetailInfoFragment", "onGetAppDetailInfo info=" + info);

        List<MultiData> list = new ArrayList<>();


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
                public int getHeaderSpanCount() {
                    return 3;
                }
            });
        }

        wrapper.setData(list)
                .setMaxSpan(3)
                .setFooterView(LayoutInflater.from(context).inflate(R.layout.item_footer_home, null, false))
                .build();
        wrapper.notifyDataSetChanged();
    }

    private void addItem(List<MultiData> list, String title, String text) {
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
        public void onHeaderClick() {

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
        public void onBindHeader(EasyViewHolder holder, List<Object> payloads) {
            super.onBindHeader(holder, payloads);
            holder.setVisible(R.id.tv_more, false);
        }

        @Override
        public void onBindChild(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
            ExpandableTextView tvContent = holder.getView(R.id.tv_content);
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
