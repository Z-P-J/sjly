package com.zpj.shouji.market.ui.widget.emoji;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.lqr.emoji.MoonUtils;

public class EmojiExpandableTextView extends ExpandableTextView {

    public EmojiExpandableTextView(Context context) {
        this(context, null);
    }

    public EmojiExpandableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiExpandableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            MoonUtils.replaceEmoticons(getContext(), builder, -1, getTextSize(), 0, builder.length());
//            EmojiconHandler.addEmojis(getContext(), builder, mEmojiconSize, mEmojiconTextSize, mTextStart, mTextLength, mUseSystemDefault);
            text = builder;
        }
        super.setText(text, type);
    }

}
