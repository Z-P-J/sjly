package com.zpj.fragmentation;

import com.zpj.fragmentation.anim.FragmentAnimator;

import java.util.logging.Handler;

public interface ISupport<T> {

    T getSupportDelegate();

    ExtraTransaction extraTransaction();

    void post(Runnable runnable);

    void postDelayed(Runnable runnable, long delay);

    FragmentAnimator onCreateFragmentAnimator();

    FragmentAnimator getFragmentAnimator();

    void setFragmentAnimator(FragmentAnimator fragmentAnimator);

//    boolean onBackPressedSupport();

}
