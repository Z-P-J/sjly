package com.zpj.shouji.market.ui.fragment.recommond;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.glide.transformations.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.ClassificationItem;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.widget.expandabletextview.ExpandableTextView;
import com.zpj.shouji.market.ui.widget.expandabletextview.app.LinkType;

import java.util.List;

public class AppClassificationFragment extends NextUrlFragment<ClassificationItem> {

    public static void startSoft() {
        start("soft");
    }

    public static void startGame() {
        start("game");
    }

    private static void start(String type) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "/androidv3/category_xml.jsp?from=" + type);
        AppClassificationFragment fragment = new AppClassificationFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle(defaultUrl.contains("game") ? "游戏分类" : "软件分类");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_classification;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<ClassificationItem> recyclerLayout) {
        super.buildRecyclerLayout(recyclerLayout);
        recyclerLayout
                .addItemDecoration(new Y_DividerItemDecoration(getContext()) {
                    @Override
                    public Y_Divider getDivider(int itemPosition) {
                        Y_DividerBuilder builder = null;
                        int color = Color.TRANSPARENT;
                        if (itemPosition == 0) {
                            builder = new Y_DividerBuilder()
                                    .setTopSideLine(true, color, 16, 0, 0)
                                    .setBottomSideLine(true, color, 8, 0, 0);
                        } else if (itemPosition == data.size() - 1) {
                            builder = new Y_DividerBuilder()
                                    .setTopSideLine(true, color, 8, 0, 0)
                                    .setBottomSideLine(true, color, 16, 0, 0);
                        } else {
                            builder = new Y_DividerBuilder()
                                    .setTopSideLine(true, color, 8, 0, 0)
                                    .setBottomSideLine(true, color, 8, 0, 0);
                        }
                        return builder
                                .setLeftSideLine(true, color, 16, 0, 0)
                                .setRightSideLine(true, color, 16, 0, 0)
                                .create();
                    }
                });
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<ClassificationItem> list, int position, List<Object> payloads) {
        ClassificationItem item = list.get(position);
        Glide.with(context).load(item.getIcon()).into(holder.getImageView(R.id.iv_icon));
        holder.setText(R.id.tv_title, item.getTitle());

        ExpandableTextView tvTags = holder.getView(R.id.tv_tags);
        tvTags.setNeedSelf(true);
        tvTags.setNeedExpend(false);
        tvTags.setSelfTextColor(Color.BLACK);
        tvTags.setContent(item.getTags());
        tvTags.setLinkClickListener(new ExpandableTextView.OnLinkClickListener() {
            @Override
            public void onLinkClickListener(LinkType type, String content, String selfContent) {
                if (type == LinkType.SELF) {
                    ToolBarAppListFragment.start(selfContent, content);
                }
            }
        });

        Glide.with(context)
                .asDrawable()
                .load(item.getIcon())
                .apply(RequestOptions.bitmapTransform(new CropBlurTransformation(25, 0.3f)))
                .into(holder.getImageView(R.id.iv_bg));
    }

    @Override
    protected void onGetDocument(Document doc) throws Exception {
        for (Element element : doc.select("category")) {
            ClassificationItem item = createData(element);
            if (item == null) {
                continue;
            }
            data.add(item);
        }
    }

    @Override
    public ClassificationItem createData(Element element) {
        return ClassificationItem.create(element);
    }

}
