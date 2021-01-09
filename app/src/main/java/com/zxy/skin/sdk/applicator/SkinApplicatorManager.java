package com.zxy.skin.sdk.applicator;


import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * @Description: 换肤器管理中心
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinApplicatorManager {

    private static final String TAG = "SkinApplicatorManager";

    private static final SkinViewApplicator defaultSkinApplicator = new SkinViewApplicator();

    private static final HashMap<Class, SkinViewApplicator> applicatorsMap = new HashMap<>();

    static {

        SkinViewApplicator textViewSkinViewApplicator = new SkinTextViewApplicator();
        applicatorsMap.put(TextView.class, textViewSkinViewApplicator);
        applicatorsMap.put(Button.class, textViewSkinViewApplicator);
        applicatorsMap.put(CardView.class, new SkinCardViewApplicator());
        applicatorsMap.put(ImageView.class, new SkinImageViewApplicator());

    }

    /**
     * 获取某个控件的换肤器
     *
     * @param viewClass
     * @return
     */
    public static SkinViewApplicator getApplicator(Class<? extends View> viewClass) {
        SkinViewApplicator skinViewApplicator = null;
        Log.d(TAG, "viewClass=" + viewClass);
        while (skinViewApplicator == null && viewClass != View.class) {
            skinViewApplicator = applicatorsMap.get(viewClass);
            viewClass = (Class<? extends View>) viewClass.getSuperclass();
            Log.d(TAG, "viewSuperClass=" + viewClass);
        }
        return skinViewApplicator == null ? defaultSkinApplicator : skinViewApplicator;
    }

    /**
     * 注册自定义的applicator
     *
     * @param viewClass
     * @param applicator
     */
    public static void register(Class<? extends View> viewClass, SkinViewApplicator applicator) {
        applicatorsMap.put(viewClass, applicator);
    }
}
