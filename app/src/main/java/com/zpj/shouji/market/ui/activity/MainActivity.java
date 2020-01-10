package com.zpj.shouji.market.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.main.MainFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;

import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {

    private long firstTime = 0;


//    private EasyNavigationBar navigationBar;
//    private Handler mHandler = new Handler();
//    private LinearLayout menuLayout;
//    private View cancelImageView;
//    private int[] menuIconItems = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4};
//
//
//    private String[] tabText = {"主页", "游戏", " ", "软件", "我的"};
//    //未选中icon
//    private int[] normalIcon = {R.drawable.index, R.drawable.find, R.drawable.add_image, R.drawable.message, R.drawable.me};
//    //选中时icon
//    private int[] selectIcon = {R.drawable.index1, R.drawable.find1, R.drawable.add_image, R.drawable.message1, R.drawable.me1};
//    private String[] menuTextItems = {"动态", "应用集", "乐图", "催更"};
//
//    private List<Fragment> fragments = new ArrayList<>();

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initView();
        mainFragment = findFragment(MainFragment.class);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            loadRootFragment(R.id.content, mainFragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExecutorHelper.destroy();
    }

//    private void initView() {
//        navigationBar = findViewById(R.id.navigationBar);
//        fragments.add(new HomeFragment());
//        fragments.add(new QianQianFragment());
//        fragments.add(new XinHaiFragment());
////        fragments.add(new AppChinaFragment());
//        fragments.add(UserFragment.newInstance("5636865", true));
//
//        navigationBar.titleItems(tabText)
//                .normalIconItems(normalIcon)
//                .selectIconItems(selectIcon)
//                .fragmentList(fragments)
//                .fragmentManager(getSupportFragmentManager())
//                .addLayoutRule(EasyNavigationBar.RULE_BOTTOM)
//                .addLayoutBottom(0)
//                .onTabClickListener(new EasyNavigationBar.OnTabClickListener() {
//                    @Override
//                    public boolean onTabClickEvent(View view, int position) {
//                        if (position == 2) {
//                            showMunu();
//                        }
//                        return false;
//                    }
//                })
//                .mode(EasyNavigationBar.MODE_ADD)
//                .anim(Anim.ZoomIn)
//                .build();
//
//
//        navigationBar.setAddViewLayout(createWeiboView());
//    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        if (System.currentTimeMillis() - firstTime > 2000) {
            Toast.makeText(this, "再次点击退出！", Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

//    private View createWeiboView() {
//        BlurBuilder.snapShotWithoutStatusBar(this);
//        ViewGroup view = (ViewGroup) View.inflate(MainActivity.this, R.layout.layout_add_view, null);
//        menuLayout = view.findViewById(R.id.icon_group);
//        cancelImageView = view.findViewById(R.id.cancel_iv);
//        cancelImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                closeAnimation();
//            }
//        });
//        for (int i = 0; i < 4; i++) {
//            View itemView = View.inflate(MainActivity.this, R.layout.item_icon, null);
//            ImageView menuImage = itemView.findViewById(R.id.menu_icon_iv);
//            TextView menuText = itemView.findViewById(R.id.menu_text_tv);
//
//            menuImage.setImageResource(menuIconItems[i]);
//            menuText.setText(menuTextItems[i]);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.weight = 1;
//            itemView.setLayoutParams(params);
//            itemView.setVisibility(View.GONE);
//            menuLayout.addView(itemView);
//        }
//        ImageView background = view.findViewById(R.id.background);
//        background.setImageBitmap(BlurBuilder.blur(background));
//        return view;
//    }
//
//    private void showMunu() {
//        startAnimation();
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                //＋ 旋转动画
//                cancelImageView.animate().rotation(90).setDuration(400);
//            }
//        });
//        //菜单项弹出动画
//        for (int i = 0; i < menuLayout.getChildCount(); i++) {
//            final View child = menuLayout.getChildAt(i);
//            child.setVisibility(View.INVISIBLE);
//            mHandler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    child.setVisibility(View.VISIBLE);
//                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
//                    fadeAnim.setDuration(500);
//                    KickBackAnimator kickAnimator = new KickBackAnimator();
//                    kickAnimator.setDuration(500);
//                    fadeAnim.setEvaluator(kickAnimator);
//                    fadeAnim.start();
//                }
//            }, i * 50 + 100);
//        }
//    }
//
//    private void startAnimation() {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //圆形扩展的动画
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                        int x = NavigationUtil.getScreenWidth(MainActivity.this) / 2;
//                        int y = NavigationUtil.getScreenHeith(MainActivity.this) - NavigationUtil.dip2px(MainActivity.this, 25);
//                        Animator animator = ViewAnimationUtils.createCircularReveal(navigationBar.getAddViewLayout(), x,
//                                y, 0, navigationBar.getAddViewLayout().getHeight());
//                        animator.addListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                navigationBar.getAddViewLayout().setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                //							layout.setVisibility(View.VISIBLE);
//                            }
//                        });
//                        animator.setDuration(300);
//                        animator.start();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
//
//    private void closeAnimation() {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                cancelImageView.animate().rotation(0).setDuration(400);
//            }
//        });
//
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//
//                int x = NavigationUtil.getScreenWidth(this) / 2;
//                int y = (NavigationUtil.getScreenHeith(this) - NavigationUtil.dip2px(this, 25));
//                Animator animator = ViewAnimationUtils.createCircularReveal(navigationBar.getAddViewLayout(), x,
//                        y, navigationBar.getAddViewLayout().getHeight(), 0);
//                animator.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        //							layout.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        navigationBar.getAddViewLayout().setVisibility(View.GONE);
//                        BlurBuilder.recycle();
//                        //dismiss();
//                    }
//                });
//                animator.setDuration(300);
//                animator.start();
//            }
//        } catch (Exception ignored) {
//
//        }
//    }
}
