package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.yalantis.ucrop.CropEvent;
import com.zpj.rxbus.RxBus;
import com.zpj.toast.ZToast;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.MemberInfo;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.dialog.EmailModifiedDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.NicknameModifiedDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.PasswordModifiedDialogFragment;
import com.zpj.shouji.market.ui.widget.setting.IconSettingItem;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.shouji.market.utils.UploadUtils;
import com.zpj.widget.setting.CommonSettingItem;
import com.zpj.widget.setting.OnCommonItemClickListener;

import io.reactivex.functions.Consumer;

public class MyInfoFragment extends BaseSwipeBackFragment implements OnCommonItemClickListener {

    private final MemberInfo memberInfo = UserManager.getInstance().getMemberInfo();

    private ImageView ivAvatar;
    private ImageView ivWallpaper;

    private CommonSettingItem nickNameItem;
    private CommonSettingItem emailItem;

    public static void start() {
        start(new MyInfoFragment());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onSignOutEvent(this, new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                pop();
            }
        });
        EventBus.onUserInfoChangeEvent(this, s -> {
            nickNameItem.setRightText(memberInfo.getMemberNickName());
            String email = memberInfo.getMemberEmail();
            if (TextUtils.isEmpty(email)) {
                emailItem.setRightText("未设置");
            } else {
                emailItem.setRightText(email);
            }
        });
        EventBus.onImageUploadEvent(this, new RxBus.SingleConsumer<CropEvent>() {
            @Override
            public void onAccept(CropEvent event) throws Exception {
                if (event.isAvatar()) {
                    PictureUtil.loadAvatar(ivAvatar);
                } else {
                    PictureUtil.loadBackground(ivWallpaper);
                }
            }
        });
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
//        ivAvatar.setCornerRadius(0);
//        ivAvatar.isCircle(true);
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
                ZToast.normal("TODO 绑定QQ");
                break;
            case R.id.item_wx:
                ZToast.normal("TODO 绑定微信");
                break;
            case R.id.item_password:
//                PasswordModifiedPopup.with(context).show();
                new PasswordModifiedDialogFragment().show(context);
                break;
        }
    }


}
