package com.zpj.http.core;

public interface IHttp {

    interface OnRedirectListener {
        boolean onRedirect(String redirectUrl);
    }

    interface OnErrorListener {
        void onError();
    }

    interface OnSuccessListener {
        void onSuccess();
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
