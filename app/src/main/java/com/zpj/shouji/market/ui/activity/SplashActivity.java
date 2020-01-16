package com.zpj.shouji.market.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.AppUpdateHelper;

import site.gemus.openingstartanimation.OpeningStartAnimation;
import site.gemus.openingstartanimation.RotationDrawStrategy;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        AppUpdateHelper.getInstance().checkUpdate(this);
        OpeningStartAnimation openingStartAnimation3 = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new RotationDrawStrategy())
                .setAppName("手机乐园")
                .setAppStatement("分享优质应用")
                .setAnimationInterval(1500)
                .setAppIcon(getResources().getDrawable(R.drawable.ic_app))
                .setAnimationListener(new OpeningStartAnimation.AnimationListener() {
                    @Override
                    public void onFinish(OpeningStartAnimation openingStartAnimation, Activity activity) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .create();
        openingStartAnimation3.show(this);
    }


}
