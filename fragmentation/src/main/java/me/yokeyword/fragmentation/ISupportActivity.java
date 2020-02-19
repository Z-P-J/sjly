package me.yokeyword.fragmentation;

import android.view.MotionEvent;

import me.yokeyword.fragmentation.anim.FragmentAnimator;

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

    void postDelay(Runnable runnable, long delay);

    void onBackPressed();

    void onBackPressedSupport();

    boolean dispatchTouchEvent(MotionEvent ev);
}
