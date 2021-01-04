package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.widget.ZViewPager;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.toast.ZToast;
import com.zpj.widget.toolbar.ZSearchBar;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseSwipeBackFragment {

    private ZSearchBar searchBar;
    private ZViewPager viewPager;

    public static void start() {
        start(new SearchFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setCanScroll(false);
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.setOnSearchListener(this::getSearchResult);
        searchBar.setOnLeftButtonClickListener(v -> pop());
        searchBar.addTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) && viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0, true);
                }
                EventBus.sendKeywordChangeEvent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        List<Fragment> list = new ArrayList<>();
        SearchPanelFragment searchPanelFragment = findChildFragment(SearchPanelFragment.class);
        if (searchPanelFragment == null) {
            searchPanelFragment = new SearchPanelFragment();
        }
        searchPanelFragment.setOnItemClickListener((index, v, text) -> {
            searchBar.setText(text);
            getSearchResult(text);
        });
        searchPanelFragment.init();

        SearchResultFragment searchResultFragment = findChildFragment(SearchResultFragment.class);
        if (searchResultFragment == null) {
            searchResultFragment = new SearchResultFragment();
        }

        list.add(searchPanelFragment);
        list.add(searchResultFragment);
        postOnEnterAnimationEnd(new Runnable() {
            @Override
            public void run() {
                viewPager.setOffscreenPageLimit(list.size());
                FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, null);
                viewPager.setAdapter(adapter);
                showSoftInput(searchBar.getEditor());
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        if (viewPager != null && viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0, true);
            searchBar.setText("");
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        hideSoftInput();
    }

    @Override
    public void onDestroy() {
        hideSoftInput();
        super.onDestroy();
    }

    private boolean getSearchResult(String text) {
        hideSoftInput();
        if (TextUtils.isEmpty(text)) {
            ZToast.warning("关键词不能为空");
            return false;
        }
        viewPager.setCurrentItem(1, true);
        EventBus.sendSearchEvent(text);
        return true;
    }

}
