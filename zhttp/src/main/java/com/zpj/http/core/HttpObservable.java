package com.zpj.http.core;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HttpObservable<T> {

    private Observable<T> observable;

    private Scheduler subscribeScheduler;
    private Scheduler observeScheduler;

    private IHttp.OnSubscribeListener onSubscribeListener;
    private IHttp.OnSuccessListener<T> onSuccessListener;
    private IHttp.OnErrorListener onErrorListener;
    private IHttp.OnCompleteListener onCompleteListener;

    public interface OnFlatMapListener<T, R> {
        void onNext(T data, ObservableEmitter<R> emitter);
    }

    public interface OnNextListener<T, R> {
        HttpObservable<R> onNext(T data);
    }

    HttpObservable(Observable<T> observable) {
        this.observable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public HttpObservable<T> subscribeOn(Scheduler scheduler) {
        this.subscribeScheduler = scheduler;
        return this;
    }

    public HttpObservable<T> observeOn(Scheduler scheduler) {
        this.observeScheduler = scheduler;
        return this;
    }

    public HttpObservable<T> onSubscribe(IHttp.OnSubscribeListener listener) {
        this.onSubscribeListener = listener;
        return this;
    }

    public final HttpObservable<T> onError(IHttp.OnErrorListener listener) {
        this.onErrorListener = listener;
        return this;
    }

    public final HttpObservable<T> onSuccess(IHttp.OnSuccessListener<T> listener) {
        this.onSuccessListener = listener;
        return this;
    }

    public HttpObservable<T> onComplete(IHttp.OnCompleteListener listener) {
        this.onCompleteListener = listener;
        return this;
    }

    public final <R> HttpObservable<R> flatMap(final OnFlatMapListener<T, R> listener) {
        initScheduler();
        Observable<R> o = observable
                .flatMap(new Function<T, ObservableSource<R>>() {
                    @Override
                    public ObservableSource<R> apply(final T t) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<R>() {
                            @Override
                            public void subscribe(ObservableEmitter<R> emitter) throws Exception {
                                if (listener != null) {
                                    listener.onNext(t, emitter);
                                }
                                emitter.onComplete();
                            }
                        }).subscribeOn(subscribeScheduler).observeOn(observeScheduler);
                    }
                });
        return new HttpObservable<>(o)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

    public final <R> HttpObservable<R> onNext(final OnNextListener<T, R> listener) {
        initScheduler();
        Observable<R> o = observable
                .flatMap(new Function<T, ObservableSource<R>>() {
                    @Override
                    public ObservableSource<R> apply(final T t) throws Exception {
                        if (listener != null) {
                            HttpObservable<R> httpObservable = listener.onNext(t);
                            if (httpObservable != null) {
                                return httpObservable.observable;
                            }
                        }
                        return Observable.empty();
                    }
                });
        return new HttpObservable<>(o)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

    public void subscribe() {
        initScheduler();
        observable.subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (onSubscribeListener != null) {
                            onSubscribeListener.onSubscribe(d);
                        }
                    }

                    @Override
                    public void onNext(T data) {
                        if (onSuccessListener != null) {
                            try {
                                onSuccessListener.onSuccess(data);
                            } catch (Exception e) {
                                onError(e);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (onErrorListener != null) {
                            onErrorListener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (onCompleteListener != null) {
                            onCompleteListener.onComplete();
                        }
                    }
                });
    }

    private void initScheduler() {
        if (subscribeScheduler == null) {
            subscribeScheduler = Schedulers.io();
        }
        if (observeScheduler == null) {
            observeScheduler = AndroidSchedulers.mainThread();
        }
    }

}
