package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.shehuan.niv.NiceImageView;
import com.zpj.dialog.ZAlertDialog;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.popupmenuview.popup.EverywherePopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.setting.AboutSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.CommonSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.DownloadSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.InstallSettingFragment;
import com.zpj.shouji.market.ui.widget.PullZoomView;
import com.zpj.shouji.market.ui.widget.popup.LoginPopup;
import com.zpj.utils.ClickHelper;

public class MeFragment extends BaseFragment implements View.OnClickListener {

    private View view;

    private TextView tvName;
    private TextView tvSignature;
    private NiceImageView ivAvatar;

    private TextView tvCloudBackup;
    private TextView tvFeedback;
    private TextView tvNightMode;
    private TextView tvCommonSetting;
    private TextView tvDownloadSetting;
    private TextView tvInstallSetting;
    private TextView tvAbout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        PullZoomView pullZoomView = view.findViewById(R.id.view_pull_zoom);
        pullZoomView.setIsZoomEnable(true);
        pullZoomView.setIsParallax(false);
        pullZoomView.setSensitive(2.5f);
        pullZoomView.setZoomTime(500);

        tvName = view.findViewById(R.id.tv_name);
        tvSignature = view.findViewById(R.id.tv_signature);
        ivAvatar = view.findViewById(R.id.iv_avatar);

        tvCloudBackup = view.findViewById(R.id.tv_cloud_backup);
        tvFeedback = view.findViewById(R.id.tv_feedback);
        tvNightMode = view.findViewById(R.id.tv_night_mode);
        tvCommonSetting = view.findViewById(R.id.tv_common_setting);
        tvDownloadSetting = view.findViewById(R.id.tv_download_setting);
        tvInstallSetting = view.findViewById(R.id.tv_install_setting);
        tvAbout = view.findViewById(R.id.tv_about);

        tvCloudBackup.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
        tvNightMode.setOnClickListener(this);
        tvCommonSetting.setOnClickListener(this);
        tvDownloadSetting.setOnClickListener(this);
        tvInstallSetting.setOnClickListener(this);
        tvAbout.setOnClickListener(this);

        ClickHelper.with(ivAvatar)
                .setOnLongClickListener((v, x, y) -> {
                    EverywherePopup.create(context)
                            .addItem("更换我的头像")
                            .addItem("保存头像")
                            .setOnItemClickListener((title, position) -> {
                                switch (position) {
                                    case 0:
                                        break;
                                    case 1:
                                        break;
                                }
                                AToast.normal(title);
                            })
                            .apply()
                            .showEverywhere(v, x, y);
                    return true;
                });

        ClickHelper.with(view.findViewById(R.id.iv_wallpaper))
                .setOnLongClickListener((v, x, y) -> {
                    EverywherePopup.create(context)
                            .addItem("更换主页背景")
                            .addItem("保存背景")
                            .setOnItemClickListener((title, position) -> {
                                switch (position) {
                                    case 0:
                                        break;
                                    case 1:
                                        break;
                                }
                                AToast.normal(title);
                            })
                            .apply()
                            .showEverywhere(v, x, y);
                    return true;
                });
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> EverywherePopup.create(context)
                .addItems("打开主页网页", "分享主页", "注销", "登录")
                .setOnItemClickListener(new EverywherePopup.OnItemClickListener() {
                    @Override
                    public void onItemClicked(String title, int position) {
                        AToast.normal(title);
                        switch (position) {
                            case 0:
                                _mActivity.start(WebFragment.newInstance("https://www.shouji.com.cn/user/5544802/home.html"));
                                break;
                            case 1:
                                break;
                            case 2:
                                ZAlertDialog.with(context)
                                        .setTitle("确认注销？")
                                        .setTitleTextColor(getResources().getColor(R.color.rect))
                                        .setContent("您将注销当前登录的账户，确定继续？")
                                        .setPositiveButton(dialog -> {
                                            dialog.dismiss();
                                            AToast.success("TODO 注销成功");
                                        })
                                        .show();
                                break;
                            case 3:
//                                new XPopup.Builder(context)
//                                        .asCustom(LoginPopup.with(context))
//                                        .show();
                                LoginPopup.with(context)
                                        .show();
                                break;
                        }
                    }
                })
                .apply()
                .show(v));
    }

    @Override
    public void onClick(View v) {
        if (v == tvCloudBackup) {

        } else if (v == tvFeedback) {

        } else if (v == tvNightMode) {

        } else if (v == tvCommonSetting) {
            _mActivity.start(new CommonSettingFragment());
        } else if (v == tvDownloadSetting) {
            _mActivity.start(new DownloadSettingFragment());
        } else if (v == tvInstallSetting) {
            _mActivity.start(new InstallSettingFragment());
        } else if (v == tvAbout) {
            _mActivity.start(new AboutSettingFragment());
        }
    }
}
