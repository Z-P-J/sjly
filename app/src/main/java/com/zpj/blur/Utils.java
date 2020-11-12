package com.zpj.blur;

/**
 * @author CuiZhen
 * @date 2019/8/10
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
class Utils {

    static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    static <T> T requireNonNull(T obj) {
        return requireNonNull(obj, "");
    }
}
