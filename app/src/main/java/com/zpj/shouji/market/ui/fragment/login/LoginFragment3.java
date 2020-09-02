package com.zpj.shouji.market.ui.fragment.login;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.SignInEvent;
import com.zpj.shouji.market.event.SignUpEvent;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.ui.widget.SignInLayout3;
import com.zpj.shouji.market.ui.widget.SignUpLayout2;
import com.zpj.shouji.market.ui.widget.SignUpLayout3;
import com.zpj.shouji.market.utils.RxAnimationTool;
import com.zpj.shouji.market.utils.SoftInputHelper;
import com.zpj.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class LoginFragment3 extends BaseFragment {

    private static final String REGISTRATION = "key_registration";

    private View contentView;

    private boolean isRegistration = false;

    public static LoginFragment3 newInstance(boolean isRegistration) {
        Bundle args = new Bundle();
        args.putBoolean(REGISTRATION, isRegistration);
        LoginFragment3 fragment = new LoginFragment3();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(boolean isRegistration) {
        StartFragmentEvent.start(LoginFragment3.newInstance(isRegistration));
    }

    public static void start() {
        start(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login3;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "账号登录";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            isRegistration = getArguments().getBoolean(REGISTRATION, false);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(contentView);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
                    LoginFragment3.start(true);
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
        EventBus.getDefault().register(contentView);
        llContainer.addView(contentView);

        int dp16 = ScreenUtils.dp2pxInt(context, 16);
        KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
            if (height > 0) {
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

    @Subscribe
    public void onSignInEvent(SignInEvent event) {
        if (event.isSuccess()) {
            AToast.success("登录成功！");
            pop();
        }
    }

    @Subscribe
    public void onSignUpEvent(SignUpEvent event) {
        if (event.isSuccess()) {
            AToast.success("注册成功，请输入账账户信息登录！");
            if (SupportHelper.getPreFragment(this) instanceof LoginFragment3) {
                pop();
            } else {
                startWithPop(LoginFragment3.newInstance(false));
            }
        }
    }


}
