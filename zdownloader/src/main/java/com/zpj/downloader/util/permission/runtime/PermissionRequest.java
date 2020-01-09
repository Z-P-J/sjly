/*
 * Copyright © Zhenjie Yan
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
package com.zpj.downloader.util.permission.runtime;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;

import com.zpj.downloader.util.permission.PermissionUtil;
import com.zpj.downloader.util.permission.bridge.BridgeRequest;
import com.zpj.downloader.util.permission.bridge.RequestThread;
import com.zpj.downloader.util.permission.checker.DoubleChecker;
import com.zpj.downloader.util.permission.checker.PermissionChecker;
import com.zpj.downloader.util.permission.checker.StandardChecker;
import com.zpj.downloader.util.permission.source.ActivitySource;
import com.zpj.downloader.util.permission.source.ContextSource;
import com.zpj.downloader.util.permission.source.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanZhenjie on 2016/9/9.
 */
public class PermissionRequest implements BridgeRequest.Callback {

    private static final PermissionChecker STANDARD_CHECKER = new StandardChecker();
    private static final PermissionChecker DOUBLE_CHECKER = new DoubleChecker();

    private Source mSource;

    public interface PermissionRequestListener {

        void onDenied();

    }

    private PermissionRequestListener listener;

    private PermissionRequest(Source source) {
        this.mSource = source;
    }

    public static PermissionRequest with(Context context) {
        return new PermissionRequest(getContextSource(context));
    }

    public PermissionRequest setPermissionRequestListener(PermissionRequestListener listener) {
        this.listener = listener;
        return this;
    }

    public void start() {
        List<String> mDeniedPermissions = getDeniedPermissions(STANDARD_CHECKER, mSource);
        if (mDeniedPermissions.size() > 0) {
            execute();
        } else {
            onCallback();
        }
    }

    public void execute() {
        BridgeRequest request = new BridgeRequest(mSource);
        request.setCallback(this);
        new RequestThread(request).start();
    }

    public void cancel() {
        onCallback();
    }

    @Override
    public void onCallback() {
        MyAsyncTask.build()
                .setListener(new MyAsyncTask.AsyncTaskListener() {

                    @Override
                    public List<String> doInBackground() {
                        if (!DOUBLE_CHECKER.hasStoragePermission(mSource.getContext())) {
                            return PermissionUtil.STORAGE_LIST;
                        }
                        return new ArrayList<>(0);
                    }

                    @Override
                    public void onPostExecute(List<String> deniedList) {
                        if (!deniedList.isEmpty()) {
                            if (listener != null) {
                                listener.onDenied();
                            }
                        }
                    }

                })
                .execute();
    }

    private static List<String> getDeniedPermissions(PermissionChecker checker, Source source) {
        if (!checker.hasStoragePermission(source.getContext())) {
            return PermissionUtil.STORAGE_LIST;
        }
        return new ArrayList<>(0);
    }

    private static Source getContextSource(Context context) {
        if (context instanceof Activity) {
            return new ActivitySource((Activity)context);
        } else if (context instanceof ContextWrapper) {
            return getContextSource(((ContextWrapper)context).getBaseContext());
        }
        return new ContextSource(context);
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, List<String>> {

        private MyAsyncTask() {
        }

        public static MyAsyncTask build() {

            return new MyAsyncTask();
        }

        public interface AsyncTaskListener {

            List<String> doInBackground();

            void onPostExecute(List<String> strings);

        }

        private AsyncTaskListener listener;

        MyAsyncTask setListener(AsyncTaskListener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            if (listener != null) {
                return listener.doInBackground();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (listener != null) {
                listener.onPostExecute(strings);
            }
            cancel(true);
        }
    }

}