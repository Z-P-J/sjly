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
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.toast.ZToast;
import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionExtClickListener;
import com.lqr.emoji.IEmotionSelectedListener;
import com.zpj.matisse.entity.Item;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.ThemeUtils;
import com.zpj.utils.KeyboardHeightProvider;
import com.zpj.utils.KeyboardUtils;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ActionPanel extends RelativeLayout
        implements KeyboardHeightProvider.KeyboardHeightObserver {

    public interface OnOperationListener extends IEmotionSelectedListener {

        void sendText(String content);

    }

    private final List<Item> imgList = new ArrayList<>();
    private EditText etEditor;
    private LinearLayout llActionsContainer;
    private ImageView ivEmoji;
//    private ImageView ivImage;
//    private ImageView ivApp;
    private ImageView ivSend;
    //    private RelativeLayout rlEmojiPanel;
    private EmotionLayout elEmotion;

    private OnOperationListener listener;

    private boolean isKeyboardShowing;

    public ActionPanel(Context context) {
        super(context);
        init(context);
    }

    public ActionPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActionPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View root = View.inflate(context, R.layout.layout_panel_action, null);
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
        llActionsContainer = findViewById(R.id.ll_actions_container);
//        ivImage = findViewById(R.id.iv_image);
        ivEmoji = findViewById(R.id.iv_emoji);
//        ivEmoji.setColorFilter(ThemeUtils.getTextColorMajor(getContext()));
//        ivApp = findViewById(R.id.iv_app);
        ivSend = findViewById(R.id.iv_send);
//        ivSend.setColorFilter(ThemeUtils.getTextColorMajor(getContext()));
//        rlEmojiPanel = findViewById(R.id.rl_emoji_panel);
        elEmotion = findViewById(R.id.el_emotion);
        elEmotion.setEmotionSelectedListener(listener);
        elEmotion.setEmotionAddVisiable(false);
        elEmotion.setEmotionSettingVisiable(false);
        elEmotion.setEmotionExtClickListener(new IEmotionExtClickListener() {
            @Override
            public void onEmotionAddClick(View view) {
                ZToast.normal("add");
            }

            @Override
            public void onEmotionSettingClick(View view) {
                ZToast.normal("setting");
            }
        });

//        EasyRecyclerView<Item> recyclerView = new EasyRecyclerView<>(findViewById(R.id.rv_img));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false))
//                .setItemRes(R.layout.item_image_square)
//                .addItemDecoration(new Y_DividerItemDecoration(getContext()) {
//                    @Override
//                    public Y_Divider getDivider(int itemPosition) {
//                        Y_DividerBuilder builder = null;
//                        if (itemPosition == 0) {
//                            builder = new Y_DividerBuilder()
//                                    .setLeftSideLine(true, Color.WHITE, 12, 0, 0);
//                        } else if (itemPosition == imgList.size() - 1) {
//                            builder = new Y_DividerBuilder()
//                                    .setRightSideLine(true, Color.WHITE, 12, 0, 0);
//                        } else {
//                            builder = new Y_DividerBuilder();
//                        }
//                        return builder.setTopSideLine(true, Color.WHITE, 4, 0, 0)
//                                .setBottomSideLine(true, Color.WHITE, 4, 0, 0)
//                                .create();
//                    }
//                })
//                .setData(imgList)
//                .onBindViewHolder((holder, list, position, payloads) -> {
//                    ImageView img = holder.getImageView(R.id.iv_img);
//                    Glide.with(getContext())
//                            .load(list.get(position).uri)
//                            .apply(MyRequestOptions.DEFAULT_OPTIONS)
//                            .into(img);
//
//                    holder.setOnItemClickListener(v -> {
//                        CustomImageViewerPopup.with(getContext())
//                                .setOnSelectedListener(itemList -> {
//                                    postDelayed(() -> {
//                                        imgList.clear();
//                                        imgList.addAll(itemList);
//                                        recyclerView.notifyDataSetChanged();
//                                        if (imgList.isEmpty()) {
//                                            recyclerView.getRecyclerView().setVisibility(GONE);
//                                        }
//                                    }, 100);
//                                })
//                                .setImageUrls(imgList)
//                                .setSrcView(img, holder.getAdapterPosition())
//                                .setSrcViewUpdateListener((popupView, pos) -> {
//                                    int layoutPos = recyclerView.getRecyclerView().indexOfChild(holder.getItemView());
//                                    View view = recyclerView.getRecyclerView().getChildAt(layoutPos + pos - position);
//                                    ImageView imageView;
//                                    if (view != null) {
//                                        imageView = view.findViewById(R.id.iv_img);
//                                    } else {
//                                        imageView = img;
//                                    }
//                                    popupView.updateSrcView(imageView);
//                                })
//                                .show();
//                    });
//                })
//                .onViewClick(R.id.iv_close, new IEasy.OnClickListener<Item>() {
//                    @Override
//                    public void onClick(EasyViewHolder holder, View view, Item data) {
//                        imgList.remove(data);
//                        if (imgList.isEmpty()) {
//                            recyclerView.getRecyclerView().setVisibility(GONE);
//                        } else {
////                            recyclerView.notifyItemRemoved(holder.getRealPosition());
//                            recyclerView.notifyDataSetChanged();
//                        }
//                    }
//                })
//                .build();


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
            KeyboardUtils.hideSoftInputKeyboard(etEditor);
            String content = etEditor.getText().toString();
            if (TextUtils.isEmpty(content)) {
                ZToast.warning("内容为空！");
                return;
            }
            if (listener != null) {
                listener.sendText(content);
            }
        });
