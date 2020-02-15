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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.felix.atoast.library.AToast;
import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionExtClickListener;
import com.lqr.emoji.IEmotionSelectedListener;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.zpj.shouji.market.R;
import com.zpj.utils.KeyboardHeightProvider;
import com.zpj.utils.KeyboardUtil;

public class ChatPanel extends RelativeLayout implements KeyboardHeightProvider.KeyboardHeightObserver {

    public interface OnOperationListener extends IEmotionSelectedListener {

        void sendText(String content);

    }

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



        ivEmoji.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                elEmotion.setVisibility(View.VISIBLE);
                KeyboardUtil.hideSoftInputKeyboard(etEditor);
            } else if (elEmotion.getVisibility() == View.GONE){
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
                KeyboardUtil.hideSoftInputKeyboard(etEditor);
            }
            elEmotion.setVisibility(View.GONE);
            AToast.normal("图片");
        });
        ivApp.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                KeyboardUtil.hideSoftInputKeyboard(etEditor);
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
            elEmotion.getLayoutParams().height = height;
            elEmotion.requestLayout();
        } else {
            if (elEmotion.getVisibility() != View.VISIBLE) {
                elEmotion.setVisibility(View.GONE);
            }
        }
    }

}
