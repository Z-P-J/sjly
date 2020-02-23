/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.wj.android.colorcardview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * A FrameLayout with a rounded corner background and shadow.
 * <p>
 * CardView uses <code>elevation</code> property on Lollipop for shadows and falls back to a custom emulated shadow
 * implementation on older platforms.
 * <p>
 * Due to expensive nature of rounded corner clipping, on platforms before Lollipop, CardView does not clip its children
 * that intersect with rounded corners. Instead, it adds padding to avoid such intersection (See {@link
 * #setPreventCornerOverlap(boolean)} to change this behavior).
 * <p>
 * Before Lollipop, CardView adds padding to its content and draws shadows to that area. This padding amount is equal
 * to
 * <code>maxCardElevation + (1 - cos45) * cornerRadius</code> on the sides and <code>maxCardElevation * 1.5 + (1 -
 * cos45) * cornerRadius</code> on top and bottom.
 * <p>
 * Since padding is used to offset content for shadows, you cannot set padding on CardView. Instead, you can use content
 * padding attributes in XML or {@link #setContentPadding(int, int, int, int)} in code to set the padding between the
 * edges of the CardView and children of CardView.
 * <p>
 * Note that, if you specify exact dimensions for the CardView, because of the shadows, its content area will be
 * different between platforms before Lollipop and after Lollipop. By using api version specific resource values, you
 * can avoid these changes. Alternatively, If you want CardView to add inner padding on platforms Lollipop and after as
 * well, you can call {@link #setUseCompatPadding(boolean)} and pass <code>true</code>.
 * <p>
 * To change CardView's elevation in a backward compatible way, use {@link #setCardElevation(float)}. CardView will use
 * elevation API on Lollipop and before Lollipop, it will change the shadow size. To avoid moving the View while shadow
 * size is changing, shadow size is clamped by {@link #getMaxCardElevation()}. If you want to change elevation
 * dynamically, you should call {@link #setMaxCardElevation(float)} when CardView is initialized.
 *
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardBackgroundColor
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardCornerRadius
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardElevation
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardMaxElevation
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardUseCompatPadding
 * @attr ref android.support.v7.cardview.R.styleable#CardView_cardPreventCornerOverlap
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPadding
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingLeft
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingTop
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingRight
 * @attr ref android.support.v7.cardview.R.styleable#CardView_contentPaddingBottom
 */
public class CardView2 extends CardView {


    public CardView2(@NonNull Context context) {
        super(context);
    }

    public CardView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
