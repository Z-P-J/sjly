package com.zpj.shouji.market.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.felix.atoast.library.AToast;
import com.just.agentweb.AbsAgentWebSettings;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.IAgentWebSettings;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.popup.ZPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;

public class WebFragment extends BaseFragment {

    private static final String UA_PHONE = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Mobile Safari/537.36";
    private static final String UA_PC = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36";

    private AgentWeb mAgentWeb;

    public static void start(String url, String title) {
        Bundle args = new Bundle();
        args.putString(Keys.URL, url);
        args.putString(Keys.TITLE, title);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    public static void start(String url) {
        start(url, url);
    }

    public static void shareHomepage(String id) {
        start("https://m.shouji.com.cn/user/" + id + "/home.html");
    }

    public static void appPage(String type, String id) {
        start("https://" + type + ".shouji.com.cn/down/" + id + ".html");
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    public void onDestroy() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();
    }


    @Override
    public boolean onBackPressedSupport() {
        if (mAgentWeb != null && mAgentWeb.back()) {
            mAgentWeb.getWebCreator().getWebView().goBack();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            pop();
            return;
        }
        String url = getArguments().getString(Keys.URL);
        String title = getArguments().getString(Keys.TITLE);
        if (TextUtils.isEmpty(title)) {
            title = url;
        }
        setToolbarTitle(title);
        setToolbarSubTitle(url);

        FrameLayout content = view.findViewById(R.id.content);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(content, new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setAgentWebWebSettings(new CustomSettings())
//                .setWebViewClient(new WebViewClient(){
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        view.loadUrl(url);
//                        setToolbarTitle(view.getTitle());
//                        return true;
//                    }
//
//                    @Override
//                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                        super.onPageStarted(view, url, favicon);
//                        setToolbarSubTitle(url);
//                        setToolbarTitle(view.getTitle());
//                    }
//
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//                        super.onPageFinished(view, url);
//                    }
//                })
                .createAgentWeb()
                .ready()
                .go(url);
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
            boolean isPhoneUA = isPhoneUA();
            ZPopup.attachList(context)
                    .addItem("浏览器中打开")
                    .addItem(isPhoneUA ? "电脑版网页" : "移动版网页")
                    .addItem("复制链接")
                    .setOnSelectListener((position, title) -> {
                        switch (position) {
                            case 0:
                                Uri uri = Uri.parse(mAgentWeb.getWebCreator().getWebView().getUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                break;
                            case 1:
                                mAgentWeb.getAgentWebSettings().getWebSettings().setUserAgentString(isPhoneUA ? UA_PC : UA_PHONE);
                                mAgentWeb.getWebCreator().getWebView().reload();
                                break;
                            case 2:
                                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setPrimaryClip(ClipData.newPlainText(null, mAgentWeb.getWebCreator().getWebView().getUrl()));
                                AToast.success("已复制到粘贴板");
                                break;
                        }
                    })
                    .show(imageButton);
        });
    }

    private boolean isPhoneUA() {
        return UA_PHONE.equals(mAgentWeb.getAgentWebSettings().getWebSettings().getUserAgentString());
    }

    private static class CustomSettings extends AbsAgentWebSettings {

        @Override
        protected void bindAgentWebSupport(AgentWeb agentWeb) {

        }

        @Override
        public IAgentWebSettings toSetting(WebView webView) {
            super.toSetting(webView);
            getWebSettings().setBlockNetworkImage(false);
            getWebSettings().setAllowFileAccess(false);
            getWebSettings().setNeedInitialFocus(true);
            getWebSettings().setGeolocationEnabled(false);
            getWebSettings().setDomStorageEnabled(true);
            getWebSettings().setUseWideViewPort(true);
            getWebSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            getWebSettings().setLoadWithOverviewMode(true);
            getWebSettings().setSupportZoom(true);
            getWebSettings().setBuiltInZoomControls(true);
            getWebSettings().setUserAgentString(UA_PHONE);
            return this;
        }
    }

}
