package com.zpj.shouji.market.installer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Z-P-J
 * 参考AutoInstaller库
 */
public class ZApkInstaller {

    private static final String TAG = "ZInstaller";

    private Context mContext;

    private InstallMode mInstallMode;
    private InstallerListener mInstallerListener;

    public static ZApkInstaller with(Context context) {
        return new ZApkInstaller(context);
    }

    private ZApkInstaller(Context context) {
        this.mContext = context;
    }

    public ZApkInstaller setInstallMode(InstallMode installMode) {
        this.mInstallMode = installMode;
        return this;
    }

    public ZApkInstaller setInstallerListener(InstallerListener installerListener) {
        this.mInstallerListener = installerListener;
        return this;
    }

    public void install(final String filePath) {
        install(new File(filePath));
    }

    public void install(File file) {
        Observable.create(
                (ObservableOnSubscribe<Integer>) emitter -> {
                    emitter.onNext(1);
                    switch (mInstallMode) {
                        case AUTO:
//                            Log.d(TAG, "checkRooted=" + Utils.checkRooted());
                            if (!installRoot(file)) { // !Utils.checkRooted() ||
                                installAS(file, emitter);
                            }
                            break;
                        case ROOT:
                            installRoot(file);
                            break;
                        case ACCESSIBILITY:
                            installAS(file, emitter);
                            break;
                        case NORMAL:
                            break;
                    }
                    emitter.onNext(0);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(integer -> {
                    if (mInstallerListener != null) {
                        switch (integer) {
                            case 0:
                                mInstallerListener.onComplete();
                                break;
                            case 1:
                                mInstallerListener.onStart();
                                break;
                            case 3:
                                mInstallerListener.onNeed2OpenService();
                                break;
                            case 4:
                                mInstallerListener.onNeedInstallPermission();
                                break;
                        }
                    }
                })
                .subscribe();
    }

    private void installAS(File file, ObservableEmitter<Integer> emitter) {
        Log.d(TAG, "installUseAS");

        // 允许安装应用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = mContext.getPackageManager().canRequestPackageInstalls();
            if (!b) {
                emitter.onNext(4);
                Uri packageURI = Uri.parse("package:"+mContext.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                mContext.startActivity(intent);
                return;
            }
        }

//        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "apk file not exists, path: " + file.getAbsolutePath());
            return;
        }
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
            mContext.grantUriPermission(mContext.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
        if (!isAccessibilitySettingsOn(mContext)) {
            toAccessibilityService();
            emitter.onNext(3);
        }
    }

    private boolean installRoot(File file) {
        Log.d(TAG, "installUseRoot");
        if (file == null)
            throw new IllegalArgumentException("Please check apk file!");
        boolean result = false;
        Process process = null;
        OutputStream outputStream = null;
        BufferedReader errorStream = null;
        try {
            process = Runtime.getRuntime().exec("su");
            outputStream = process.getOutputStream();
            String command = "cat '" + file.getAbsolutePath() + "' | pm install -S " + file.length() + "\n";
            Log.d(TAG, "command=" + command);
            outputStream.write(command.getBytes());
            outputStream.flush();
            outputStream.write("exit\n".getBytes());
            outputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder msg = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                msg.append(line);
            }
            Log.d(TAG, "install msg is " + msg);
            if (!msg.toString().toLowerCase().contains("failure")) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
            result = false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                outputStream = null;
                errorStream = null;
                process.destroy();
            }
        }
        return result;
    }


    private void toAccessibilityService() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        mContext.startActivity(intent);
    }


    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + InstallAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


    public interface InstallerListener {

        void onStart();

        void onComplete();

        void onNeed2OpenService();

        void onNeedInstallPermission();

    }


}
