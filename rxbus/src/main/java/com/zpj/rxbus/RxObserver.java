package com.zpj.rxbus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;

import com.dhh.rxlife2.LifecycleTransformer;
import com.dhh.rxlife2.RxLife;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

public class RxObserver<T> {

    private final Object o;
    private final String key;
    private final Observable<T> observable;
    private Scheduler subscribeScheduler = Schedulers.io();
    private Scheduler observeScheduler = AndroidSchedulers.mainThread();
    private ObservableTransformer<? super T, ? extends T> composer;

    private RxObserver(Object o, Class<T> type) {
        this(o, null, type);
    }

    private RxObserver(Object o, String key, Observable<T> observable) {
        this.o = o;
        this.key = key == null ? null : key.trim();
        this.observable = observable;
    }

    private RxObserver(Object o, String key, Class<T> type) {
        this.o = o;
        this.key = key == null ? null : key.trim();
//        this.key = key.trim();
//        observable = RxBus2.get()
//                .toObservable(type);
        if (TextUtils.isEmpty(key)) {
            observable = RxBus.get()
                    .toObservable(type);
        } else {
            observable = RxBus.get()
                    .toObservable(key, type);
        }
    }

    public static <T> RxObserver<T> with(@NonNull Object o, @NonNull Class<T> type) {
        return new RxObserver<>(o, type);
    }

    public static RxObserver<String> with(@NonNull Object o, @NonNull String key) {
        return new RxObserver<>(o, key, RxBus.get().toObservable(key));
    }

    public static <T> RxObserver<T> with(@NonNull Object o, @NonNull String key, @NonNull Class<T> type) {
        return new RxObserver<>(o, key, type);
    }

    public static <T> RxObserver<T> withSticky(@NonNull Object o, @NonNull Class<T> type) {
        return new RxObserver<>(o, null, RxBus.get().toObservableSticky(type));
    }

    public static RxObserver<String> withSticky(@NonNull Object o, @NonNull String key) {
        return new RxObserver<>(o, key, RxBus.get().toObservableSticky(key));
    }

    public static <T> RxObserver<T> withSticky(@NonNull Object o, @NonNull String key, @NonNull Class<T> type) {
        return new RxObserver<>(o, key, RxBus.get().toObservableSticky(key, type));
    }

    public static <T> T removeStickyEvent(Class<T> eventType) {
        return RxBus.get().removeStickyEvent(eventType);
    }

    public static Object removeStickyEvent(String key) {
        return RxBus.get().removeStickyEvent(key);
    }

    public static <T> T removeStickyEvent(String key, Class<T> type) {
        return RxBus.get().removeStickyEvent(key, type);
    }

    public static void removeAllStickyEvents() {
        RxBus.get().removeAllStickyEvents();
    }

    public RxObserver<T> subscribeOn(Scheduler scheduler) {
        this.subscribeScheduler = scheduler;
        return this;
    }

    public RxObserver<T> observeOn(Scheduler scheduler) {
        this.observeScheduler = scheduler;
        return this;
    }

//    public RxObserver<T> compose(ObservableTransformer<? super T, ? extends T> composer) {
//        this.composer = composer;
//        return this;
//    }

    public RxObserver<T> bindToLife(LifecycleOwner lifecycleOwner) {
        this.composer = RxLife.with(lifecycleOwner).bindToLifecycle();
//        this.observable = this.observable.compose(RxLife.with(lifecycleOwner).bindToLifecycle());
        return this;
    }

    public RxObserver<T> bindToLife(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        this.composer = RxLife.with(lifecycleOwner).bindUntilEvent(event);
//        this.observable = this.observable.compose(RxLife.with(lifecycleOwner).bindUntilEvent(event));
        return this;
    }

    public RxObserver<T> bindOnDestroy(LifecycleOwner lifecycleOwner) {
        return bindToLife(lifecycleOwner, Lifecycle.Event.ON_DESTROY);
    }

//    public void subscribe() {
//        RxBus2.get().addSubscription(o, observable.subscribe());
//    }

    public void subscribe(Consumer<T> next) {
//        RxBus2.get().addSubscription(o, wrapObservable().subscribe(next));
        subscribe(next, Functions.ON_ERROR_MISSING);
    }

    public void subscribe(Consumer<T> next, Consumer<Throwable> error) {
//        RxBus2.get().addSubscription(o, wrapObservable().subscribe(next, error));


        Observable<T> observable;
        if (composer == null && o instanceof LifecycleOwner) {
            composer = RxLife.with((LifecycleOwner) o).bindToLifecycle();
        }
        if (composer != null) {
            observable = this.observable.compose(composer);
        } else {
            observable = this.observable;
        }
        if (subscribeScheduler == null) {
            subscribeScheduler = Schedulers.io();
        }
        if (observeScheduler == null) {
            observeScheduler = AndroidSchedulers.mainThread();
        }
        Disposable disposable = observable
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(next, error);
        if (!(composer instanceof LifecycleTransformer)) {
            RxBus.get().addSubscription(o, disposable);
        }
    }

//    public static class RxEmptyObserver extends RxObserver<String> {
//
//        private RxEmptyObserver(@NonNull Object o, @NonNull String key, @NonNull Observable<String> observable) {
//            super(o, key, observable);
//        }
//
//        @Override
//        public void subscribe(Consumer<String> next) {
//            super.subscribe(next);
//        }
//    }

//    private Observable<T> wrapObservable() {
//        Observable<T> observable;
//        if (composer != null) {
//            observable = this.observable.compose(composer);
//        } else {
//            observable = this.observable;
//        }
//        if (subscribeScheduler == null) {
//            subscribeScheduler = Schedulers.io();
//        }
//        if (observeScheduler == null) {
//            observeScheduler = AndroidSchedulers.mainThread();
//        }
//        return observable
//                .subscribeOn(subscribeScheduler)
//                .observeOn(observeScheduler);
//    }

    public static void unSubscribe(Object o) {
        RxBus.get().unSubscribe(o);
    }

}