package com.zpj.shouji.market.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.zpj.toast.ZToast;
import com.zpj.downloader.ZDownloader;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.fragmentation.dialog.impl.LoadingDialogFragment;
import com.zpj.rxbus.RxObserver;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zxy.skin.sdk.SkinLayoutInflater;

import io.reactivex.functions.Consumer;

public class BaseActivity extends SupportActivity {

    private long firstTime = 0;

    private SkinLayoutInflater mLayoutInflater;
    private LoadingDialogFragment loadingDialogFragment;

    @NonNull
    @Override
    public final LayoutInflater getLayoutInflater() {
        if (mLayoutInflater == null) {
            mLayoutInflater = new SkinLayoutInflater(this);
        }
        return mLayoutInflater;
    }

    @Override
    public final Object getSystemService(@NonNull String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            return getLayoutInflater();
        }
        return super.getSystemService(name);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater();
        mLayoutInflater.applyCurrentSkin();
        // AppCompatActivity 需要设置
        AppCompatDelegate delegate = this.getDelegate();
        if (delegate instanceof LayoutInflater.Factory2) {
            mLayoutInflater.setFactory2((LayoutInflater.Factory2) delegate);
        }

//        registerObserver(StartFragmentEvent.class, event -> start(event.getFragment()));
//
        registerObserver(SupportFragment.class, this::start);

        registerObserver(ShowLoadingEvent.class, event -> {
            if (loadingDialogFragment != null) {
                if (event.isUpdate()) {
                    loadingDialogFragment.setTitle(event.getText());
                    return;
                }
                loadingDialogFragment.dismiss();
            }
            loadingDialogFragment = null;
            loadingDialogFragment = new LoadingDialogFragment().setTitle(event.getText());
            loadingDialogFragment.show(BaseActivity.this);
        });

        registerObserver(HideLoadingEvent.class, event -> {
            if (loadingDialogFragment != null) {
                loadingDialogFragment.setOnDismissListener(event.getOnDismissListener());
                loadingDialogFragment.dismiss();
                loadingDialogFragment = null;
            }
        });

//        EventSender.onHideLoadingEvent(this, listener -> {
//            if (loadingDialogFragment != null) {
//                loadingDialogFragment.setOnDismissListener(listener);
//                loadingDialogFragment.dismiss();
//                loadingDialogFragment = null;
//            }
//        });

//        RxObserver.with(this, "start_fragment", SupportFragment.class)
//                .bindToLife(this)
//                .subscribe(this::start);
//        RxObserver.with(this, "start_fragment")
//                .bindToLife(this)
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        ZToast.normal("sssss111=" + s);
//                    }
//                });
//        RxObserver.with(this, "start_fragment", String.class)
//                .bindToLife(this)
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        ZToast.normal("ssssss222222222=" + s);
//                    }
//                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialogFragment = null;
        mLayoutInflater.destory();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else if (System.currentTimeMillis() - firstTime > 2000) {
            ZToast.warning("再次点击退出！");
            firstTime = System.currentTimeMillis();
        } else {
//            finish();
            ZDownloader.pauseAll();
//            ZDownloader.onDestroy();
            ActivityCompat.finishAfterTransition(this);
        }
    }

    protected <T> void registerObserver(Class<T> type, Consumer<T> next) {
        RxObserver.with(this, type)
                .bindToLife(this)
                .subscribe(next);
    }

}
