package com.zpj.shouji.market.ui.fragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.imagetrans.ImageItemView;
import com.zpj.fragmentation.dialog.imagetrans.listener.SourceImageViewGet;
import com.zpj.http.core.IHttp;
import com.zpj.matisse.Matisse;
import com.zpj.matisse.MimeType;
import com.zpj.matisse.engine.impl.GlideEngine;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.ui.fragment.CustomImageViewerDialogFragment2;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CommentApi;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.widget.NineGridView;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;
import com.zpj.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

public class FeedbackFragment extends BaseSwipeBackFragment {

    private final List<Item> imgList = new ArrayList<>();

    private NineGridView nineGridView;

    public static void start() {
        start(new FeedbackFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_feedback;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "意见反馈";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        FlowLayout flowLayout = findViewById(R.id.fl_tags);
        flowLayout.setSelectedPosition(0);
        flowLayout.setMultiSelectMode(false);
        flowLayout.addItems(getResources().getStringArray(R.array.default_feedback_mode));

        EditText etContent = findViewById(R.id.et_content);

        nineGridView = findViewById(R.id.nine_grid_view);
        nineGridView.setEditMode(true);
        nineGridView.setCallback(new NineGridView.Callback() {
            @Override
            public void onImageItemClicked(int position, List<String> urls) {
                new CustomImageViewerDialogFragment2()
                        .setOnSelectedListener(itemList -> {
                            postDelayed(() -> {
                                if (imgList.size() != itemList.size()) {
                                    imgList.clear();
                                    imgList.addAll(itemList);
                                    initNineGrid();
                                }
                            }, 100);
                        })
                        .setImageList(imgList)
                        .setNowIndex(position)
                        .setSourceImageView(new SourceImageViewGet<Item>() {
                            @Override
                            public void updateImageView(ImageItemView<Item> imageItemView, int pos, boolean isCurrent) {
                                imageItemView.update(nineGridView.getImageView(pos));
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

        TextView tvSubmit = findViewById(R.id.tv_submit);
        tvSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etContent.getText())) {
                ZToast.warning("请输入反馈内容！");
                return;
            }
            StringBuilder content = new StringBuilder();
            for (String tag : flowLayout.getSelectedItem()) {
                content.append("#").append(tag).append("#").append(" ");
            }
            content.append("@大麦子 @阿里爸爸 @潮牛汇123 ");

            content.append("\n手机厂商：").append(DeviceUtils.getBuildBrand());
            content.append("\n手机型号：").append(DeviceUtils.getModel());
            content.append("\nAndroid版本：").append(DeviceUtils.getOSVersion());
            content.append("\nAndroid版本号：").append(DeviceUtils.getBuildVersionSDK());
            content.append("\nCPU类型：").append(Build.CPU_ABI);
            content.append("\n系统语言：").append(DeviceUtils.getLanguage());
            content.append("\n乐园版本：").append(AppUtils.getAppVersionName(context, context.getPackageName()));

            content.append("\n反馈内容：").append(etContent.getText());

            Log.d("FeedbackFragment", "content=" + content.toString());
            CommentApi.feedbackApi(
                    context,
                    content.toString(),
                    "0",
                    "21220",
                    "soft",
                    "cn.com.shouji.market",
                    imgList,
                    () -> {
                        ZToast.success("反馈成功！");
                        pop();
                    },
                    new IHttp.OnStreamWriteListener() {
                        @Override
                        public void onBytesWritten(int bytesWritten) {

                        }

                        @Override
                        public boolean shouldContinue() {
                            return true;
                        }
                    }
            );
        });

//        ElasticScrollView scrollView = findViewById(R.id.scroll_view);
//        KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
//            tvSubmit.setTranslationY(-height);
//            scrollView.setTranslationY(-height);
//        });

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

}
