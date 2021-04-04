package com.zpj.shouji.market.ui.multidata;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.BookingApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.toast.ZToast;

public class GameBookingMultiData extends RecyclerMultiData<BookingAppInfo> {

    public GameBookingMultiData() {
        super("新游预约");
    }

    @Override
    public int getItemRes() {
        return R.layout.item_app_booking_grid;
    }

    @Override
    public void buildRecyclerView(EasyRecyclerView<BookingAppInfo> recyclerView) {
        recyclerView
                .addItemDecoration(new Y_DividerItemDecoration(recyclerView.getRecyclerView().getContext()) {
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
                })
                .onBindViewHolder((holder, list, position, payloads) -> {
                    BookingAppInfo info = list.get(position);
                    holder.setText(R.id.tv_name, info.getAppName());
                    holder.setText(R.id.tv_info, info.getComment());
                    Context context = holder.getItemView().getContext();
                    Glide.with(context).load(info.getAppIcon())
                            .apply(GlideRequestOptions.getDefaultIconOption())
                            .into(holder.getImageView(R.id.iv_icon));
                    Glide.with(context).load(info.getAppIcon()).into(holder.getImageView(R.id.iv_bg));

                    if (info.isBooking()) {
                        holder.setText(R.id.tv_booking, "预约");
                        holder.setBackgroundResource(R.id.tv_booking, R.drawable.bg_button_download);
                        holder.setTextColor(R.id.tv_booking, context.getResources().getColor(R.color.colorPrimary));
                        holder.setOnClickListener(R.id.tv_booking, v -> {
                            BookingApi.bookingApi(info, refreshRunnable);
                        });
                    } else {
                        holder.setText(R.id.tv_booking, "已预约");
                        holder.setBackgroundResource(R.id.tv_booking, R.drawable.bg_button_remove);
                        holder.setTextColor(R.id.tv_booking, context.getResources().getColor(R.color.red5));
                        holder.setOnClickListener(R.id.tv_booking, v -> showCancelBookingPopup(context, info, refreshRunnable));
                    }
                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppDetailFragment.start(info);
                        }
                    });
                });
    }

    @Override
    public void onHeaderClick() {
        ZToast.normal("TODO");
    }

    @Override
    public boolean loadData() {
        BookingApi.latestBookingApi(dataList -> {
            if (dataList.isEmpty()) {
                showError();
                return;
            }
            list.clear();
            list.addAll(dataList);
//            adapter.notifyDataSetChanged();
            showContent();
        });
        return false;
    }

    protected void showCancelBookingPopup(Context context, BookingAppInfo appInfo, Runnable runnable) {
        new AlertDialogFragment()
                .setTitle("取消预约？")
                .setContent("确定取消预约？取消预约后您将不能及时在软件上架时及时收到通知。")
                .setPositiveButton((fragment, which) -> BookingApi.cancelBookingApi(appInfo, runnable))
                .show(context);
    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (recyclerView != null) {
                recyclerView.notifyVisibleItemChanged();
            }
        }
    };

}
