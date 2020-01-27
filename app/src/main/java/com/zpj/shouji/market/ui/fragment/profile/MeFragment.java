package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.lzy.widget.PullZoomView;
import com.zpj.popupmenuview.popup.EverywherePopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.widget.CircleImageView;
import com.zpj.shouji.market.utils.UserManager;
import com.zpj.utils.ClickHelper;

public class MeFragment extends BaseFragment {

    private TextView tvName, tvSignature;
    private CircleImageView ivAvatar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        PullZoomView pullZoomView = view.findViewById(R.id.view_pull_zoom);
        pullZoomView.setIsZoomEnable(true);
        pullZoomView.setIsParallax(false);
        pullZoomView.setSensitive(3f);
        pullZoomView.setZoomTime(500);

        tvName = view.findViewById(R.id.tv_name);
        tvSignature = view.findViewById(R.id.tv_signature);
        ivAvatar = view.findViewById(R.id.iv_avatar);

        ClickHelper.with(ivAvatar)
                .setOnLongClickListener((v, x, y) -> {
                    EverywherePopup.create(context)
                            .addItem("更换我的头像")
                            .addItem("下载头像")
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
                            .addItem("下载背景")
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
//                    if (UserManager.isLogin()) {
//
//                        return true;
//                    }
//                    return false;
                });

//        if (UserManager.isLogin()) {
//
//        } else {
//
//        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EverywherePopup.create(context)
                        .addItem("分享主页")
                        .addItem("复制主页链接")
                        .setOnItemClickListener(new EverywherePopup.OnItemClickListener() {
                            @Override
                            public void onItemClicked(String title, int position) {
                                AToast.normal(title);
                            }
                        })
                        .apply()
                        .show(v);
            }
        });
    }
}
