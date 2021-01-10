package com.zpj.shouji.market.ui.fragment.subject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.donkingliang.consecutivescroller.ConsecutiveScrollerLayout;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.glide.transformations.blur.CropBlurTransformation;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.SubjectInfo;
import com.zpj.shouji.market.ui.fragment.base.StateSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.toast.ZToast;
import com.zpj.utils.ColorUtils;
import com.zxy.skin.sdk.SkinEngine;

import java.util.ArrayList;
import java.util.List;

import static com.donkingliang.consecutivescroller.ConsecutiveScrollerLayout.*;

public class SubjectDetailFragment extends StateSwipeBackFragment
        implements IEasy.OnBindViewHolderListener<AppInfo>,
        IEasy.OnItemClickListener<AppInfo> {

    private final List<AppInfo> appInfoList = new ArrayList<>();


//    private StateLayout stateLayout;
    private EasyRecyclerView<AppInfo> recyclerView;

    private String id;
    private String title;

    private float alpha = 0f;
    private boolean isLightStyle = true;

    public static void start(SubjectInfo subjectInfo) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, subjectInfo.getId());
        args.putString(Keys.TITLE, subjectInfo.getTitle());
        args.putString(Keys.INFO, subjectInfo.getM());
        args.putString(Keys.CONTENT, subjectInfo.getComment());
        args.putString(Keys.ICON, subjectInfo.getIcon());
        SubjectDetailFragment fragment = new SubjectDetailFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_subject_detail;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            id = getArguments().getString(Keys.ID, "");
//            stateLayout.showLoadingView();


            TextView tvTitle = findViewById(R.id.tv_title);
            TextView tvInfo = findViewById(R.id.tv_info);
            TextView tvDesc = findViewById(R.id.tv_desc);
            ImageView ivBg = findViewById(R.id.iv_bg);
            ImageView ivIcon = findViewById(R.id.iv_icon);

            title = getArguments().getString(Keys.TITLE, "Title");
            setToolbarTitle(title);
            toolbar.getCenterTextView().setAlpha(0);
            tvTitle.setText(title);

            tvInfo.setText(getArguments().getString(Keys.INFO, ""));
            tvDesc.setText(getArguments().getString(Keys.CONTENT, ""));

            Glide.with(context)
                    .load(getArguments().getString(Keys.ICON))
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivIcon);
            Glide.with(context)
                    .load(getArguments().getString(Keys.ICON))
                    .apply(
                            RequestOptions
                                    .bitmapTransform(new CropBlurTransformation(25, 0.1f))
                                    .error(R.drawable.bg_member_default)
                                    .placeholder(R.drawable.bg_member_default)
                    )
                    .into(ivBg);
        } else {
            pop();
            ZToast.error("查看专题失败！");
            return;
        }

        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setBackground(new ColorDrawable(Color.TRANSPARENT), true);

        View shadowView = findViewById(R.id.shadow_bottom);
        shadowView.setAlpha(0f);

        ConsecutiveScrollerLayout scrollerLayout = findViewById(R.id.layout_scroller);
        scrollerLayout.setOnVerticalScrollChangeListener((v, scrollY, oldScrollY, scrollState) -> {
            float tempAlpha = alpha;
            alpha = (float) scrollY / toolbar.getMeasuredHeight();
            alpha = Math.min(1f, alpha);
            if (alpha == tempAlpha) {
                return;
            }
            shadowView.setAlpha(alpha);
//                    int color = ColorUtils.alphaColor(Color.WHITE, alpha * 0.95f);
            int backgroundColor = SkinEngine.getColor(context, R.attr.backgroundColor);
            boolean isDark = ColorUtils.isDarkenColor(backgroundColor);
            int color = ColorUtils.alphaColor(backgroundColor, alpha);
            toolbar.setBackgroundColor(color);
//                    isLightStyle = alpha <= 0.5;
            if (!isDark && alpha < 0.5f) {
                isLightStyle = true;
            } else {
                isLightStyle = isDark;
            }

            toolbar.setLightStyle(isLightStyle);
            toolbar.getCenterTextView().setAlpha(alpha);

            if (isLightStyle) {
                lightStatusBar();
            } else {
                darkStatusBar();
            }
        });

        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        recyclerView.setData(appInfoList)
                .setItemRes(R.layout.item_app_linear)
                .onBindViewHolder(this)
                .onItemClick(this)
                .build();
        getData();
    }

    @Override
    protected void initStatusBar() {
        if (isLazyInit()) {
            if (isLightStyle) {
                lightStatusBar();
            } else {
                darkStatusBar();
            }
        } else {
            super.initStatusBar();
        }
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<AppInfo> list, int position, List<Object> payloads) {
        final AppInfo appInfo = list.get(position);
        holder.getTextView(R.id.tv_title).setText(appInfo.getAppTitle());
        holder.getTextView(R.id.tv_info).setText(appInfo.getAppSize() + " | " + appInfo.getAppInfo());
        holder.getTextView(R.id.tv_desc).setText(appInfo.getAppComment());
        Glide.with(context).load(appInfo.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        downloadButton.bindApp(appInfo);
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, AppInfo data) {
        AppDetailFragment.start(data);
    }

    private void getData() {
        HttpApi.getXml("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=" + id)
                .bindToLife(this)
                .onSuccess(data -> {
                    for (Element element : data.select("item")) {
                        appInfoList.add(AppInfo.parse(element));
                    }
                    postOnEnterAnimationEnd(() -> {
                        recyclerView.notifyDataSetChanged();
                        showContent();
                        onSupportVisible();
                    });
                })
                .onError(throwable -> ZToast.error("出错了！" + throwable.getMessage()))
                .subscribe();
    }


}
