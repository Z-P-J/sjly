package com.joker.richeditor.interfaces;


import com.joker.richeditor.widget.ActionType;

/**
 * OnActionPerformListener
 * Created by even.wu on 17/8/17.
 */

public interface OnActionPerformListener {
    void onActionPerform(ActionType type, Object... values);
}
