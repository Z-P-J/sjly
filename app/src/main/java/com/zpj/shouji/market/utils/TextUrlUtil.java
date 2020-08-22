package com.zpj.shouji.market.utils;

import java.util.regex.Pattern;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.zpj.shouji.market.R;


public class TextUrlUtil {

    private static final String SCHEME_HTTP = "http://";
    private static final String SCHEME_HTTPS = "https://";
    private static final String SCHEME_AT = "at://";
    private static final String SCHEME_TOPIC = "topic://";

    public interface OnClickString {
        void onLinkClick(String link);
        void onAtClick(String at);
        void onTopicClick(String topic);
        void onViewClick(View view);
    }

    public static void dealContent(CharSequence content, TextView textView, int color, OnClickString mOnClickString, OnClickListener listener) {

        textView.setText(getClickableSpan(textView.getContext(), content, color, mOnClickString));
        textView.setMovementMethod(new LinkTouchMovementMethod(listener));
    }


    private static SpannableStringBuilder getClickableSpan(Context c, CharSequence content, int color, OnClickString mOnClickString) {

        SpannableStringBuilder spanableInfo = new SpannableStringBuilder(content);

        SpannableString spanableInfo_temp = new SpannableString(content);

        // 网页链接
        Linkify.addLinks(spanableInfo_temp,
                Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]"),
                SCHEME_HTTP);

        // 网页链接
        Linkify.addLinks(spanableInfo_temp,
                Pattern.compile("https://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]"),
                SCHEME_HTTPS);

//        String schemephone = "phone";
//        Linkify.addLinks(spanableInfo_temp, Pattern.compile("\\d{11}"), schemephone);
        Linkify.addLinks(spanableInfo_temp, Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}"), SCHEME_AT);

        Linkify.addLinks(spanableInfo_temp, Pattern.compile("#[^#]+#"), SCHEME_TOPIC);


//        //国内号码
//        String schemephone2 = "fixphone";
//        Linkify.addLinks(spanableInfo_temp, Pattern.compile("\\d{3}-\\d{8}|\\d{4}-\\d{8}"), schemephone2);

        URLSpan[] urlSpans = spanableInfo_temp.getSpans(0, spanableInfo_temp.length(),
                URLSpan.class);
        for (URLSpan urlSpan : urlSpans) {
            int start = spanableInfo_temp.getSpanStart(urlSpan);
            int end = spanableInfo_temp.getSpanEnd(urlSpan);
            spanableInfo.setSpan(new Clickable(c, urlSpan.getURL(), color, mOnClickString), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        return spanableInfo;
    }

    private static class Clickable extends ClickableSpan implements OnClickListener {
        Context cont;
        String s;

        public int textcolor = 0xffeeeeee;

        private boolean mIsPressed;
        TextUrlUtil.OnClickString mOnClickString;

        public Clickable(Context c, String string, int color, TextUrlUtil.OnClickString mOnClickString) {
            cont = c;
            s = string;
            this.mOnClickString = mOnClickString;
            if (color != -100)
                textcolor = cont.getResources().getColor(color);
            else
                textcolor = cont.getResources().getColor(R.color.colorPrimary);
        }

        public void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void onClick(View v) {
            v.setTag("false");
            Log.d("TextUrlUtil", "s=" + s);
            if (s.startsWith(SCHEME_HTTP) || s.startsWith(SCHEME_HTTPS)) {
                Log.d("TextUrlUtil", "onLinkClick " + s);
                mOnClickString.onLinkClick(s);
            } else if (s.startsWith(SCHEME_AT)) {
                Log.d("TextUrlUtil", "onAtClick " + s);
                mOnClickString.onAtClick(s.replace(SCHEME_AT, ""));
            } else if (s.startsWith(SCHEME_TOPIC)) {
                Log.d("TextUrlUtil", "onTopicClick " + s);
                mOnClickString.onTopicClick(s.replace(SCHEME_TOPIC, ""));
            }
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            // TODO Auto-generated method stub
            tp.setColor(mIsPressed ? cont.getResources().getColor(android.R.color.black) : textcolor);
            tp.bgColor = mIsPressed ? 0xffeeeeee : cont.getResources().getColor(android.R.color.transparent);
            tp.setUnderlineText(false);//设置下划线
            tp.clearShadowLayer();
        }
    }

    public static class LinkTouchMovementMethod extends LinkMovementMethod {

        private final OnClickListener listener;
        private Clickable mPressedSpan;

        public LinkTouchMovementMethod(OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            Log.d("LinkTouchMovementMethod", "onTouchEvent spannable" + spannable);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                Log.d("LinkTouchMovementMethod", "onTouchEvent DOWN mPressedSpan=" + mPressedSpan);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Clickable touchedSpan = getPressedSpan(textView, spannable, event);
                Log.d("LinkTouchMovementMethod", "onTouchEvent MOVE mPressedSpan" + mPressedSpan);
                Log.d("LinkTouchMovementMethod", "onTouchEvent MOVE touchedSpan" + touchedSpan);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                Log.d("LinkTouchMovementMethod", "onTouchEvent other mPressedSpan" + mPressedSpan);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                } else if (event.getAction() == MotionEvent.ACTION_UP){
//                    ViewParent parent = textView.getParent();
//                    if (parent instanceof ViewGroup) {
//                        Log.d("LinkTouchMovementMethod", "onTouchEvent other performClick");
//                        return ((ViewGroup) parent).performClick();
//                    }
                    listener.onClick(textView);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return true;
        }

        private Clickable getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            Clickable[] link = spannable.getSpans(off, off, Clickable.class);
            Clickable touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;

        }


    }

}
