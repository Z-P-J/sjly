package com.zpj.shouji.market.ui.fragment.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.PrivateLetterInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.animator.SlideInOutBottomItemAnimator;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.dialog.BottomListMenuDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.CommonImageViewerDialogFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.ReplyPanel;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends NextUrlFragment<PrivateLetterInfo>
        implements ReplyPanel.OnOperationListener {

    public int position; //加载滚动刷新位置


    private String userId;
    private ReplyPanel replyPanel;

//    public final List<PrivateLetterInfo> letterInfoList = new ArrayList<>();


    public static void start(String id, String title) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.tljpxm.com/app/user_message_index_xml_v3.jsp?mmid=" + id);
        args.putString(Keys.ID, id);
        args.putString(Keys.TITLE, title);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        StartFragmentEvent.start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
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
//        Matisse.onDestroy();
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

        replyPanel = view.findViewById(R.id.panel_reply);
        replyPanel.setOnOperationListener(this);

        replyPanel.getEditor().setBackground(getResources().getDrawable(R.drawable.grey_shape));

        com.zpj.popup.util.KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
            replyPanel.onKeyboardHeightChanged(height, 0);
        });
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        darkStatusBar();
    }

    @Override
    public boolean onBackPressedSupport() {
        if (replyPanel.isEmotionPanelShow()) {
            replyPanel.hideEmojiPanel();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        showSoftInput(replyPanel.getEditor());
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<PrivateLetterInfo> recyclerLayout) {
        recyclerLayout.setLayoutManager(
                new LinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, true
                ))
                .setItemAnimator(new SlideInOutBottomItemAnimator(
                        recyclerLayout.getEasyRecyclerView().getRecyclerView()
                ))
                .onGetChildViewType((list, position) -> {
                    PrivateLetterInfo info = list.get(position);
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
                .onGetChildLayoutId(viewType -> {
                    switch (viewType) {
                        case 0:
                            return R.layout.item_chat;
                        case 1:
                            return R.layout.item_chat_replay;
                        case 2:
                            return R.layout.item_chat_img;
                        case 3:
                            return R.layout.item_chat_replay_img;
                    }
                    return 0;
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

                                replyPanel.hideEmojiPanel();

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
        Element pics = element.selectFirst("pics");
        for (Element pic : pics.select("pic")) {
            info.addPic(pic.text());
        }
        for (Element size : pics.select("psize")) {
            info.addSize(size.text());
        }
        for (Element spic : element.selectFirst("spics").select("spic")) {
            info.addSpic(spic.text());
        }
//        data.add(0, info);
//        return null;
        return info;
    }

    @Override
    public void onGetDocument(Document doc) throws Exception {
        Log.d("getData", "doc=" + doc);
        List<PrivateLetterInfo> list = new ArrayList<>();
        for (Element element : doc.select("item")) {
            PrivateLetterInfo item = createData(element);
            if (item == null) {
                continue;
            }
            list.add(0, item);
        }
        data.addAll(list);
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
        new BottomListMenuDialogFragment()
                .setMenu(R.menu.menu_private_letter)
                .addHideItem(hideList)
                .onItemClick((menu, view1, data1) -> {
                    switch (data1.getItemId()) {
                        case R.id.blacklist:
                            new AlertDialogFragment()
                                    .setTitle("添加黑名单")
                                    .setContent("确定将该用户加入黑名单？")
                                    .setPositiveButton(popup -> HttpApi.addBlacklistApi(data.getSendId()))
                                    .show(context);
                            break;
                        case R.id.cancel_follow:
                            new AlertDialogFragment()
                                    .setTitle("取消关注")
                                    .setContent("确定取消关注该用户？")
                                    .setPositiveButton(popup -> HttpApi.deleteFriendApi(data.getSendId())
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
                                    .show(context);
                            break;
                        case R.id.copy:
                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setPrimaryClip(ClipData.newPlainText(null, data.getContent()));
                            AToast.success("已复制到粘贴板");
                            break;
                        case R.id.delete:
                            new AlertDialogFragment()
                                    .setTitle("删除信息")
                                    .setContent("确定删除该信息？")
                                    .setPositiveButton(popup -> HttpApi.deletePrivateLetterApi(data.getId())
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
                                    .show(context);
                            break;
                        case R.id.share:
                            AToast.normal("分享");
                            break;
                    }
                    menu.dismiss();
                })
                .show(context);
//        BottomListPopupMenu.with(context)
//                .setMenu(R.menu.menu_private_letter)
//                .addHideItem(hideList)
//                .onItemClick((menu, view1, data1) -> {
//                    switch (data1.getItemId()) {
//                        case R.id.blacklist:
////                            ZPopup.alert(context)
////                                    .setTitle("添加黑名单")
////                                    .setContent("确定将该用户加入黑名单？")
////                                    .setConfirmButton(popup -> HttpApi.addBlacklistApi(data.getSendId()))
////                                    .show();
//                            new AlertDialogFragment()
//                                    .setTitle("添加黑名单")
//                                    .setContent("确定将该用户加入黑名单？")
//                                    .setPositiveButton(popup -> HttpApi.addBlacklistApi(data.getSendId()))
//                                    .show(context);
//                            break;
//                        case R.id.cancel_follow:
//                            new AlertDialogFragment()
//                                    .setTitle("取消关注")
//                                    .setContent("确定取消关注该用户？")
//                                    .setPositiveButton(popup -> HttpApi.deleteFriendApi(data.getSendId())
//                                            .onSuccess(element -> {
//                                                Log.d("deleteFriendApi", "element=" + element);
//                                                String result = element.selectFirst("result").text();
//                                                if ("success".equals(result)) {
//                                                    AToast.success("取消关注成功");
//                                                } else {
//                                                    AToast.error(element.selectFirst("info").text());
//                                                }
//                                            })
//                                            .onError(throwable -> AToast.error(throwable.getMessage()))
//                                            .subscribe())
//                                    .show(context);
////                            ZPopup.alert(context)
////                                    .setTitle("取消关注")
////                                    .setContent("确定取消关注该用户？")
////                                    .setConfirmButton(popup -> HttpApi.deleteFriendApi(data.getSendId())
////                                            .onSuccess(element -> {
////                                                Log.d("deleteFriendApi", "element=" + element);
////                                                String result = element.selectFirst("result").text();
////                                                if ("success".equals(result)) {
////                                                    AToast.success("取消关注成功");
////                                                } else {
////                                                    AToast.error(element.selectFirst("info").text());
////                                                }
////                                            })
////                                            .onError(throwable -> AToast.error(throwable.getMessage()))
////                                            .subscribe())
////                                    .show();
//                            break;
//                        case R.id.copy:
//                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                            cm.setPrimaryClip(ClipData.newPlainText(null, data.getContent()));
//                            AToast.success("已复制到粘贴板");
//                            break;
//                        case R.id.delete:
//                            new AlertDialogFragment()
//                                    .setTitle("删除信息")
//                                    .setContent("确定删除该信息？")
//                                    .setPositiveButton(popup -> HttpApi.deletePrivateLetterApi(data.getId())
//                                            .onSuccess(element -> {
//                                                Log.d("deleteFriendApi", "element=" + element);
//                                                String result = element.selectFirst("result").text();
//                                                if ("success".equals(result)) {
//                                                    AToast.success("删除成功");
//                                                    onRefresh();
//                                                } else {
//                                                    AToast.error(element.selectFirst("info").text());
//                                                }
//                                            })
//                                            .onError(throwable -> AToast.error(throwable.getMessage()))
//                                            .subscribe())
//                                    .show(context);
////                            ZPopup.alert(context)
////                                    .setTitle("删除信息")
////                                    .setContent("确定删除该信息？")
////                                    .setConfirmButton(popup -> HttpApi.deletePrivateLetterApi(data.getId())
////                                            .onSuccess(element -> {
////                                                Log.d("deleteFriendApi", "element=" + element);
////                                                String result = element.selectFirst("result").text();
////                                                if ("success".equals(result)) {
////                                                    AToast.success("删除成功");
////                                                    onRefresh();
////                                                } else {
////                                                    AToast.error(element.selectFirst("info").text());
////                                                }
////                                            })
////                                            .onError(throwable -> AToast.error(throwable.getMessage()))
////                                            .subscribe())
////                                    .show();
//                            break;
//                        case R.id.share:
//                            AToast.normal("分享");
//                            break;
//                    }
//                    menu.dismiss();
//                })
//                .show();
        return super.onLongClick(holder, view, data);
    }

    private void gridImageView(final EasyViewHolder holder, final PrivateLetterInfo info, final int position) {
        NineGridView nineGridImageView = holder.getView(R.id.gv_img);
        if (nineGridImageView == null) {
            return;
        }
        nineGridImageView.setImageLoader(DiscoverBinder.getImageLoader());
        nineGridImageView.setOnItemClickListener(new NineGridView.onItemClickListener() {
            @Override
            public void onNineGirdAddMoreClick(int dValue) {

            }

            @Override
            public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {
//                CommonImageViewerPopup.with(context)
//                        .setOriginalImageList(info.getPics())
//                        .setImageSizeList(info.getSizes())
//                        .setImageUrls(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(context) ? info.getPics() : info.getSpics())
//                        .setSrcView(imageContainer.getImageView(), position)
//                        .setSrcViewUpdateListener((popup, pos) -> {
//                            NineGirdImageContainer view = (NineGirdImageContainer) nineGridImageView.getChildAt(pos);
//                            popup.updateSrcView(view.getImageView());
//                        })
//                        .show();
                new CommonImageViewerDialogFragment()
                        .setOriginalImageList(info.getPics())
                        .setImageSizeList(info.getSizes())
                        .setImageUrls(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(context) ? info.getPics() : info.getSpics())
                        .setSrcView(imageContainer.getImageView(), position)
                        .setSrcViewUpdateListener((popup, pos) -> {
                            NineGirdImageContainer view = (NineGirdImageContainer) nineGridImageView.getChildAt(pos);
                            popup.updateSrcView(view.getImageView());
                        })
                        .show(context);
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
        hideSoftInput();
        HttpApi.sendPrivateLetterApi(
                context,
                userId,
                content,
                replyPanel.getImgList(),
                () -> {
                    replyPanel.getEditor().setText(null);
                    onRefresh();
                },
                new IHttp.OnStreamWriteListener() {
                    @Override
                    public void onBytesWritten(int bytesWritten) {

                    }

                    @Override
                    public boolean shouldContinue() {
                        return true;
                    }
                }
        );
    }

    @Override
    public void onEmojiSelected(String key) {

    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
        AToast.normal("categoryName=" + categoryName + " stickerName=" + stickerName);
//        sendImage(stickerBitmapPath);
    }


}
