package com.zpj.shouji.market.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.BlurBuilder;
import com.zpj.utils.ScreenUtil;

import eightbitlab.com.blurview.BlurAlgorithm;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class AddLayout extends FrameLayout {

    private final int[] menuIconItems = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4};
    private final String[] menuTextItems = {"动态", "应用集", "乐图", "私信"};
    private final Handler mHandler = new Handler();

    private BlurView blurView;
    private LinearLayout menuLayout;
    private View button;

    public AddLayout(@NonNull Context context) {
        this(context, null);
    }

    public AddLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_add_view, this, true);

        menuLayout = view.findViewById(R.id.icon_group);
        for (int i = 0; i < 4; i++) {
            View itemView = View.inflate(getContext(), R.layout.item_icon, null);
            ImageView menuImage = itemView.findViewById(R.id.menu_icon_iv);
            TextView menuText = itemView.findViewById(R.id.menu_text_tv);

            menuImage.setImageResource(menuIconItems[i]);
            menuText.setText(menuTextItems[i]);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            itemView.setLayoutParams(params);
            itemView.setVisibility(View.GONE);
            menuLayout.addView(itemView);
        }
        blurView = view.findViewById(R.id.blur_view);
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    public void bindButton(View view) {
        this.button = view;
    }

    public void initBlurView(ViewGroup view) {
        //        View decorView = activity.getWindow().getDecorView();
//        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
//        ViewGroup rootView = decorView.findViewById(android.R.id.content);
//        //Set drawable to draw in the beginning of each blurred frame (Optional).
//        //Can be used in case your layout has a lot of transparent space and your content
//        //gets kinda lost after after blur is applied.
//        Drawable windowBackground = view.getContext().getWindow().getDecorView().getBackground();
        blurView.setupWith(view)
//                .setFrameClearDrawable(view.getBackground())
                .setBlurAlgorithm(new RenderScriptBlur(view.getContext()))
                .setBlurRadius(16f)
                .setHasFixedTransformationMatrix(true);
    }

    public void show() {
        button.animate().rotation(135).setDuration(500);
        startAnimation();
        //菜单项弹出动画
        for (int i = 0; i < menuLayout.getChildCount(); i++) {
            final View child = menuLayout.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(() -> {
                child.setVisibility(View.VISIBLE);
                ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                fadeAnim.setDuration(500);
                KickBackAnimator kickAnimator = new KickBackAnimator();
                kickAnimator.setDuration(500);
                fadeAnim.setEvaluator(kickAnimator);
                fadeAnim.start();
            }, i * 50 + 100);
        }
    }

    public void close() {
        post(() -> button.animate().rotation(0).setDuration(500));
        closeAnimation();
    }

    private void startAnimation() {
        post(() -> {
            try {
                //圆形扩展的动画
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int x;
                    int y;
                    if (button == null) {
                        x = ScreenUtil.getScreenWidth(getContext()) / 2;
                        y = ScreenUtil.getScreenHeight(getContext());
                    } else {
                        x = button.getLeft() + button.getWidth() / 2;
                        y = button.getTop() + button.getHeight() / 2;
                    }

                    Animator animator = ViewAnimationUtils.createCircularReveal(this, x,
                            y, 0, getHeight());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //							layout.setVisibility(View.VISIBLE);
                        }
                    });
                    animator.setDuration(300);
                    animator.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void closeAnimation() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                int x;
                int y;
                if (button == null) {
                    x = ScreenUtil.getScreenWidth(getContext()) / 2;
                    y = ScreenUtil.getScreenHeight(getContext());
                } else {
                    x = button.getLeft() + button.getWidth() / 2;
                    y = button.getTop() + button.getHeight() / 2;
                }
                Animator animator = ViewAnimationUtils.createCircularReveal(this, x,
                        y, getHeight(), 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(View.GONE);
                        //dismiss();
                    }
                });
                animator.setDuration(300);
                animator.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
