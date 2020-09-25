//package com.zpj.shouji.market.ui.fragment.setting;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.View;
//
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.event.StartFragmentEvent;
//import com.zpj.widget.setting.CheckableSettingItem;
//import com.zpj.widget.setting.CommonSettingItem;
//
//public class InstallSettingFragment extends BaseSettingFragment {
//
//    public static void start() {
//        StartFragmentEvent.start(new InstallSettingFragment());
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_setting_install;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        setToolbarTitle("安装设置");
//        CheckableSettingItem itemInstallDownloaded = view.findViewById(R.id.item_install_downloaded);
//        itemInstallDownloaded.setOnItemClickListener(this);
//
//        CheckableSettingItem itemAutoDeleteApk = view.findViewById(R.id.item_auto_delete_apk);
//        itemAutoDeleteApk.setOnItemClickListener(this);
//
//        CheckableSettingItem itemAutoInstall = view.findViewById(R.id.item_auto_install);
//        itemAutoInstall.setOnItemClickListener(this);
//
//        CheckableSettingItem itemRootInstall = view.findViewById(R.id.item_root_install);
//        itemRootInstall.setOnItemClickListener(this);
//    }
//
//
//    @Override
//    public void onItemClick(CheckableSettingItem item) {
//        switch (item.getId()) {
//            case R.id.item_install_downloaded:
//
//                break;
//            case R.id.item_auto_delete_apk:
//
//                break;
//            case R.id.item_auto_install:
//
//                break;
//            case R.id.item_root_install:
//
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onItemClick(CommonSettingItem item) {
//
//    }
//}
