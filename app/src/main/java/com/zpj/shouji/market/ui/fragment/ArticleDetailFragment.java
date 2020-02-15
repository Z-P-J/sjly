package com.zpj.shouji.market.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.lxj.xpopup.XPopup;
import com.zpj.popupmenuview.popup.EverywherePopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.PopupImageLoader;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.model.article.ArticleDetailInfo;
import com.zpj.shouji.market.model.article.ArticleInfo;
import com.zpj.shouji.market.model.article.HtmlElement;
import com.zpj.shouji.market.model.article.ImageElement;
import com.zpj.shouji.market.model.article.LinkElement;
import com.zpj.shouji.market.model.article.TextElement;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.selection.SelectableTextView;
import com.zpj.shouji.market.utils.HttpApi;
import com.zpj.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArticleDetailFragment extends BaseFragment {

    private static final String KEY_URL = "key_url";

    private String url;
    private LinearLayout contentWrapper;
    private ArticleDetailInfo articleDetailInfo;
    private AtomicBoolean isEnterAnimationEnd = new AtomicBoolean(false);

    public static ArticleDetailFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_article_detail;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            pop();
            return;
        }
        url = getArguments().getString(KEY_URL);
        setToolbarTitle(url);
        setToolbarSubTitle(url);
        contentWrapper = view.findViewById(R.id.content_wrapper);
        parseHtml(url);
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> EverywherePopup.create(context)
                .addItem("网页中打开")
                .addItem("收藏")
                .setOnItemClickListener((title, position) -> {
                    switch (position) {
                        case 0:
                            _mActivity.start(WebFragment.newInstance(url));
                            break;
                        case 1:
                            AToast.warning("TODO");
                            break;
                    }
                })
                .show(imageButton));
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        if (articleDetailInfo != null) {
            parseArticleInfo(articleDetailInfo);
        }
        isEnterAnimationEnd.set(true);
    }

    private void parseHtml(final String url) {
        HttpApi.connect(url)
                .onSuccess(data -> {
                    articleDetailInfo = ArticleDetailInfo.parse(url.startsWith("https://soft.shouji.com.cn/") ? "soft" : "game", data);
                    if (isEnterAnimationEnd.get()) {
                        parseArticleInfo(articleDetailInfo);
                    }
                })
                .subscribe();
    }

    private synchronized void parseArticleInfo(ArticleDetailInfo info) {
        if (info == null) {
            AToast.error("文章解析失败！即将跳转至网页");
            pop();
            _mActivity.start(WebFragment.newInstance(url));
            return;
        }
        Log.d("parseArticleInfo", "parseArticleInfo");
        initHeaderView(info);
        initAppView(info);
        initContent(info);
        initRelateArticle(info);
        initRelateApp(info);
    }

    private void initHeaderView(ArticleDetailInfo info) {
        View header = LayoutInflater.from(context).inflate(R.layout.layout_article_header, null, false);
        TextView tvTitle = header.findViewById(R.id.tv_title);
        TextView tvInfo = header.findViewById(R.id.tv_info);
        setToolbarTitle(info.getTitle());
        tvTitle.setText(info.getTitle());
        tvInfo.setText(info.getArticleInfo());
        contentWrapper.addView(header);
    }

    private void initAppView(ArticleDetailInfo info) {
        AppInfo appInfo = info.getAppInfo();
        if (appInfo != null) {
            View app = LayoutInflater.from(context).inflate(R.layout.item_app_linear, null, false);
            contentWrapper.addView(app);
            app.setBackgroundResource(R.drawable.bg_gray_little);
            app.setOnClickListener(v -> _mActivity.start(AppDetailFragment.newInstance(appInfo.getAppType(), appInfo.getAppId())));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = ScreenUtil.dp2pxInt(context, 20f);
            lp.setMargins(margin, 0, margin, 0);
            app.setLayoutParams(lp);
            Glide.with(context).load(appInfo.getAppIcon()).into((ImageView) app.findViewById(R.id.iv_icon));
            ((TextView) app.findViewById(R.id.tv_title)).setText(appInfo.getAppTitle());
            ((TextView) app.findViewById(R.id.tv_desc)).setText(appInfo.getAppComment());
            TextView i = app.findViewById(R.id.tv_info);
            if (TextUtils.isEmpty(appInfo.getAppInfo())) {
                i.setVisibility(View.GONE);
                ((TextView) app.findViewById(R.id.tv_desc)).setTextColor(getResources().getColor(R.color.color_text_normal));
            } else {
                i.setText(appInfo.getAppInfo());
            }
        }
    }

    private void initContent(ArticleDetailInfo info) {
        final RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.bga_pp_ic_holder_light)
                .error(R.drawable.bga_pp_ic_holder_light)
                .override(Target.SIZE_ORIGINAL);
        for (HtmlElement element : info.getContentElementList()) {
            if (element instanceof LinkElement) {
                SelectableTextView tvText = (SelectableTextView) LayoutInflater.from(context).inflate(R.layout.layout_article_text, null, false);

                LinkElement linkElement = (LinkElement) element;
                SpannableStringBuilder builder = new SpannableStringBuilder(linkElement.getText());
                builder.clearSpans();
                int spanStart = linkElement.getText().indexOf(linkElement.getLinkText());
                builder.setSpan(new URLSpan(linkElement.getLinkUrl()),
                        spanStart,
                        spanStart + linkElement.getLinkText().length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                tvText.setText(builder);
                tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (linkElement.getLinkUrl().startsWith("http://sjsoft.wozaiai.cn/down/")) {
                            String id = linkElement.getLinkUrl().replace("http://sjsoft.wozaiai.cn/down/", "").replace(".html", "");
                            _mActivity.start(AppDetailFragment.newInstance("soft", id));
                        } else {
                            AToast.normal(linkElement.getLinkUrl());
                        }
                    }
                });
                contentWrapper.addView(tvText);
            } else if (element instanceof TextElement) {
                if (TextUtils.isEmpty(((TextElement) element).getText())) {
                    continue;
                }
                SelectableTextView tvText = (SelectableTextView) LayoutInflater.from(context).inflate(R.layout.layout_article_text, null, false);

                tvText.setText("        " + ((TextElement) element).getText());
//                tvText.setText(Html.fromHtml(element.getSourceCode()));
                contentWrapper.addView(tvText);
            } else if (element instanceof ImageElement) {
                String url = ((ImageElement) element).getImageUrl();
                View view = LayoutInflater.from(context).inflate(R.layout.layout_article_image, null, false);
                ImageView ivImage = view.findViewById(R.id.iv_image);
                ivImage.setOnClickListener(v -> {
                    List<Object> objects = new ArrayList<>();
                    objects.add(url);
                    new XPopup.Builder(context)
                            .asImageViewer(ivImage,
                                    0,
                                    objects, (popupView, position) -> popupView.updateSrcView(ivImage),
                                    new PopupImageLoader())
                            .show();
                });
                contentWrapper.addView(view);
                Glide.with(context).load(url).apply(options).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();
                        int margin = ScreenUtil.dp2pxInt(context, 20f);
                        int scaledW = ScreenUtil.getScreenWidth(context) - 2 * margin;
                        int scaledH = (height * scaledW) / width;
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(scaledW, scaledH);
                        lp.setMargins(margin, margin / 2, margin, margin / 2);
                        ivImage.setLayoutParams(lp);
                        ivImage.setImageDrawable(resource);
                    }
                });

            }
        }
    }

    private void initRelateArticle(ArticleDetailInfo info) {
        if (!info.getRelateArticleList().isEmpty()) {
            View title = LayoutInflater.from(context).inflate(R.layout.item_recommend_header, null, false);
            TextView tvHead = title.findViewById(R.id.tv_title);
            tvHead.setText("相关文章");
            contentWrapper.addView(title);
            for (ArticleInfo articleInfo : info.getRelateArticleList()) {
                SelectableTextView tvText = (SelectableTextView) LayoutInflater.from(context).inflate(R.layout.layout_article_text, null, false);


                tvText.setText(articleInfo.getTitle());
                tvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _mActivity.start(ArticleDetailFragment.newInstance(articleInfo.getUrl()));
                    }
                });
                contentWrapper.addView(tvText);
            }
        }
    }

    private void initRelateApp(ArticleDetailInfo info) {
        if (!info.getRelateAppList().isEmpty()) {
            View title = LayoutInflater.from(context).inflate(R.layout.item_recommend_header, null, false);
            TextView tvHead = title.findViewById(R.id.tv_title);
            tvHead.setText("相关软件");
            contentWrapper.addView(title);
            for (AppInfo app : info.getRelateAppList()) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_app_linear, null, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _mActivity.start(AppDetailFragment.newInstance(app));
                    }
                });
                ImageView ivIcon = view.findViewById(R.id.iv_icon);
                Glide.with(context).load(app.getAppIcon()).into(ivIcon);
                TextView tvName = view.findViewById(R.id.tv_title);
                tvName.setText(app.getAppTitle());
                TextView tvInfo1 = view.findViewById(R.id.tv_info);
                tvInfo1.setText(app.getAppSize());
                TextView tvDesc = view.findViewById(R.id.tv_desc);
                tvDesc.setText(app.getAppComment());
                contentWrapper.addView(view);
            }
        }
    }

}
