package com.zpj.shouji.market.ui.multidata;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.widget.DownloadButton;

import java.util.List;

public abstract class AppInfoMultiData extends BaseHeaderMultiData<AppInfo> {

    public AppInfoMultiData(String title) {
        super(title);
    }

    @Override
    public int getChildColumnCount(int viewType) {
        return getMaxColumnCount();
    }

    @Override
    public int getMaxColumnCount() {
        return 4;
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.item_app_grid;
    }

    @Override
    public int getChildViewType(int position) {
        return R.layout.item_app_grid;
    }

    @Override
    public boolean hasChildViewType(int viewType) {
        return viewType == R.layout.item_app_grid;
    }

    @Override
    public boolean loadData() {
        if (getKey() != null) {
            HttpApi.getXml(getKey().getUrl())
                    .onSuccess(this::onGetDoc)
                    .onError(new IHttp.OnErrorListener() {
                        @Override
                        public void onError(Throwable throwable) {
                            showError();
                        }
                    })
                    .subscribe();
        }
        return false;
    }

    @Override
    public void onBindChild(EasyViewHolder holder, List<AppInfo> list, int position, List<Object> payloads) {
        long temp = System.currentTimeMillis();
        AppInfo info = list.get(position);
        holder.getTextView(R.id.item_title).setText(info.getAppTitle());
        holder.getTextView(R.id.item_info).setText(info.getAppSize());
        ImageView ivIcon = holder.getImageView(R.id.item_icon);
        Glide.with(ivIcon)
                .load(info.getAppIcon())
                .apply(GlideRequestOptions.getDefaultIconOption())
                .into(ivIcon);
        long start = System.currentTimeMillis();
        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        downloadButton.bindApp(info);
        Log.i("onBindChild", "bind deltaTime=" + (System.currentTimeMillis() - start));
        holder.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailFragment.start(info);
            }
        });
        Log.i("onBindChild", "deltaTime=" + (System.currentTimeMillis() - temp));
    }

    public abstract PreloadApi getKey();

    protected void onGetDoc(Document document) {
        Elements elements = document.select("item");
        for (Element element : elements) {
            AppInfo info = AppInfo.parse(element);
            if (info == null) {
                continue;
            }
            list.add(info);
        }
//        int count = adapter.getItemCount();
//        adapter.notifyItemRangeInserted(count - getCount(), getCount());
        showContent();
    }

}