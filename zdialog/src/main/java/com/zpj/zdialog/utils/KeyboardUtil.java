package com.zpj.zdialog.utils;

import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.concurrent.atomic.AtomicInteger;

public class KeyboardUtil {

    private static final String TAG = "KeyboardUtil";

    private static final int KEYBOARD_RETRY_ATTEMPTS = 10;
    private static final long KEYBOARD_RETRY_DELAY_MS = 100;

    /**
     * Shows the software keyboard if necessary.
     * @param view The currently focused {@link View}, which would receive soft keyboard input.
     */
    public static void showKeyboard(final View view) {
        final Handler handler = new Handler();
        final AtomicInteger attempt = new AtomicInteger();
        Runnable openRunnable = new Runnable() {
            @Override
            public void run() {
                // Not passing InputMethodManager.SHOW_IMPLICIT as it does not trigger the
                // keyboard in landscape mode.
                InputMethodManager imm =
                        (InputMethodManager) view.getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                // Third-party touches disk on showSoftInput call. http://crbug.com/619824,
                // http://crbug.com/635118
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
                try {
                    imm.showSoftInput(view, 0);
                } catch (IllegalArgumentException e) {
                    if (attempt.incrementAndGet() <= KEYBOARD_RETRY_ATTEMPTS) {
                        handler.postDelayed(this, KEYBOARD_RETRY_DELAY_MS);
                    } else {
                        Log.e(TAG, "Unable to open keyboard.  Giving up.", e);
                    }
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        };
        openRunnable.run();
    }

    /**
     * Hides the keyboard.
     * @param view The {@link View} that is currently accepting input.
     * @return Whether the keyboard was visible before.
     */
    public static boolean hideKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
