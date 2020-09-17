package com.zpj.fragmentation.dialog;

import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;

public interface IDialog {

    public interface OnCancelListener  {
        void onCancel(AlertDialogFragment fragment);
    }

    public interface OnConfirmListener {
        void onConfirm(AlertDialogFragment fragment);
    }

    public interface OnDismissListener {
        void onDismiss();
    }

}
