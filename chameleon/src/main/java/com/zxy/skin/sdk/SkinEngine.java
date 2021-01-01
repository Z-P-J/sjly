package com.zxy.skin.sdk;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxy.skin.sdk.applicator.SkinApplicatorManager;
import com.zxy.skin.sdk.applicator.SkinViewApplicator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @Description: 换肤引擎，管理当前使用的主题及换肤监听器
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinEngine {


    private static final HashSet<ISkinObserver> skinObservers = new HashSet<>();

    private static final HashMap<View, SkinViewWrapper> skinViewMap = new HashMap<>();

    private static int themeId;

    private SkinEngine() {

    }

    /**
     * 变更皮肤
     *
     * @param themeId
     */
    public static void changeSkin(int themeId) {
        if (SkinEngine.themeId != themeId) {
            SkinEngine.themeId = themeId;
            if (SkinEngine.themeId != 0) {
                Iterator<ISkinObserver> iterator = skinObservers.iterator();
                while (iterator.hasNext()) {
                    ISkinObserver skinObserver = iterator.next();
                    if (!skinObserver.onChangeSkin()) {
                        iterator.remove();
                    }
                }
            }
        }

    }

    /**
     * 获取当前皮肤
     *
     * @return
     */
    public static int getSkin() {
        return themeId;
    }

    /**
     * 注册皮肤变化监听器
     *
     * @param observer
     */
    public static void registerSkinObserver(ISkinObserver observer) {
        if (observer != null && !skinObservers.contains(observer)) {
            skinObservers.add(observer);
        }
    }

    /**
     * 解除注册皮肤变化监听器
     *
     * @param observer
     */
    public static void unRegisterSkinObserver(ISkinObserver observer) {
        if (observer != null) {
            skinObservers.remove(observer);
        }
    }

    /**
     * 注册skinapplicator
     *
     * @param viewClass
     * @param applicator
     */
    public static void registerSkinApplicator(Class<? extends View> viewClass, SkinViewApplicator applicator) {
        if (viewClass == null || applicator == null) {
            return;
        }
        SkinApplicatorManager.register(viewClass, applicator);
    }

    public static int getColor(Context context, int colorAttrId) {
        return getColor(context, colorAttrId, Color.BLACK);
    }

    public static int getColor(Context context, int colorAttrId, int defaultColor) {
        int[] ints = { colorAttrId };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        int color = typedArray.getColor(0, defaultColor);
        typedArray.recycle();
        return color;
    }

    public static Drawable getDrawable(Context context, int drawableAttrId) {
        int[] ints = { drawableAttrId };
        TypedArray typedArray = context.obtainStyledAttributes(ints);
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    /**
     * 代码设置背景
     *
     * @param view
     * @param backgroundAttrId
     */
    public static void setBackground(View view, int backgroundAttrId) {
        applyViewAttr(view, "background", backgroundAttrId);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setForeground(View view, int foregroundAttrId) {
        applyViewAttr(view, "foreground", foregroundAttrId);
    }

    /**
     * 代码设置字的颜色
     *
     * @param view
     * @param textColorAttrId
     */
    public static void setTextColor(TextView view, int textColorAttrId) {
        applyViewAttr(view, "textColor", textColorAttrId);
    }

    public static void setTint(ImageView view, int tintColorAttrId) {
        applyViewAttr(view, "tint", tintColorAttrId);
    }

    public static void setCardBackgroundColor(ImageView view, int colorAttrId) {
        applyViewAttr(view, "cardBackgroundColor", colorAttrId);
    }


    /**
     * @param view
     * @param attrName
     * @param skinAttrId
     */
    public static void applyViewAttr(View view, String attrName, int skinAttrId) {
        SkinViewWrapper skinViewWrapper = skinViewMap.get(view);
        if (skinViewWrapper == null) {
            skinViewWrapper = new SkinViewWrapper(view);
            skinViewMap.put(view, skinViewWrapper);
            skinObservers.add(skinViewWrapper);
        }
        skinViewWrapper.attrsMap.put(attrName, skinAttrId);
        view.getContext().setTheme(SkinEngine.getSkin());
        SkinApplicatorManager.getApplicator(view.getClass()).apply(view, skinViewWrapper.attrsMap);
    }

    /** 解除对view的监控
     * @param view
     */
    public static void unRegisterSkinObserver(View view) {
        SkinViewWrapper skinViewWrapper = skinViewMap.get(view);
        if (skinViewWrapper != null) {
            skinViewMap.remove(view);
            skinObservers.remove(skinViewWrapper);
        }

    }


    /**
     * @Description: 换肤监听器
     * @author: zhaoxuyang
     * @Date: 2019/1/31
     */
    public interface ISkinObserver {

        boolean onChangeSkin();

    }

    static class SkinViewWrapper implements ISkinObserver {

        View view;

        ArrayMap<String, Integer> attrsMap = new ArrayMap<>();

        SkinViewWrapper(View view) {
            this.view = view;
        }

        @Override
        public boolean onChangeSkin() {
            SkinApplicatorManager.getApplicator(view.getClass()).apply(view, attrsMap);
            return true;
        }
    }


}
