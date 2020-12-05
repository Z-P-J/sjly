package com.zpj.rxbus;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class RxSubscriber {

    public static void post(Object o) {
        RxBus.get().post(o);
    }

    public static void post(Object o, long delay) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .doOnComplete(() -> RxBus.get().post(o))
                .subscribe();
    }

    public static void post(String key, Object o) {
        RxBus.get().post(new KeyMessage(key, o));
    }

    public static void postSticky(Object o) {
        RxBus.get().postSticky(o);
    }

    public static void postSticky(String key) {
        RxBus.get().postSticky(key, key);
    }

    public static void postSticky(String key, Object o) {
        RxBus.get().postSticky(key, o);
    }

     static class KeyMessage {
        private final String key;
        private final Object o;

        public KeyMessage(String key, Object o) {
            this.key = key;
            this.o = o;
        }

        public String getKey() {
            return key;
        }

        public Object getObject() {
            return o;
        }

//        public <T> boolean equals(final String key, final Class<T> type) {
//            if (o == null || type == null) {
//                return TextUtils.equals(this.key, key);
//            }
//            return type.isInstance(o)
//                    && TextUtils.equals(this.key, key);
////            return Utils.equals(Utils.getClassFromObject(o), eventType)
////                    && TextUtils.equals(this.key, key);
//        }

    }

}