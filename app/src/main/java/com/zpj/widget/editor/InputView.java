package com.zpj.widget.editor;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.editor.validator.EmptyValidator;
import com.zpj.widget.editor.validator.Validator;
import com.zpj.skin.SkinEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class InputView extends FrameLayout
        implements View.OnFocusChangeListener, TextWatcher, SubmitView.EditTextWrapper {


    private LinearLayout llLeftContainer;
    private LinearLayout llRightContainer;

    private ZEditText mEditText;
    private View mBottomLine;
    private TextView mHelperTextView;
    private int mViewHeightFocus;
    protected int mViewColorFocus;
    private int mViewHeightNormal;
    protected int mViewColorNormal;
    protected int mViewColorError;
    private boolean isEmpty = true;

    private boolean firstFocus = false;


    private final List<Validator> validators = new ArrayList<>();
    private boolean allowEmpty = false;


    public InputView(Context context) {
        this(context, null);
    }

    public InputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    @Override
    public ZEditText getEditText() {
        return mEditText;
    }

    @Override
    public void setError(CharSequence error) {
//        mEditText.setError(error);
        mHelperTextView.setText(error);
        if (TextUtils.isEmpty(error)) {
//            if (getEditText().getText() != null) {
//                mHelperTextView.setText("" + getEditText().getText().toString().length());
//            } else {
//                mHelperTextView.setText("");
//            }
            mHelperTextView.setText("");
        }
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

    protected void initViews(Context context, AttributeSet attrs) {

        LayoutInflater.from(context).inflate(R.layout.layout_input_view, this, true);
        llLeftContainer = findViewById(R.id.ll_left_container);
        llRightContainer = findViewById(R.id.ll_right_container);

        allowEmpty(allowEmpty);



//        int etMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics());
        mEditText = findViewById(R.id.et_text);
//        LayoutParams etParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        etParams.leftMargin = icIconSize * ivIconLeftCount + icIconMargin * (ivIconLeftCount - 1) + etMargin;
//        etParams.rightMargin = icIconSize * ivIconRightCount + icIconMargin * (ivIconRightCount - 1) + etMargin;
//        mEditText.setLayoutParams(etParams);
//        mEditText.setBackgroundColor(Color.TRANSPARENT);
//        mEditText.setBackground(null);

//        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.text_medium));
//        mEditText.setSingleLine();
        mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mEditText.setOnFocusChangeListener(this);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isValid();
                isEmpty = s.toString().length() == 0;
            }
        });
        mEditText.addTextChangedListener(this);

        mViewColorError = ContextCompat.getColor(getContext(), R.color.red);
//        mViewColorError = Color.parseColor("#aadd2727");
        mViewColorNormal = SkinEngine.getColor(getContext(), R.attr.textColorMinor); // ContextCompat.getColor(getContext(), R.color.light_gray_10);
        mViewHeightNormal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().getResources().getDisplayMetrics());
        mViewColorFocus = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mViewHeightFocus = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().getResources().getDisplayMetrics());

        mBottomLine = findViewById(R.id.bottom_line);
        mBottomLine.setBackgroundColor(getResources().getColor(R.color.light_gray_1));

        mHelperTextView = findViewById(R.id.tv_helper);
//        mHelperTextView.setTextSize(12);
//        mHelperTextView.setText("Helper Info");
        mHelperTextView.setText("");
        mHelperTextView.setTextColor(mViewColorError);


        int icIconSize = ScreenUtils.dp2pxInt(context, 16);
        int icIconMargin = ScreenUtils.dp2pxInt(context, 8);
        ImageView[] ivIconLefts = getLeftIcons();
        int ivIconLeftCount = ivIconLefts != null ? ivIconLefts.length : 0;
        for (int i = 0; i < ivIconLeftCount; i++) {
            ImageView ivIconLeft = ivIconLefts[i];
            ivIconLeft.setColorFilter(mViewColorNormal);
            LayoutParams ivIconLeftParams = new LayoutParams(icIconSize, icIconSize);
            ivIconLeftParams.rightMargin = icIconMargin;
            ivIconLeftParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            llLeftContainer.addView(ivIconLeft, ivIconLeftParams);
        }

        icIconSize = ScreenUtils.dp2pxInt(context, 24);
        ImageView[] ivIconRights = getRightIcons();
        int ivIconRightCount = ivIconRights != null ? ivIconRights.length : 0;
        for (int i = 0; i < ivIconRightCount; i++) {
            ImageView ivIconRight = ivIconRights[i];
            ivIconRight.setColorFilter(mViewColorNormal);
            LayoutParams ivIconRightParams = new LayoutParams(icIconSize, icIconSize);
            ivIconRightParams.leftMargin = icIconMargin;
            ivIconRightParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            llRightContainer.addView(ivIconRight, ivIconRightParams);
        }

    }

    public void allowEmpty(boolean empty) {
        if (empty) {
            allowEmpty = false;
            for (int i = validators.size() - 1; i >= 0; --i) {
                Validator validator = validators.get(i);
                if (validator instanceof EmptyValidator) {
                    validators.remove(i);
                }
            }
        } else {
            for (Validator validator : validators) {
                if (validator instanceof EmptyValidator) {
                    return;
                }
            }
            allowEmpty = true;
            validators.add(0, new EmptyValidator("输入内容不能为空"));
        }
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
            color = getResources().getColor(R.color.light_gray_1);
            height = mViewHeightNormal;
        }
        mBottomLine.setBackgroundColor(color);
        mBottomLine.getLayoutParams().height = height;
        mBottomLine.requestLayout();
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
//            String result = testValid();
//            if (!TextUtils.isEmpty(result)) {
//                mHelperTextView.setText(result);
//            }
            isValid();
        }
        firstFocus = true;
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
//        String error = testValid();
//        if (!TextUtils.isEmpty(error)) {
//            mHelperTextView.setText(error);
//        }
        isValid();
        isEmpty = s.toString().length() == 0;
    }

    public void setHint(CharSequence hint) {
        getEditText().setHint(hint);
    }

    public boolean isValid() {
        if (!firstFocus) {
            return true;
        }
        if (validators == null || validators.isEmpty()) {
            setError("");
            return true;
        }
        for (Validator validator : validators) {
            if (!validator.isValid(mEditText)) {
                setError(validator.getErrorMessage());
                return false;
            }
        }
        setError("");
        return true;
//        return TextUtils.isEmpty(testValid());
    }

    public String testValid() {
        if (validators == null || validators.isEmpty()) {
            return null;
        }
        for (Validator validator : validators) {
            if (!validator.isValid(mEditText)) {
                return validator.getErrorMessage();
            }
        }
        return null;
    }

    public void addValidator(Validator validator) {
        validators.add(validator);
    }


    public boolean isAllowEmpty() {
        return allowEmpty;
    }

}
