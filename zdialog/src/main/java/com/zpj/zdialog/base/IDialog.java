package com.zpj.zdialog.base;

import android.support.annotation.IdRes;
import android.view.View;

public interface IDialog {

    /** The identifier for the positive button. */
    int BUTTON_POSITIVE = -1;

    /** The identifier for the negative button. */
    int BUTTON_NEGATIVE = -2;

    /** The identifier for the neutral button. */
    int BUTTON_NEUTRAL = -3;

    void dismiss();

    void hide();

    IDialog show();

    void dismissWithoutAnim();

    <T extends View> T getView(@IdRes int id);

    interface OnViewCreateListener {
        void onViewCreate(IDialog dialog, View view);
    }

    interface OnClickListener {
        void onClick(IDialog dialog);
    }

    interface OnDismissListener {
        void onDismiss(IDialog dialog);
    }

    interface OnCancelListener {
        void onCancel(IDialog dialog);
    }

    interface OnDialogStartListener {
        void onStart();
    }

    interface OnPositiveButtonClickListener {
        void onClick(IDialog dialog, String text);
    }
}
