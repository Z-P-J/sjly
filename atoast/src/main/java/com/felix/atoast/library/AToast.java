package com.felix.atoast.library;


import com.felix.atoast.library.config.AToastConfig;
import com.felix.atoast.library.util.ToastUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class AToast {

    @ColorInt
    private static int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");

    @ColorInt
    private static int NORMAL_COLOR = Color.parseColor("#70000000");

    @ColorInt
    private static int ERROR_COLOR = Color.parseColor("#D50000");

    @ColorInt
    private static int INFO_COLOR = Color.parseColor("#3F51B5");

    @ColorInt
    private static int SUCCESS_COLOR = Color.parseColor("#388E3C");

    @ColorInt
    private static int WARNING_COLOR = Color.parseColor("#FFA900");


    private static Context mContext;

    private AToast() {
    }

    /**
     * 在Application onCreate中初始化
     */
    public static void onInit(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
    }

    /**
     * 在Application onCreate中初始化
     *
     * @param config toast 背景颜色
     */
    public static void onInit(Context context, AToastConfig config) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        if (config != null) {
            DEFAULT_TEXT_COLOR = config.getText_color() != 0 ? config.getText_color()
                    : DEFAULT_TEXT_COLOR;

            NORMAL_COLOR = config.getNormal_color() != 0 ? config.getNormal_color()
                    : NORMAL_COLOR;

            ERROR_COLOR = config.getError_color() != 0 ? config.getError_color()
                    : ERROR_COLOR;

            INFO_COLOR = config.getInfo_color() != 0 ? config.getInfo_color()
                    : INFO_COLOR;

            SUCCESS_COLOR = config.getSuccess_color() != 0 ? config.getSuccess_color()
                    : SUCCESS_COLOR;

            WARNING_COLOR = config.getWarning_color() != 0 ? config.getWarning_color()
                    : WARNING_COLOR;
        }
    }

    public static void normal(@NonNull String message) {
        normal(message, Toast.LENGTH_SHORT, null, false);
    }

    public static void normal(@StringRes int messageId) {
        normal(mContext.getString(messageId), Toast.LENGTH_SHORT, null, false);
    }

    public static void normal(@NonNull String message, Drawable icon) {
        normal(message, Toast.LENGTH_SHORT, icon, true);
    }

    public static void normal(@NonNull String message, int duration) {
        normal(message, duration, null, false);
    }

    public static void normal(@NonNull String message, int duration,
            Drawable icon) {
        normal(message, duration, icon, true);
    }

    public static void normal(@NonNull String message, int duration,
            Drawable icon, boolean withIcon) {
        custom(message, icon, DEFAULT_TEXT_COLOR, duration, withIcon);
    }

    public static void warning(@NonNull String message) {
        warning(message, Toast.LENGTH_SHORT, true);
    }

    public static void warning(@StringRes int messageId) {
        warning(mContext.getString(messageId), Toast.LENGTH_SHORT, true);
    }

    public static void warning(@NonNull String message, int duration) {
        warning(message, duration, true);
    }

    public static void warning(@NonNull String message, int duration, boolean withIcon) {
        custom(message, ToastUtils.getDrawable(mContext, R.drawable.ic_error_outline_white_48dp),
                DEFAULT_TEXT_COLOR, WARNING_COLOR, duration, withIcon, true);
    }

    public static void info(@NonNull String message) {
        info(message, Toast.LENGTH_SHORT, true);
    }

    public static void info(@StringRes int messageId) {
        info(mContext.getString(messageId), Toast.LENGTH_SHORT, true);
    }

    public static void info(@NonNull String message, int duration) {
        info(message, duration, true);
    }

    public static void info(@NonNull String message, int duration, boolean withIcon) {
        custom(message, ToastUtils.getDrawable(mContext, R.drawable.ic_info_outline_white_48dp),
                DEFAULT_TEXT_COLOR, INFO_COLOR, duration, withIcon, true);
    }

    public static void success(@NonNull String message) {
        success(message, Toast.LENGTH_SHORT, true);
    }

    public static void success(@StringRes int messageId) {
        success(mContext.getString(messageId), Toast.LENGTH_SHORT, true);
    }

    public static void success(@NonNull String message, int duration) {
        success(message, duration, true);
    }

    public static void success(@NonNull String message, int duration, boolean withIcon) {
        custom(message, ToastUtils.getDrawable(mContext, R.drawable.ic_check_white_48dp),
                DEFAULT_TEXT_COLOR, SUCCESS_COLOR, duration, withIcon, true);
    }

    public static void error(@NonNull String message) {
        error(message, Toast.LENGTH_SHORT, true);
    }

    public static void error(@StringRes int messageId) {
        error(mContext.getString(messageId), Toast.LENGTH_SHORT, true);
    }

    public static void error(@NonNull String message, int duration) {
        error(message, duration, true);
    }

    public static void error(@NonNull String message, int duration, boolean withIcon) {
        custom(message, ToastUtils.getDrawable(mContext, R.drawable.ic_clear_white_48dp),
                DEFAULT_TEXT_COLOR, ERROR_COLOR, duration, withIcon, true);
    }

    public static void custom(@NonNull String message, Drawable icon,
            @ColorInt int textColor, int duration, boolean withIcon) {
        custom(message, icon, textColor, -1, duration, withIcon, false);
    }

    public static void custom(@NonNull String message, @DrawableRes int iconRes,
            @ColorInt int textColor, @ColorInt int tintColor, int duration,
            boolean withIcon, boolean shouldTint) {
        custom(message, ToastUtils.getDrawable(mContext, iconRes), textColor,
                tintColor, duration, withIcon, shouldTint);
    }

    public static void custom(@NonNull String message, Drawable icon,
            @ColorInt int textColor, @ColorInt int tintColor, int duration,
            boolean withIcon, boolean shouldTint) {
        final Toast currentToast = new Toast(mContext);
        final View toastLayout = ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint) {
            drawableFrame = ToastUtils.getDrawableFrame(mContext, tintColor);
        } else {
            drawableFrame = ToastUtils.getDrawableFrame(mContext, NORMAL_COLOR);
        }
        ToastUtils.setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null) {
                throw new IllegalArgumentException(
                        "Avoid passing 'icon' as null if 'withIcon' is set to true");
            }
            ToastUtils.setBackground(toastIcon, icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setTextColor(textColor);
        toastTextView.setText(message);

        currentToast.setView(toastLayout);
        currentToast.setDuration(duration);
        currentToast.show();
    }

    public static LoadToast loadToast(Context context, String loadText) {
        LoadToast lt = new LoadToast(context);
        lt.setText(loadText);
        return lt;
    }

    public static LoadToast loadToast(Context context, @StringRes int textRes) {
        LoadToast lt = new LoadToast(context);
        lt.setText(mContext.getString(textRes));
        return lt;
    }

}
