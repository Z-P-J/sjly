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
package com.zpj.qxdownloader.util.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.zpj.qxdownloader.util.permission.runtime.PermissionRequest;

import java.util.Arrays;
import java.util.List;

/**
 *
 * 本工具类基于开源项目AndPermission
 * 项目地址https://github.com/yanzhenjie/AndPermission
 * @author Zhenjie Yan
 * Created by Zhenjie Yan on 2016/9/9.
 * Modify by Z-P-J
 */
public class PermissionUtil {

    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final String[] STORAGE = new String[] {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    public static final List<String> STORAGE_LIST = Arrays.asList(STORAGE);

    public static void grandStoragePermission(Context context) {
        if (!checkStoragePermissions(context)) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                throw  new RuntimeException("must grant storage permission");
            } else {
                PermissionRequest.with(context)
                        .setPermissionRequestListener(new PermissionRequest.PermissionRequestListener() {
                            @Override
                            public void onDenied() {
                                throw  new RuntimeException("must grant storage permission");
                            }
                        })
                        .start();
            }
        }
    }

    public static boolean checkStoragePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private PermissionUtil() {
    }
}