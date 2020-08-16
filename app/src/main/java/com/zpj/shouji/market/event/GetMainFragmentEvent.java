package com.zpj.shouji.market.event;

import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.shouji.market.utils.Callback;

public class GetMainFragmentEvent extends BaseEvent {

    private final Callback<MainFragment> callback;

    private GetMainFragmentEvent(Callback<MainFragment> callback) {
        this.callback = callback;
    }

    public Callback<MainFragment> getCallback() {
        return callback;
    }

    public static void post(Callback<MainFragment> callback) {
        new GetMainFragmentEvent(callback).post();
    }

}
