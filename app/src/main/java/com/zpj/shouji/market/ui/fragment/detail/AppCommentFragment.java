package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;

import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class AppCommentFragment extends ThemeListFragment {

    public static AppCommentFragment newInstance(String url) {
        AppCommentFragment fragment = new AppCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSupportInvisible() {
        getSupportDelegate().onSupportInvisible();
    }

    @Override
    public void onSupportVisible() {
        getSupportDelegate().onSupportVisible();
    }

}
