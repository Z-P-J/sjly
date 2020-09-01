package com.zpj.shouji.market;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        DoraemonKit.install(this);

        ViewTarget.setTagId(R.id.glide_tag_id);

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

}
