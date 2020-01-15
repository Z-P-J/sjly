package it.liuting.imagetrans.listener;

/**
 * Created by liuting on 17/5/26.
 */

public interface OnPullCloseListener {

    void onClose();

    void onPull(float range);

    void onCancel();
}
