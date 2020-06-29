package com.zpj.shouji.market.event;

import com.zpj.fragmentation.SupportFragment;

public class StartFragmentEvent extends BaseEvent {

    private final SupportFragment fragment;

    public StartFragmentEvent(SupportFragment fragment) {
        this.fragment = fragment;
    }

    public SupportFragment getFragment() {
        return fragment;
    }

    public static void start(SupportFragment fragment) {
        new StartFragmentEvent(fragment).post();
    }

}
