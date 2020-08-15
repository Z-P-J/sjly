package com.zpj.shouji.market.ui.widget.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.popup.core.BottomPopup;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.widget.input.SubmitView;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;
import com.zpj.widget.statelayout.StateLayout;

import java.util.List;

public class ThemeAppDownloadPopup extends BottomPopup<ThemeAppDownloadPopup> {

    private final Elements permissionList = new Elements();

    private StateLayout stateLayout;
//    private EasyRecyclerView<Element> recyclerView;
    private LinearLayout llContainer;
    private TextView tvDesc;
    private TextView tvDownload;

    private String id;
    private DiscoverInfo discoverInfo;

    private String apkUrl;

    private boolean isShow;
    private boolean hasInit;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stateLayout.showContentView();
//                recyclerView.notifyDataSetChanged();
            if (permissionList.isEmpty()) {
                TextView textView = new TextView(context);
                textView.setText("无敏感权限");
                textView.setGravity(Gravity.CENTER);
                int padding = ScreenUtils.dp2pxInt(context, 56);
                textView.setPadding(0, padding, 0, padding);
                llContainer.addView(textView);
            } else {
                int padding = ScreenUtils.dp2pxInt(context, 4);
                for (Element element : permissionList) {
                    TextView textView = new TextView(context);
                    textView.setText(element.text());
                    textView.setPadding(0, padding, 0, padding);
                    llContainer.addView(textView);
                }
            }

        }
    };

    public static ThemeAppDownloadPopup with(Context context) {
        return new ThemeAppDownloadPopup(context);
    }

    public ThemeAppDownloadPopup(@NonNull Context context) {
        super(context);
        popupInfo.autoOpenSoftInput = true;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_theme_app_download;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        stateLayout = findViewById(R.id.state_layout);
        stateLayout.showLoadingView();

        llContainer = findViewById(R.id.ll_container);
//        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
//        recyclerView.setData(permissionList)
//                .setItemRes(R.layout.item_text)
//                .onBindViewHolder(new IEasy.OnBindViewHolderListener<Element>() {
//                    @Override
//                    public void onBindViewHolder(EasyViewHolder holder, List<Element> list, int position, List<Object> payloads) {
//                        holder.setText(R.id.tv_title, list.get(position).text());
//                    }
//                })
//                .build();

        ImageView ivIcon = findViewById(R.id.iv_icon);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvInfo = findViewById(R.id.tv_info);
        tvDesc = findViewById(R.id.tv_desc);

        Glide.with(ivIcon).load(discoverInfo.getAppIcon()).into(ivIcon);
        tvTitle.setText(discoverInfo.getAppName());
        tvInfo.setText(discoverInfo.getAppPackageName());
        tvDesc.setText(discoverInfo.getAppSize());

        tvDownload = findViewById(R.id.tv_down);
        tvDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AToast.normal("TODO Download " + apkUrl);
            }
        });
        tvDownload.setEnabled(false);
        tvDownload.setAlpha(0.8f);


        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        getPermissions();

    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(context) - ScreenUtils.dp2pxInt(context, 56) - ScreenUtils.getStatusBarHeight(context);
    }

    @Override
    public ThemeAppDownloadPopup show() {
        if (TextUtils.isEmpty(id) || discoverInfo == null) {
            AToast.error("应用信息弹窗打开失败！");
            return null;
        }
        return super.show();
    }

    @Override
    protected void onShow() {
        super.onShow();
        isShow = true;
        if (hasInit) {
            post(runnable);
        }
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
                    }
                    String version = null;
                    for (Element element : data.selectFirst("infos").select("info")) {
                        if ("版本".equals(element.selectFirst("name").text())) {
                            version = element.selectFirst("value").text();
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(version)) {
                        tvDesc.setText(version + "\t" + discoverInfo.getAppSize());
                    }
                    permissionList.addAll(data.selectFirst("pers").select("ptitle"));
                    if (isShow) {
                        hasInit = false;
                        post(runnable);
                    } else {
                        hasInit = true;
                    }
                })
                .onError(throwable -> stateLayout.showErrorView(throwable.getMessage()))
                .subscribe();
    }

    public ThemeAppDownloadPopup setId(String id) {
        this.id = id;
        return self();
    }

    public ThemeAppDownloadPopup setDiscoverInfo(DiscoverInfo discoverInfo) {
        this.discoverInfo = discoverInfo;
        return self();
    }
}
