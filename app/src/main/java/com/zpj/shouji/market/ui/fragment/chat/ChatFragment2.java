package com.zpj.shouji.market.ui.fragment.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.matisse.Matisse;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.database.ChatManager;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.glide.CustomShapeTransformation;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.ChatMessageBean;
import com.zpj.shouji.market.model.PrivateLetterInfo;
import com.zpj.shouji.market.ui.adapter.ChatRecyclerAdapter;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.animator.SlideInOutBottomItemAnimator;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.BubbleImageView;
import com.zpj.shouji.market.ui.widget.ChatPanel;
import com.zpj.shouji.market.ui.widget.GifTextView;
import com.zpj.shouji.market.ui.widget.WrapContentLinearLayoutManager;
import com.zpj.shouji.market.ui.widget.popup.ImageViewer;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.zpj.shouji.market.ui.adapter.ChatRecyclerAdapter.FROM_USER_IMG;
import static com.zpj.shouji.market.ui.adapter.ChatRecyclerAdapter.FROM_USER_MSG;
import static com.zpj.shouji.market.ui.adapter.ChatRecyclerAdapter.TO_USER_IMG;
import static com.zpj.shouji.market.ui.adapter.ChatRecyclerAdapter.TO_USER_MSG;

public class ChatFragment2 extends NextUrlFragment<PrivateLetterInfo> implements ChatPanel.OnOperationListener {

    public int position; //加载滚动刷新位置


    private ChatPanel chatPanel;

//    public final List<PrivateLetterInfo> letterInfoList = new ArrayList<>();


