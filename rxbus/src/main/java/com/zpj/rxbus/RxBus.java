package com.zpj.rxbus;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

final class RxBus {

    private static volatile RxBus INSTANCE;

    private final Subject<Object> mSubject;
    private Map<String, CompositeDisposable> mSubscriptionMap;

    //    private final Map<Class<?>, Object> mStickyEventMap;
    private final Map<Object, Object> mStickyEventMap;

    public static RxBus get() {
        if (INSTANCE == null) {
            synchronized (RxBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxBus();
                }
            }
        }
        return INSTANCE;
    }

    private RxBus() {
        mSubject = PublishSubject.create().toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    /**
     * 发送事件
     *
     * @param o
     */
    public void post(Object o) {
        mSubject.onNext(o);
    }

//    public static void post(Object o) {
//        get().post(o);
//    }

    /**
     * 返回指定类型的Observable实例
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T> Observable<T> toObservable(final Class<T> type) {
        return mSubject.ofType(type);
    }

    public Observable<String> toObservable(final String key) {
//        if (TextUtils.isEmpty(key)) {
//            return Observable.empty();
//        }
        return mSubject.ofType(String.class)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        return TextUtils.equals(s, key);
                    }
                });
//                .cast(String.class);
    }

    public <T> Observable<T> toObservable(final String key, final Class<T> type) {
        return mSubject.ofType(RxSubscriber.KeyMessage.class)
                .filter(new Predicate<RxSubscriber.KeyMessage>() {
                    @Override
                    public boolean test(@NonNull RxSubscriber.KeyMessage msg) throws Exception {
//                        List<SupportFragment> list = new ArrayList<>();
//                        Log.d("toObservable", "test getClassFromObject=" + Utils.getClassFromObject(list));
//                        Log.d("toObservable", "test isAssignableFrom=" + SupportFragment.class.isAssignableFrom(AppDetailFragment.class));
//                        Log.d("toObservable", "msg=" + msg + " isAssignableFrom" + msg.getObject().getClass().isAssignableFrom(type));
//                        boolean value = msg.equals(key, type);
//                        Log.d("toObservable", "key=" + key + " type=" + type + " value=" + value);
//                        return value;

                        Object o = msg.getObject();
                        boolean isSameKey = TextUtils.equals(msg.getKey(), key);
                        if (isSameKey) {
                            if (o != null && type != null) {
                                return type.isInstance(o);
                            }
                        }
                        return isSameKey;
//                        if (o == null || type == null) {
//                            return TextUtils.equals(msgKey, key);
//                        }
//                        return type.isInstance(o)
//                                && TextUtils.equals(this.key, key);
                    }
                })
                .map(new Function<RxSubscriber.KeyMessage, Object>() {
                    @Override
                    public Object apply(RxSubscriber.KeyMessage msg) {
                        return msg.getObject();
                    }
                })
                .cast(type);
    }

    /**
     * 是否已有观察者订阅
     *
     * @return
     */
    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    /**
     * 一个默认的订阅方法
     *
     * @param type
     * @param next
     * @param error
     * @param <T>
     * @return
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return toObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    public <T> Disposable doSubscribe(String key, Consumer<String> next, Consumer<Throwable> error) {
        return toObservable(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    public <T> void subscribe(Object o, Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        Disposable disposable = toObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
        addSubscription(o, disposable);
    }

    public <T> void subscribe(Object o, Class<T> type, Consumer<T> next) {
        Disposable disposable = toObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next);
        addSubscription(o, disposable);
    }

    /**
     * 保存订阅后的subscription
     *
     * @param o
     * @param disposable
     */
    public void addSubscription(Object o, Disposable disposable) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = new HashMap<>();
        }
        String key = o.getClass().getName();
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).add(disposable);
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            mSubscriptionMap.put(key, compositeDisposable);
        }
    }

    /**
     * 取消订阅
     *
     * @param o
     */
    public void unSubscribe(Object o) {
        if (mSubscriptionMap == null) {
            return;
        }

        String key = o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)) {
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).dispose();
        }

        mSubscriptionMap.remove(key);
    }


    /**
     * Stciky 相关
     */

    /**
     * 发送一个新Sticky事件
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    public void postSticky(String key, Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(key, event);
        }
        post(event);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> toObservableSticky(final Class<T> eventType) {
        synchronized (mStickyEventMap) {
//            Observable<T> observable = mSubject.ofType(eventType);
            Observable<T> observable = toObservable(eventType);
            final Object event = mStickyEventMap.get(eventType);

            if (event != null) {
//                return observable.mergeWith(Observable.just(eventType.cast(event)));
                return observable.mergeWith(Observable.just(event).cast(eventType));
            } else {
                return observable;
            }
        }
    }

    public Observable<String> toObservableSticky(final String tag) {
        synchronized (mStickyEventMap) {
//            Observable<T> observable = mSubject.ofType(eventType);
            Observable<String> observable = toObservable(tag);
            final Object event = mStickyEventMap.get(tag);

            if (event != null) {
                return observable.mergeWith(Observable.just(tag));
            } else {
                return observable;
            }
        }
    }

    public <T> Observable<T> toObservableSticky(final String tag, final Class<T> eventType) {
        synchronized (mStickyEventMap) {
//            Observable<T> observable = mSubject.ofType(eventType);
            Observable<T> observable = toObservable(tag, eventType);
            final Object event = mStickyEventMap.get(tag);

            if (event != null) {
                return observable.mergeWith(Observable.just(event).cast(eventType));
            } else {
                return observable;
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    public Object removeStickyEvent(String key) {
        synchronized (mStickyEventMap) {
            return mStickyEventMap.remove(key);
        }
    }

    public <T> T removeStickyEvent(String key, Class<T> type) {
        synchronized (mStickyEventMap) {
            return type.cast(mStickyEventMap.remove(key));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }


}
