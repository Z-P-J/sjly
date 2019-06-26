/*
 * Copyright 2019 Zhenjie Yan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.qxdownloader.util.permission.bridge;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Zhenjie Yan on 2/13/19.
 */
public final class RequestThread extends Thread implements Messenger.Callback {

    private final BridgeRequest mRequest;
    private Messenger mMessenger;

    public RequestThread(BridgeRequest queue) {
        mRequest = queue;
        mMessenger = new Messenger(mRequest.getSource().getContext(), this);
        mMessenger.register();
    }

    @Override
    public void run() {
        synchronized (this) {
            BridgeActivity.requestPermission(mRequest.getSource());
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCallback() {
        synchronized (this) {
            mMessenger.unRegister();
            mRequest.getCallback().onCallback();
            notify();
        }
    }
}