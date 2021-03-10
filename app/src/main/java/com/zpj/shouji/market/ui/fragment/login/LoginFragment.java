package com.zpj.shouji.market.ui.fragment.login;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zpj.fragmentation.SupportHelper;
import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.fragmentation.dialog.impl.InputDialogFragment;
import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.widget.SignInLayout3;
import com.zpj.shouji.market.ui.widget.SignUpLayout3;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.utils.SoftInputHelper;
import com.zpj.toast.ZToast;
import com.zpj.utils.KeyboardObserver;
import com.zpj.utils.ScreenUtils;


public class LoginFragment extends BaseSwipeBackFragment {

    private static final String REGISTRATION = "key_registration";

    private View contentView;

    private boolean isRegistration = false;

    public static LoginFragment newInstance(boolean isRegistration) {
        Bundle args = new Bundle();
        args.putBoolean(REGISTRATION, isRegistration);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(boolean isRegistration) {
        start(newInstance(isRegistration));
    }

    public static void start() {
        start(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "账号登录";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onSignInEvent(this, new RxBus.PairConsumer<Boolean, String>() {
            @Override
            public void onAccept(Boolean isSuccess, String errorMsg) throws Exception {
                if (isSuccess) {
                    ZToast.success("登录成功！");
                    EventBus.hideLoading(() -> pop());
                } else {
                    EventBus.hideLoading();
                }
                if (contentView instanceof SignInLayout3) {
                    ((SignInLayout3) contentView).onSignIn(isSuccess, errorMsg);
                }
            }
        });
        EventBus.onSignUpEvent(this, new RxBus.PairConsumer<Boolean, String>() {
            @Override
            public void onAccept(Boolean isSuccess, String errorMsg) throws Exception {
                if (isSuccess) {
                    ZToast.success("注册成功，请输入账账户信息登录！");
                    if (SupportHelper.getPreFragment(LoginFragment.this) instanceof LoginFragment) {
                        pop();
                    } else {
                        startWithPop(LoginFragment.newInstance(false));
                    }
                }
                if (contentView instanceof SignUpLayout3) {
                    ((SignUpLayout3) contentView).onSignUp(isSuccess, errorMsg);
                }
            }
        });
        if (getArguments() != null) {
            isRegistration = getArguments().getBoolean(REGISTRATION, false);
        }
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        super.toolbarRightImageButton(imageButton);
        if (isRegistration) {
            imageButton.setVisibility(View.GONE);
        } else {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new InputDialogFragment()
                            .setEmptyable(false)
                            .setHint("请输入jsessionid登录")
                            .setTitle("jsessionid登录")
                            .setContent("\t\t\t\t本应用由于一些限制，不支持第三方登录，所以这里提供了一种通过jsessionid登录的方法。")
                            .setAutoDismiss(false)
                            .setPositiveButton((fragment, which) -> {
                                fragment.dismiss();
                                EventBus.showLoading("登录中...");
                                UserManager.getInstance().signIn(((InputDialogFragment) fragment).getText());
                            })
                            .setNegativeButton((fragment, which) -> fragment.dismiss())
                            .show(context);
                }
            });
        }
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        LinearLayout llContainer = findViewById(R.id.ll_container);
        View tvSubmit;
        if (isRegistration) {
            setToolbarTitle("账号注册");
            SignUpLayout3 signUpLayout3 = new SignUpLayout3(context);
            contentView = signUpLayout3;
            SoftInputHelper mSoftInputHelper = SoftInputHelper.attach(_mActivity).moveBy(contentView);
            mSoftInputHelper.moveWith(
                    signUpLayout3.getSubmitView(),
                    signUpLayout3.getEtAccount(),
                    signUpLayout3.getEtEmail(),
                    signUpLayout3.getEtPassword(),
                    signUpLayout3.getEtPasswordAgain()
            );
            tvSubmit = contentView.findViewById(R.id.tv_sign_in);
        } else {
            setToolbarTitle("账号登录");
            SignInLayout3 signInLayout3 = new SignInLayout3(context);
            contentView = signInLayout3;
            contentView.findViewById(R.id.tv_regist).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startWithPop(LoginFragment3.newInstance(true));
                    LoginFragment.start(true);
                }
            });
            SoftInputHelper mSoftInputHelper = SoftInputHelper.attach(_mActivity).moveBy(contentView);
            mSoftInputHelper.moveWith(
                    signInLayout3.getSubmitView(),
                    signInLayout3.getEtAccount(),
                    signInLayout3.getEtPassword()
            );
            tvSubmit = contentView.findViewById(R.id.sv_login);
        }
        llContainer.addView(contentView);

        int dp16 = ScreenUtils.dp2pxInt(context, 16);
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, view, height -> {
            if (height > 0 && getTopFragment() == LoginFragment.this) {
                Rect rect = new Rect();
                tvSubmit.getGlobalVisibleRect(rect);
                float bottom = ScreenUtils.getScreenHeight(context) - rect.bottom - dp16;
                Log.d("LoginFragment3", "rect.bottom=" + rect.bottom + " bottom=" + bottom + " height=" + height);
                if (bottom < height) {
                    llContainer.setTranslationY(bottom - height);
                }
            } else {
                llContainer.setTranslationY(0);
            }
        });

    }

}
