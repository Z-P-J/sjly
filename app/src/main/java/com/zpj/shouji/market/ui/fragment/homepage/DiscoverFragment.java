package com.zpj.shouji.market.ui.fragment.homepage;

import android.os.Bundle;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class DiscoverFragment extends ThemeListFragment {

    private static final String DEFAULT_URL = "http://tt.tljpxm.com/app/faxian.jsp?index=faxian";

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEFAULT_URL, DEFAULT_URL);
        fragment.setArguments(bundle);
        return fragment;
    }

}
