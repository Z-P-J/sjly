package com.zpj.shouji.market.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.zpj.fragmentation.dialog.ZDialog;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.skin.SkinEngine;
import com.zpj.toast.ZToast;
import com.zpj.utils.ScreenUtils;

public class WebFragment extends SkinFragment {

    private static final String UA_PHONE = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Mobile Safari/537.36";
    private static final String UA_PC = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36";

    private FrameLayout container;
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private boolean isAnimStart = false;
    private int currentProgress;

    public static WebFragment newInstance(String url, String title) {
        Bundle args = new Bundle();
        args.putString(Keys.URL, url);
        args.putString(Keys.TITLE, title);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(String url, String title) {
        start(newInstance(url, title));
    }

    public static void start(String url) {
        start(newInstance(url, url));
    }

    public static void shareHomepage(String id) {
        start("https://m.shouji.com.cn/user/" + id + "/home.html");
    }

    public static void appPage(String type, String id) {
        start("https://" + type + ".shouji.com.cn/down/" + id + ".html");
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

        container = view.findViewById(R.id.content);

        mWebView = new WebView(context);
        container.addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mProgressBar = new ProgressBar(context, null, R.style.Widget_AppCompat_ProgressBar_Horizontal);
        mProgressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_progress));
        container.addView(mProgressBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dp2pxInt(context, 2)));


        initWebView();
        mWebView.loadUrl(url);
//        mWebView.evaluateJavascript(NIGHT, null);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web;
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            if (container != null) {
                container.removeView(mWebView);
            }
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
        super.onPause();
    }


    @Override
    public boolean onBackPressedSupport() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onBackPressedSupport();
    }


    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
            boolean isPhoneUA = isPhoneUA();
            ZDialog.attach()
                    .addItem("浏览器中打开")
                    .addItem(isPhoneUA ? "电脑版网页" : "移动版网页")
                    .addItems("复制链接", "刷新")
                    .setOnSelectListener((fragment, position, title) -> {
                        fragment.dismiss();
                        switch (position) {
                            case 0:
                                Uri uri = Uri.parse(mWebView.getUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                break;
                            case 1:
                                mWebView.getSettings().setUserAgentString(isPhoneUA ? UA_PC : UA_PHONE);
                                mWebView.reload();
                                break;
                            case 2:
                                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setPrimaryClip(ClipData.newPlainText(null, mWebView.getUrl()));
                                ZToast.success("已复制到粘贴板");
                                break;
                            case 3:
                                mWebView.reload();
                                break;
                        }
                    })
                    .setAttachView(imageButton)
                    .show(this);
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setUserAgentString(UA_PHONE);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
//        settings.setAllowContentAccess(true);
//        settings.setAllowFileAccess(true);
//        settings.setAllowFileAccessFromFileURLs(true);
//        settings.setAllowUniversalAccessFromFileURLs(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        settings.setLoadWithOverviewMode(true);
        settings.setDatabaseEnabled(true);
//        settings.setSavePassword(true);
        settings.setDomStorageEnabled(true);

        mWebView.setSaveEnabled(true);
//        mWebView.setKeepScreenOn(true);
//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                view.evaluateJavascript(NIGHT, null);
                super.onPageFinished(view, url);
                if (!settings.getLoadsImagesAutomatically()) {
                    settings.setLoadsImagesAutomatically(true);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                view.evaluateJavascript(NIGHT, null);
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
//                view.evaluateJavascript(NIGHT, null);
                super.onPageCommitVisible(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WebFragment", "shouldOverrideUrlLoading url=" + url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return false;
                }

                // Otherwise allow the OS to handle things like tel, mailto, etc.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                currentProgress = mProgressBar.getProgress();
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true;
                    mProgressBar.setProgress(newProgress);
                    // 开启属性动画让进度条平滑消失
                    startDismissAnimation(mProgressBar.getProgress());
                } else {
                    // 开启属性动画让进度条平滑递增
                    startProgressAnimation(newProgress);
                }
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4, long paramAnonymousLong) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(paramAnonymousString1));
                context.startActivity(intent);
            }
        });
        mWebView.setBackgroundColor(SkinEngine.getColor(context, R.attr.backgroundColor));
    }

    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(valueAnimator -> {
            float fraction = valueAnimator.getAnimatedFraction();
            int offset = 100 - progress;
            mProgressBar.setProgress((int) (progress + offset * fraction));
        });

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }

    private boolean isPhoneUA() {
        return UA_PHONE.equals(mWebView.getSettings().getUserAgentString());
    }

    private final String NIGHT = "javascript: var loopCount = 0;\n" +
