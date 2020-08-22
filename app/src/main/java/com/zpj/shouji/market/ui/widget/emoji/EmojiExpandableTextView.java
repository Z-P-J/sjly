package com.zpj.shouji.market.ui.widget.emoji;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.ctetin.expandabletextviewlibrary.app.LinkType;
import com.lqr.emoji.MoonUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.fragment.theme.TopicThemeListFragment;

public class EmojiExpandableTextView extends ExpandableTextView implements ExpandableTextView.OnLinkClickListener {

    public EmojiExpandableTextView(Context context) {
        this(context, null);
    }

    public EmojiExpandableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiExpandableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int color = context.getResources().getColor(R.color.colorPrimary);
        setSelfTextColor(color);
        setMentionTextColor(color);
        setExpandableLinkTextColor(color);
        setNeedSelf(true);
        setNeedMention(true);
        setNeedLink(true);
        setLinkClickListener(this);
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

    @Override
    public void onLinkClickListener(LinkType type, String content, String selfContent) {
        if (type == LinkType.LINK_TYPE) {
            WebFragment.start(content);
        } else if (type == LinkType.MENTION_TYPE) {
            ProfileFragment.start(content.replace("@", "").trim());
        } else if (type == LinkType.TOPIC_TYPE) {
            TopicThemeListFragment.start(content.replaceAll("#", "").trim());
        } else if (type == LinkType.SELF) {
            ProfileFragment.start(content);
        }
    }
}