//        ivImage.setOnClickListener(v -> {
//            if (isKeyboardShowing) {
//                KeyboardUtils.hideSoftInputKeyboard(etEditor);
//            }
//            elEmotion.setVisibility(View.GONE);
//            ZToast.normal("图片");
//            GetMainActivityEvent.post(obj -> Matisse.from(obj)
//                    .choose(MimeType.ofImage())//照片视频全部显示MimeType.allOf()
//                    .countable(true)//true:选中后显示数字;false:选中后显示对号
//                    .maxSelectable(9)//最大选择数量为9
//                    //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
////                    .gridExpectedSize(this.getResources().getDimensionPixelSize(R.dimen.photo))//图片显示表格的大小
//                    .spanCount(3)
//                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
//                    .thumbnailScale(0.85f)//缩放比例
//                    .imageEngine(new GlideEngine())//图片加载方式，Glide4需要自定义实现
//                    .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
//                    //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
//                    .setDefaultSelection(imgList)
//                    .capture(true, CaptureMode.All)//存储到哪里
//                    .setOnSelectedListener(new OnSelectedListener() {
//                        @Override
//                        public void onSelected(@NonNull List<Item> itemList) {
//                            recyclerView.getRecyclerView().setVisibility(VISIBLE);
////                                    imgList.clear();
////                                    imgList.addAll(itemList);
//                            recyclerView.notifyDataSetChanged();
//                        }
//                    })
//                    .start());
//
//        });
//        ivApp.setOnClickListener(v -> {
//            if (isKeyboardShowing) {
//                KeyboardUtils.hideSoftInputKeyboard(etEditor);
//            }
//            elEmotion.setVisibility(View.GONE);
//            ZToast.normal("app");
//        });
    }

    public void attachEditText(EditText etEditor) {
        this.etEditor = etEditor;
        elEmotion.attachEditText(etEditor);
    }

//    public void removeImageAction() {
//        llActionsContainer.removeView(ivImage);
//    }

//    public void removeAppAction() {
//        llActionsContainer.removeView(ivApp);
//    }

    public void setSendAction(OnClickListener listener) {
        ivSend.setOnClickListener(listener);
    }

//    public void removeSendAction() {
//        llActionsContainer.removeView(ivSend);
//    }

    public ImageView addAction(@DrawableRes int res, OnClickListener listener) {
        ImageView imageView = new ImageView(getContext());
        imageView.setOnClickListener(listener);
        imageView.setImageResource(res);
//        int size = ScreenUtils.dp2pxInt(getContext(), 24);
        int margin = ScreenUtils.dp2pxInt(getContext(), 8);
        MarginLayoutParams params = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = margin;
        params.rightMargin = margin;
        imageView.setLayoutParams(params);
        imageView.setColorFilter(ThemeUtils.getTextColorMajor(getContext()));
//        int padding = ScreenUtils.dp2pxInt(getContext(), 6);
//        imageView.setPadding(padding, padding, padding, padding);
        llActionsContainer.addView(imageView);
        return imageView;
    }

    public TextView addAction(String text, OnClickListener listener) {
        TextView textView = new TextView(getContext());
        textView.setOnClickListener(listener);
        textView.setText(text);
        int margin = ScreenUtils.dp2pxInt(getContext(), 8);
        MarginLayoutParams params = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = margin;
        params.rightMargin = margin;
        textView.setLayoutParams(params);
        textView.setTextColor(ThemeUtils.getTextColorMajor(getContext()));
//        int padding = ScreenUtils.dp2pxInt(getContext(), 6);
//        textView.setPadding(padding, padding, padding, padding);
        llActionsContainer.addView(textView);
        return textView;
    }

    public EditText getEditor() {
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
