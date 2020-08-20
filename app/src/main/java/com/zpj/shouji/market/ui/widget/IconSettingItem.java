package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.shehuan.niv.NiceImageView;
import com.zpj.shouji.market.R;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.tinted.TintedImageView;

public class IconSettingItem extends CommonSettingItem {

    private NiceImageView rightIcon;

    public IconSettingItem(Context context) {
        this(context, null);
    }

    public IconSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconSettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void inflateRightContainer(ViewStub viewStub) {
        if (!showRightArrow) {
            return;
        }
        viewStub.setLayoutResource(R.layout.item_image);
        viewStub.setInflatedId(R.id.iv_right_icon);
        rightIcon = (NiceImageView) viewStub.inflate();
        ViewGroup.LayoutParams params = rightIcon.getLayoutParams();
        int maxSize = ScreenUtils.dp2pxInt(rightIcon.getContext(), 36);
        params.height = maxSize;
        params.width = maxSize;
        rightIcon.setMaxHeight(maxSize);
        rightIcon.setMaxWidth(maxSize);
        rightIcon.setBorderWidth(0);
        rightIcon.setCornerRadius(4);
        if (mRightIcon != null) {
            rightIcon.setImageDrawable(mRightIcon);
        }
    }

    public NiceImageView getRightIcon() {
        return rightIcon;
    }

}
