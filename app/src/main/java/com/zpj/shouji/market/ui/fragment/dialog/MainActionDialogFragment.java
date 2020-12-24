package com.zpj.shouji.market.ui.fragment.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.blur.ZBlurry;
import com.zpj.fragmentation.dialog.impl.FullScreenDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.animator.KickBackAnimator;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionShareFragment;
import com.zpj.shouji.market.ui.fragment.login.LoginFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyPrivateLetterFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeShareFragment;
import com.zpj.shouji.market.ui.fragment.wallpaper.WallpaperShareFragment;
import com.zpj.toast.ZToast;
import com.zpj.utils.ScreenUtils;

public class MainActionDialogFragment extends FullScreenDialogFragment
        implements View.OnClickListener {

    private final int[] menuIconItems = {
            R.drawable.ic_homepage_white_24dp,
            R.drawable.ic_app_collection_white_24dp,
            R.drawable.ic_image_black_24dp,
            R.drawable.ic_message_white_24dp
    };
    private final int[] menuItemColors = {
            R.color.light_blue_1,
            R.color.light_red_1,
            R.color.light_blue_2,
            R.color.light_purple
    };
    private final String[] menuTextItems = {"动态", "应用集", "乐图", "私聊"};

    private LinearLayout menuLayout;
    private FloatingActionButton floatingActionButton;

    public static MainActionDialogFragment with(Context context) {
        return new MainActionDialogFragment();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_popup_main_action;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        ImageView ivBg = findViewById(R.id.iv_bg);
        MainFragment fragment = findFragment(MainFragment.class);
        if (fragment != null) {
            ZBlurry.with(fragment.getView())
                    .backgroundColor(Color.WHITE)
                    .scale(0.2f)
                    .radius(20)
                    .blur(bitmap -> {
                        if (ivBg != null) {
                            ivBg.setImageBitmap(bitmap);
                        }
                    });
        }
//        GetMainFragmentEvent.post(new Callback<MainFragment>() {
//            @Override
//            public void onCallback(MainFragment fragment) {
//                ZBlurry.with(fragment.getView())
//                        .backgroundColor(Color.WHITE)
//                        .scale(0.2f)
//                        .radius(20)
//                        .blur(new ZBlurry.Callback() {
//                            @Override
//                            public void down(Bitmap bitmap) {
//                                if (ivBg != null) {
//                                    ivBg.setImageBitmap(bitmap);
//                                }
//                            }
//                        });
//            }
//        });


        getContentView().setAlpha(0f);
        menuLayout = findViewById(R.id.icon_group);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        for (int i = 0; i < 4; i++) {
            View itemView = createView(context, i);
            itemView.setTag(i);
            itemView.setOnClickListener(this);
            menuLayout.addView(itemView);
        }

        menuLayout.setOnClickListener(v -> dismiss());
    }

    @Override
    public void doShowAnimation() {
        //菜单项弹出动画
        for (int i = 0; i < menuLayout.getChildCount(); i++) {
            final View child = menuLayout.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
            getContentView().postDelayed(() -> {
                child.setVisibility(View.VISIBLE);
                ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                fadeAnim.setDuration(500);
                KickBackAnimator kickAnimator = new KickBackAnimator();
                kickAnimator.setDuration(500);
                fadeAnim.setEvaluator(kickAnimator);
                fadeAnim.start();
            }, i * 50 + 100);
        }

        floatingActionButton.animate().rotation(135).setDuration(300);
        startAnimation();
    }

    @Override
    public void doDismissAnimation() {
//        super.doDismissAnimation();
        post(() -> floatingActionButton.animate().rotation(0).setDuration(300));
        closeAnimation();
    }

    private View createView(Context context, int index) {
        View itemView = View.inflate(context, R.layout.item_icon, null);
//        ImageView menuImage = itemView.findViewById(R.id.menu_icon_iv);
        FloatingActionButton btnAction = itemView.findViewById(R.id.btn_action);
        TextView menuText = itemView.findViewById(R.id.menu_text_tv);

        btnAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(menuItemColors[index])));
        btnAction.setImageResource(menuIconItems[index]);
//        menuImage.setImageResource(menuIconItems[index]);
        menuText.setText(menuTextItems[index]);
        menuText.setTextColor(getResources().getColor(menuItemColors[index]));
        menuText.setShadowLayer(1, 1, 1, Color.WHITE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.bottomMargin = ScreenUtils.dp2pxInt(context, 120);
        itemView.setLayoutParams(params);
        itemView.setVisibility(View.GONE);
        return itemView;
    }

//    @Override
//    public BasePopupView show() {
//        return super.show();
//    }

    private void startAnimation() {
        post(() -> {
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
                    animator.setDuration(300);
                    animator.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

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
            animator.setDuration(300);
            animator.start();
        } else {
            super.doDismissAnimation();
        }
    }

    @Override
    public void onClick(View v) {

        Object tag = v.getTag();
        if (tag instanceof Integer) {
            if (!UserManager.getInstance().isLogin()) {
                ZToast.warning(R.string.text_msg_not_login);
                LoginFragment.start();
//                dismiss();
//                dismissWithStart(LoginFragment.newInstance(false));
//                return;
            } else {
                switch ((int) v.getTag()) {
                    case 0:
                        ThemeShareFragment.start();
//                    dismissWithStart(new ThemeShareFragment());
                        break;
                    case 1:
                        CollectionShareFragment.start();
//                    dismissWithStart(new CollectionShareFragment());
                        break;
                    case 2:
                        WallpaperShareFragment.start();
//                    dismissWithStart(new WallpaperShareFragment());
                        break;
                    case 3:
                        MyPrivateLetterFragment.start();
//                    dismissWithStart(MyPrivateLetterFragment.newInstance());
                        break;
//                default:
//                    dismiss();
//                    break;
                }
            }

        }
        dismiss();
    }

}
