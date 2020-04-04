package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.markdown.MarkdownEditorFragment;
import com.zpj.markdown.MarkdownViewFragment;
import com.zpj.popup.ZPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscoverEditorFragment2 extends BaseFragment {

    protected ViewPager viewPager;
    private MarkdownEditorFragment editorFragment;
    private MarkdownViewFragment viewFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_viewpager;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.view_pager);
        List<Fragment> fragments = new ArrayList<>();
        editorFragment = findChildFragment(MarkdownEditorFragment.class);
        if (editorFragment == null) {
            editorFragment = new MarkdownEditorFragment();
        }
        viewFragment = findChildFragment(MarkdownViewFragment.class);
        if (viewFragment == null) {
            viewFragment = new MarkdownViewFragment();
        }
        fragments.add(editorFragment);
        fragments.add(viewFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, null));
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setToolbarTitle("编辑");
                    editorFragment.showSoftInput();
//                    viewFragment.processMarkdown("");
//                    showSoftInput(_mActivity.getCurrentFocus());
                } else if (position == 1) {
                    setToolbarTitle("预览");
                    viewFragment.processMarkdown(editorFragment.getMarkdownContent());
                    editorFragment.hideSoftInput();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> ZPopup.attachList(context)
                .addItems("显示源码", "清空内容", "保存", "回显")
                .setOnSelectListener((position, title) -> {
                    switch (position) {
                        case 0:

                            break;
                        case 1:

                            break;
                        case 2:

                            break;
                        case 3:

                            break;
                    }
                })
                .show(imageButton));
    }

}
