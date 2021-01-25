package com.zpj.shouji.market.ui.fragment.collection;

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
import com.zpj.shouji.market.api.CollectionApi;
import com.zpj.shouji.market.api.PublishApi;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.manager.AppPickerFragment;
import com.zpj.shouji.market.ui.fragment.profile.UserPickerFragment;
import com.zpj.shouji.market.ui.widget.ActionPanel;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.toast.ZToast;
import com.zpj.utils.Callback;
import com.zpj.utils.KeyboardObserver;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class CollectionShareFragment extends BaseSwipeBackFragment {


    private final List<InstalledAppInfo> appList = new ArrayList<>();

    private EditText etTitle;
    private EditText etContent;

    private EasyRecyclerView<InstalledAppInfo> recyclerView;

    private FlowLayout flowLayout;
    private TextView tvShareMode;
    private ActionPanel actionPanel;

    private boolean isPrivate;

    public static void start() {
        start(new CollectionShareFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collection_share;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("分享应用集");

        etTitle = view.findViewById(R.id.et_title);
        etContent = view.findViewById(R.id.et_content);

        etTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (actionPanel != null) {
                actionPanel.attachEditText(hasFocus ? etTitle : etContent);
            }
        });

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

                    tvRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appList.remove(holder.getAdapterPosition());
                            recyclerView.notifyItemRemoved(holder.getAdapterPosition());
                        }
                    });
                    Glide.with(context).load(appItem).into(holder.getImageView(R.id.iv_icon));
                })
                .build();

        flowLayout = view.findViewById(R.id.fl_tags);
        actionPanel = view.findViewById(R.id.panel_action);
        actionPanel.attachEditText(etTitle);
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, view, height -> {
            actionPanel.onKeyboardHeightChanged(height, 0);
            if (height != 0) {
                if (etContent.isFocused()) {
                    actionPanel.attachEditText(etContent);
                } else {
                    actionPanel.attachEditText(etTitle);
                }
            }
        });

//        actionPanel.removeImageAction();
//        actionPanel.removeAppAction();

        actionPanel.addAction(R.drawable.ic_at_black_24dp, v -> {
            showUserPicker();
        });

        actionPanel.addAction(R.drawable.ic_android_black_24dp, v -> {
            hideSoftInput();
            showAppPicker();
        });
        tvShareMode = actionPanel.addAction("公开", v -> {
            tvShareMode.setText(isPrivate ? "公开" : "私有");
            isPrivate = !isPrivate;
        });
        actionPanel.setSendAction(v -> {
            hideSoftInput();
            if (appList.isEmpty()) {
                ZToast.warning("请添加应用");
                return;
            }
            if (appList.size() < 3) {
                ZToast.warning("添加的应用过少");
                return;
            }

            if (TextUtils.isEmpty(etTitle.getText())) {
                ZToast.warning("请输入应用集标题");
                return;
            } else if (TextUtils.isEmpty(etContent.getText())) {
                ZToast.warning("请输入应用集描述");
                return;
            }
            String tags = "";
            for (String tag : flowLayout.getSelectedItem()) {
                if (!TextUtils.isEmpty(tags)) {
                    tags += ",";
                }
                tags += tag;
            }
            CollectionApi.shareCollectionApi(
                    etTitle.getText().toString(),
                    etContent.getText().toString(),
                    appList,
                    tags,
                    isPrivate,
                    this::pop,
                    new IHttp.OnStreamWriteListener() {
                        @Override
                        public void onBytesWritten(int bytesWritten) {

                        }

                        @Override
                        public boolean shouldContinue() {
                            return true;
                        }
                    });
        });
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
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
        initFlowLayout();
    }

    @Override
    public boolean onBackPressedSupport() {
        if (actionPanel.isEmotionPanelShow()) {
            actionPanel.hideEmojiPanel();
            return true;
        }
        return super.onBackPressedSupport();
    }

    private void initFlowLayout() {
        PublishApi.getPublishTags(tags -> {
//            flowLayout.setOnItemClickListener((index, v, text) -> {
//                flowLayout.setSelectedPosition(index);
//            });
            flowLayout.setMaxSelectCount(3);
            flowLayout.setMultiSelectMode(true);
//            flowLayout.addSelectedPosition(0);
            flowLayout.setSpace(ScreenUtils.dp2pxInt(context, 8));
            flowLayout.setItems(tags);
        });

    }

    private void showUserPicker() {
        hideSoftInput();
        UserPickerFragment.start((Callback<String>) content -> {
            actionPanel.getEditor().append(content);
            showSoftInput(actionPanel.getEditor());
        });
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
