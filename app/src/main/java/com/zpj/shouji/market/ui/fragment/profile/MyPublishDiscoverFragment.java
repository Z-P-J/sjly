package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class MyPublishDiscoverFragment extends ThemeListFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultUrl = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?versioncode=198&t=discuss&jsessionid="
                + UserManager.getInstance().getSessionId() + "&sn="
                + UserManager.getInstance().getMemberInfo().getSn();
        nextUrl = defaultUrl;
    }

}
