package com.zpj.shouji.market.ui.fragment.profile;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.shehuan.niv.NiceImageView;
import com.yalantis.ucrop.CropEvent;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.anim.DefaultVerticalAnimator;
import com.zpj.matisse.CaptureMode;
import com.zpj.matisse.Matisse;
import com.zpj.matisse.MimeType;
import com.zpj.matisse.engine.impl.GlideEngine;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.listener.OnSelectedListener;
import com.zpj.popup.ZPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.event.SignInEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.setting.AboutSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.CommonSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.DownloadSettingFragment;
import com.zpj.shouji.market.ui.fragment.setting.InstallSettingFragment;
import com.zpj.shouji.market.ui.widget.PullZoomView;
import com.zpj.shouji.market.ui.widget.ToolBoxCard;
import com.zpj.shouji.market.ui.widget.popup.NicknameModifiedPopup;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.utils.ClickHelper;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.tinted.TintedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

public class MyFragment extends BaseFragment
        implements View.OnClickListener { // UserManager.OnSignInListener

    private ImageView ivWallpaper;
    private TextView tvName;
    private TextView tvSignature;
    private NiceImageView ivAvatar;
    private TextView tvCheckIn;
    private TextView tvLevel;
    private TextView tvFollower;
    private TextView tvFans;

    private ToolBoxCard toolBoxCard;

    private TextView tvCloudBackup;
    private TextView tvFeedback;
    private TextView tvNightMode;
    private TextView tvCommonSetting;
    private TextView tvDownloadSetting;
    private TextView tvInstallSetting;
    private TextView tvAbout;
    private View tvSignOut;

    private View shadowView;
    private TintedImageView ivAppName;

    private boolean isPickAvatar;

//    private LoginPopup loginPopup;

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
        pullZoomView.setOnScrollListener(new PullZoomView.OnScrollListener() {
            @Override
            public void onScroll(int offsetX, int offsetY, int oldOffsetX, int oldOffsetY) {
                super.onScroll(offsetX, offsetY, oldOffsetX, oldOffsetY);
                if (oldOffsetY <= toolbar.getHeight()) {
                    if (Math.abs(offsetY - oldOffsetY) < 2) {
                        return;
                    }
                    float alpha = 1f * offsetY / toolbar.getHeight();
                    alpha = Math.min(alpha, 1f);
                    int color = alphaColor(Color.WHITE, alpha * 0.95f);
                    toolbar.setBackgroundColor(color);
                    toolbar.setLightStyle(alpha <= 0.5);
                    if (alpha > 0.5) {
                        darkStatusBar();
                        shadowView.setVisibility(View.VISIBLE);
                        ivAppName.setTint(Color.BLACK);
                    } else {
                        lightStatusBar();
                        shadowView.setVisibility(View.GONE);
                        ivAppName.setTint(Color.WHITE);
                    }
                }
            }
        });
        pullZoomView.setOnPullZoomListener(new PullZoomView.OnPullZoomListener() {
            @Override
            public void onPullZoom(int originHeight, int currentHeight) {
                super.onPullZoom(originHeight, currentHeight);
                Log.d("onPullZoom", "onPullZoom originHeight=" + originHeight + " currentHeight=" + currentHeight);
            }
        });

        shadowView = view.findViewById(R.id.shadow_view);

        ivWallpaper = view.findViewById(R.id.iv_wallpaper);
        tvName = view.findViewById(R.id.tv_name);
        tvSignature = view.findViewById(R.id.tv_signature);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvCheckIn = view.findViewById(R.id.tv_check_in);
        tvLevel = view.findViewById(R.id.tv_level);
        tvFollower = view.findViewById(R.id.tv_follower);
        tvFans = view.findViewById(R.id.tv_fans);
        toolBoxCard = view.findViewById(R.id.my_tools_card);
        EventBus.getDefault().register(toolBoxCard);
        toolBoxCard.attachFragment(this);

        tvCloudBackup = view.findViewById(R.id.tv_cloud_backup);
        tvFeedback = view.findViewById(R.id.tv_feedback);
        tvNightMode = view.findViewById(R.id.tv_night_mode);
        tvCommonSetting = view.findViewById(R.id.tv_common_setting);
        tvDownloadSetting = view.findViewById(R.id.tv_download_setting);
        tvInstallSetting = view.findViewById(R.id.tv_install_setting);
        tvAbout = view.findViewById(R.id.tv_about);
        tvSignOut = view.findViewById(R.id.tv_sign_out);


        tvName.setOnClickListener(this);
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
                    ZPopup.attachList(context)
                            .addItems("更换我的头像", "保存头像")
                            .setOnSelectListener((position, title) -> {
                                switch (position) {
                                    case 0:
                                        showImagePicker(true);
                                        break;
                                    case 1:
                                        PictureUtil.saveImage(context, UserManager.getInstance().getMemberInfo().getMemberAvatar());
                                        break;
                                }
                                AToast.normal(title);
                            })
                            .show(x, y);
                    return true;
                });

        ClickHelper.with(view.findViewById(R.id.iv_wallpaper))
                .setOnLongClickListener((v, x, y) -> {
                    if (!UserManager.getInstance().isLogin()) {
                        return false;
                    }
                    String bgUrl = UserManager.getInstance().getMemberInfo().getMemberBackGround();
                    boolean canDelete = !TextUtils.isEmpty(bgUrl) && !bgUrl.contains("default_user_bg");
                    ZPopup.attachList(context)
                            .addItems("更换主页背景", "保存背景")
                            .addItemIf(canDelete, "删除背景")
                            .setOnSelectListener((position, title) -> {
                                switch (position) {
                                    case 0:
                                        showImagePicker(false);
                                        break;
                                    case 1:
                                        PictureUtil.saveImage(context, bgUrl);
                                        break;
                                    case 2:
                                        HttpApi.deleteBackgroundApi()
                                                .onSuccess(data -> {
                                                    Log.d("deleteBackgroundApi", "data=" + data);
                                                    String info = data.selectFirst("info").text();
                                                    if ("success".equals(data.selectFirst("result").text())) {
                                                        AToast.success(info);
                                                        ivWallpaper.setImageResource(R.drawable.bg_member_default);
                                                        UserManager.getInstance().getMemberInfo().setMemberBackGround("");
                                                        UserManager.getInstance().saveUserInfo();
                                                    } else {
                                                        AToast.error(info);
                                                    }
                                                })
                                                .onError(throwable -> {
                                                    throwable.printStackTrace();
                                                    AToast.error(throwable.getMessage());
                                                })
                                                .subscribe();
                                        break;
                                }
                                AToast.normal(title);
                            })
                            .show(x, y);
                    return true;
                });

