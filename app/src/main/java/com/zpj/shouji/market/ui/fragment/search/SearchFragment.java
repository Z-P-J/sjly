package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.widget.ZViewPager;
import com.zpj.widget.toolbar.ZSearchBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment {

    static class SearchEvent {
        String keyword;

        SearchEvent(String keyword) {
            this.keyword = keyword;
        }
    }

    static class TextChangedEvent {
        String keyword;

        TextChangedEvent(String keyword) {
            this.keyword = keyword;
        }
    }

    private ZSearchBar searchBar;
    private ZViewPager viewPager;

    public static void start() {
        StartFragmentEvent.start(new SearchFragment());
    }

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
                EventBus.getDefault().post(new TextChangedEvent(s.toString()));
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
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
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

    //    @Override
//    public FragmentAnimator onCreateFragmentAnimator() {
//        return new DefaultHorizontalAnimator();
//    }

    private boolean getSearchResult(String text) {
//        AToast.normal("TODO getSearchResult");
        hideSoftInput();
        if (TextUtils.isEmpty(text)) {
            AToast.warning("关键词不能为空");
            return false;
        }
        viewPager.setCurrentItem(1, true);
        EventBus.getDefault().post(new SearchEvent(text));
        return true;
    }

}
