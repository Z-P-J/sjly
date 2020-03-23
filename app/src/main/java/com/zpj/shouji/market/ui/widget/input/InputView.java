package com.zpj.shouji.market.ui.widget.input;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.SubmitView;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.editor.ZEditText;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class InputView extends FrameLayout implements View.OnFocusChangeListener, TextWatcher, SubmitView.EditTextWrapper {

    private ZEditText mEditText;
    private View mBottomLine;
    private TextView mHelperTextView;
    private int mViewHeightFocus;
    protected int mViewColorFocus;
    private int mViewHeightNormal;
    protected int mViewColorNormal;
    protected int mViewColorError;
    private boolean isEmpty = true;

    public InputView(Context context) {
        this(context, null);
    }

    public InputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    @Override
    public ZEditText getEditText() {
        return mEditText;
    }

    @Override
    public void setError(CharSequence error) {
        mEditText.setError(error);
    }

    public View getBottomLine() {
        return mBottomLine;
    }

    public String getText() {
        return mEditText.getText().toString().trim();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    protected void initViews(AttributeSet attrs) {
        int icIconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getContext().getResources().getDisplayMetrics());
        int icIconMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics());
        ImageView[] ivIconLefts = getLeftIcons();
        int ivIconLeftCount = ivIconLefts != null ? ivIconLefts.length : 0;
        for (int i = 0; i < ivIconLeftCount; i++) {
            ImageView ivIconLeft = ivIconLefts[i];
            LayoutParams ivIconLeftParams = new LayoutParams(icIconSize, icIconSize);
            ivIconLeftParams.leftMargin = (icIconSize + icIconMargin) * i;
            ivIconLeftParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            addView(ivIconLeft, ivIconLeftParams);
        }
        ImageView[] ivIconRights = getRightIcons();
        int ivIconRightCount = ivIconRights != null ? ivIconRights.length : 0;
        for (int i = 0; i < ivIconRightCount; i++) {
            ImageView ivIconRight = ivIconRights[i];
            LayoutParams ivIconRightParams = new LayoutParams(icIconSize, icIconSize);
            ivIconRightParams.rightMargin = (icIconSize + icIconMargin) * i;
            ivIconRightParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            addView(ivIconRight, ivIconRightParams);
        }

        int etMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics());
        mEditText = new ZEditText(getContext());
        LayoutParams etParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        etParams.leftMargin = icIconSize * ivIconLeftCount + icIconMargin * (ivIconLeftCount - 1) + etMargin;
        etParams.rightMargin = icIconSize * ivIconRightCount + icIconMargin * (ivIconRightCount - 1) + etMargin;
        mEditText.setLayoutParams(etParams);
        mEditText.setBackgroundColor(Color.TRANSPARENT);
        mEditText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_text_major));
        mEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.color_text_minor));
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.text_medium));
        mEditText.setSingleLine();
        mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mEditText.setOnFocusChangeListener(this);
        mEditText.addTextChangedListener(this);
        addView(mEditText);

        mViewColorError = ContextCompat.getColor(getContext(), R.color.red5);
        mViewColorNormal = ContextCompat.getColor(getContext(), R.color.color_text_normal);
        mViewHeightNormal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources().getDisplayMetrics());
        mViewColorFocus = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mViewHeightFocus = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics());

        mBottomLine = new View(getContext());
        LayoutParams vParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mViewHeightNormal);
        vParams.gravity = Gravity.BOTTOM;
        vParams.bottomMargin = ScreenUtils.dp2pxInt(getContext(), 14);
        mBottomLine.setLayoutParams(vParams);
        mBottomLine.setBackgroundColor(mViewColorNormal);
        addView(mBottomLine);

        mHelperTextView = new TextView(getContext());
        mHelperTextView.setTextSize(12);
        vParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vParams.gravity = Gravity.BOTTOM;
        mHelperTextView.setLayoutParams(vParams);
        addView(mHelperTextView);
    }

    protected ImageView[] getLeftIcons() {
        return null;
    }

    protected ImageView[] getRightIcons() {
        return null;
    }

    private void changeBottomStyle(final boolean hasFocus) {
        final int height;
        final int color;
        if (hasFocus) {
            color = mViewColorFocus;
            height = mViewHeightFocus;
        } else {
            color = mViewColorNormal;
            height = mViewHeightNormal;
        }
        mBottomLine.setBackgroundColor(color);
        mBottomLine.getLayoutParams().height = height;
        mBottomLine.requestLayout();
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        changeBottomStyle(hasFocus);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String error = mEditText.testValid();
        if (!TextUtils.isEmpty(error)) {

        }
        isEmpty = s.toString().length() == 0;
    }
}
