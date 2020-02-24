package com.zpj.shouji.market.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.widget.ScrollableViewPager;
import com.zpj.widget.toolbar.ZSearchbar;

import org.greenrobot.eventbus.EventBus;

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

    private ZSearchbar searchBar;
    private ScrollableViewPager viewPager;

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
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, null);
        viewPager.setAdapter(adapter);

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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchBar.getLeftImageButton().setOnClickListener(v -> pop());
//        searchBar.getRightImageButton().setOnClickListener(v -> getSearchResult(searchBar.getCenterSearchEditText().getText().toString()));
//        searchBar.getCenterSearchEditText().setOnEditorActionListener((v, actionId, event) -> getSearchResult(v.getText().toString()));
//        searchBar.getCenterSearchEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (TextUtils.isEmpty(s.toString()) && viewPager.getCurrentItem() == 1) {
//                    viewPager.setCurrentItem(0, true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

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
        searchBar.setText(s);
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
