package com.zpj.shouji.market.ui.fragment.booking;

import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.popup.ZPopup;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.BookingApi;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class BookingAppListFragment extends NextUrlFragment<BookingAppInfo> {


//    @Override
//    protected int getItemLayoutId() {
//        return R.layout.item_app_booking;
//    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_linear;
    }

    @Override
    public BookingAppInfo createData(Element element) {
        return BeanUtils.createBean(element, BookingAppInfo.class);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<BookingAppInfo> list, int position, List<Object> payloads) {
        BookingAppInfo appInfo = list.get(position);
        Glide.with(context).load(appInfo.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
        holder.setText(R.id.tv_title, appInfo.getAppName());
        if (TextUtils.isEmpty(appInfo.getBookingCount())) {
            holder.setText(R.id.tv_info, appInfo.getBookingInfo().replace(",", " · "));
            holder.setVisible(R.id.tv_desc, true);
            holder.setText(R.id.tv_desc, appInfo.getComment());

            if (appInfo.isBooking()) {
                holder.setText(R.id.tv_download, "预约");
                holder.setBackgroundResource(R.id.tv_download, R.drawable.bg_download_button);
                holder.setTextColor(R.id.tv_download, getResources().getColor(R.color.colorPrimary));
                holder.setOnClickListener(R.id.tv_download, v -> {
                    BookingApi.bookingApi(appInfo, refreshRunnable);
                });
            } else {
                holder.setText(R.id.tv_download, "已预约");
                holder.setBackgroundResource(R.id.tv_download, R.drawable.bg_button_remove);
                holder.setTextColor(R.id.tv_download, getResources().getColor(R.color.red5));
                holder.setOnClickListener(R.id.tv_download, v -> showCancelBookingPopup(appInfo, refreshRunnable));
            }

        } else {
            holder.setText(R.id.tv_info, String.format("该%s有%s人预约", "game".equals(appInfo.getAppType()) ? "游戏" : "应用", appInfo.getBookingCount()));
            holder.setVisible(R.id.tv_desc, false);

            holder.setText(R.id.tv_download, "已预约");
            holder.setBackgroundResource(R.id.tv_download, R.drawable.bg_button_remove);
            holder.setTextColor(R.id.tv_download, getResources().getColor(R.color.red5));
            holder.setOnClickListener(R.id.tv_download, v -> showCancelBookingPopup(appInfo, refreshRunnable));
        }

    }

    @Override
    public void onClick(EasyViewHolder holder, View view, BookingAppInfo data) {
        AppDetailFragment.start(data.getAppType(), data.getAppId());
    }

    protected void showCancelBookingPopup(BookingAppInfo appInfo, Runnable runnable) {
        ZPopup.alert(context)
                .setTitle("取消预约？")
                .setContent("确定取消预约？取消预约后您将不能及时在软件上架时及时收到通知。")
                .setConfirmButton(popup -> {
                    postDelayed(() -> BookingApi.cancelBookingApi(appInfo, runnable), popup.getAnimationDuration());
                })
                .show();
    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            recyclerLayout.notifyVisibleItemChanged();
        }
    };

}
