package com.zpj.shouji.market.ui.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.shouji.market.BaseApplication;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.utils.ContextUtils;
import com.zpj.skin.SkinLayoutInflater;

public abstract class SkinFragment extends BaseFragment {

    protected void initStatusBar() {
        if (toolbar != null && toolbar.getVisibility() == View.VISIBLE) {
            if (AppConfig.isNightMode()) {
                lightStatusBar();
            } else {
                darkStatusBar();
            }
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        initStatusBar();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LayoutInflater layoutInflater = getLayoutInflater();
        if (layoutInflater instanceof SkinLayoutInflater) {
            SkinLayoutInflater skinLayoutInflater = (SkinLayoutInflater) layoutInflater;
            skinLayoutInflater.destory();
        }
    }

    @Override
    public void onDestroy() {
        ISupportFragment topFragment = getTopFragment();
        if (topFragment != null) {
            topFragment.onSupportVisible();
        }
        super.onDestroy();
    }

    public static void start(SupportFragment fragment) {
        BaseApplication.startFragment(fragment);
    }

}
