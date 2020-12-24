package com.zpj.shouji.market.ui.fragment.backup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zpj.http.core.IHttp;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CloudBackupApi;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.manager.AppPickerFragment;
import com.zpj.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateBackupFragment extends BaseSwipeBackFragment {


    private final List<InstalledAppInfo> appList = new ArrayList<>();

    private EditText etTitle;
    private EditText etContent;

    private EasyRecyclerView<InstalledAppInfo> recyclerView;

    public static void start() {
        start(new CreateBackupFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_create_backup;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "创建云备份";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        etTitle = view.findViewById(R.id.et_title);
        etContent = view.findViewById(R.id.et_content);

        etTitle.setText(DeviceUtils.getModel());
        etContent.setText(DeviceUtils.getModel() + "的备份列表");

        recyclerView = new EasyRecyclerView<>(view.findViewById(R.id.recycler_view));
        recyclerView.setData(appList)
                .setItemRes(R.layout.item_app_collection_share)
//                .setShowCheckBox(true)
                .setFooterView(R.layout.layout_footer_add_app, new IEasy.OnBindFooterListener() {
                    @Override
                    public void onBindFooter(EasyViewHolder holder) {
                        holder.setOnItemClickListener(v -> showAppPicker());
                    }
                })
                .onBindViewHolder((holder, list, position, payloads) -> {
                    final InstalledAppInfo appItem = list.get(position);
                    holder.setText(R.id.tv_title, appItem.getName());

                    holder.getTextView(R.id.tv_info).setText(appItem.getPackageName());

//                        EmojiExpandableTextView tvDesc = holder.getView(R.id.tv_desc);
//                        tvDesc.setContent(appItem.getComment());

                    TextView tvRemove = holder.getTextView(R.id.tv_remove);

                    tvRemove.setOnClickListener(v -> {
                        appList.remove(holder.getAdapterPosition());
                        recyclerView.notifyItemRemoved(holder.getAdapterPosition());
                    });
                    Glide.with(context).load(appItem).into(holder.getImageView(R.id.iv_icon));
                })
                .build();

        findViewById(R.id.tv_create).setOnClickListener(v -> {
            if (TextUtils.isEmpty(etTitle.getText()) || TextUtils.isEmpty(etContent.getText()) || appList.isEmpty()) {
                return;
            }
            CloudBackupApi.createBackup(
                    etTitle.getText().toString(),
                    etContent.getText().toString(),
                    appList,
                    new IHttp.OnStreamWriteListener() {
                        @Override
                        public void onBytesWritten(int bytesWritten) {

                        }

                        @Override
                        public boolean shouldContinue() {
                            return true;
                        }
                    },
                    () -> {
                        EventBus.sendRefreshEvent();
                        pop();
                    });
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        showSoftInput(etTitle);
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
    }

    private void showAppPicker() {
        AppPickerFragment.start(appList, obj -> {
            appList.clear();
            appList.addAll(obj);
            recyclerView.notifyDataSetChanged();
//            flEmpty.setVisibility(appList.isEmpty() ? View.VISIBLE : View.GONE);
//            recyclerView.getRecyclerView().setVisibility(appList.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }


}
