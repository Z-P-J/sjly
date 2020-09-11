package com.zpj.popup.interfaces;

import com.zpj.popup.core.BasePopup;

/**
 * Description:
 * Create by dance, at 2018/12/17
 */
public interface OnCancelListener<T extends BasePopup>  {
    void onCancel(T popup);
}
