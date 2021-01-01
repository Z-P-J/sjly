package com.zpj.shouji.market.glide.custom;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

public abstract class GlideCustomData<T> implements DataFetcher<T> {

    protected Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super T> callback) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

    @NonNull
    public abstract Object getKey();

}
