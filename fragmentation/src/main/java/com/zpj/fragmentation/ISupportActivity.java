package com.zpj.fragmentation;

import android.view.MotionEvent;

import com.zpj.fragmentation.anim.FragmentAnimator;

/**
 * Created by YoKey on 17/6/13.
 * Modified by Z-P-J
 */

public interface ISupportActivity {
    SupportActivityDelegate getSupportDelegate();

    ExtraTransaction extraTransaction();

    FragmentAnimator getFragmentAnimator();

    void setFragmentAnimator(FragmentAnimator fragmentAnimator);

    FragmentAnimator onCreateFragmentAnimator();

    void post(Runnable runnable);

    void postDelayed(Runnable runnable, long delay);

    void onBackPressed();

    void onBackPressedSupport();

    boolean dispatchTouchEvent(MotionEvent ev);
}
