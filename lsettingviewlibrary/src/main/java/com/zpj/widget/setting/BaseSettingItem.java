package com.zpj.widget.setting;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.widget.R;


/**
 * 作者：Leon
 * 时间：2016/12/21 10:32
 * Modified by Z-P-J
 */
public abstract class BaseSettingItem extends RelativeLayout implements ViewStub.OnInflateListener {

    protected LinearLayout llContentContainer;
    protected TextView tvTitle;
    protected TextView tvInfo;
    protected ViewStub vsLeftIcon;
    protected ViewStub vsRightText;
    protected ViewStub vsInfoButton;
    protected ViewStub vsRightContainer;

    protected View inflatedLeftIcon;
    protected View inflatedRightText;
    protected View inflatedInfoButton;
    protected View inflatedRightContainer;

    private LayoutParams contentContainerParams;

    private boolean mEnable = true;

    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onItemClick();
        }
    };

    public BaseSettingItem(Context context) {
        this(context, null);
    }

    public BaseSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getMinimumHeight() == 0) {
            setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.setting_item_min_height));
        }
        if (getPaddingStart() == 0 || getPaddingEnd() == 0) {
            int padding = getResources().getDimensionPixelSize(R.dimen.setting_item_default_padding);
            setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
        }

        initView(context);
        initAttribute(context, attrs);
        setOnClickListener(onClickListener);
        inflateLeftIcon(vsLeftIcon);
        inflateRightContainer(vsRightContainer);
        inflateInfoButton(vsInfoButton);
        inflateRightText(vsRightText);
    }

    private void initView(Context context) {
        Log.d("BaseSettingItem", "initView ps=" + getPaddingStart() + " pe=" + getPaddingEnd()
                + " pt=" + getPaddingTop() + " pb=" + getPaddingBottom());
        View mView = LayoutInflater.from(context).inflate(R.layout.z_item_setting, this, true);
        llContentContainer = mView.findViewById(R.id.ll_content_container);
        contentContainerParams = (LayoutParams) llContentContainer.getLayoutParams();
        tvTitle = mView.findViewById(R.id.tv_title);
        tvInfo = mView.findViewById(R.id.tv_info);
        vsLeftIcon = mView.findViewById(R.id.vs_left_icon);
        vsInfoButton = mView.findViewById(R.id.vs_info);
        vsRightText = mView.findViewById(R.id.vs_right_text);
        vsRightContainer = mView.findViewById(R.id.vs_right_container);

        vsLeftIcon.setOnInflateListener(this);
        vsInfoButton.setOnInflateListener(this);
        vsRightText.setOnInflateListener(this);
        vsRightContainer.setOnInflateListener(this);
    }

    public void setEnable(boolean enable) {
        mEnable = enable;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (l == onClickListener) {
            super.setOnClickListener(l);
        }
    }

    @Override
    public void onInflate(ViewStub stub, View inflated) {
        Log.d("BaseSettingItem", "onInflate ps=" + getPaddingStart() + " pe=" + getPaddingEnd()
                + " pt=" + getPaddingTop() + " pb=" + getPaddingBottom());
        LayoutParams params = (LayoutParams) inflated.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        int inflatedId = getInflatedId(stub, inflated);
        if (stub == vsLeftIcon) {
            inflatedLeftIcon = inflated;
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            contentContainerParams.addRule(END_OF, inflatedId);
            contentContainerParams.setMarginStart(getPaddingStart());
        } else if (stub == vsRightContainer) {
            inflatedRightContainer = inflated;
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            contentContainerParams.addRule(RelativeLayout.START_OF, inflatedId);
            contentContainerParams.setMarginEnd(getPaddingStart());
        } else if (stub == vsInfoButton) {
            inflatedInfoButton = inflated;
            if (inflatedRightContainer != null) {
                params.addRule(RelativeLayout.START_OF, getInflatedId(vsRightContainer, inflatedRightContainer));
                params.setMarginEnd(getPaddingEnd());
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
            }
            contentContainerParams.addRule(RelativeLayout.START_OF, inflatedId);
        } else if (stub == vsRightText) {
            inflatedRightText = inflated;
            if (inflatedRightContainer != null && inflatedInfoButton == null) {
                params.addRule(RelativeLayout.START_OF, getInflatedId(vsRightContainer, inflatedRightContainer));
                params.setMarginEnd(getPaddingEnd());
            } else if (inflatedInfoButton != null) {
                params.addRule(RelativeLayout.START_OF, getInflatedId(vsInfoButton, inflatedInfoButton));
                params.setMarginEnd(getPaddingEnd());
            }  else {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
            }
            contentContainerParams.addRule(RelativeLayout.START_OF, inflatedId);
        }
    }

    private int getInflatedId(ViewStub stub, View inflated) {
        int inflatedId;
        if (stub.getInflatedId() == NO_ID) {
            if (inflated.getId() == NO_ID) {
                inflated.setId(stub.getId());
            }
            inflatedId = inflated.getId();
        } else {
            inflatedId = stub.getInflatedId();
        }
        return inflatedId;
    }

    public abstract void initAttribute(final Context context, AttributeSet attrs);

    public abstract void onItemClick();

    public abstract void inflateRightText(ViewStub viewStub);

    public abstract void inflateRightContainer(ViewStub viewStub);

    public abstract void inflateInfoButton(ViewStub viewStub);

    public abstract void inflateLeftIcon(ViewStub viewStub);


}

