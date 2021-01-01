package com.zpj.recyclerview.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class CustomizedViewHolder extends BaseViewHolder {

    protected LinearLayout container;

    public CustomizedViewHolder() {
        super(0);
    }

    @Override
    public final View onCreateView(final Context context) {
        super.onCreateView(context);
        this.container = new LinearLayout(context);
        this.container.setGravity(Gravity.CENTER);
        this.container.setOrientation(LinearLayout.VERTICAL);
        this.view = this.container;
        return this.view;
    }

    @Override
    public void onDestroyView() {
        this.container = null;
        super.onDestroyView();
    }

    public void addView(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        this.container.addView(view);
    }

    protected TextView addTextViewWithPadding(int textId, int color) {
        return addTextViewWithPadding(textId, color, 14);
    }

    protected TextView addTextViewWithPadding(String text, int color) {
        return addTextViewWithPadding(text, color, 14);
    }

    protected TextView addTextViewWithPadding(int textId, int color, int size) {
        int padding = context.getResources().getDimensionPixelSize(R.dimen.text_padding);
        return addTextView(context.getResources().getString(textId), color, size, padding);
    }

    protected TextView addTextViewWithPadding(String text, int color, int size) {
        int padding = context.getResources().getDimensionPixelSize(R.dimen.text_padding);
        return addTextView(text, color, size, padding);
    }

    protected TextView addTextView(int textId, int color) {
        return addTextView(context.getResources().getString(textId), color, 14, 0);
    }

    protected TextView addTextView(String text, int color) {
        return addTextView(text, color, 14, 0);
    }

    protected TextView addTextView(int textId, int color, int size) {
        return addTextView(context.getString(textId), color, size, 0);
    }

    protected TextView addTextView(String text, int color, int size) {
        return addTextView(text, color, size, 0);
    }

    protected TextView addTextView(String text, int color, int size, int padding) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(size);
        textView.setPadding(padding, padding, padding, padding);
        textView.setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        addView(textView);
        return textView;
    }

    protected ImageView addImageView(int imgId) {
        return addImageView(context.getResources().getDrawable(imgId));
    }

    protected ImageView addImageView(Drawable drawable) {
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(drawable);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        addView(imageView);
        return imageView;
    }

}
