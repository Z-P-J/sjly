package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.felix.atoast.library.AToast;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.IconUploadSuccessEvent;
import com.zpj.shouji.market.event.SignOutEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.event.UserInfoChangeEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.ui.fragment.dialog.EmailModifiedDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.NicknameModifiedDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.PasswordModifiedDialogFragment;
import com.zpj.shouji.market.ui.widget.IconSettingItem;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.shouji.market.utils.UploadUtils;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnCommonItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MyInfoFragment extends BaseFragment implements OnCommonItemClickListener {

    private final MemberInfo memberInfo = UserManager.getInstance().getMemberInfo();

    private NiceImageView ivAvatar;
    private NiceImageView ivWallpaper;

    private CommonSettingItem nickNameItem;
    private CommonSettingItem emailItem;

    public static void start() {
        StartFragmentEvent.start(new MyInfoFragment());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_info;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("个人信息");

        CommonSettingItem memberIdItem = view.findViewById(R.id.item_member_id);
        nickNameItem = view.findViewById(R.id.item_nickname);
        CommonSettingItem levelItem = view.findViewById(R.id.item_level);
        IconSettingItem avatarItem = view.findViewById(R.id.item_avatar);
        IconSettingItem backgroundItem = view.findViewById(R.id.item_background);
        CommonSettingItem qqItem = view.findViewById(R.id.item_qq);
        CommonSettingItem wxItem = view.findViewById(R.id.item_wx);
        emailItem = view.findViewById(R.id.item_email);
        CommonSettingItem passwordItem = view.findViewById(R.id.item_password);
        View tvSignOut = view.findViewById(R.id.tv_sign_out);

        nickNameItem.setOnItemClickListener(this);
        levelItem.setOnItemClickListener(this);
        avatarItem.setOnItemClickListener(this);
        backgroundItem.setOnItemClickListener(this);
        qqItem.setOnItemClickListener(this);
        wxItem.setOnItemClickListener(this);
        emailItem.setOnItemClickListener(this);
        passwordItem.setOnItemClickListener(this);
        tvSignOut.setOnClickListener(v -> UserManager.getInstance().signOut(context));

        memberIdItem.setRightText(memberInfo.getMemberId());
        nickNameItem.setRightText(memberInfo.getMemberNickName());
        levelItem.setRightText("Lv." + memberInfo.getMemberLevel());
        ivAvatar = avatarItem.getRightIcon();
        ivAvatar.setCornerRadius(0);
        ivAvatar.isCircle(true);
//        Glide.with(context)
//                .load(memberInfo.getMemberAvatar())
//                .into(ivAvatar);
        PictureUtil.loadAvatar(ivAvatar);

        ivWallpaper = backgroundItem.getRightIcon();
//        Glide.with(context)
//                .load(memberInfo.getMemberBackGround())
//                .into(ivWallpaper);
        PictureUtil.loadBackground(ivWallpaper);

        if (memberInfo.isBindQQ()) {
            qqItem.setRightText(memberInfo.getBindQQName());
        } else {
            qqItem.setRightText("未绑定");
        }

        if (memberInfo.isBindWX()) {
            wxItem.setRightText(memberInfo.getBindWXName());
        } else {
            wxItem.setRightText("未绑定");
        }

        String email = memberInfo.getMemberEmail();
        if (TextUtils.isEmpty(email)) {
            emailItem.setRightText("未绑定");
        } else {
            emailItem.setRightText(email);
        }

    }

    @Override
    public void onItemClick(CommonSettingItem item) {
        switch (item.getId()) {
            case R.id.item_nickname:
                new NicknameModifiedDialogFragment().show(context);
//                NicknameModifiedPopup.with(context).show();
                break;
            case R.id.item_level:
                String content;
                if (TextUtils.isEmpty(memberInfo.getMemberSignature())) {
                    content = memberInfo.getMemberScoreInfo();
                } else {
                    content = memberInfo.getMemberSignature();
                }
                new AlertDialogFragment()
                        .setTitle(memberInfo.getMemberNickName())
                        .setContent(content)
                        .hideCancelBtn()
                        .show(context);
//                ZPopup.alert(context)
//                        .setTitle(memberInfo.getMemberNickName())
//                        .setContent(content)
//                        .hideCancelBtn()
//                        .show();
                break;
            case R.id.item_avatar:
                UploadUtils.upload(_mActivity, true);
                break;
            case R.id.item_background:
                UploadUtils.upload(_mActivity, false);
                break;
            case R.id.item_email:
                new EmailModifiedDialogFragment().show(context);
//                EmailModifiedPopup.with(context).show();
                break;
            case R.id.item_qq:
                AToast.normal("TODO 绑定QQ");
                break;
            case R.id.item_wx:
                AToast.normal("TODO 绑定微信");
                break;
            case R.id.item_password:
//                PasswordModifiedPopup.with(context).show();
                new PasswordModifiedDialogFragment().show(context);
                break;
        }
    }

    @Subscribe
    public void onSignOutEvent(SignOutEvent event) {
        pop();
    }

    @Subscribe
    public void onIconUploadSuccessEvent(IconUploadSuccessEvent event) {
//        ImageView imageView = event.isAvatar() ? ivAvatar : ivWallpaper;
//        Glide.with(context)
//                .load(event.getUri())
//                .apply(
//                        new RequestOptions()
//                                .error(imageView.getDrawable())
//                                .placeholder(imageView.getDrawable())
//                )
//                .into(imageView);
        if (event.isAvatar()) {
            PictureUtil.loadAvatar(ivAvatar);
        } else {
            PictureUtil.loadBackground(ivWallpaper);
        }
    }

    @Subscribe
    public void onUserInfoChangeEvent(UserInfoChangeEvent event) {
        nickNameItem.setRightText(memberInfo.getMemberNickName());
        String email = memberInfo.getMemberEmail();
        if (TextUtils.isEmpty(email)) {
            emailItem.setRightText("未设置");
        } else {
            emailItem.setRightText(email);
        }
    }


}
