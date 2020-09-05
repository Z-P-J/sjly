package com.zpj.popup;

import android.content.Context;
import android.view.View;

import com.zpj.popup.core.BasePopup;
import com.zpj.popup.core.CenterPopup;
import com.zpj.popup.core.ImageViewerPopup;
import com.zpj.popup.core.ImageViewerPopup2;
import com.zpj.popup.impl.AlertPopup;
import com.zpj.popup.impl.AttachListPopup;
import com.zpj.popup.impl.BottomListPopup;
import com.zpj.popup.impl.BottomSelectPopup;
import com.zpj.popup.impl.CenterListPopup;
import com.zpj.popup.impl.CenterListPopup2;
import com.zpj.popup.impl.CenterSelectPopup;
import com.zpj.popup.impl.CheckPopup;
import com.zpj.popup.impl.InputPopup;
import com.zpj.popup.impl.LoadingPopup;
import com.zpj.popup.impl.SimpleCenterListPopup;

import java.lang.reflect.Constructor;

public class ZPopup {

    private final Context context;

    private ZPopup(Context context) {
        this.context = context;
    }

//    public static ZPopup with(Context context) {
//        return new ZPopup(context);
//    }

//    public AlertPopup<AlertPopup> alert() {
//        return new AlertPopup<>(context);
//    }

    public static <T extends BasePopup> T custom(Context context, Class<T> clazz) throws Exception {
        Constructor<T> cons = clazz.getConstructor(Context.class);
        return cons.newInstance(context);
    }

    public static AlertPopup alert(Context context) {
        return new AlertPopup(context);
    }

    public static InputPopup input(Context context) {
        return new InputPopup(context);
    }

    public static CheckPopup check(Context context) {
        return new CheckPopup(context);
    }

    public static LoadingPopup loading(Context context) {
        return new LoadingPopup(context);
    }

    public static AttachListPopup<String> attachList(Context context) {
        return new AttachListPopup<>(context);
    }

    public static <T> AttachListPopup<T> attachList(Context context, Class<T> clazz) {
        return new AttachListPopup<>(context);
    }

    public static CenterPopup<CenterPopup> center(Context context) {
        return new CenterPopup<>(context);
    }

    public static CenterListPopup<CenterListPopup> centerList(Context context) {
        return new CenterListPopup<>(context);
    }

    public static <T> CenterListPopup2<T> centerList(Context context, Class<T> clazz) {
        return new CenterListPopup2<T>(context);
    }

    public static SimpleCenterListPopup simpleCenterList(Context context) {
        return new SimpleCenterListPopup(context);
    }

    public static BottomListPopup<String> bottomList(Context context) {
        return new BottomListPopup<>(context);
    }

    public static <T> BottomListPopup<T> bottomList(Context context, Class<T> clazz) {
        return new BottomListPopup<>(context);
    }

    public static <T> CenterSelectPopup<T> centerSelect(Context context, Class<T> clazz) {
        return new CenterSelectPopup<>(context);
    }

    public static <T> BottomSelectPopup<T> bottomSelect(Context context, Class<T> clazz) {
        return new BottomSelectPopup<>(context);
    }

    public static ImageViewerPopup<String> imageViewer(Context context) {
        return new ImageViewerPopup<>(context);
    }

    public static ImageViewerPopup2<String> imageViewer2(Context context) {
        return new ImageViewerPopup2<>(context);
    }

    public static <T> ImageViewerPopup<T> imageViewer(Context context, Class<T> clazz) {
        return new ImageViewerPopup<>(context);
    }

}
