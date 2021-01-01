package com.zpj.shouji.market.ui.fragment.wallpaper;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.http.core.IHttp;
import com.zpj.matisse.Matisse;
import com.zpj.matisse.MimeType;
import com.zpj.matisse.engine.impl.GlideEngine;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.listener.OnSelectedListener;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.WallpaperApi;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.profile.UserPickerFragment;
import com.zpj.shouji.market.ui.widget.ActionPanel;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.toast.ZToast;
import com.zpj.utils.KeyboardObserver;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.util.List;

public class WallpaperShareFragment extends BaseSwipeBackFragment
        implements View.OnClickListener {

    private EditText etContent;
    private FlowLayout flowLayout;
    private FrameLayout flEmpty;
    private ImageView ivWallpaper;
    private TextView tvShareMode;
    private ActionPanel actionPanel;


    private File imgFile;
    private String tag = "壁纸";
    private boolean isPrivate;

    public static void start() {
        start(new WallpaperShareFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wallpaper_share;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("分享乐图");
        etContent = view.findViewById(R.id.et_content);
        flowLayout = view.findViewById(R.id.wallpaper_tags);
        flEmpty = view.findViewById(R.id.fl_empty);
        flEmpty.setOnClickListener(this);
        ivWallpaper = view.findViewById(R.id.iv_wallpaper);
        ivWallpaper.setOnClickListener(this);
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
        actionPanel.addAction(R.drawable.ic_image_black_24dp, this);
        tvShareMode = actionPanel.addAction("公开", v -> {
            tvShareMode.setText(isPrivate ? "公开" : "私有");
            isPrivate = !isPrivate;
        });
        actionPanel.setSendAction(v -> {
            if (TextUtils.isEmpty(etContent.getText())) {
                ZToast.warning("请输入内容");
                return;
            }
            if (imgFile == null) {
                ZToast.warning("请选择图片");
                return;
            }

            WallpaperApi.shareWallpaperApi(imgFile, etContent.getText().toString(), tag, isPrivate, this::pop, new IHttp.OnStreamWriteListener() {
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
        showSoftInput(actionPanel.getEditor());
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
        WallpaperApi.getWallpaperTags(tags -> {
            flowLayout.setOnItemClickListener((index, v, text) -> {
                tag = text;
//                flowLayout.setSelectedPosition(index);
            });
            flowLayout.setSpace(ScreenUtils.dp2pxInt(context, 8));
            flowLayout.setSelectedPosition(0);
            flowLayout.clear();
            for (WallpaperTag tag : tags) {
                if ("全部".equals(tag.getName())) {
                    continue;
                }
                flowLayout.addItem(tag.getName());
            }
        });

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
                .maxSelectable(1)
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .spanCount(3)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)//缩放比例
                .imageEngine(new GlideEngine())
                .capture(false)
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Item> itemList) {
                        imgFile = itemList.get(0).getFile(context);
                        Glide.with(context).load(imgFile).into(ivWallpaper);
                        ivWallpaper.setVisibility(View.VISIBLE);
                        flEmpty.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    @Override
    public void onClick(View v) {
        showImagePicker();
    }
}
