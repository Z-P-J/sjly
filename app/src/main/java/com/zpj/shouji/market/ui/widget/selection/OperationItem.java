package com.zpj.shouji.market.ui.widget.selection;

/**
 * Created by wangyang53 on 2018/3/28.
 */

public class OperationItem {
    public static final int ACTION_COPY = 1;
    public static final int ACTION_SELECT_ALL = 2;
    public static final int ACTION_CANCEL = 3;
    public String name;
    public int action;

    @Override
    public String toString() {
        return "OperationItem{" +
                "name='" + name + '\'' +
                ", action=" + action +
                '}';
    }
}
