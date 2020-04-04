package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;

public class MyRelatedDiscoverFragment extends ThemeListFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultUrl = "http://tt.shouji.com.cn/app/user_content_list_xml_v2.jsp?versioncode=198&t=discuss&jsessionid="
                + UserManager.getInstance().getSessionId() + "&thread=thread&sn="
                + UserManager.getInstance().getMemberInfo().getSn();
        nextUrl = defaultUrl;
    }

}