    public static void start(String id, String title) {
        Bundle args = new Bundle();
        args.putString(Keys.DEFAULT_URL, "http://tt.tljpxm.com/app/user_message_index_xml_v3.jsp?mmid=" + id);
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

//    @Override
//    public void onEnterAnimationEnd(Bundle savedInstanceState) {
//        super.onEnterAnimationEnd(savedInstanceState);
//        if (getArguments() != null) {
//            Log.d("privateLetterApi", "id=" + getArguments().getString(Keys.ID, ""));
//            HttpApi.privateLetterApi(getArguments().getString(Keys.ID, ""))
//                    .onSuccess(data -> {
//                        Log.d("privateLetterApi", "data=" + data);
//                        Elements elements = data.select("item");
//                        for (Element element : elements) {
//                            Log.d("privateLetterApi", "element=" + element);
//                            letterInfoList.add(BeanUtils.createBean(element, PrivateLetterInfo.class));
//                        }
//                        recyclerLayout.notifyDataSetChanged();
//                    })
//                    .onError(throwable -> recyclerLayout.showErrorView(throwable.getMessage()))
//                    .subscribe();
//        } else {
//            recyclerLayout.showError();
//        }
//    }


    @Override
    protected void handleArguments(Bundle arguments) {
        super.handleArguments(arguments);
        setToolbarTitle(arguments.getString(Keys.TITLE, ""));
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
                    public void onScrollStateChanged(RecyclerView view, int scrollState) {
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

//    private void fromMsgUserLayout(final EasyViewHolder holder, final ChatMessageBean tbub, final int position) {
//        holder.getImageView(R.id.tb_other_user_icon).setBackgroundResource(R.drawable.tongbao_hiv);
//        TextView chatTime = holder.getView(R.id.chat_time);
//        /* time */
//        if (position != 0) {
//            String showTime = getTime(tbub.getTime(), letterInfoList.get(position - 1)
//                    .getTime());
//            if (showTime != null) {
//                chatTime.setVisibility(View.VISIBLE);
//                chatTime.setText(showTime);
//            } else {
//                chatTime.setVisibility(View.GONE);
//            }
//        } else {
//            String showTime = getTime(tbub.getTime(), null);
//            chatTime.setVisibility(View.VISIBLE);
//            chatTime.setText(showTime);
//        }
//        GifTextView content = holder.getView(R.id.content);
//        content.setVisibility(View.VISIBLE);
//        content.setSpanText(new Handler(), tbub.getUserContent(), isGif);
//    }
//
//    private void fromImgUserLayout(final EasyViewHolder holder, final ChatMessageBean tbub, final int position) {
//        holder.getImageView(R.id.tb_other_user_icon).setBackgroundResource(R.drawable.tongbao_hiv);
//        TextView chatTime = holder.getView(R.id.chat_time);
//        /* time */
//        if (position != 0) {
//            String showTime = getTime(tbub.getTime(), letterInfoList.get(position - 1)
//                    .getTime());
//            if (showTime != null) {
//                chatTime.setVisibility(View.VISIBLE);
//                chatTime.setText(showTime);
//            } else {
//                chatTime.setVisibility(View.GONE);
//            }
//        } else {
//            String showTime = getTime(tbub.getTime(), null);
//            chatTime.setVisibility(View.VISIBLE);
//            chatTime.setText(showTime);
//        }
////        if (isPicRefresh) {
//        final String imageSrc = tbub.getImageLocal() == null ? "" : tbub
//                .getImageLocal();
//        final String imageUrlSrc = tbub.getImageUrl() == null ? "" : tbub
//                .getImageUrl();
//        final String imageIconUrl = tbub.getImageIconUrl() == null ? ""
//                : tbub.getImageIconUrl();
//        File file = new File(imageSrc);
//        final boolean hasLocal = !imageSrc.equals("")
//                && file.exists();
//        int res;
//        res = R.drawable.chatfrom_bg_focused;
////        Glide.with(context).load(imageSrc).transform(new CustomShapeTransformation(context, res)).into(holder.image_Msg);
//        BubbleImageView ivMsg = holder.getView(R.id.image_message);
//        Glide.with(context).load(imageSrc).placeholder(R.drawable.cygs_cs).transform(new CustomShapeTransformation(context, res)).into(ivMsg);
//
//        ivMsg.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "image_Msg", Toast.LENGTH_SHORT).show();
//            }
//
//        });
////        }
//
//    }
//
//    private void toMsgUserLayout(final EasyViewHolder holder, final ChatMessageBean tbub, final int position) {
//        holder.getImageView(R.id.tb_my_user_icon).setBackgroundResource(R.mipmap.ic_launcher);
//        TextView chatTime = holder.getView(R.id.mychat_time);
//        /* time */
//        if (position != 0) {
//            String showTime = getTime(tbub.getTime(), letterInfoList.get(position - 1)
//                    .getTime());
//            if (showTime != null) {
//                chatTime.setVisibility(View.VISIBLE);
//                chatTime.setText(showTime);
//            } else {
//                chatTime.setVisibility(View.GONE);
//            }
//        } else {
//            String showTime = getTime(tbub.getTime(), null);
//            chatTime.setVisibility(View.VISIBLE);
//            chatTime.setText(showTime);
//        }
//
//        GifTextView content = holder.getView(R.id.mycontent);
//        content.setVisibility(View.VISIBLE);
//        content.setSpanText(new Handler(), tbub.getUserContent(), isGif);
//    }
//
//    private void toImgUserLayout(final EasyViewHolder holder, final ChatMessageBean tbub, final int position) {
//        holder.getImageView(R.id.tb_my_user_icon).setBackgroundResource(R.mipmap.ic_launcher);
//        ImageView ivSendFail = holder.getView(R.id.mysend_fail_img);
//        switch (tbub.getSendState()) {
//            case ChatConst.SENDING:
//                Animation an = AnimationUtils.loadAnimation(context,
//                        R.anim.update_loading_progressbar_anim);
//                LinearInterpolator lin = new LinearInterpolator();
//                an.setInterpolator(lin);
//                an.setRepeatCount(-1);
//                ivSendFail.setBackgroundResource(R.drawable.xsearch_loading);
//                ivSendFail.startAnimation(an);
//                an.startNow();
//                ivSendFail.setVisibility(View.VISIBLE);
//                break;
//
//            case ChatConst.COMPLETED:
//                ivSendFail.clearAnimation();
//                ivSendFail.setVisibility(View.GONE);
//                break;
//
//            case ChatConst.SENDERROR:
//                ivSendFail.clearAnimation();
//                ivSendFail.setBackgroundResource(R.drawable.msg_state_fail_resend_pressed);
//                ivSendFail.setVisibility(View.VISIBLE);
//                ivSendFail.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        // TODO Auto-generated method stub
////                        if (sendErrorListener != null) {
////                            sendErrorListener.onClick(position);
////                        }
//                    }
//
//                });
//                break;
//            default:
//                break;
//        }
//
//        TextView chatTime = holder.getView(R.id.mychat_time);
//        /* time */
//        if (position != 0) {
//            String showTime = getTime(tbub.getTime(), letterInfoList.get(position - 1)
//                    .getTime());
//            if (showTime != null) {
//                chatTime.setVisibility(View.VISIBLE);
//                chatTime.setText(showTime);
//            } else {
//                chatTime.setVisibility(View.GONE);
//            }
//        } else {
//            String showTime = getTime(tbub.getTime(), null);
//            chatTime.setVisibility(View.VISIBLE);
//            chatTime.setText(showTime);
//        }
//
////        if (isPicRefresh) {
//        holder.getView(R.id.image_group).setVisibility(View.VISIBLE);
//        final String imageSrc = tbub.getImageLocal() == null ? "" : tbub
//                .getImageLocal();
//        final String imageUrlSrc = tbub.getImageUrl() == null ? "" : tbub
//                .getImageUrl();
//        final String imageIconUrl = tbub.getImageIconUrl() == null ? ""
//                : tbub.getImageIconUrl();
//        File file = new File(imageSrc);
//        final boolean hasLocal = !imageSrc.equals("")
//                && file.exists();
//        int res;
//        res = R.drawable.chatto_bg_focused;
////        Glide.with(context).load(imageSrc).transform(new CustomShapeTransformation(context, res)).into(holder.image_Msg);
//
//
//        BubbleImageView ivMsg = holder.getView(R.id.image_message);
//        Glide.with(context).load(imageSrc).placeholder(R.drawable.cygs_cs).transform(new CustomShapeTransformation(context, res)).into(ivMsg);
//
//        ivMsg.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                AToast.normal("image_Msg");
//            }
//
//        });
////        }
//    }


//    private void receiveMsgText(final String content) {
//        String message = "回复：" + content;
//        ChatMessageBean tbub = new ChatMessageBean();
//        tbub.setUserName(userName);
//        String time = returnTime();
//        tbub.setUserContent(message);
//        tbub.setTime(time);
//        tbub.setType(FROM_USER_MSG);
//        tblist.add(0, tbub);
//        recyclerLayout.notifyItemInserted(0);
////            recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//        recyclerLayout.getEasyRecyclerView().getRecyclerView().smoothScrollToPosition(0);
//        tbub.save();
//    }

//    private void receiveImageText(final String filePath) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ChatMessageBean tbub = new ChatMessageBean();
//                tbub.setUserName(userName);
//                String time = returnTime();
//                tbub.setTime(time);
//                tbub.setImageLocal(filePath);
//                tbub.setType(FROM_USER_IMG);
//                tblist.add(tbub);
//                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
//                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
//                post(() -> {
//                    recyclerLayout.notifyItemInserted(0);
////                    recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//                    recyclerLayout.getEasyRecyclerView().getRecyclerView().smoothScrollToPosition(0);
//                });
//                tbub.save();
//            }
//        }).start();
//    }

//    protected void sendImage(final String filePath) {
//        // TODO 压缩图片 Bitmap bitmap = PictureUtil.compressSizeImage(path);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (i == 0) {
//                    tblist.add(0, getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
//                            0f, ChatConst.SENDING));
//                } else if (i == 1) {
//                    tblist.add(0, getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
//                            0f, ChatConst.SENDERROR));
//                } else if (i == 2) {
//                    tblist.add(0, getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
//                            0f, ChatConst.COMPLETED));
//                    i = -1;
//                }
//                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
//                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
//                post(() -> {
//                    recyclerLayout.notifyItemInserted(0);
////                    recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//                    recyclerLayout.getEasyRecyclerView().getRecyclerView().smoothScrollToPosition(0);
//                });
//                ChatFragment2.this.filePath = filePath;
//                postDelayed(() -> receiveImageText(filePath), 3000);
//                i++;
//            }
//        }).start();
//    }

    @Override
    public void sendText(String content) {
        AToast.normal("send");
//        hideSoftInput();
//        tblist.add(0, getTbub(userName, ChatRecyclerAdapter.TO_USER_MSG, content, null, null,
//                null, null, null, 0f, ChatConst.COMPLETED));
//        recyclerLayout.notifyItemInserted(0);
////        recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//        recyclerLayout.getEasyRecyclerView().getRecyclerView().smoothScrollToPosition(0);
//        postDelayed(() -> receiveMsgText(content), 1000);
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
