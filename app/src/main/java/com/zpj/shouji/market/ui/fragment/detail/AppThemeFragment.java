package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;

import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class AppThemeFragment extends ThemeListFragment {

    public static AppThemeFragment newInstance(String url) {
        AppThemeFragment fragment = new AppThemeFragment();
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
