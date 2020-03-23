package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.input.AccountInputView;
import com.zpj.shouji.market.ui.widget.input.EmailInputView;
import com.zpj.widget.editor.PasswordEditText;
import com.zpj.widget.editor.ZEditText;
import com.zpj.widget.editor.validator.EmailValidator;
import com.zpj.widget.editor.validator.LengthValidator;
import com.zpj.widget.editor.validator.SameValueValidator;
import com.zpj.widget.editor.validator.Validator;

/**
 * @author CuiZhen
 * @date 2019/5/16
 * GitHub: https://github.com/goweii
 */
public class SubmitView extends AppCompatTextView implements TextWatcher {

    private int[] mBindIds;
    private EditTextWrapper[] mEditTexts;
    private boolean mHasInit = false;

    public SubmitView(Context context) {
        this(context, null);
    }

    public SubmitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubmitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SubmitView);
        mBindIds = new int[]{
                typedArray.getResourceId(R.styleable.SubmitView_sv_bindEditText1, NO_ID),
                typedArray.getResourceId(R.styleable.SubmitView_sv_bindEditText2, NO_ID),
                typedArray.getResourceId(R.styleable.SubmitView_sv_bindEditText3, NO_ID),
                typedArray.getResourceId(R.styleable.SubmitView_sv_bindEditText4, NO_ID)
        };
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initViews();
    }

    private void initViews() {
        if (mHasInit) {
            return;
        }
        mHasInit = true;
        mEditTexts = new EditTextWrapper[mBindIds.length];
        for (int i = 0; i < mBindIds.length; i++) {
            View bindView = getRootView().findViewById(mBindIds[i]);
//            EditText editText = null;
//            if (bindView instanceof EditText) {
//                editText = (EditText) bindView;
//            } else
            EditTextWrapper editTextWrapper = null;
            if (bindView instanceof EditTextWrapper) {
                editTextWrapper = (EditTextWrapper) bindView;
//                editText = editTextWrapper.getEditText();
            }
            mEditTexts[i] = editTextWrapper;
            if (mEditTexts[i] != null) {
                mEditTexts[i].getEditText().addTextChangedListener(this);
            }
        }
        for (EditTextWrapper et : mEditTexts) {
            if (et != null) {
                et.getEditText().setText(et.getEditText().getText().toString());
                et.getEditText().setSelection(et.getEditText().getText().toString().length());
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean hasError = false;
        for (int i = 0; i < mEditTexts.length; i++) {
            EditTextWrapper et = mEditTexts[i];
            if (et != null) {
                if (TextUtils.isEmpty(et.getEditText().getText().toString())) {
                    hasError = true;
//                    et.setError("请输入内容");
                    break;
                } else {
//                    Validator validator = null;
//                    if (et instanceof AccountInputView) {
//                        validator = new LengthValidator("账号长度必须在3-20之间", 3, 20);
//                    } else if (et instanceof EmailInputView) {
//                        validator = new EmailValidator("邮箱格式有误");
//                    } else if (et instanceof PasswordEditText) {
//                        if (i > 0 && mEditTexts[i - 1] instanceof PasswordEditText) {
//                            SameValueValidator sameValueValidator =
//                                    new SameValueValidator(mEditTexts[i - 1].getEditText(), "两次输入的密码不相同");
//                            if (!sameValueValidator.isValid(et.getEditText())) {
//                                hasError = true;
//                                et.setError(sameValueValidator.getErrorMessage());
//                                break;
//                            }
//                        }
//                        validator = new LengthValidator("密码长度不能小于6", 6, Integer.MAX_VALUE);
//                    }
//                    if (validator != null && !validator.isValid(et.getEditText())) {
//                        hasError = true;
//                        et.setError(validator.getErrorMessage());
//                        break;
//                    }
                }
            }
        }
        if (hasError) {
            setAlpha(0.7F);
            setEnabled(false);
        } else {
            setAlpha(1.0F);
            setEnabled(true);
        }
    }

    public interface EditTextWrapper {
        ZEditText getEditText();
        void setError(CharSequence error);
    }
}
