package com.zpj.shouji.market.ui.widget.recommend;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.BookingApi;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.ui.fragment.booking.LatestBookingFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;

import java.util.List;

public class GameBookingRecommendCard extends RecommendCard<BookingAppInfo> {

    public GameBookingRecommendCard(Context context) {
        this(context, null);
    }

    public GameBookingRecommendCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameBookingRecommendCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitle("新游预约");
    }

    @Override
    public void loadData(Runnable successRunnable) {
        BookingApi.latestBookingApi(dataList -> {
            list.clear();
            list.addAll(dataList);
            recyclerView.notifyDataSetChanged();
            if (successRunnable != null) {
                successRunnable.run();
            }
        });
    }

    @Override
    protected void buildRecyclerView(EasyRecyclerView<BookingAppInfo> recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1,
                LinearLayoutManager.HORIZONTAL, false));
        for (int i = 0; i < recyclerView.getRecyclerView().getItemDecorationCount(); i++) {
            recyclerView.getRecyclerView().removeItemDecorationAt(i);
        }
        recyclerView.addItemDecoration(new Y_DividerItemDecoration(context) {
            @Override
            public Y_Divider getDivider(int itemPosition) {
                Y_DividerBuilder builder;
                int color = Color.TRANSPARENT;
                if (itemPosition == 0) {
                    builder = new Y_DividerBuilder()
                            .setLeftSideLine(true, color, 16, 0, 0)
                            .setRightSideLine(true, color, 4, 0, 0);
                } else if (itemPosition == list.size() - 1) {
                    builder = new Y_DividerBuilder()
                            .setRightSideLine(true, color, 16, 0, 0)
                            .setLeftSideLine(true, color, 4, 0, 0);
                } else {
                    builder = new Y_DividerBuilder()
                            .setLeftSideLine(true, color, 4, 0, 0)
                            .setRightSideLine(true, color, 4, 0, 0);
                }
                return builder.setTopSideLine(true, color, 8, 0, 0)
                        .setBottomSideLine(true, color, 8, 0, 0)
                        .create();
            }
        });
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<BookingAppInfo> list, int position, List<Object> payloads) {
        BookingAppInfo info = list.get(position);
        holder.setText(R.id.tv_name, info.getAppName());
        holder.setText(R.id.tv_info, info.getComment());
        Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
        Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_bg));

        if (info.isBooking()) {
            holder.setText(R.id.tv_booking, "预约");
            holder.setBackgroundResource(R.id.tv_booking, R.drawable.bg_download_button);
            holder.setTextColor(R.id.tv_booking, getResources().getColor(R.color.colorPrimary));
            holder.setOnClickListener(R.id.tv_booking, v -> {
                BookingApi.bookingApi(info, refreshRunnable);
            });
        } else {
            holder.setText(R.id.tv_booking, "已预约");
            holder.setBackgroundResource(R.id.tv_booking, R.drawable.bg_button_remove);
            holder.setTextColor(R.id.tv_booking, getResources().getColor(R.color.red5));
            holder.setOnClickListener(R.id.tv_booking, v -> showCancelBookingPopup(info, refreshRunnable));
        }
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, BookingAppInfo data) {
        AppDetailFragment.start(data);
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_booking_grid;
    }

    @Override
    public void onMoreClicked(View v) {
        LatestBookingFragment.start();
    }

    protected void showCancelBookingPopup(BookingAppInfo appInfo, Runnable runnable) {
        new AlertDialogFragment()
                .setTitle("取消预约？")
                .setContent("确定取消预约？取消预约后您将不能及时在软件上架时及时收到通知。")
                .setPositiveButton(popup -> {
                    BookingApi.cancelBookingApi(appInfo, runnable);
                })
                .show(context);
    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            recyclerView.notifyVisibleItemChanged();
        }
    };

}
