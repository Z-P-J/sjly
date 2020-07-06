package com.zpj.shouji.market.ui.fragment.detail;

import android.os.Bundle;

import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class AppThemeFragment extends ThemeListFragment {

    public static AppThemeFragment newInstance(String id, String type) {
        AppThemeFragment fragment = new AppThemeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/app/faxian.jsp?apptype=" + type + "&appid=" + id);
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
