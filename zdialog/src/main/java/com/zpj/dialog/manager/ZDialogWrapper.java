package com.zpj.dialog.manager;

import com.zpj.dialog.ZDialog;

/**
 * 管理多个dialog 按照dialog的优先级依次弹出
 * Created by mq on 2018/9/16 下午9:44
 * mqcoder90@gmail.com
 */

public class ZDialogWrapper {

    private ZDialog dialog;//统一管理dialog的弹出顺序

    public ZDialogWrapper(ZDialog dialog) {
        this.dialog = dialog;
    }

    public ZDialog getDialog() {
        return dialog;
    }

    public void setDialog(ZDialog dialog) {
        this.dialog = dialog;
    }

}