//            "setNightMode();\n" +
            "function setNightMode() {\n" +
            "    if (document.getElementById(\"browser_night_mode_style\")) {\n" +
            "        console.log(\"has inserted and return\");\n" +
            "        return\n" +
            "    }\n" +
            "    console.log(\"begin create link element\");\n" +
            "    css = document.createElement(\"link\"),\n" +
            "    console.log(\"end create link element\");\n" +
            "    css.id = \"browser_night_mode_style\";\n" +
            "    css.rel = \"stylesheet\";\n" +
            "    css.href = 'data:text/css,html,body,h2,h3,h4,h5,h6,table,tr,td,th,tbody,form,ul,ol,li,dl,dd,section,footer,nav,strong,aside,header,label{background:rgba(34,34,34,.99)!important;background-image:none!important;background-color:rgba(34,34,34,.99)!important;color:rgba(119,119,119,.99)!important;border-color:rgba(33,42,50,.99)!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}html body{background-color:rgba(34,34,34,.99)!important}article,dt,h1,button.suggest-item-title{background-color:rgba(34,34,34,.99)!important;color:rgba(119,119,119,.99)!important;border-color:rgba(33,42,50,.99)!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}div{background-color:transparent!important;color:rgba(119,119,119,.99)!important;border-color:rgba(33,42,50,.99)!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}p{color:rgba(119,119,119,.99)!important;border-color:rgba(33,42,50,.99)!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}div:empty,div[id=&quot;x-video-button&quot;],div[class=&quot;x-advert&quot;],div[class=&quot;player_controls svp_ctrl&quot;]{background-color:transparent!important}dt:not(:empty),div:not(:empty),p:not(:empty),span:not(:empty){background-image:none!important}span,em{background-color:transparent!important;color:rgba(119,119,119,.99)!important;border-color:rgba(33,42,50,.99)!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}html,html body{scrollbar-base-color:rgba(70,86,123,.99)!important;scrollbar-face-color:rgba(86,104,143,.99)!important;scrollbar-shadow-color:rgba(2,2,2,.99)!important;scrollbar-highlight-color:rgba(86,104,143,.99)!important;scrollbar-dlight-color:rgba(46,57,82,.99)!important;scrollbar-darkshadow-color:rgba(2,2,2,.99)!important;scrollbar-track-color:rgba(70,86,123,.99)!important;scrollbar-arrow-color:rgba(0,0,0,.99)!important;scrollbar-3dlight-color:rgba(122,121,103,.99)!important}html input,html select,html button,html textarea{box-shadow:0 0 0!important;color:rgba(119,119,119,.99)!important;background-color:rgba(34,34,34,.99)!important;border-color:rgba(33,42,50,.99)!important}html input:focus,html select:focus,html option:focus,html button:focus,html textarea:focus{background-color:rgba(34,34,34,.99)!important;color:rgba(119,119,119,.99)!important;border-color:rgba(26,57,115,.99)!important;outline:2px solid rgba(26,57,115,.99)!important}html input:hover,html select:hover,html option:hover,html button:hover,html textarea:hover{background-color:rgba(34,34,34,.99)!important;color:rgba(119,119,119,.99)!important;border-color:rgba(26,57,115,.99)!important;outline:2px solid rgba(26,57,115,.99)!important}html input[type=text],html input[type=password]{background-image:none!important}html input[type=submit],html button{opacity:.5;border:1px solid rgba(33,42,50,.99)!important}html input[type=submit]:hover,html button:hover{opacity:1;border:1px solid rgba(26,57,115,.99)!important;outline:2px solid rgba(26,57,115,.99)!important}html img[src],html input[type=image]{opacity:.5}html input[type=image]:hover{opacity:1}div[class=&quot;img-view&quot;],ul[id=&quot;imgview&quot;],a[class^=&quot;prev&quot;],a[class^=&quot;next&quot;]a[class^=&quot;topic_img&quot;],a[class^=&quot;arrow&quot;],a:active[class^=&quot;arrow&quot;],a:visited[class^=&quot;arrow&quot;],img[src^=&quot;data&quot;],img[loaded=&quot;1&quot;]{background:none!important}a[class^=&quot;arrow&quot;]{height:0}.anythingSlider .arrow{background:none!important}html a,html a *{background-color:transparent!important;color:rgba(119,119,119,.99)!important;text-decoration:none!important;border-color:rgba(33,42,50,.99)!important;text-shadow:0 0 0!important}html a:visited,html a:visited *{color:rgba(167,22,185,.99)!important}html a:hover,html a:active{color:none!important;border-color:none!important}a img{background:none!important}#toolbarBox,#move_tip{background:none!important}#logolink,#mask{background-color:rgba(34,34,34,.99)!important;border-bottom:none!important}div::after{background-color:transparent!important}*:before,*:after{background-color:transparent!important;border-color:rgba(33,42,50,.99)!important;color:rgba(119,119,119,.99)!important}input::-webkit-input-placeholder{color:rgba(119,119,119,.99)!important}div[class=&quot;x-prompt&quot;],div[class=&quot;x-dashboard&quot;]{background:none!important}div[class=&quot;x-progress-play-mini&quot;]{background:rgba(235,60,16,.99)!important}div[class=&quot;suggest-box&quot;]{background:rgba(0,0,0,.99)!important}div[class=&quot;x-console&quot;],div[class=&quot;x-progress&quot;],div[class=&quot;x-progress-seek&quot;]{background:none!important}div[class=&quot;x-progress-track&quot;]{background-color:rgba(85,85,85,.99)!important}div[class=&quot;x-progress-load&quot;]{background-color:rgba(144,144,144,.99)!important}div[class=&quot;x-progress-play&quot;],div[class=&quot;x-seek-handle&quot;]{background-color:rgba(235,60,16,.99)!important}';\n" +
            "    var oHeads = document.getElementsByTagName(\"head\");\n" +
            "    var oBodys = document.getElementsByTagName(\"body\");\n" +
            "    loopCount++;\n" +
            "    if ((oHeads != null && oHeads.length > 0) || (oBodys != null && oBodys.length > 0)) {\n" +
            "        document.getElementsByTagName(\"head\").length != 0 ? document.getElementsByTagName(\"head\")[0].appendChild(css) : document.getElementsByTagName(\"body\")[0].appendChild(css)\n" +
            "    } else {\n" +
            "        if (loopCount < 6) {\n" +
            "            setTimeout(\"setNightMode()\", 100 * (loopCount - 1))\n" +
            "        }\n" +
            "    }\n" +
            "};" +
//            "window.addEventListener('DOMContentLoaded', () => {\n" +
//            "setNightMode();\n" +
//            "});";
            "setNightMode();";

}
