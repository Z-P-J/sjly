package com.zpj.shouji.market.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.footer.AbsFooterViewHolder;
import com.zpj.recyclerview.footer.IFooterViewHolder;
import com.zpj.shouji.market.R;

public class FooterViewHolder extends AbsFooterViewHolder {

    private View progressLayout;
    private TextView tvMsg;
    private final boolean showSpace;

    public FooterViewHolder() {
        this(false);
    }

    public FooterViewHolder(boolean showSpace) {
        this.showSpace = showSpace;
    }

    @Override
    public View onCreateFooterView(ViewGroup root) {
        View view = LayoutInflater.from(root.getContext()).inflate(R.layout.item_footer_home, null, false);
        progressLayout = view.findViewById(R.id.ll_container_progress);
        tvMsg = view.findViewById(R.id.tv_msg);
        Space space = view.findViewById(R.id.space);
        space.setVisibility(showSpace ? View.VISIBLE : View.GONE);
        return view;
    }

    @Override
    public void onShowLoading() {
        progressLayout.setVisibility(View.VISIBLE);
        tvMsg.setVisibility(View.GONE);
    }

    @Override
    public void onShowHasNoMore() {
        progressLayout.setVisibility(View.GONE);
        tvMsg.setVisibility(View.VISIBLE);
        tvMsg.setText(R.string.easy_has_no_more);
    }

    @Override
    public void onShowError(String msg) {
        progressLayout.setVisibility(View.GONE);
        tvMsg.setVisibility(View.VISIBLE);
        tvMsg.setText("出错了！" + msg);
    }
}
