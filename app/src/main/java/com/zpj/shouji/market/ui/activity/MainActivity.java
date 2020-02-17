package com.zpj.shouji.market.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.felix.atoast.library.AToast;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.utils.AppUpdateHelper;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class MainActivity extends SupportActivity {

    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        OpeningStartAnimation openingStartAnimation3 = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new NormalDrawStrategy())
                .setAppName("手机乐园")
                .setAppStatement("分享优质应用")
                .setAnimationInterval(2000)
                .setAppIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .setAnimationListener(new OpeningStartAnimation.AnimationListener() {
                    @Override
                    public void onFinish(OpeningStartAnimation openingStartAnimation, Activity activity) {
                        openingStartAnimation.dismiss(activity);
                        StatusBarUtils.setDarkMode(getWindow());
                        AppUpdateHelper.getInstance().checkUpdate(MainActivity.this);
                    }
                })
                .create();
        openingStartAnimation3.show(this);

        MainFragment mainFragment = findFragment(MainFragment.class);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            loadRootFragment(R.id.content, mainFragment);
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else if (System.currentTimeMillis() - firstTime > 2000) {
            AToast.warning("再次点击退出！");
            firstTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
}
