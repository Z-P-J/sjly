package com.zpj.widget.editor;

import android.content.Context;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.zpj.shouji.market.R;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class PasswordInputView extends InputView {

    private ImageView mIvPasswordIcon;
    private ImageView mIvDeleteIcon;
    private ImageView mIcEyeIcon;

    private boolean isHidePwdMode = true;
    private OnPwdFocusChangedListener mOnPwdFocusChangedListener = null;

    public PasswordInputView(Context context) {
        super(context);
    }

    public PasswordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswordInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnPwdFocusChangedListener(OnPwdFocusChangedListener onPwdFocusChangedListener) {
        mOnPwdFocusChangedListener = onPwdFocusChangedListener;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs) {
        super.initViews(context, attrs);
        getEditText().setHint("请输入密码");
        changeFocusMode(false);
        changePwdHideMode(true);
    }

    @Override
    protected ImageView[] getLeftIcons() {
        mIvPasswordIcon = new ImageView(getContext());
        mIvPasswordIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mIvPasswordIcon.setImageResource(R.drawable.ic_password_normal);
        return new ImageView[]{mIvPasswordIcon};
    }

    @Override
    protected ImageView[] getRightIcons() {
        int paddingDelete = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getContext().getResources().getDisplayMetrics());
        mIvDeleteIcon = new ImageView(getContext());
        mIvDeleteIcon.setVisibility(GONE);
        mIvDeleteIcon.setPadding(paddingDelete, paddingDelete, paddingDelete, paddingDelete);
        mIvDeleteIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mIvDeleteIcon.setImageResource(R.drawable.ic_clear_black_24dp);
        mIvDeleteIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditText().setText("");
            }
        });
        int paddingEye = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics());
        mIcEyeIcon = new ImageView(getContext());
        mIcEyeIcon.setVisibility(GONE);
        mIcEyeIcon.setPadding(paddingEye, paddingEye, paddingEye, paddingEye);
        mIcEyeIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mIcEyeIcon.setImageResource(R.drawable.ic_eye_normal);
        mIcEyeIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwdHideMode(!isHidePwdMode);
            }
        });
        return new ImageView[]{mIvDeleteIcon, mIcEyeIcon};
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        changeFocusMode(hasFocus);
        if (mOnPwdFocusChangedListener != null) {
            mOnPwdFocusChangedListener.onFocusChanged(hasFocus);
        }
    }

    private void changeFocusMode(boolean focus) {
        if (focus) {
            if (isEmpty()) {
                mIvDeleteIcon.setVisibility(GONE);
            } else {
                mIvDeleteIcon.setVisibility(VISIBLE);
            }
            mIcEyeIcon.setVisibility(VISIBLE);
            mIvPasswordIcon.setColorFilter(mViewColorFocus);
        } else {
            mIvDeleteIcon.setVisibility(GONE);
//            mIcEyeIcon.setVisibility(INVISIBLE);
            mIvPasswordIcon.setColorFilter(mViewColorNormal);
        }
    }

    private void changePwdHideMode(boolean isHidePwdMode) {
        this.isHidePwdMode = isHidePwdMode;
        if (isHidePwdMode) {
            //隐藏密码
            getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
            mIcEyeIcon.setColorFilter(mViewColorNormal);
        } else {
            //显示密码
            getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mIcEyeIcon.setColorFilter(mViewColorFocus);
        }
        getEditText().setText(getEditText().getText().toString());
        getEditText().setSelection(getEditText().getText().toString().length());
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
        if (isEmpty()) {
            mIvDeleteIcon.setVisibility(GONE);
        } else {
            mIvDeleteIcon.setVisibility(VISIBLE);
        }
    }

    public interface OnPwdFocusChangedListener {
        void onFocusChanged(boolean focus);
    }
}
