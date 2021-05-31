package com.zpj.shouji.market.ui.widget.emoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.zpj.emoji.EmojiUtils;
import com.zpj.shouji.market.R;

public class EmojiTextView extends AppCompatTextView {

    private int mEmojiconSize;

    public EmojiTextView(Context context) {
        this(context, null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            mEmojiconSize = (int) (getTextSize() * 1.6);
//            mEmojiconSize = -1;
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EmojiTextView);
            mEmojiconSize = (int) a.getDimension(R.styleable.EmojiTextView_z_emojiSize, (int) (getTextSize() * 1.6));
            a.recycle();
        }
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            EmojiUtils.replaceEmoticons(getContext(), builder, mEmojiconSize, getTextSize(), 0, builder.length());
            text = builder;
        }
        super.setText(text, type);
    }
}
