package com.zpj.shouji.market.ui.fragment.chat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.matisse.Matisse;
import com.zpj.popup.ZPopup;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.PrivateLetterInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.animator.SlideInOutBottomItemAnimator;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.ChatPanel;
import com.zpj.shouji.market.ui.widget.WrapContentLinearLayoutManager;
import com.zpj.shouji.market.ui.widget.popup.BottomListPopupMenu;
import com.zpj.shouji.market.ui.widget.popup.ImageViewer;
import com.zpj.shouji.market.utils.BeanUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment2 extends NextUrlFragment<PrivateLetterInfo> implements ChatPanel.OnOperationListener {

    public int position; //加载滚动刷新位置


    private String userId;
    private ChatPanel chatPanel;

//    public final List<PrivateLetterInfo> letterInfoList = new ArrayList<>();


    public static void start(String id, String title) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.tljpxm.com/app/user_message_index_xml_v3.jsp?mmid=" + id);
        args.putString(Keys.ID, id);
        args.putString(Keys.TITLE, title);
        ChatFragment2 fragment = new ChatFragment2();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat2;
    }

    @Override
    protected int getItemLayoutId() {
        return 0;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public void onDestroy() {
        Matisse.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        super.handleArguments(arguments);
        setToolbarTitle(arguments.getString(Keys.TITLE, ""));
        userId = arguments.getString(Keys.ID, "");
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        chatPanel = view.findViewById(R.id.chat_panel);
        chatPanel.setOnOperationListener(this);

        com.zpj.popup.util.KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
            chatPanel.onKeyboardHeightChanged(height, 0);
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        if (chatPanel.isEmotionPanelShow()) {
            chatPanel.hideEmojiPanel();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        showSoftInput(chatPanel.getEditor());
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<PrivateLetterInfo> recyclerLayout) {
        recyclerLayout.setLayoutManager(
                new WrapContentLinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, true
                ))
                .setItemAnimator(new SlideInOutBottomItemAnimator(
                        recyclerLayout.getEasyRecyclerView().getRecyclerView()
                ))
                .onGetChildViewType(position -> {
                    PrivateLetterInfo info = data.get(position);
                    if (UserManager.getInstance().getUserId().equals(info.getSendId())) {
                        if (info.getPics().size() == 0) {
                            return 0;
                        } else {
                            return 2;
                        }
                    } else {
                        if (info.getPics().size() == 0) {
                            return 1;
                        } else {
                            return 3;
                        }
                    }
                })
//                .onGetChildLayoutId(viewType -> {
//                    switch (viewType) {
//                        case 0:
//                            return R.layout.item_chat;
//                        case 1:
//                            return R.layout.item_chat_replay;
//                        case 2:
//                            return R.layout.item_chat_img;
//                        case 3:
//                            return R.layout.item_chat_replay_img;
//                    }
//                    return 0;
//                })
                .onCreateViewHolder((parent, layoutRes, viewType) -> {
                    switch (viewType) {
                        case 0:
                            layoutRes = R.layout.item_chat;
                            break;
                        case 1:
                            layoutRes = R.layout.item_chat_replay;
                            break;
                        case 2:
                            layoutRes = R.layout.item_chat_img;
                            break;
                        case 3:
                            layoutRes = R.layout.item_chat_replay_img;
                            break;
                    }
                    return LayoutInflater.from(parent.getContext()).
                            inflate(layoutRes, parent, false);
                })
                .onViewClick(R.id.iv_icon, (holder, view, data) ->
                        ProfileFragment.start(data.getSendId(), true))
                .addOnScrollListener(new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView view, int scrollState) {
                        // TODO Auto-generated method stub
                        switch (scrollState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
//                                isGif = true;
//                                tbAdapter.isPicRefresh = false;
//                                tbAdapter.notifyDataSetChanged();
                                break;
                            case RecyclerView.SCROLL_STATE_DRAGGING:
//                                tbAdapter.handler.removeCallbacksAndMessages(null);
//                                tbAdapter.setIsGif(false);
//                                tbAdapter.isPicRefresh = true;

                                chatPanel.hideEmojiPanel();

                                hideSoftInput();
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
    }

    @Override
    public PrivateLetterInfo createData(Element element) {
        PrivateLetterInfo info = BeanUtils.createBean(element, PrivateLetterInfo.class);
        for (Element pic : element.selectFirst("pics").select("pic")) {
            info.addPic(pic.text());
        }
        for (Element spic : element.selectFirst("spics").select("spic")) {
            info.addSpic(spic.text());
        }
        data.add(0, info);
        return null;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<PrivateLetterInfo> list, int position, List<Object> payloads) {
        PrivateLetterInfo info = list.get(position);
        Glide.with(context).load(info.getAvatar()).into(holder.getImageView(R.id.iv_icon));
        holder.setText(R.id.tv_content, info.getContent());
        holder.setText(R.id.tv_time, info.getTime());
        switch (holder.getViewType()) {
            case 0:
                break;
            case 3:
            case 1:
                holder.setText(R.id.tv_name, info.getNikeName());
                break;
            case 2:

                break;
        }
        gridImageView(holder, info, position);
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, PrivateLetterInfo data) {
        List<Integer> hideList = new ArrayList<>();
        if (UserManager.getInstance().getUserId().equals(data.getSendId())) {
            hideList.add(R.id.blacklist);
            hideList.add(R.id.cancel_follow);
        } else {
            hideList.add(R.id.delete);
        }
        BottomListPopupMenu.with(context)
                .setMenu(R.menu.menu_private_letter)
                .addHideItem(hideList)
                .onItemClick((menu, view1, data1) -> {
                    switch (data1.getItemId()) {
                        case R.id.blacklist:
                            ZPopup.alert(context)
                                    .setTitle("添加黑名单")
                                    .setContent("确定将该用户加入黑名单？")
                                    .setConfirmButton(popup -> HttpApi.addBlacklistApi(data.getSendId()))
                                    .show();
                            break;
                        case R.id.cancel_follow:
                            ZPopup.alert(context)
                                    .setTitle("取消关注")
                                    .setContent("确定取消关注该用户？")
                                    .setConfirmButton(popup -> HttpApi.deleteFriendApi(data.getSendId())
                                            .onSuccess(element -> {
                                                Log.d("deleteFriendApi", "element=" + element);
                                                String result = element.selectFirst("result").text();
                                                if ("success".equals(result)) {
                                                    AToast.success("取消关注成功");
                                                } else {
                                                    AToast.error(element.selectFirst("info").text());
                                                }
                                            })
                                            .onError(throwable -> AToast.error(throwable.getMessage()))
                                            .subscribe())
                                    .show();
                            break;
                        case R.id.copy:
                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setPrimaryClip(ClipData.newPlainText(null, data.getContent()));
                            AToast.success("已复制到粘贴板");
                            break;
                        case R.id.delete:
                            ZPopup.alert(context)
                                    .setTitle("删除信息")
                                    .setContent("确定删除该信息？")
                                    .setConfirmButton(popup -> HttpApi.deletePrivateLetterApi(data.getId())
                                            .onSuccess(element -> {
                                                Log.d("deleteFriendApi", "element=" + element);
                                                String result = element.selectFirst("result").text();
                                                if ("success".equals(result)) {
                                                    AToast.success("删除成功");
                                                    onRefresh();
                                                } else {
                                                    AToast.error(element.selectFirst("info").text());
                                                }
                                            })
                                            .onError(throwable -> AToast.error(throwable.getMessage()))
                                            .subscribe())
                                    .show();
                            break;
                        case R.id.share:
                            AToast.normal("分享");
                            break;
                    }
                    menu.dismiss();
                })
                .show();
        return super.onLongClick(holder, view, data);
    }

    private void gridImageView(final EasyViewHolder holder, final PrivateLetterInfo info, final int position) {
        NineGridView nineGridImageView = holder.getView(R.id.gv_img);
        if (nineGridImageView == null) {
            return;
        }
        nineGridImageView.setImageLoader(new DiscoverBinder.GlideImageLoader());
        nineGridImageView.setOnItemClickListener(new NineGridView.onItemClickListener() {
            @Override
            public void onNineGirdAddMoreClick(int dValue) {

            }

            @Override
            public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {
                ImageViewer.with(context)
                        .setImageList(info.getSpics())
                        .setNowIndex(position)
                        .setSourceImageView(pos -> {
                            NineGirdImageContainer view = (NineGirdImageContainer) nineGridImageView.getChildAt(pos);
                            return view.getImageView();
                        })
                        .show();
            }

            @Override
            public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

            }
        });
        List<NineGridBean> gridList = new ArrayList<>();
        for (String url : info.getSpics()) {
            gridList.add(new NineGridBean(url));
        }
        nineGridImageView.setDataList(gridList);
    }

    @Override
    public void sendText(String content) {
        HttpApi.sendPrivateLetterApi(userId, content)
                .onSuccess(element -> {
                    Log.d("deleteFriendApi", "element=" + element);
                    String result = element.selectFirst("result").text();
                    if ("success".equals(result)) {
                        AToast.success("发送成功");
                        onRefresh();
                    } else {
                        AToast.error(element.selectFirst("info").text());
                    }
                })
                .onError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();
    }

    @Override
    public void onEmojiSelected(String key) {

    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
        AToast.normal("categoryName=" + categoryName + " stickerName=" + stickerName);
//        sendImage(stickerBitmapPath);
    }


    @SuppressLint("SimpleDateFormat")
    public String getTime(String time, String before) {
        String show_time = null;
        if (before != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date now = df.parse(time);
                java.util.Date date = df.parse(before);
                long l = now.getTime() - date.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                if (min >= 1) {
                    show_time = time.substring(11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            show_time = time.substring(11);
        }
        String getDay = getDay(time);
        if (show_time != null && getDay != null)
            show_time = getDay + " " + show_time;
        return show_time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public String getDay(String time) {
        String showDay = null;
        String nowTime = returnTime();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date now = df.parse(nowTime);
            java.util.Date date = df.parse(time);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            if (day >= 365) {
                showDay = time.substring(0, 10);
            } else if (day >= 1 && day < 365) {
                showDay = time.substring(5, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showDay;
    }

}
