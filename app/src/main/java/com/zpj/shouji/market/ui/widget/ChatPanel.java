/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionExtClickListener;
import com.lqr.emoji.IEmotionSelectedListener;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.matisse.CaptureMode;
import com.zpj.matisse.Matisse;
import com.zpj.matisse.MimeType;
import com.zpj.matisse.engine.impl.GlideEngine;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.listener.OnSelectedListener;
import com.zpj.matisse.model.SelectedItemManager;
import com.zpj.matisse.ui.widget.CustomImageViewerPopup;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.MyRequestOptions;
import com.zpj.utils.KeyboardHeightProvider;
import com.zpj.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatPanel extends RelativeLayout
        implements KeyboardHeightProvider.KeyboardHeightObserver {

    public interface OnOperationListener extends IEmotionSelectedListener {

        void sendText(String content);

    }

    private final List<Item> imgList = new ArrayList<>();
    private EmojiconEditText etEditor;
    private ImageView ivEmoji;
    private ImageView ivImage;
    private ImageView ivApp;
    private ImageView ivSend;
    //    private RelativeLayout rlEmojiPanel;
    private EmotionLayout elEmotion;

    private OnOperationListener listener;

    private boolean isKeyboardShowing;

    public ChatPanel(Context context) {
        super(context);
        init(context);
    }

    public ChatPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View root = View.inflate(context, R.layout.layout_chat_panel, null);
        this.addView(root);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initWidget();
    }

    public boolean isEmotionPanelShow() {
        return !isKeyboardShowing && elEmotion.getVisibility() == VISIBLE;
    }

    private void initWidget() {
        etEditor = findViewById(R.id.et_editor);
        ivImage = findViewById(R.id.iv_image);
        ivEmoji = findViewById(R.id.iv_emoji);
        ivApp = findViewById(R.id.iv_app);
        ivSend = findViewById(R.id.iv_send);
//        rlEmojiPanel = findViewById(R.id.rl_emoji_panel);
        elEmotion = findViewById(R.id.el_emotion);
        elEmotion.attachEditText(etEditor);
        elEmotion.setEmotionSelectedListener(listener);
        elEmotion.setEmotionAddVisiable(false);
        elEmotion.setEmotionSettingVisiable(false);
        elEmotion.setEmotionExtClickListener(new IEmotionExtClickListener() {
            @Override
            public void onEmotionAddClick(View view) {
                AToast.normal("add");
            }

            @Override
            public void onEmotionSettingClick(View view) {
                AToast.normal("setting");
            }
        });

        EasyRecyclerView<Item> recyclerView = new EasyRecyclerView<>(findViewById(R.id.rv_img));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false))
                .setItemRes(R.layout.item_image_square)
                .setData(imgList)
                .onBindViewHolder((holder, list, position, payloads) -> {
                    ImageView img = holder.getImageView(R.id.iv_img);
                    Glide.with(getContext())
                            .load(list.get(position).uri)
                            .apply(MyRequestOptions.DEFAULT_OPTIONS)
                            .into(img);

                    holder.setOnItemClickListener(v -> {
                        SelectedItemManager.OnCheckStateListener listener = () -> {
                            imgList.clear();
                            imgList.addAll(SelectedItemManager.getInstance().asList());
                            recyclerView.notifyDataSetChanged();
                            if (imgList.isEmpty()) {
                                recyclerView.getRecyclerView().setVisibility(GONE);
                            }
                        };
                        CustomImageViewerPopup.with(getContext())
                                .setImageUrls(imgList)
                                .setSrcView(img, holder.getAdapterPosition())
                                .setSrcViewUpdateListener((popupView, pos) -> {
                                    int layoutPos = recyclerView.getRecyclerView().indexOfChild(holder.getItemView());
                                    View view = recyclerView.getRecyclerView().getChildAt(layoutPos + pos - position);
                                    ImageView imageView;
                                    if (view != null) {
                                        imageView = view.findViewById(R.id.iv_img);
                                    } else {
                                        imageView = img;
                                    }
                                    popupView.updateSrcView(imageView);
                                })
                                .setOnShowListener(() -> SelectedItemManager.getInstance().addOnCheckStateListener(listener))
                                .setOnDismissListener(() -> SelectedItemManager.getInstance().removeOnCheckStateListener(listener))
                                .show();
                    });
                })
                .build();


        ivEmoji.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                elEmotion.setVisibility(View.VISIBLE);
                KeyboardUtils.hideSoftInputKeyboard(etEditor);
            } else if (elEmotion.getVisibility() == View.GONE) {
                elEmotion.setVisibility(View.VISIBLE);
            } else {
                elEmotion.setVisibility(View.GONE);
            }
        });
        ivSend.setOnClickListener(v -> {
            String content = etEditor.getText().toString();
            if (TextUtils.isEmpty(content)) {
                AToast.warning("内容为空！");
                return;
            }
            if (listener != null) {
                listener.sendText(content);
                etEditor.setText(null);
            }
        });
        ivImage.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                KeyboardUtils.hideSoftInputKeyboard(etEditor);
            }
            elEmotion.setVisibility(View.GONE);
            AToast.normal("图片");
            Matisse.from((SupportActivity) getContext())
                    .choose(MimeType.ofImage())//照片视频全部显示MimeType.allOf()
                    .countable(true)//true:选中后显示数字;false:选中后显示对号
                    .maxSelectable(3)//最大选择数量为9
                    //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                    .gridExpectedSize(this.getResources().getDimensionPixelSize(R.dimen.photo))//图片显示表格的大小
                    .spanCount(3)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                    .thumbnailScale(0.85f)//缩放比例
                    .imageEngine(new GlideEngine())//图片加载方式，Glide4需要自定义实现
                    .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                    //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                    .capture(true, CaptureMode.All)//存储到哪里
                    .setOnSelectedListener(new OnSelectedListener() {
                        @Override
                        public void onSelected(@NonNull List<Item> itemList) {
                            recyclerView.getRecyclerView().setVisibility(VISIBLE);
                            imgList.clear();
                            imgList.addAll(itemList);
                            recyclerView.notifyDataSetChanged();
                        }
                    })
                    .start();
        });
        ivApp.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                KeyboardUtils.hideSoftInputKeyboard(etEditor);
            }
            elEmotion.setVisibility(View.GONE);
            AToast.normal("app");
        });
    }

    public EmojiconEditText getEditor() {
        return etEditor;
    }

//    public void backspace() {
//        if (etEditor == null) {
//            return;
//        }
//        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0,
//                0, KeyEvent.KEYCODE_ENDCALL);
//        etEditor.dispatchKeyEvent(event);
//    }

    public void hideEmojiPanel() {
        elEmotion.setVisibility(GONE);
    }


    public void setOnOperationListener(OnOperationListener onOperationListener) {
        this.listener = onOperationListener;
        if (elEmotion != null) {
            elEmotion.setEmotionSelectedListener(listener);
        }
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        isKeyboardShowing = height > 0;
        if (height != 0) {
            elEmotion.setVisibility(View.INVISIBLE);
//            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    float v = (float) animation.getAnimatedValue();
//                    elEmotion.getLayoutParams().height = (int) (v * height);
//                    elEmotion.requestLayout();
//                }
//            });
//            animator.setDuration(200);
//            animator.start();
            ViewGroup.LayoutParams params = elEmotion.getLayoutParams();
            params.height = height;
            elEmotion.setLayoutParams(params);
//            elEmotion.invalidate();
//            elEmotion.requestLayout();
        } else {
            if (elEmotion.getVisibility() != View.VISIBLE) {
                elEmotion.setVisibility(View.GONE);
            }
        }
    }

}
