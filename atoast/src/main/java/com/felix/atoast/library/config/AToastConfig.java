package com.felix.atoast.library.config;

import android.support.annotation.ColorInt;

/**
 * Created by chaichuanfa on 17/6/2.
 */

public class AToastConfig {

    private int text_color;

    private int normal_color;

    private int error_color;

    private int info_color;

    private int success_color;

    private int warning_color;

    private AToastConfig(int text_color, int normal_color, int error_color, int info_color,
            int success_color, int warning_color) {
        this.text_color = text_color;
        this.normal_color = normal_color;
        this.error_color = error_color;
        this.info_color = info_color;
        this.success_color = success_color;
        this.warning_color = warning_color;
    }

    @ColorInt
    public int getText_color() {
        return text_color;
    }

    @ColorInt
    public int getNormal_color() {
        return normal_color;
    }

    @ColorInt
    public int getError_color() {
        return error_color;
    }

    @ColorInt
    public int getInfo_color() {
        return info_color;
    }

    @ColorInt
    public int getSuccess_color() {
        return success_color;
    }

    @ColorInt
    public int getWarning_color() {
        return warning_color;
    }

    public static class Builder {

        int text_color;

        int normal_color;

        int error_color;

        int info_color;

        int success_color;

        int warning_color;

        public Builder text_color(@ColorInt int text_color) {
            this.text_color = text_color;
            return this;
        }

        public Builder normal_color(@ColorInt int normal_color) {
            this.normal_color = normal_color;
            return this;
        }

        public Builder error_color(@ColorInt int error_color) {
            this.error_color = error_color;
            return this;
        }

        public Builder info_color(@ColorInt int info_color) {
            this.info_color = info_color;
            return this;
        }

        public Builder success_color(@ColorInt int success_color) {
            this.success_color = success_color;
            return this;
        }

        public Builder warning_color(@ColorInt int warning_color) {
            this.warning_color = warning_color;
            return this;
        }

        public AToastConfig build() {
            return new AToastConfig(text_color, normal_color, error_color, info_color,
                    success_color, warning_color);
        }

    }


}
