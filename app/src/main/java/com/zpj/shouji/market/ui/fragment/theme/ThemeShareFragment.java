package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.PublishApi;
import com.zpj.shouji.market.api.ThemePublishApi;
import com.zpj.shouji.market.imagepicker.ImagePicker;
import com.zpj.shouji.market.imagepicker.LocalImageViewer;
import com.zpj.shouji.market.imagepicker.entity.Item;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.manager.AppPickerFragment;
import com.zpj.shouji.market.ui.fragment.profile.UserPickerFragment;
import com.zpj.shouji.market.ui.widget.ActionPanel;
import com.zpj.shouji.market.ui.widget.ninegrid.NineGridView;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.toast.ZToast;
import com.zpj.utils.KeyboardObserver;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ThemeShareFragment extends BaseSwipeBackFragment {

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
        start(new ThemeShareFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_theme_share;
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


        nineGridView = view.findViewById(R.id.nine_grid_view);
        nineGridView.setEditMode(true);
        nineGridView.setCallback(new NineGridView.Callback() {
            @Override
            public void onImageItemClicked(int position, List<String> urls) {
                new LocalImageViewer()
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
                        .setSrcView(nineGridView.getImageView(position), position)
                        .setSrcViewUpdateListener(new ImageViewerDialogFragment.OnSrcViewUpdateListener<Item>() {
                            @Override
                            public void onSrcViewUpdate(@NonNull ImageViewerDialogFragment<Item> popup, int position) {
                                popup.updateSrcView(nineGridView.getImageView(position));
                            }
                        })
                        .show(context);
            }

            @Override
            public void onAddItemClicked(int position) {
                showImagePicker();
            }

            @Override
            public void onImageItemDelete(int position, String url) {
                imgList.remove(position);
            }
        });

        actionPanel = view.findViewById(R.id.panel_action);
        actionPanel.attachEditText(etContent);
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, view, height -> {
            actionPanel.onKeyboardHeightChanged(height, 0);
        });

//        actionPanel.removeImageAction();
//        actionPanel.removeAppAction();
        actionPanel.addAction(R.drawable.ic_at_black_24dp, v -> {
            showUserPicker();
        });
        actionPanel.addAction(R.drawable.ic_picture, new View.OnClickListener() {
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
                ZToast.warning("请输入动态内容");
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
//        List<NineGridBean> beanList = new ArrayList<>();
//        for (Item item : imgList) {
//            NineGridBean bean = new NineGridBean(item.getPath(context));
//            beanList.add(bean);
//        }
//        nineGridView.setDataList(beanList);
        List<String> urls = new ArrayList<>();
        for (Item item : imgList) {
            urls.add(item.getPath(context));
        }
        nineGridView.setUrls(urls);
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
        ImagePicker.with()
                .maxSelectable(9)
                .setSelectedList(imgList)
                .setOnSelectedListener(itemList -> {
                    postDelayed(this::initNineGrid, 500);
                })
                .start();
//        ImagePicker.from(_mActivity)
//                .choose(MimeType.ofImage())
//                .countable(true)
//                .maxSelectable(9)
//                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                .spanCount(3)
//                .thumbnailScale(0.85f)//缩放比例
//                .imageEngine(new GlideEngine())
//                .setDefaultSelection(imgList)
//                .setOnSelectedListener(itemList -> {
//                    postDelayed(this::initNineGrid, 500);
//                })
//                .start();
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
