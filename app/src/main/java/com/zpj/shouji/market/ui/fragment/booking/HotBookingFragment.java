package com.zpj.shouji.market.ui.fragment.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.BookingApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;

public class HotBookingFragment extends BookingAppListFragment {

    public static void start() {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, BookingApi.HOT_BOOKING_URL);
        HotBookingFragment fragment = new HotBookingFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("热门预约");
    }

}
