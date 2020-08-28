package com.zpj.shouji.market.ui.fragment.booking;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.BookingAppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;
import java.util.Locale;

public class BookingAppListFragment extends NextUrlFragment<BookingAppInfo> {


    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_booking;
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
        holder.setText(R.id.tv_info, String.format("该%s有%s人预约", "game".equals(appInfo.getAppType()) ? "游戏" : "应用", appInfo.getBookingCount()));
    }

}
