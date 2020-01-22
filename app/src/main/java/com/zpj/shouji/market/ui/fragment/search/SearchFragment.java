package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.database.SearchHistoryManager;
import com.zpj.shouji.market.model.SearchHistory;
import com.zpj.shouji.market.ui.adapter.ZFragmentPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.view.ZViewPager;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class SearchFragment extends BaseFragment implements OnLabelClickListener {

    class SearchEvent {
        String keyword;

        SearchEvent(String keyword) {
            this.keyword = keyword;
        }
    }

    private CommonTitleBar titleBar;
    private ZViewPager viewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCanScroll(false);

        List<Fragment> list = new ArrayList<>();
        SearchPanelFragment searchPanelFragment = findChildFragment(SearchPanelFragment.class);
        if (searchPanelFragment == null) {
            searchPanelFragment = new SearchPanelFragment();
        }
        searchPanelFragment.setOnLabelClickListener(this);

        SearchResultFragment searchResultFragment = findChildFragment(SearchResultFragment.class);
        if (searchResultFragment == null) {
            searchResultFragment = new SearchResultFragment();
        }
        list.add(searchPanelFragment);
        list.add(searchResultFragment);
        ZFragmentPagerAdapter adapter = new ZFragmentPagerAdapter(getChildFragmentManager(), list, null);
        viewPager.setAdapter(adapter);


        titleBar = view.findViewById(R.id.title_bar);
        titleBar.getLeftImageButton().setOnClickListener(v -> pop());
        titleBar.getRightImageButton().setOnClickListener(v -> getSearchResult(titleBar.getCenterSearchEditText().getText().toString()));
        titleBar.getCenterSearchEditText().setOnEditorActionListener((v, actionId, event) -> getSearchResult(v.getText().toString()));
        titleBar.getCenterSearchEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) && viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0, true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onVisible() {
        ImmersionBar.with(this).statusBarDarkFont(false).init();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        setFragmentAnimator(new DefaultHorizontalAnimator());
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Override
    public void onClick(int index, View v, String s) {
        titleBar.getCenterSearchEditText().setText(s);
        getSearchResult(s);
    }

    private boolean getSearchResult(String text) {
        AToast.normal("TODO getSearchResult");
        hideSoftInput();
        if (TextUtils.isEmpty(text)) {
            AToast.warning("关键词不能为空");
            return false;
        }
        EventBus.getDefault().post(new SearchEvent(text));
        viewPager.setCurrentItem(1, true);
        return true;
    }

}
