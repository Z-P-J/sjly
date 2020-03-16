package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.felix.atoast.library.AToast;
import com.lxj.xpopup.ZPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.popupmenuview.popup.EverywherePopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.setting.AboutSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.CommonSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.DownloadSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.InstallSettingFragment;
import com.zpj.shouji.market.ui.widget.MyToolsCard;
import com.zpj.shouji.market.ui.widget.PullZoomView;
import com.zpj.shouji.market.ui.widget.popup.LoginPopup;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.utils.ClickHelper;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class MyFragment extends BaseFragment
        implements View.OnClickListener,
        UserManager.OnSignInListener {

    private ImageView ivWallpaper;
    private TextView tvName;
    private TextView tvSignature;
    private NiceImageView ivAvatar;
    private TextView tvCheckIn;
    private TextView tvLevel;
    private TextView tvFollower;
    private TextView tvFans;

    private MyToolsCard myToolsCard;

    private TextView tvCloudBackup;
    private TextView tvFeedback;
    private TextView tvNightMode;
    private TextView tvCommonSetting;
    private TextView tvDownloadSetting;
    private TextView tvInstallSetting;
    private TextView tvAbout;
    private TextView tvSignOut;

    private LoginPopup loginPopup;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        PullZoomView pullZoomView = view.findViewById(R.id.view_pull_zoom);
        pullZoomView.setIsZoomEnable(true);
        pullZoomView.setIsParallax(false);
        pullZoomView.setSensitive(2.5f);
        pullZoomView.setZoomTime(500);

        ivWallpaper = view.findViewById(R.id.iv_wallpaper);
        tvName = view.findViewById(R.id.tv_name);
        tvSignature = view.findViewById(R.id.tv_signature);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvCheckIn = view.findViewById(R.id.tv_check_in);
        tvLevel = view.findViewById(R.id.tv_level);
        tvFollower = view.findViewById(R.id.tv_follower);
        tvFans = view.findViewById(R.id.tv_fans);
        myToolsCard = view.findViewById(R.id.my_tools_card);
        myToolsCard.attachFragment(this);
        myToolsCard.attachActivity(_mActivity);

        tvCloudBackup = view.findViewById(R.id.tv_cloud_backup);
        tvFeedback = view.findViewById(R.id.tv_feedback);
        tvNightMode = view.findViewById(R.id.tv_night_mode);
        tvCommonSetting = view.findViewById(R.id.tv_common_setting);
        tvDownloadSetting = view.findViewById(R.id.tv_download_setting);
        tvInstallSetting = view.findViewById(R.id.tv_install_setting);
        tvAbout = view.findViewById(R.id.tv_about);
        tvSignOut = view.findViewById(R.id.tv_sign_out);


        ivAvatar.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        tvCloudBackup.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
        tvNightMode.setOnClickListener(this);
        tvCommonSetting.setOnClickListener(this);
        tvDownloadSetting.setOnClickListener(this);
        tvInstallSetting.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvSignOut.setOnClickListener(this);


        hideSoftInput();

        ClickHelper.with(ivAvatar)
                .setOnLongClickListener((v, x, y) -> {
                    if (!UserManager.getInstance().isLogin()) {
                        return false;
                    }
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
                    if (!UserManager.getInstance().isLogin()) {
                        return false;
                    }
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

        UserManager.getInstance().addOnSignInListener(this);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (loginPopup != null && !loginPopup.isShow()) {
            loginPopup.show();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (loginPopup != null && loginPopup.isShow()) {
            loginPopup.hide();
        }
    }

    @Override
    public void onDestroy() {
        UserManager.getInstance().removeOnSignInListener(this);
        if (loginPopup != null && loginPopup.isShow()) {
            loginPopup.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> EverywherePopup.create(context)
                .addItems("打开主页网页", "分享主页", "注销", "登录")
                .setOnItemClickListener((title, position) -> {
                    AToast.normal(title);
                    switch (position) {
                        case 0:
                            _mActivity.start(WebFragment.newInstance("https://www.shouji.com.cn/user/5544802/home.html"));
                            break;
                        case 1:
                            break;
                        case 2:
                            showSignOutPopup();
                            break;
                        case 3:
                            showLoginPopup(0);
                            break;
                    }
                })
                .apply()
                .show(v));
    }

    @Override
    public void onClick(View v) {
        if (v == tvCheckIn) {
            MemberInfo memberInfo = UserManager.getInstance().getMemberInfo();
            if (memberInfo.isCanSigned()) {
                HttpApi.openConnection("http://tt.shouji.com.cn/app/xml_signed.jsp?versioncode=198&version=2.9.9.9.3")
                        .data("jsessionid", UserManager.getInstance().getSessionId())
                        .toHtml()
                        .onSuccess(data -> {
                            String info = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
                                AToast.success(info);
                                memberInfo.setCanSigned(false);
                                info = memberInfo.toStr();
                                if (info != null) {
                                    Log.d("xml_signed", "memberInfo=" + info);
                                    UserManager.getInstance().setUserInfo(info);
                                }
                                tvCheckIn.setBackgroundResource(R.drawable.bg_button_round_purple);
                                tvCheckIn.setText("已签到");
                            } else {
                                AToast.error(info);
                            }
                        })
                        .onError(throwable -> AToast.error(throwable.getMessage()))
                        .subscribe();
            } else {
                AToast.warning("你已签到过了");
            }
        } else if (v == ivAvatar) {
            if (UserManager.getInstance().isLogin()) {
                AToast.normal("TODO 显示用户信息");
            } else {
                showLoginPopup(0);
            }
        } else if (v == tvCloudBackup) {
//            _mActivity.start(new FragmentTest());
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
        } else if (v == tvSignOut) {
            showSignOutPopup();
        }
    }

    @Override
    public void onSignInSuccess() {
        myToolsCard.onLogin();
        MemberInfo info = UserManager.getInstance().getMemberInfo();
        if (!info.isCanSigned()) {
            tvCheckIn.setBackgroundResource(R.drawable.bg_button_round_purple);
            tvCheckIn.setText("已签到");
        }
        tvName.setText(info.getMemberNickName());
        tvLevel.setText("Lv." + info.getMemberLevel());
        if (TextUtils.isEmpty(info.getMemberSignature())) {
            tvSignature.setText(info.getMemberScoreInfo());
        } else {
            tvSignature.setText(info.getMemberSignature());
        }
        tvFollower.setText("关注 " + info.getFollowerCount());
        tvFans.setText("粉丝 " + info.getFansCount());
        Glide.with(context).load(info.getMemberAvatar())
                .apply(new RequestOptions()
                        .error(R.drawable.ic_user_head)
                        .placeholder(R.drawable.ic_user_head)
                )
                .into(ivAvatar);
        if (!TextUtils.isEmpty(info.getMemberBackGround())) {
            Glide.with(context).load(info.getMemberBackGround())
                    .apply(new RequestOptions()
                            .error(R.drawable.bg_member_default)
                            .placeholder(R.drawable.bg_member_default)
                    )
                    .into(ivWallpaper);
        }
        tvSignOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignInFailed(String errInfo) {

    }

    public void showLoginPopup(int page) {
        _mActivity.start(new LoginFragment());
//        if (loginPopup == null) {
//            loginPopup = LoginPopup.with(context);
//            loginPopup.setPopupCallback(new SimpleCallback() {
//                @Override
//                public void onDismiss() {
//                    loginPopup = null;
//                }
//
////                @Override
////                public void onShow() {
////                    loginPopup.clearFocus();
////                }
////
//                @Override
//                public void onHide() {
//                    loginPopup.clearFocus();
//                }
//            });
//        }
//        loginPopup.setCurrentPosition(page);
//        loginPopup.show();
    }

    private void showSignOutPopup() {
        ZPopup.with(context)
                .alert()
                .setTitle("确认注销？")
                .setContent("您将注销当前登录的账户，确认继续？")
                .setConfirmButton(new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        UserManager.getInstance().signOut();
                        tvCheckIn.setVisibility(View.GONE);
                        tvSignOut.setVisibility(View.GONE);
                        tvName.setText("点击头像登录");
                        tvLevel.setText("Lv.0");
                        tvSignature.setText("手机乐园，发现应用的乐趣");
                        tvFollower.setText("关注 0");
                        tvFans.setText("粉丝 0");
                        ivAvatar.setImageResource(R.drawable.ic_user_head);
                        ivWallpaper.setImageResource(R.drawable.bg_member_default);
                        AToast.success("TODO 注销成功");
                    }
                })
                .show();
    }

}
