package com.zpj.shouji.market.installer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.zpj.notification.ZNotify;
import com.zpj.utils.FileUtils;

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
public class ApkInstaller {

    private static final String TAG = "ZInstaller";

    private final Context mContext;

    private String appName;
    private String packageName;

    private InstallMode mInstallMode;
    private InstallerListener mInstallerListener;

    public static ApkInstaller with(Context context) {
        return new ApkInstaller(context);
    }

    private ApkInstaller(Context context) {
        this.mContext = context;
    }

    public ApkInstaller setInstallMode(InstallMode installMode) {
        this.mInstallMode = installMode;
        return this;
    }

    public ApkInstaller setInstallerListener(InstallerListener installerListener) {
        this.mInstallerListener = installerListener;
        return this;
    }

    public void install(final String filePath) {
        install(new File(filePath));
    }

    public void install(File file) {
        if (mInstallerListener != null) {
            mInstallerListener.onStart();
        }
        try {
            if (file == null || !file.exists()) {
                onError(new Throwable("file is not exists!"));
                return;
            }
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            appName = String.valueOf(info.applicationInfo.loadLabel(pm));
            packageName = info.packageName;
            if (TextUtils.isEmpty(packageName)) {
                onError(new Throwable("The package name is null!"));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
            return;
        }

        Observable.create(
                (ObservableOnSubscribe<Integer>) emitter -> {
                    switch (mInstallMode) {
                        case AUTO:
//                            Log.d(TAG, "checkRooted=" + Utils.checkRooted());
//                            if (!installRoot(file)) { // !Utils.checkRooted() ||
//                                installAS(file, emitter);
//                            }

                            try {
                                installRoot(file);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                ZNotify.cancel(packageName.hashCode());
                                installAS(file, emitter);
                            }
                            break;
                        case ROOT:
                            try {
                                installRoot(file);
                                emitter.onNext(0);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                ZNotify.with(mContext)
                                        .buildNotify()
                                        .setContentTitle("安装失败")
                                        .setContentText(appName + "静默安装失败！" + e.getMessage())
                                        .setId(packageName.hashCode())
                                        .show();
                                emitter.onError(e);
                                emitter.onComplete();
                                return;
                            }
//                            if (!installRoot(file)) {
//                                emitter.onNext(0);
//                            }
                            break;
                        case ACCESSIBILITY:
                            installAS(file, emitter);
                            break;
                        case NORMAL:
                            installNormally(file);
                            break;
                    }
//                    emitter.onNext(0);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(integer -> {
                    switch (integer) {
                        case 0:
                            onComplete();
                            break;
                        case 3:
                            onNeed2OpenService();
                            break;
                        case 4:
                            onNeedInstallPermission();
                            break;
                    }

                })
                .doOnError(this::onError)
                .subscribe();
    }

    private void onComplete() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ZNotify.with(mContext)
                .buildNotify()
                .setContentTitle(appName + "安装成功")
                .setContentText("点击打开" + appName + "应用")
                .setId(packageName.hashCode())
                .setContentIntent(pendingIntent)
                .show();
        if (mInstallerListener != null) {
            mInstallerListener.onComplete();
        }
    }

    private void onError(Throwable throwable) {
        if (mInstallerListener != null) {
            mInstallerListener.onError(throwable);
        }
    }

    private void onNeed2OpenService() {
        if (mInstallerListener != null) {
            mInstallerListener.onNeed2OpenService();
        }
    }

    private void onNeedInstallPermission() {
        if (mInstallerListener != null) {
            mInstallerListener.onNeedInstallPermission();
        }
    }

    private void installAS(File file, ObservableEmitter<Integer> emitter) {
        Log.d(TAG, "installUseAS");

        // 允许安装应用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = mContext.getPackageManager().canRequestPackageInstalls();
            if (!b) {
                emitter.onNext(4);
                Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                mContext.startActivity(intent);
                return;
            }
        }

        installNormally(file);

        if (!isAccessibilitySettingsOn(mContext)) {
            toAccessibilityService();
            emitter.onNext(3);
        }
    }

    private void installNormally(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, FileUtils.getFileProviderName(mContext), file);
            mContext.grantUriPermission(mContext.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }

    private void installRoot(File file) throws Throwable {
        Log.d(TAG, "installUseRoot");
        if (file == null)
            throw new IllegalArgumentException("The apk file is not exists!");
        ZNotify.with(mContext)
                .buildNotify()
                .setContentTitle("静默安装")
                .setContentText("开始静默安装" + appName + "应用")
                .setId(packageName.hashCode())
                .show();
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
            String message = msg.toString().toLowerCase();
            Log.d(TAG, "install msg is " + message);
            if (message.contains("permission denied")) {
                throw new Throwable("root permission denied.");
            } else if (message.contains("failure")) {
                throw new Throwable(message);
            }
//            if (!message.contains("failure") && !message.contains("permission denied")) {
////                result = true;
//            } else {
//                throw new Exception(message);
//            }
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

        void onError(Throwable throwable);

        void onNeed2OpenService();

        void onNeedInstallPermission();

    }

}
