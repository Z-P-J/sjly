package com.zpj.shouji.market.ui.fragment.theme;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.felix.atoast.library.AToast;
import com.lwkandroid.widget.ninegridview.INineGridImageLoader;
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.core.IHttp;
import com.zpj.matisse.Matisse;
import com.zpj.matisse.MimeType;
import com.zpj.matisse.engine.impl.GlideEngine;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.ui.widget.CustomImageViewerPopup;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.PublishApi;
import com.zpj.shouji.market.api.ThemePublishApi;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.glide.GlideUtils;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.model.UserInfo;
import com.zpj.shouji.market.ui.fragment.manager.AppPickerFragment;
import com.zpj.shouji.market.ui.fragment.profile.UserPickerFragment;
import com.zpj.shouji.market.ui.widget.ActionPanel;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.utils.KeyboardHeightProvider;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemeShareFragment extends BaseFragment {

    private final List<Item> imgList = new ArrayList<>();

    private EditText etContent;

    private FlowLayout flowLayout;
    private NineGridView nineGridView;
    private TextView tvShareMode;
    private ActionPanel actionPanel;

    private RelativeLayout rlAppItem;
    private TextView tvAddApp;
    private ImageView ivIcon;
    private TextView tvTitle;
    private TextView tvInfo;

    private InstalledAppInfo installedAppInfo;

    private boolean isPrivate;

    public static void start() {
        StartFragmentEvent.start(new ThemeShareFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_theme_share;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("分享动态");

        etContent = view.findViewById(R.id.et_content);

        flowLayout = view.findViewById(R.id.fl_tags);


        view.findViewById(R.id.rl_upload_app).setOnClickListener(v -> {
            if (tvAddApp.getVisibility() == View.VISIBLE && installedAppInfo == null) {
                showAppPicker();
            }
        });
        view.findViewById(R.id.tv_remove).setOnClickListener(v -> {
            installedAppInfo = null;
            initUploadApp();
        });
        rlAppItem = view.findViewById(R.id.rl_app_item);
        tvAddApp = view.findViewById(R.id.tv_add_app);
        ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle = view.findViewById(R.id.tv_app_name);
        tvInfo = view.findViewById(R.id.tv_info);


        nineGridView = view.findViewById(R.id.nine_grid_image_view);
        nineGridView.setImageLoader(new INineGridImageLoader() {

            @Override
            public void displayNineGridImage(Context context, String url, ImageView imageView) {
                Glide.with(context)
                        .load(new File(url))
                        .apply(GlideUtils.REQUEST_OPTIONS)
                        .into(imageView);
            }

            @Override
            public void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height) {
                displayNineGridImage(context, url, imageView);
            }
        });
        nineGridView.setOnItemClickListener(new NineGridView.onItemClickListener() {
            @Override
            public void onNineGirdAddMoreClick(int dValue) {
                showImagePicker();
            }

            @Override
            public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {
                CustomImageViewerPopup.with(context)
                        .setOnSelectedListener(itemList -> {
                            postDelayed(() -> {
                                if (imgList.size() != itemList.size()) {
                                    imgList.clear();
                                    imgList.addAll(itemList);
                                    initNineGrid();
                                }
                            }, 100);
                        })
                        .setImageUrls(imgList)
                        .setSrcView(imageContainer.getImageView(), position)
                        .setSrcViewUpdateListener((popup, pos) -> {
                            NineGirdImageContainer view = (NineGirdImageContainer) nineGridView.getChildAt(pos);
                            popup.updateSrcView(view.getImageView());
                        })
                        .show();
            }

            @Override
            public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {
                imgList.remove(position);
            }
        });

        actionPanel = view.findViewById(R.id.panel_action);
        actionPanel.attachEditText(etContent);
        KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
            actionPanel.onKeyboardHeightChanged(height, 0);
        });

//        actionPanel.removeImageAction();
//        actionPanel.removeAppAction();
        actionPanel.addAction(R.drawable.ic_at_black_24dp, v -> {
            showUserPicker();
        });
        actionPanel.addAction(R.drawable.ic_image_black_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });
        actionPanel.addAction(R.drawable.ic_android_black_24dp, v -> {
            hideSoftInput();
            showAppPicker();
        });
        tvShareMode = actionPanel.addAction("公开", v -> {
            tvShareMode.setText(isPrivate ? "公开" : "私有");
            isPrivate = !isPrivate;
        });
        actionPanel.setSendAction(v -> {
            hideSoftInput();

            if (TextUtils.isEmpty(etContent.getText())) {
                AToast.warning("请输入动态内容");
                return;
            }
            String tags = "";
            for (String tag : flowLayout.getSelectedItem()) {
                if (!TextUtils.isEmpty(tags)) {
                    tags += ",";
                }
                tags += tag;
            }

            ThemePublishApi.publishThemeApi(
                    context,
                    etContent.getText().toString(),
                    "0",
                    installedAppInfo,
                    imgList,
                    tags,
                    isPrivate,
                    new Runnable() {
                        @Override
                        public void run() {
                            pop();
                        }
                    },
                    new IHttp.OnStreamWriteListener() {
                        @Override
                        public void onBytesWritten(int bytesWritten) {

                        }

                        @Override
                        public boolean shouldContinue() {
                            return true;
                        }
                    });

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        darkStatusBar();
        showSoftInput(etContent);
        initFlowLayout();
    }

    @Override
    public boolean onBackPressedSupport() {
        if (actionPanel.isEmotionPanelShow()) {
            actionPanel.hideEmojiPanel();
            return true;
        }
        return super.onBackPressedSupport();
    }

    private void initFlowLayout() {
        PublishApi.getPublishTags(tags -> {
            flowLayout.setMaxSelectCount(3);
            flowLayout.setMultiSelectMode(true);
            flowLayout.setSpace(ScreenUtils.dp2pxInt(context, 8));
            flowLayout.setItems(tags);
        });

    }

    private void initUploadApp() {
        if (installedAppInfo != null) {
            rlAppItem.setVisibility(View.VISIBLE);
            tvAddApp.setVisibility(View.INVISIBLE);
            Glide.with(context).load(installedAppInfo).into(ivIcon);
            tvTitle.setText(installedAppInfo.getName());
            tvInfo.setText(installedAppInfo.getVersionName() + " | "
                    + installedAppInfo.getAppSize() + " | " + installedAppInfo.getPackageName());
        } else {
            rlAppItem.setVisibility(View.INVISIBLE);
            tvAddApp.setVisibility(View.VISIBLE);
        }
    }

    private void initNineGrid() {
        List<NineGridBean> beanList = new ArrayList<>();
        for (Item item : imgList) {
            NineGridBean bean = new NineGridBean(item.getPath(context));
            beanList.add(bean);
        }
        nineGridView.setDataList(beanList);
    }

    private void showUserPicker() {
        hideSoftInput();
        UserPickerFragment.start(content -> {
            etContent.append(content);
            showSoftInput(etContent);
        });
    }

    private void showImagePicker() {
        hideSoftInput();
        Matisse.from(_mActivity)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(9)
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .spanCount(3)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)//缩放比例
                .imageEngine(new GlideEngine())
                .capture(false)
                .setDefaultSelection(imgList)
                .setOnSelectedListener(itemList -> {
                    postDelayed(this::initNineGrid, 500);
                })
                .start();
    }

    private void showAppPicker() {
        AppPickerFragment.start(installedAppInfo, obj -> {
            if (!obj.isEmpty()) {
                this.installedAppInfo = obj.get(0);
                initUploadApp();
            }
        });
    }


}
