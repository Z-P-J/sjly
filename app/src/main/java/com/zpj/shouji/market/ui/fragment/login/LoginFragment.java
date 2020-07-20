package com.zpj.shouji.market.ui.fragment.login;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.swipeback.SwipeBackLayout;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.event.ToggleLoginModeEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.setting.CommonSettingFragment;
import com.zpj.shouji.market.ui.widget.AutoSizeViewPager;
import com.zpj.shouji.market.ui.widget.popup.LoginPopup2;
import com.zpj.shouji.market.utils.RxAnimationTool;
import com.zpj.shouji.market.utils.SoftInputHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Random;

import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.DefaultVerticalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;

public class LoginFragment extends BaseFragment
        implements UserManager.OnSignInListener, UserManager.OnSignUpListener {

//    private RelativeLayout rl_input;
//    private ImageView iv_circle_1;
//    private ImageView iv_circle_2;
    private AutoSizeViewPager vp;
//    private FrameLayout fl_eye;

    private boolean isRunning = false;
    private AnimatorSet mSet1;
    private AnimatorSet mSet2;
    private SoftInputHelper mSoftInputHelper;

    private ValueAnimator valueAnimator;

    public static LoginFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(Keys.PAGE, page);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start() {
        StartFragmentEvent.start(new LoginFragment());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setFragmentAnimator(new DefaultVerticalAnimator());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        UserManager.getInstance().addOnSignInListener(this);
        UserManager.getInstance().addOnSignUpListener(this);

//        rl_input = view.findViewById(R.id.rl_input);
//        iv_circle_1 = view.findViewById(R.id.iv_circle_1);
//        iv_circle_2 = view.findViewById(R.id.iv_circle_2);
        vp = view.findViewById(R.id.vp);
//        vp.setCanScroll(false);
//        fl_eye = view.findViewById(R.id.fl_eye);

//        view.findViewById(R.id.scroll_view)
//        mSoftInputHelper = SoftInputHelper.attach(_mActivity).moveBy(vp);

//        View test = view.findViewById(R.id.test);


        com.zpj.popup.util.KeyboardUtils.registerSoftInputChangedListener(_mActivity, vp, height -> {

            Log.d("KeyboardUtils", "height=" + height);
            if (height == 0) {
                vp.setTranslationY(0);
                return;
            }
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            valueAnimator = ValueAnimator.ofInt((int) vp.getTranslationY(), -height);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();//根据时间因子的变化系数进行设置高度
                    vp.setTranslationY(value);
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    valueAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    valueAnimator = null;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.setDuration(250);
            valueAnimator.start();
        });

        postOnEnterAnimationEnd(() -> {
            ArrayList<Fragment> list = new ArrayList<>();
            SignInFragment signInFragment = findChildFragment(SignInFragment.class);
            if (signInFragment == null) {
                signInFragment = new SignInFragment();
            }
            SignUpFragment signUpFragment = findChildFragment(SignUpFragment.class);
            if (signUpFragment == null) {
                signUpFragment = new SignUpFragment();
            }

            list.add(signInFragment);
            list.add(signUpFragment);

            FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, null);
            vp.setAdapter(adapter);

            setEdgeOrientation(SwipeBackLayout.EDGE_TOP);


            if (getArguments() != null) {
                vp.setCurrentItem(getArguments().getInt(Keys.PAGE), false);
            }

//            LoginPopup2.with(context).show();
        });
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultVerticalAnimator();
    }

    @Override
    public void onDestroy() {
        _mActivity.setFragmentAnimator(new DefaultHorizontalAnimator());
        UserManager.getInstance().removeOnSignInListener(this);
        UserManager.getInstance().removeOnSignUpListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        isRunning = true;
//        mSet1 = startCircleAnim(iv_circle_1);
//        mSet2 = startCircleAnim(iv_circle_2);
        super.onStart();
    }

    @Override
    public void onStop() {
        isRunning = false;
        stopCircleAnim();
        super.onStop();
    }

    @Subscribe
    public void onToggleLoginModeEvent(ToggleLoginModeEvent event) {
        if (vp != null) {
            if (vp.getCurrentItem() <= 1) {
                vp.setCurrentItem(1 - vp.getCurrentItem());
            }
        }
    }

    public SoftInputHelper getSoftInputHelper() {
        return mSoftInputHelper;
    }

    private void stopCircleAnim() {
        if (mSet1 != null) {
            mSet1.cancel();
            mSet1 = null;
        }
        if (mSet2 != null) {
            mSet2.cancel();
            mSet2 = null;
        }
    }

//    public void doEyeAnim(boolean close) {
//        int h = fl_eye.getHeight();
//        if (h <= 0) {
//            return;
//        }
//        float endY = close ? h : 0;
//        ObjectAnimator anim = ObjectAnimator.ofFloat(fl_eye, "translationY", fl_eye.getTranslationY(), endY);
//        anim.setInterpolator(new AccelerateDecelerateInterpolator());
//        anim.start();
//    }

    private AnimatorSet startCircleAnim(View target) {
        if (target == null) {
            return null;
        }
        float[] xy = calculateRandomXY();
        AnimatorSet set = createTranslationAnimator(target, xy[0], xy[1]);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isRunning) {
                    startCircleAnim(target);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
        return set;
    }

    private final long mMaxMoveDuration = 20000L;
    private final int mMaxMoveDistanceX = 200;
    private final int mMaxMoveDistanceY = 20;

    private AnimatorSet createTranslationAnimator(View target, float toX, float toY) {
        float fromX = target.getTranslationX();
        float fromY = target.getTranslationY();
        long duration = calculateDuration(fromX, fromY, toX, toY);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(target, "translationX", fromX, toX);
        animatorX.setDuration(duration);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(target, "translationY", fromY, toY);
        animatorY.setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        return set;
    }

    private Random mRandom = new Random();

    private float[] calculateRandomXY() {
        float x = mRandom.nextInt(mMaxMoveDistanceX) - (mMaxMoveDistanceX * 0.5F);
        float y = mRandom.nextInt(mMaxMoveDistanceY) - (mMaxMoveDistanceY * 0.5F);
        return new float[]{x, y};
    }

    private long calculateDuration(float x1, float y1, float x2, float y2) {
        float distance = (float) Math.abs(Math.sqrt(Math.pow(Math.abs((x1 - x2)), 2) + Math.pow(Math.abs((y1 - y2)), 2)));
        float maxDistance = (float) Math.abs(Math.sqrt(Math.pow(mMaxMoveDistanceX, 2) + Math.pow(mMaxMoveDistanceY, 2)));
        long duration = (long) (mMaxMoveDuration * (distance / maxDistance));
        Log.d("calculateDuration", "distance=" + distance + ", duration=" + duration);
        return duration;
    }

    @Override
    public void onSignInSuccess() {
        pop();
    }

    @Override
    public void onSignInFailed(String errInfo) {

    }

    @Override
    public void onSignUpSuccess() {
        if (vp != null) {
            vp.setCurrentItem(0, true);
        }
    }

    @Override
    public void onSignUpFailed(String errInfo) {

    }

}
