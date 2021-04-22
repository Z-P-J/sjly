package com.zpj.shouji.market.ui.widget.indicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;
import com.zpj.shouji.market.ui.widget.ExpandIcon;
import com.zpj.skin.SkinEngine;

import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpandablePagerTitle extends CommonPagerTitleView {

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


        View view = LayoutInflater.from(context).inflate(R.layout.layout_pager_title_expandable, null, false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(view, params);

        tvTitle = view.findViewById(R.id.tv_title);
        ivExpand = view.findViewById(R.id.iv_expand);

    }

    public void init(ViewPager viewPager, int index, boolean isMe, @NonNull List<String> items) {
        List<String> list = new ArrayList<>();
        for (String item : items) {
            list.add(isMe ? "我的" : "Ta的" + item);
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
//        tvSubTitle.setBackgroundResource(R.drawable.bg_msg_bubble);
//        tvSubTitle.setBackground(subTitleBackground);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        isSelected = false;
        super.onDeselected(index, totalCount);
//        tvSubTitle.setBackground(null);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        tvTitle.setTextColor(color);
        ivExpand.setAlpha((int) ((1 - leavePercent) * 255));
        ivExpand.setArrowColor(color);
//        if (leavePercent == 1) {
//            ivExpand.setVisibility(INVISIBLE);
//        }
//        int subColor = ArgbEvaluatorHolder.eval(leavePercent, mSubSelectedColor, mSubNormalColor);
//        tvSubTitle.setTextColor(subColor);
//        subTitleBackground.setAlpha((int) ((1 - leavePercent) * 255));
//        tvSubTitle.setBackground(subTitleBackground);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        tvTitle.setTextColor(color);
        ivExpand.setAlpha((int) (enterPercent * 255));
        ivExpand.setArrowColor(color);
//        if (enterPercent == 1) {
//            ivExpand.setVisibility(VISIBLE);
//        }
//        int subColor = ArgbEvaluatorHolder.eval(enterPercent, mSubNormalColor, mSubSelectedColor);
//        tvSubTitle.setTextColor(subColor);
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
