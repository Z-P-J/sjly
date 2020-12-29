package com.zpj.shouji.market.ui.fragment.homepage.multi;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.MultiAdapter;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.api.HttpPreLoader;
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
    public int getChildSpanCount(int viewType) {
        return 1;
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
    public boolean loadData(MultiAdapter adapter) {
        if (getKey() != null) {
            if (HttpPreLoader.getInstance().hasKey(getKey())) {
                HttpPreLoader.getInstance().setLoadListener(getKey(), document -> onGetDoc(adapter, document));
            } else {
                HttpApi.getXml(getKey().getUrl())
                        .onSuccess(document -> onGetDoc(adapter, document))
//                            .onError(new IHttp.OnErrorListener() {
//                                @Override
//                                public void onError(Throwable throwable) {
//                                    isDataLoaded = false;
//                                    adapter.notifyDataSetChanged();
//                                }
//                            })
                        .subscribe();
            }
        }
        return false;
    }

    @Override
    public void onBindChild(EasyViewHolder holder, List<AppInfo> list, int position, List<Object> payloads) {
        AppInfo info = list.get(position);
        holder.getTextView(R.id.item_title).setText(info.getAppTitle());
        holder.getTextView(R.id.item_info).setText(info.getAppSize());
        ImageView ivIcon = holder.getImageView(R.id.item_icon);
        Glide.with(ivIcon)
                .load(info.getAppIcon())
                .apply(GlideRequestOptions.getDefaultIconOption())
                .into(ivIcon);
        DownloadButton downloadButton = holder.getView(R.id.tv_download);
        downloadButton.bindApp(info);
        holder.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailFragment.start(info);
            }
        });
    }

    public abstract PreloadApi getKey();

    protected void onGetDoc(MultiAdapter adapter, Document document) {
        Elements elements = document.select("item");
        for (Element element : elements) {
            AppInfo info = AppInfo.parse(element);
            if (info == null) {
                continue;
            }
            list.add(info);
//            if (list.size() == 8) {
//                break;
//            }
        }
        adapter.notifyDataSetChanged();
    }

}