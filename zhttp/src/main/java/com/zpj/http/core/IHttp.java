package com.zpj.http.core;

import io.reactivex.disposables.Disposable;

public interface IHttp {

    interface OnRedirectListener {
        boolean onRedirect(String redirectUrl);
    }

    interface OnErrorListener {
        void onError(Throwable throwable);
    }

    interface OnSuccessListener<T> {
        void onSuccess(T data) throws Exception;
    }

    interface OnCompleteListener {
        void onComplete();
    }

    interface OnSubscribeListener {
        void onSubscribe(Disposable d);
    }

    interface OnStreamWriteListener {
        /**
         * Called every time that a bunch of bytes were written to the body
         * @param bytesWritten number of written bytes
         */
        void onBytesWritten(int bytesWritten);

        boolean shouldContinue();
    }
}
