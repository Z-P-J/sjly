package com.zpj.shouji.market.ui.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.PageAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.fragment.main.homepage.HomeFragment;
import com.zpj.shouji.market.ui.fragment.main.user.UserFragment;
import com.zpj.shouji.market.ui.view.KickBackAnimator;
import com.zpj.shouji.market.utils.BlurBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment {

//    private EasyNavigationBar navigationBar;
    private Handler mHandler = new Handler();
    private LinearLayout menuLayout;
    private View cancelImageView;
    private int[] menuIconItems = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4};


    private String[] tabText = {"主页", "游戏", " ", "软件", "我的"};
    //未选中icon
    private int[] normalIcon = {R.drawable.index, R.drawable.find, R.drawable.tab_add_selector, R.drawable.message, R.drawable.me};
    //选中时icon
    private int[] selectIcon = {R.drawable.index1, R.drawable.find1, R.drawable.tab_add_selector, R.drawable.message1, R.drawable.me1};
    private String[] menuTextItems = {"动态", "应用集", "乐图", "催更"};

    private final List<Fragment> fragments = new ArrayList<>();

    private FloatingActionButton floatingActionButton;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_tab;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        fragments.add(new HomeFragment());
        fragments.add(new QianQianFragment());
        fragments.add(new XinHaiFragment());
        fragments.add(UserFragment.newInstance("5636865", true));

        floatingActionButton = view.findViewById(R.id.fab);

        BottomNavigationViewEx navigationView = view.findViewById(R.id.navigation_view);
        navigationView.enableItemShiftingMode(false);
        navigationView.enableShiftingMode(false);
        navigationView.enableAnimation(false);
        ViewPager viewPager = view.findViewById(R.id.vp);
        viewPager.setOffscreenPageLimit(10);
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(), fragments, tabText);
        viewPager.setAdapter(adapter);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int position = 0;
                switch (menuItem.getItemId()) {
                    case R.id.i_homepage:
                        position = 0;
                        break;
                    case R.id.i_app:
                        position = 1;
                        break;
                    case R.id.i_game:
                        position = 2;
                        break;
                    case R.id.i_me:
                        position = 3;
                        break;
                    case R.id.i_empty: {
                        return false;
                    }
                }
                if(previousPosition != position) {
                    viewPager.setCurrentItem(position, false);
                    previousPosition = position;
                }

                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                navigationView.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weiboView.getVisibility() == View.VISIBLE) {
                    closeAnimation();
                } else {
                    showMunu();
                }
            }
        });
        weiboView = view.findViewById(R.id.layout_add_view);
        createWeiboView(weiboView);
    }

    @Override
    public boolean onBackPressedSupport() {
        if (weiboView.getVisibility() == View.VISIBLE) {
            closeAnimation();
            return true;
        }
        return super.onBackPressedSupport();
    }

    private View weiboView;

    private View createWeiboView(View view) {
        BlurBuilder.snapShotWithoutStatusBar(getActivity());
//        ViewGroup view = (ViewGroup) View.inflate(getContext(), R.layout.layout_add_view, null);
        menuLayout = view.findViewById(R.id.icon_group);
        cancelImageView = view.findViewById(R.id.cancel_iv);
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAnimation();
            }
        });
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
        return view;
    }

    private void showMunu() {
        startAnimation();
        post(() -> {
            floatingActionButton.animate().rotation(135).setDuration(500);
        });
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

    private void startAnimation() {
        post(() -> {
            try {
                //圆形扩展的动画
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int x = floatingActionButton.getLeft() + floatingActionButton.getWidth() / 2;
                    int y = floatingActionButton.getTop() + floatingActionButton.getHeight() / 2;
                    Animator animator = ViewAnimationUtils.createCircularReveal(weiboView, x,
                            y, 0, weiboView.getHeight());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            weiboView.setVisibility(View.VISIBLE);
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
        post(() -> floatingActionButton.animate().rotation(0).setDuration(500));

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                int[] viewLocation = new int[2];
//                floatingActionButton.getLocationInWindow(viewLocation);
//                int viewX = viewLocation[0];
//                int viewY = viewLocation[1];
                int x = floatingActionButton.getLeft() + floatingActionButton.getWidth() / 2;
                int y = floatingActionButton.getTop() + floatingActionButton.getHeight() / 2;
                Animator animator = ViewAnimationUtils.createCircularReveal(weiboView, x,
                        y, weiboView.getHeight(), 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        weiboView.setVisibility(View.GONE);
                        BlurBuilder.recycle();
                        //dismiss();
                    }
                });
                animator.setDuration(300);
                animator.start();
            }
        } catch (Exception ignored) {

        }
    }
}
