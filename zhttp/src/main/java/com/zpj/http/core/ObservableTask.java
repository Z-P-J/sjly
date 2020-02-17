package com.zpj.http.core;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ObservableTask<T> {

    private Observable<T> observable;
    private Disposable disposable;

    private Scheduler subscribeScheduler;
    private Scheduler observeScheduler;

    private IHttp.OnSubscribeListener onSubscribeListener;
    private IHttp.OnSuccessListener<T> onSuccessListener;
    private IHttp.OnErrorListener onErrorListener;
    private IHttp.OnCompleteListener onCompleteListener;

    public interface OnFlatMapListener<T, R> {
        void onNext(T data, ObservableEmitter<R> emitter) throws Exception;
    }

    public interface OnNextListener<T, R> {
        ObservableTask<R> onNext(T data) throws Exception;
    }

    public ObservableTask(final ObservableOnSubscribe<T> observableOnSubscribe) {
        this(Observable.create(observableOnSubscribe));
    }

    public ObservableTask(Observable<T> observable) {
        this.observable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public ObservableTask<T> subscribeOn(Scheduler scheduler) {
        this.subscribeScheduler = scheduler;
        return this;
    }

    public ObservableTask<T> observeOn(Scheduler scheduler) {
        this.observeScheduler = scheduler;
        return this;
    }

    public ObservableTask<T> onSubscribe(IHttp.OnSubscribeListener listener) {
        this.onSubscribeListener = listener;
        return this;
    }

    public final ObservableTask<T> onError(IHttp.OnErrorListener listener) {
        this.onErrorListener = listener;
        return this;
    }

    public final ObservableTask<T> onSuccess(IHttp.OnSuccessListener<T> listener) {
        this.onSuccessListener = listener;
        return this;
    }

    public ObservableTask<T> onComplete(IHttp.OnCompleteListener listener) {
        this.onCompleteListener = listener;
        return this;
    }

    public final <R> ObservableTask<R> onNext(final OnNextListener<T, R> listener) {
        initScheduler();
        Observable<R> o = observable
                .flatMap(new Function<T, ObservableSource<R>>() {
                    @Override
                    public ObservableSource<R> apply(final T t) throws Exception {
                        if (listener != null) {
                            ObservableTask<R> httpObservable = listener.onNext(t);
                            if (httpObservable != null) {
                                return httpObservable.observable;
                            }
                        }
                        return Observable.empty();
                    }
                });
        return new ObservableTask<>(o)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

    public final <R> ObservableTask<R> flatMap(final OnFlatMapListener<T, R> listener) {
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
        return new ObservableTask<>(o)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

//    public final <R> ObservableTask<R> as(View view) {
//        initScheduler();
//        Observable<R> o = observable
//                .as(RxLife.as(view));
//        return new ObservableTask<>(o)
//                .subscribeOn(subscribeScheduler)
//                .observeOn(observeScheduler);
//    }

    public Disposable subscribe() {
        initScheduler();

//        observable.subscribeOn(subscribeScheduler)
//                .observeOn(observeScheduler)
//                .subscribe(new Observer<T>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        if (onSubscribeListener != null) {
//                            onSubscribeListener.onSubscribe(d);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(T data) {
//                        if (onSuccessListener != null) {
//                            try {
//                                onSuccessListener.onSuccess(data);
//                            } catch (Exception e) {
//                                onError(e);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        if (onErrorListener != null) {
//                            onErrorListener.onError(e);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        if (onCompleteListener != null) {
//                            onCompleteListener.onComplete();
//                        }
//                    }
//                });
        if (disposable != null) {
            return disposable;
        }

        disposable = observable
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        if (onSuccessListener != null) {
                            onSuccessListener.onSuccess(t);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (onErrorListener != null) {
                            onErrorListener.onError(throwable);
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if (onCompleteListener != null) {
                            onCompleteListener.onComplete();
                        }
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (onSubscribeListener != null) {
                            onSubscribeListener.onSubscribe(disposable);
                        }
                    }
                });
        return disposable;
    }

    private void initScheduler() {
        if (subscribeScheduler == null) {
            subscribeScheduler = Schedulers.io();
        }
        if (observeScheduler == null) {
            observeScheduler = AndroidSchedulers.mainThread();
        }
    }

    public void cancel() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = null;
    }

}
