package com.zpj.shouji.market.utils;

import android.text.TextUtils;
import android.util.Log;

import com.zpj.http.parser.html.nodes.Element;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class BeanUtils {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Select {
        String selector() default "";
    }

    public static <T> T createBean(Element doc, Class<T> clazz) {
        T bean = null;
        try {
            Log.d("createBean", "newInstance");
            bean = clazz.newInstance();
//            bean.getClass()
            Log.d("createBean", "getDeclaredFields");
            for(Field field : clazz.getDeclaredFields()) {
                String name = field.getName();
                String type = field.getGenericType().toString();
                Select selectAnnotation = field.getAnnotation(Select.class);
                String selector = "";
                if (selectAnnotation != null) {
                    selector = selectAnnotation.selector();
                }
                if (TextUtils.isEmpty(selector)) {
                    selector = name;
                }
                String text = doc.selectFirst(selector).text();
                if (TextUtils.isEmpty(text)) {
                    continue;
                }

                Log.d("BeanUtils", "selector=" + selector + " text=" + text + " type=" + type);
                text = text.replace("<![CDATA[","").replace("]]>", "").trim();
                if (type.equals("boolean")) {
                    String t = text.trim();
                    boolean value;
                    if (TextUtils.isEmpty(t)) {
                        value = false;
                    } else if (t.length() == 1) {
                        value = "1".equals(t);
                    } else {
                        value = "true".equals(t);
                    }
                    field.setAccessible(true);
                    field.setBoolean(bean, value); // !"0".equals(text.trim())
                } else if (type.equals("int")) {
                    field.setAccessible(true);
                    field.setInt(bean, Integer.parseInt(text));
                } else if (type.equals("long")) {
                    field.setAccessible(true);
                    field.setLong(bean, Long.parseLong(text));
                } else if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class
                    field.setAccessible(true);
                    field.set(bean, text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("createBean", "bean=" + bean);
        return bean;
    }

}
