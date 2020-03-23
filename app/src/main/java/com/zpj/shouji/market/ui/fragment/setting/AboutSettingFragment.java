package com.zpj.shouji.market.ui.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.AboutMeFragment;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;

import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;

public class AboutSettingFragment extends BaseSettingFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_about;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("关于");
        CommonSettingItem userAgreementItem = view.findViewById(R.id.item_user_agreement);
        userAgreementItem.setOnItemClickListener(this);

        CommonSettingItem privacyAgreementItem = view.findViewById(R.id.item_privacy_agreement);
        privacyAgreementItem.setOnItemClickListener(this);

        CommonSettingItem aboutMeItem = view.findViewById(R.id.item_about_me);
        aboutMeItem.setOnItemClickListener(this);

        CommonSettingItem itemSearchEngine = view.findViewById(R.id.item_check_update);
        itemSearchEngine.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(CheckableSettingItem item) { }

    @Override
    public void onItemClick(CommonSettingItem item) {
        switch (item.getId()) {
            case R.id.item_user_agreement:
                _mActivity.start(WebFragment.newInstance("https://wap.shouji.com.cn/sjlyyhxy.html", "用户协议"));
                break;
            case R.id.item_privacy_agreement:
                _mActivity.start(WebFragment.newInstance("https://wap.shouji.com.cn/ysxy.html", "隐私协议"));
                break;
            case R.id.item_about_me:
                _mActivity.start(new AboutMeFragment());
                break;
            case R.id.item_check_update:
                break;
        }
    }
}
