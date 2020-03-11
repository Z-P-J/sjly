package com.lxj.xpopup;

import android.content.Context;

import com.lxj.xpopup.impl.AlertPopup;
import com.lxj.xpopup.impl.AttachListPopup;

public class ZPopup {

    private final Context context;

    private ZPopup(Context context) {
        this.context = context;
    }

    public static ZPopup with(Context context) {
        return new ZPopup(context);
    }

    public AlertPopup alert() {
        return new AlertPopup(context);
    }

    public static AlertPopup alert(Context context) {
        return new AlertPopup(context);
    }

    public static AttachListPopup attachList(Context context) {
        return new AttachListPopup(context);
    }

}
