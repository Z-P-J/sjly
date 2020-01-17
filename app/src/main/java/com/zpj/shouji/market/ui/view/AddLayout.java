package com.zpj.shouji.market.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
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

public class AddLayout extends FrameLayout {

    private final int[] menuIconItems = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4};
    private final String[] menuTextItems = {"动态", "应用集", "乐图", "催更"};
    private final Handler mHandler = new Handler();

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
        ImageView background = view.findViewById(R.id.background);
        background.setImageBitmap(BlurBuilder.blur(background));
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    public void bindButton(View view) {
        this.button = view;
        button.setOnClickListener(v -> {
            if (isShow()) {
                post(() -> button.animate().rotation(0).setDuration(500));
                close();
            } else {
                button.animate().rotation(135).setDuration(500);
                show();
            }
        });
    }

    public void show() {
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
