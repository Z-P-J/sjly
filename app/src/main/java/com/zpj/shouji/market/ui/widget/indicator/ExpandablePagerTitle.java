package com.zpj.shouji.market.ui.widget.indicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;
import com.zpj.shouji.market.ui.widget.ExpandIcon;
import com.zpj.skin.SkinEngine;

import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpandablePagerTitle extends CommonPagerTitleView {

    private final LinearLayout llContainer;
    private final TextView tvTitle;
    private final ExpandIcon ivExpand;

    private int mSelectedColor;
    protected int mNormalColor;

    private boolean isSelected = false;

    private int currentPosition = 0;

    public ExpandablePagerTitle(Context context) {
        super(context);

        mNormalColor = SkinEngine.getColor(context, R.attr.textColorMajor);
        mSelectedColor = context.getResources().getColor(R.color.colorPrimary);


        llContainer = new LinearLayout(context);
        llContainer.setGravity(Gravity.CENTER);
        tvTitle = new TextView(context);
        tvTitle.setMaxLines(1);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        llContainer.addView(tvTitle, params);

        ivExpand = new ExpandIcon(context);
        ivExpand.setArrowColor(context.getResources().getColor(R.color.colorPrimary));
        ivExpand.setRoundedCorner(true);
        int size = UIUtil.dip2px(context, 14);
        params = new LinearLayout.LayoutParams(size, size);
        params.gravity = Gravity.CENTER_VERTICAL;
        llContainer.addView(ivExpand, params);

        LayoutParams p = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER;
        addView(llContainer, p);

    }

    public void init(ViewPager viewPager, int index, boolean isMe, @NonNull List<String> items) {
        List<String> list = new ArrayList<>();
        for (String item : items) {
            list.add((isMe ? "我的" : "Ta的") + item);
        }

        tvTitle.setText(list.get(currentPosition));

        setOnClickListener(view -> {
            if (isSelected && list.size() > 1) {
                ivExpand.switchState();
                new RecyclerPartShadowDialogFragment()
                        .addItems(list)
                        .setSelectedItem(currentPosition)
                        .setOnItemClickListener((view1, title, position) -> {
                            currentPosition = position;
                            tvTitle.setText(title);
                            RxBus.post(Actions.ACTION_SEND_VIEW_PAGER_INDEX, position);
                        })
                        .setAttachView(ExpandablePagerTitle.this)
                        .setOnDismissListener(ivExpand::switchState)
                        .show(getContext());
            } else {
                viewPager.setCurrentItem(index);
            }
        });
    }

    public void init(ViewPager viewPager, int index, boolean isMe, String...items) {
        init(viewPager, index, isMe, Arrays.asList(items));
    }

    @Override
    public void onSelected(int index, int totalCount) {
        isSelected = true;
        super.onSelected(index, totalCount);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        isSelected = false;
        super.onDeselected(index, totalCount);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        tvTitle.setTextColor(color);
        ivExpand.setAlpha((int) ((1 - leavePercent) * 255));
        ivExpand.setArrowColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        tvTitle.setTextColor(color);
        ivExpand.setAlpha((int) (enterPercent * 255));
        ivExpand.setArrowColor(color);
    }

    @Override
    public int getContentLeft() {
        return (getLeft() + llContainer.getLeft()) - Math.min(UIUtil.dip2px(getContext(), 8), llContainer.getLeft());
    }

    @Override
    public int getContentRight() {
        return getLeft() + getRight() - getContentLeft();
    }

    public void setTitle(CharSequence text) {
        tvTitle.setText(text);
    }

    public void setNormalColor(int mNormalColor) {
        this.mNormalColor = mNormalColor;
    }

    public void setSelectedColor(int mSelectedColor) {
        this.mSelectedColor = mSelectedColor;
    }

}
