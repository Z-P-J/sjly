package com.zpj.popup.interfaces;

import com.zpj.popup.core.BasePopup;

/**
 * Description:
 * Create by dance, at 2018/12/17
 */
public interface OnConfirmListener<T extends BasePopup> {
    void onConfirm(T popup);
}
