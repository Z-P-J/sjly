package com.zpj.shouji.market.ui.widget;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.zpj.shouji.market.ui.fragment.WebFragment;

import me.yokeyword.fragmentation.SupportActivity;

public class ClickSpan extends ClickableSpan {

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
//        ds.setColor(Color.parseColor("#ffffff"));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(@NonNull View widget) {

    }

}
