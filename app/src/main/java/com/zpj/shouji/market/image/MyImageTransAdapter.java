package com.zpj.shouji.market.image;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.felix.atoast.library.AToast;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.zpj.popupmenuview.popup.EverywherePopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.drawable.TileBitmapDrawable;
import com.zpj.shouji.market.ui.widget.RoundPageIndicator;
import com.zpj.utils.ScreenUtil;

import it.liuting.imagetrans.ImageTransAdapter;


/**
 * Created by liuting on 17/6/15.
 */

public class MyImageTransAdapter extends ImageTransAdapter {

    private Context context;
    private View view;
    private CommonTitleBar topPanel;
    private RoundPageIndicator bottomPanel;
    private boolean isShow = true;

    @Override
    protected View onCreateView(View parent, ViewPager viewPager, final DialogInterface dialogInterface) {
        context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.layout_image_trans, null);
        topPanel = view.findViewById(R.id.top_panel);
        topPanel.getLeftImageButton().setOnClickListener(v -> dialogInterface.dismiss());
        topPanel.getRightImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EverywherePopup.create(context)
                        .addItems("查看原图", "下载", "分享")
                        .setOnItemClickListener(new EverywherePopup.OnItemClickListener() {
                            @Override
                            public void onItemClicked(String title, int position) {
                                AToast.normal("TODO " + title);
                            }
                        })
                        .apply()
                        .show(v);
            }
        });
        bottomPanel = view.findViewById(R.id.page_indicator);
        topPanel.setTranslationY(-ScreenUtil.dp2px(context, 56));
        bottomPanel.setTranslationY(ScreenUtil.dp2px(context, 80));
        bottomPanel.setViewPager(viewPager);
        return view;
    }

    @Override
    public void onPullRange(float range) {
        topPanel.setTranslationY(-ScreenUtil.dp2px(context, 56) * range * 4);
        bottomPanel.setTranslationY(ScreenUtil.dp2px(context, 80) * range * 4);
    }

    @Override
    public void onPullCancel() {
        showPanel();
    }

    @Override
    protected void onOpenTransStart() {
        showPanel();
    }

    @Override
    protected void onOpenTransEnd() {

    }

    @Override
    protected void onCloseTransStart() {
        hiddenPanel();
    }

    @Override
    protected void onCloseTransEnd() {
        TileBitmapDrawable.clearCache();
    }

    @Override
    protected boolean onClick(View v,int pos) {
        if (isShow) {
            showPanel();
        } else {
            hiddenPanel();
        }
        isShow = !isShow;

        return true;
    }

    @Override
    protected void onLongClick(View v,int pos) {
        Toast.makeText(view.getContext(), "long click", Toast.LENGTH_SHORT).show();
    }

    public void hiddenPanel() {
        topPanel.animate().translationY(-ScreenUtil.dp2px(context, 56)).setDuration(200).start();
        bottomPanel.animate().translationY(ScreenUtil.dp2px(context, 80)).setDuration(200).start();
    }

    public void showPanel() {
        topPanel.animate().translationY(0).setDuration(200).start();
        bottomPanel.animate().translationY(0).setDuration(200).start();
    }

}
