package com.zpj.shouji.market.ui.fragment.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import com.zpj.blur.ZBlurry;
import com.zpj.fragmentation.dialog.impl.FullScreenDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.animator.KickBackEvaluator;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionShareFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyPrivateLetterFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeShareFragment;
import com.zpj.shouji.market.ui.fragment.wallpaper.WallpaperShareFragment;
import com.zpj.shouji.market.ui.widget.MenuActionButton;
import com.zpj.toast.ZToast;

import java.util.ArrayList;
import java.util.List;

public class MainActionDialogFragment extends FullScreenDialogFragment
        implements View.OnClickListener {

    private final List<Animator> animatorList = new ArrayList<>();

    private FloatingActionButton floatingActionButton;

    public static MainActionDialogFragment with(Context context) {
        return new MainActionDialogFragment();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_actions;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        ImageView ivBg = findViewById(R.id.iv_bg);
        ivBg.setOnClickListener(v -> dismiss());
        MainFragment fragment = findFragment(MainFragment.class);
        if (fragment != null) {
            ZBlurry.with(fragment.getView())
                    .backgroundColor(Color.WHITE)
                    .scale(0.2f)
                    .radius(12)
                    .blur(ivBg::setImageBitmap);
        }

        getContentView().setAlpha(0f);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        MenuActionButton btn1 = findViewById(R.id.btn_1);
        MenuActionButton btn2 = findViewById(R.id.btn_2);
        MenuActionButton btn3 = findViewById(R.id.btn_3);
        MenuActionButton btn4 = findViewById(R.id.btn_4);

        addAnimator(btn1);
        addAnimator(btn2);
        addAnimator(btn3);
        addAnimator(btn4);
    }

    @Override
    public void doShowAnimation() {
        //菜单项弹出动画
        AnimatorSet set = new AnimatorSet();
        try {
            //圆形扩展的动画
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                int x = floatingActionButton.getLeft() + floatingActionButton.getWidth() / 2;
                int y = floatingActionButton.getTop() + floatingActionButton.getHeight() / 2;
                Animator animator = ViewAnimationUtils.createCircularReveal(getContentView(), x,
                        y, 0, getContentView().getHeight());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
//                            setVisibility(View.VISIBLE);
                        getContentView().setAlpha(1f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //							layout.setVisibility(View.VISIBLE);
                    }
                });
                animator.setDuration(360);
//                animator.start();
                animatorList.add(animator);
            } else {
                getContentView().animate().alpha(1f).setDuration(360).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        set.playTogether(animatorList);
        set.start();
        floatingActionButton.animate().rotation(135).setDuration(360).start();
//        startAnimation();
    }

    @Override
    public void doDismissAnimation() {
        post(() -> floatingActionButton.animate().rotation(0).setDuration(360).start());
        closeAnimation();
    }

    private void addAnimator(View child) {
        child.setOnClickListener(this);
        child.setVisibility(View.INVISIBLE);
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
        fadeAnim.setDuration(500);
        KickBackEvaluator kickAnimator = new KickBackEvaluator();
        kickAnimator.setDuration(500);
        fadeAnim.setEvaluator(kickAnimator);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                child.setVisibility(View.VISIBLE);
            }
        });

        fadeAnim.setStartDelay(animatorList.size() * 50 + 100);
        animatorList.add(fadeAnim);
    }

//    private void startAnimation() {
//        post(() -> {
//            try {
//                //圆形扩展的动画
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    int x = floatingActionButton.getLeft() + floatingActionButton.getWidth() / 2;
//                    int y = floatingActionButton.getTop() + floatingActionButton.getHeight() / 2;
//                    Animator animator = ViewAnimationUtils.createCircularReveal(getContentView(), x,
//                            y, 0, getContentView().getHeight());
//                    animator.addListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
////                            setVisibility(View.VISIBLE);
//                            getContentView().setAlpha(1f);
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            //							layout.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    animator.setDuration(360);
//                    animator.start();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//    }

    private void closeAnimation() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int x = floatingActionButton.getLeft() + floatingActionButton.getWidth() / 2;
            int y = floatingActionButton.getTop() + floatingActionButton.getHeight() / 2;
            Animator animator = ViewAnimationUtils.createCircularReveal(getContentView(), x,
                    y, getContentView().getHeight(), 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    getContentView().setVisibility(View.GONE);
                }
            });
            animator.setDuration(360);
            animator.start();
        } else {
            super.doDismissAnimation();
        }
    }

    @Override
    public void onClick(View v) {
        if (!UserManager.getInstance().isLogin()) {
            ZToast.warning(R.string.text_msg_not_login);
            LoginFragment.start();
        } else if (R.id.btn_1 == v.getId()) {
            ThemeShareFragment.start();
        } else if (R.id.btn_2 == v.getId()) {
            CollectionShareFragment.start();
        } else if (R.id.btn_3 == v.getId()) {
            WallpaperShareFragment.start();
        } else if (R.id.btn_4 == v.getId()) {
            MyPrivateLetterFragment.start();
        }

        dismiss();
    }

}
