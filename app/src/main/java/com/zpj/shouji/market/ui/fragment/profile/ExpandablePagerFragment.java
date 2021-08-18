package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Actions;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.widget.ZViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;

public abstract class ExpandablePagerFragment extends BaseSwipeBackFragment {

    protected ZViewPager viewPager;
    protected MagicIndicator magicIndicator;
    protected String userId = "";
    protected boolean showToolbar = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_viewpager;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString(Keys.ID, "");
            showToolbar = getArguments().getBoolean(Keys.SHOW_TOOLBAR, true);
        }

        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setCanScroll(showToolbar);
        magicIndicator = view.findViewById(R.id.magic_indicator);

        if (showToolbar) {
            setToolbarTitle(getToolbarTitle(context));
            postOnEnterAnimationEnd(this::initViewPager);
        } else {
            toolbar.setVisibility(View.GONE);
            magicIndicator.setVisibility(View.GONE);
            setEnableSwipeBack(false);
            RxBus.observe(this, Actions.ACTION_SEND_VIEW_PAGER_INDEX, Integer.class)
                    .bindToLife(this)
                    .doOnNext(new RxBus.SingleConsumer<Integer>() {
                        @Override
                        public void onAccept(Integer integer) throws Exception {
                            if (isSupportVisible()) {
                                viewPager.setCurrentItem(integer, true);
                            }
                        }
                    })
                    .subscribe();
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!showToolbar) {
            postOnEnterAnimationEnd(this::initViewPager);
        }
    }

    protected abstract void initViewPager();

}
