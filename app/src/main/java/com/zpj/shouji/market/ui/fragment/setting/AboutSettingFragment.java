package com.zpj.shouji.market.ui.fragment.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.utils.AppUtils;
import com.zpj.widget.setting.CheckableSettingItem;
import com.zpj.widget.setting.CommonSettingItem;

import com.zpj.fragmentation.anim.DefaultNoAnimator;

public class AboutSettingFragment extends BaseSettingFragment {

    public static void start() {
        StartFragmentEvent.start(new AboutSettingFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting_about;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "关于";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        CommonSettingItem aboutMeItem = view.findViewById(R.id.item_about_me);
        aboutMeItem.setOnItemClickListener(this);

        CommonSettingItem userAgreementItem = view.findViewById(R.id.item_user_agreement);
        userAgreementItem.setOnItemClickListener(this);

        CommonSettingItem itemOpenSource = view.findViewById(R.id.item_open_source);
        itemOpenSource.setOnItemClickListener(this);

        CommonSettingItem privacyAgreementItem = view.findViewById(R.id.item_privacy_agreement);
        privacyAgreementItem.setOnItemClickListener(this);

        CommonSettingItem itemCheckUpdate = view.findViewById(R.id.item_check_update);
        itemCheckUpdate.setOnItemClickListener(this);
        itemCheckUpdate.setRightText(AppUtils.getAppVersionName(context, context.getPackageName()));

    }

    @Override
    public void onItemClick(CheckableSettingItem item) { }

    @Override
    public void onItemClick(CommonSettingItem item) {
        switch (item.getId()) {
            case R.id.item_about_me:
//                _mActivity.setFragmentAnimator(new DefaultNoAnimator());
                AboutMeFragment.start();
                break;
            case R.id.item_user_agreement:
                WebFragment.start("https://m.shouji.com.cn/sjlyyhxy.html", "用户协议");
                break;
            case R.id.item_privacy_agreement:
                WebFragment.start("https://m.shouji.com.cn/ysxy.html", "隐私协议");
                break;
            case R.id.item_open_source:
                WebFragment.start(item.getRightText(), item.getTitleText());
                break;
            case R.id.item_check_update:
                AToast.warning("TODO 检查更新");
                break;
        }
    }
}
