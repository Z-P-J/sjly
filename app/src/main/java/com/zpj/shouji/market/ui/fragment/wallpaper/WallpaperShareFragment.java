package com.zpj.shouji.market.ui.fragment.wallpaper;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.core.IHttp;
import com.zpj.matisse.CaptureMode;
import com.zpj.matisse.Matisse;
import com.zpj.matisse.MimeType;
import com.zpj.matisse.engine.impl.GlideEngine;
import com.zpj.matisse.entity.IncapableCause;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.filter.Filter;
import com.zpj.matisse.listener.OnSelectedListener;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.WallpaperApi;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.widget.ChatPanel;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WallpaperShareFragment extends BaseFragment
        implements ChatPanel.OnOperationListener,
        View.OnClickListener {

    private final List<WallpaperTag> wallpaperTags = new ArrayList<>(0);
    private FlowLayout flowLayout;
    private FrameLayout flEmpty;
    private ImageView ivWallpaper;
    private TextView tvShareMode;
    private ChatPanel chatPanel;


    private File imgFile;
    private String tag = "壁纸";
    private boolean isPrivate;

    public static void start() {
        StartFragmentEvent.start(new WallpaperShareFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wallpaper_share;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("分享乐图");
        flowLayout = view.findViewById(R.id.wallpaper_tags);
        flEmpty = view.findViewById(R.id.fl_empty);
        flEmpty.setOnClickListener(this);
        ivWallpaper = view.findViewById(R.id.iv_wallpaper);
        ivWallpaper.setOnClickListener(this);
        chatPanel = view.findViewById(R.id.chat_panel);
        chatPanel.setOnOperationListener(this);
        KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
            chatPanel.onKeyboardHeightChanged(height, 0);
        });

        chatPanel.removeImageAction();
        chatPanel.removeAppAction();
        chatPanel.addAction(R.drawable.ic_image_black_24dp, this);
        tvShareMode = chatPanel.addAction("公开", v -> {
            tvShareMode.setText(isPrivate ? "公开" : "私有");
            isPrivate = !isPrivate;
        });

        initFlowLayout();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        showSoftInput(chatPanel.getEditor());
    }

    @Override
    public boolean onBackPressedSupport() {
        if (chatPanel.isEmotionPanelShow()) {
            chatPanel.hideEmojiPanel();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void sendText(String content) {
        if (imgFile == null) {
            AToast.warning("请选择图片");
            return;
        }
        WallpaperApi.shareWallpaperApi(imgFile, content, tag, isPrivate, this::pop, new IHttp.OnStreamWriteListener() {
            @Override
            public void onBytesWritten(int bytesWritten) {

            }

            @Override
            public boolean shouldContinue() {
                return true;
            }
        });
    }

    @Override
    public void onEmojiSelected(String key) {

    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {

    }

    private void initFlowLayout() {
        WallpaperApi.getWallpaperTags(tags -> {
            flowLayout.setOnItemClickListener((index, v, text) -> {
                tag = text;
                flowLayout.setSelectedPosition(index);
            });
            flowLayout.setSpace(ScreenUtils.dp2pxInt(context, 8));
            flowLayout.setSelectedPosition(0);
            for (WallpaperTag tag : tags) {
                if ("全部".equals(tag.getName())) {
                    continue;
                }
                flowLayout.addItem(tag.getName());
            }
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
