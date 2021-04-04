package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.download.MissionBinder;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.widget.DownloadButton;
import com.zpj.statemanager.StateManager;
import com.zpj.toast.ZToast;
import com.zpj.utils.ScreenUtils;
import com.zxy.skin.sdk.SkinEngine;

public class ThemeAppDownloadDialogFragment extends BottomDragDialogFragment {

    private final Elements permissionList = new Elements();

//    private StateLayout stateLayout;
    private StateManager stateManager;
    private LinearLayout llContainer;
    private TextView tvDesc;
    private DownloadButton tvDownload;

    private String id;
    private DiscoverInfo discoverInfo;

    private String apkUrl;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            stateLayout.showContentView();
            stateManager.showContent();
//                recyclerView.notifyDataSetChanged();
            if (permissionList.isEmpty()) {
                TextView textView = new TextView(context);
                textView.setText("未知权限！");
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView.setGravity(Gravity.CENTER);
                int padding = ScreenUtils.dp2pxInt(context, 56);
                textView.setPadding(0, padding, 0, padding);
                llContainer.addView(textView);
            } else {
                int padding = ScreenUtils.dp2pxInt(context, 4);
                for (Element element : permissionList) {
                    TextView textView = new TextView(context);
                    textView.setText(element.text());
                    SkinEngine.setTextColor(textView, R.attr.textColorNormal);
//                    textView.setTextColor(ThemeUtils.getTextColorNormal(context));
                    textView.setPadding(0, padding, 0, padding);
                    llContainer.addView(textView);
                }
            }

        }
    };

    public static ThemeAppDownloadDialogFragment with(Context context) {
        return new ThemeAppDownloadDialogFragment();
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_theme_app_download;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        contentView.setBackgroundResource(R.drawable.background_bottom_sheet_dialog);

        contentView.setMinimumHeight(ScreenUtils.getScreenHeight(context) / 2);

        if (TextUtils.isEmpty(id) || discoverInfo == null) {
            ZToast.error("应用信息弹窗打开失败！");
            dismiss();
            return;
        }

//        stateLayout = findViewById(R.id.state_layout);
//        stateLayout.showLoadingView();


        llContainer = findViewById(R.id.ll_container);

        ImageView ivIcon = findViewById(R.id.iv_icon);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvInfo = findViewById(R.id.tv_info);
        tvDesc = findViewById(R.id.tv_desc);

        Glide.with(ivIcon).load(discoverInfo.getAppIcon()).into(ivIcon);
        tvTitle.setText(discoverInfo.getAppName());
        tvInfo.setText(discoverInfo.getAppPackageName());
        tvDesc.setText(discoverInfo.getAppSize());

        tvDownload = findViewById(R.id.tv_down);

//        tvDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ZToast.normal("TODO Download " + apkUrl);
//            }
//        });
        tvDownload.setEnabled(false);
        tvDownload.setAlpha(0.8f);


        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        getPermissions();
        stateManager = StateManager.with(findViewById(R.id.scroll_view))
                .showLoading();
    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(context) - ScreenUtils.dp2pxInt(context, 28) - ScreenUtils.getStatusBarHeight(context);
    }

    private void getPermissions() {
        permissionList.clear();
        HttpApi.getShareInfoApi(id)
                .onSuccess(data -> {
                    Log.d("ThemeAppDownloadPopup", "data=" + data);
                    apkUrl = data.selectFirst("apkurl").text();
                    if (!TextUtils.isEmpty(apkUrl)) {
                        tvDownload.setAlpha(1f);
                        tvDownload.setEnabled(true);
                        tvDownload.bindApp(new MissionBinder() {
                            @Override
                            public String getYunUrl() {
                                return null;
                            }

                            @Override
                            public String getAppId() {
                                return apkUrl.substring(apkUrl.lastIndexOf("id=") + 3);
                            }

                            @Override
                            public String getAppName() {
                                return discoverInfo.getAppName();
                            }

                            @Override
                            public String getAppType() {
                                return discoverInfo.getAppType();
                            }

                            @Override
                            public String getPackageName() {
                                return discoverInfo.getAppPackageName();
                            }

                            @Override
                            public String getAppIcon() {
                                return discoverInfo.getAppIcon();
                            }

                            @Override
                            public boolean isShareApp() {
                                return true;
                            }
                        });
                    }
                    String version = null;
                    for (Element element : data.selectFirst("infos").select("info")) {
                        if ("版本".equals(element.selectFirst("name").text())) {
                            version = element.selectFirst("value").text();
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(version)) {
                        tvDesc.setText(discoverInfo.getAppSize() + " | " + version);
                    } else {
                        tvDesc.setText(discoverInfo.getAppSize());
                    }
                    permissionList.addAll(data.selectFirst("pers").select("ptitle"));
                    postDelayed(runnable, 250);
                })
                .onError(throwable -> {
//                    stateLayout.showErrorView(throwable.getMessage())
                    stateManager.showError(throwable.getMessage());
                })
                .subscribe();
    }

    public ThemeAppDownloadDialogFragment setId(String id) {
        this.id = id;
        return this;
    }

    public ThemeAppDownloadDialogFragment setDiscoverInfo(DiscoverInfo discoverInfo) {
        this.discoverInfo = discoverInfo;
        return this;
    }
}
