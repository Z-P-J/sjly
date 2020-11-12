package com.zpj.shouji.market.ui.fragment.theme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.utils.ThemeUtils;

public class TopicThemeListFragment extends ThemeListFragment {

    private String topic;

    public static void start(String topic) {
        TopicThemeListFragment fragment = new TopicThemeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.DEFAULT_URL, "http://tt.shouji.com.cn/app/faxian.jsp?tagname=" + topic);
        bundle.putString(Keys.TAG, topic);
        fragment.setArguments(bundle);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        super.handleArguments(arguments);
        topic = arguments.getString(Keys.TAG, "");
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle(topic);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ThemeUtils.initStatusBar(this);
    }
}