//        UserManager.getInstance().addOnSignInListener(this);
        onSignInEvent(new SignInEvent(UserManager.getInstance().isLogin()));
    }

    @Override
    public void toolbarLeftCustomView(@NonNull View view) {
        super.toolbarLeftCustomView(view);
        ivAppName = (TintedImageView) view;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        lightStatusBar();
//        if (loginPopup != null && !loginPopup.isShow()) {
//            loginPopup.show();
//        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        darkStatusBar();
//        if (loginPopup != null && loginPopup.isShow()) {
//            loginPopup.hide();
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(toolBoxCard);
        EventBus.getDefault().unregister(this);
//        UserManager.getInstance().removeOnSignInListener(this);
//        if (loginPopup != null && loginPopup.isShow()) {
//            loginPopup.dismiss();
//        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance().getMessageInfo().post();
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
            ZPopup.attachList(context)
                    .addItems("打开我的主页", "打开主页网页", "分享主页")
                    .addItem(UserManager.getInstance().isLogin() ? "注销" : "登录")
                    .setOnSelectListener((position, title) -> {
                        AToast.normal(title);
                        switch (position) {
                            case 0:
                                ProfileFragment.start(UserManager.getInstance().getUserId(), false);
                                break;
                            case 1:
                                WebFragment.shareHomepage(UserManager.getInstance().getUserId());
                                break;
                            case 2:
                                AToast.normal("TODO 分享主页");
                                break;
                            case 3:
                                if (UserManager.getInstance().isLogin()) {
                                    showSignOutPopup();
                                } else {
                                    showLoginPopup(0);
                                }
                                break;
                            case 4:
                                break;
                        }
                    })
                    .show(v);
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tvCheckIn) {
            MemberInfo memberInfo = UserManager.getInstance().getMemberInfo();
            if (memberInfo.isCanSigned()) {
                HttpApi.get("http://tt.shouji.com.cn/app/xml_signed.jsp")
                        .onSuccess(data -> {
                            Log.d("tvCheckIn", "data=" + data);
                            String info = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
                                AToast.success(info);
                                memberInfo.setCanSigned(false);
//                                info = memberInfo.toStr();
//                                if (info != null) {
//                                    Log.d("xml_signed", "memberInfo=" + info);
//                                    UserManager.getInstance().setUserInfo(info);
//                                }
                                UserManager.getInstance().saveUserInfo();
                                tvCheckIn.setBackgroundResource(R.drawable.bg_button_round_pink);
                                tvCheckIn.setText("已签到");
                            } else {
                                AToast.error(info + "，登录可能已失效");
                            }
                        })
                        .onError(throwable -> {
                            throwable.printStackTrace();
                            AToast.error(throwable.getMessage());
                        })
                        .subscribe();
            } else {
                AToast.warning("你已签到过了");
            }
        } else if (v == ivAvatar) {
            if (UserManager.getInstance().isLogin()) {
                showImagePicker(true);
            } else {
                showLoginPopup(0);
            }
        } else if (v == tvName) {
            if (UserManager.getInstance().isLogin()) {
                NicknameModifiedPopup.with(context).show();
            } else {
                showLoginPopup(0);
            }
        } else if (v == tvCloudBackup) {
//            _mActivity.start(new FragmentTest());
        } else if (v == tvFeedback) {

        } else if (v == tvNightMode) {

        } else if (v == tvCommonSetting) {
            CommonSettingFragment.start();
        } else if (v == tvDownloadSetting) {
            DownloadSettingFragment.start();
        } else if (v == tvInstallSetting) {
            InstallSettingFragment.start();
        } else if (v == tvAbout) {
            AboutSettingFragment.start();
        } else if (v == tvSignOut) {
            showSignOutPopup();
        }
    }

    private void showImagePicker(boolean isAvatar) {
        this.isPickAvatar = isAvatar;
        Matisse.from(_mActivity)
                .choose(MimeType.ofImage())//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(1)//最大选择数量为9
                .spanCount(3)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .imageEngine(new GlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .capture(true, CaptureMode.All)//存储到哪里
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Item> itemList) {
                        String clipImageName = "clip_" + (System.currentTimeMillis() / 1000) + ".png";
                        File clipImage = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + File.separator + "PhotoPick/image",
                                clipImageName
                        );
                        UCrop uCrop = UCrop.of(itemList.get(0).getContentUri(), Uri.fromFile(clipImage));

                        if (isPickAvatar) {
                            uCrop.withAspectRatio(1, 1);
                            int maxSize = ScreenUtils.dp2pxInt(context, 144);
                            uCrop.withMaxResultSize(maxSize, maxSize);
                        } else {
                            int height = ScreenUtils.getScreenHeight(context);
                            int width = ScreenUtils.getScreenWidth(context);
                            uCrop.withAspectRatio(height, width);
//                            uCrop.withAspectRatio(16, 9);
                            uCrop.withMaxResultSize(width / 2, (int) ((float) width * width / height) / 2);
                        }


                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.NONE);
                        options.setCompressionQuality(100);
                        options.setFreeStyleCropEnabled(true);
                        options.setCircleDimmedLayer(isPickAvatar);
                        options.setShowCropGrid(false);
                        options.setHideBottomControls(true);
                        options.setShowCropFrame(false);
                        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                        uCrop.withOptions(options);

                        uCrop.start(_mActivity);
                    }
                })
                .start();
    }

    @Subscribe
    public void onSignInEvent(SignInEvent event) {
        if (event.isSuccess()) {
            toolBoxCard.onLogin();
            MemberInfo info = UserManager.getInstance().getMemberInfo();
            tvCheckIn.setVisibility(View.VISIBLE);
            if (!info.isCanSigned()) {
                tvCheckIn.setBackgroundResource(R.drawable.bg_button_round_pink);
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
        } else {
            tvCheckIn.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onCropEvent(CropEvent event) {
        AToast.success("path=" + event.getUri().getPath());
        if (isPickAvatar) {
            ShowLoadingEvent.post("上传头像...");
            try {
                HttpApi.uploadAvatarApi(event.getUri())
                        .onSuccess(doc -> {
                            Log.d("uploadAvatarApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
                                AToast.success(info);
                                Glide.with(context)
                                        .load(event.getUri())
                                        .apply(
                                                new RequestOptions()
                                                        .error(R.drawable.ic_user_head)
                                                        .placeholder(R.drawable.ic_user_head)
                                        )
                                        .into(ivAvatar);
                                UserManager.getInstance().getMemberInfo().setMemberAvatar(info);
                                UserManager.getInstance().saveUserInfo();
                            } else {
                                AToast.error(info);
                            }
                            HideLoadingEvent.postDelayed(500);
                        })
                        .onError(throwable -> {
                            AToast.error("上传头像失败！" + throwable.getMessage());
                            HideLoadingEvent.postDelayed(500);
                        })
                        .subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                AToast.error("上传头像失败！" + e.getMessage());
                HideLoadingEvent.postDelayed(500);
            }
        } else {
            ShowLoadingEvent.post("上传背景...");
            try {
                HttpApi.uploadBackgroundApi(event.getUri())
                        .onSuccess(doc -> {
                            Log.d("uploadBackgroundApi", "data=" + doc);
                            String info = doc.selectFirst("info").text();
                            if ("success".equals(doc.selectFirst("result").text())) {
//                                AToast.success(info);
                                Glide.with(context)
                                        .load(event.getUri())
                                        .apply(
                                                new RequestOptions()
                                                        .error(R.drawable.bg_member_default)
                                                        .placeholder(R.drawable.bg_member_default)
                                        )
                                        .into(ivWallpaper);
                                UserManager.getInstance().getMemberInfo().setMemberBackGround(info);
                                UserManager.getInstance().saveUserInfo();
                            } else {
                                AToast.error(info);
                            }
                            HideLoadingEvent.postDelayed(500);
                        })
                        .onError(throwable -> {
                            AToast.error("上传背景失败！" + throwable.getMessage());
                            HideLoadingEvent.postDelayed(500);
                        })
                        .subscribe();
            } catch (Exception e) {
                e.printStackTrace();
                AToast.error("上传背景失败！" + e.getMessage());
                HideLoadingEvent.postDelayed(500);
            }
        }

    }

    public void showLoginPopup(int page) {
        _mActivity.setFragmentAnimator(new DefaultVerticalAnimator());
        LoginFragment.start();
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
        ZPopup.alert(context)
                .setTitle("确认注销？")
                .setContent("您将注销当前登录的账户，确认继续？")
                .setConfirmButton(popup -> {
                    UserManager.getInstance().signOut();
                    toolBoxCard.onSignOut();
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
                })
                .show();
    }

    public static int alphaColor(int color, float alpha) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & color;
        return a + rgb;
    }

}
