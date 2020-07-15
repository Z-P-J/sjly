//package com.zpj.shouji.market.ui.fragment.chat;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//
//import com.felix.atoast.library.AToast;
//import com.zpj.matisse.Matisse;
//import com.zpj.popup.util.ActivityUtils;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.database.ChatManager;
//import com.zpj.shouji.market.event.StartFragmentEvent;
//import com.zpj.shouji.market.model.ChatMessageBean;
//import com.zpj.shouji.market.ui.adapter.ChatRecyclerAdapter;
//import com.zpj.shouji.market.ui.animator.SlideInOutBottomItemAnimator;
//import com.zpj.fragmentation.BaseFragment;
//import com.zpj.shouji.market.ui.widget.ChatPanel;
//import com.zpj.shouji.market.ui.widget.WrapContentLinearLayoutManager;
//import com.zpj.utils.KeyboardHeightProvider;
//import com.zpj.utils.ScreenUtils;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//
//public class ChatFragment extends BaseFragment implements ChatPanel.OnOperationListener {
//
////    public PullToRefreshLayout pullList;
//    public boolean isDown = false;
//    public int position; //加载滚动刷新位置
//    public int bottomStatusHeight = 0;
//
//    private ChatPanel chatPanel;
//
//    private RecyclerView recyclerView;
//    private ChatRecyclerAdapter tbAdapter;
//
//    public String userName = "test";//聊天对象昵称
//    public String[] item = {"你好!", "我正忙着呢,等等", "有啥事吗？", "有时间聊聊吗", "再见！"};
//    public final List<ChatMessageBean> tblist = new ArrayList<ChatMessageBean>();
//    private List<String> reslist;
//    public int page = 0;
//    public int number = 10;
//    public List<ChatMessageBean> pagelist = new ArrayList<ChatMessageBean>();
//    public ArrayList<String> imageList = new ArrayList<String>();//adapter图片数据
//    public HashMap<Integer, Integer> imagePosition = new HashMap<Integer, Integer>();//图片下标位置
//
//
//
//    String filePath = "";
//    int i = 0;
//
////    private KeyboardHeightProvider keyboardHeightProvider;
//
//    public static void start() {
//        StartFragmentEvent.start(new ChatFragment());
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_chat;
//    }
//
//    @Override
//    protected boolean supportSwipeBack() {
//        return true;
//    }
//
//    @Override
//    public void onDestroy() {
//        tblist.clear();
//        tbAdapter.notifyDataSetChanged();
//        recyclerView.setAdapter(null);
////        keyboardHeightProvider.close();
//        Matisse.onDestroy();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
////        keyboardHeightProvider.setKeyboardHeightObserver(chatPanel);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
////        keyboardHeightProvider.setKeyboardHeightObserver(null);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        hideSoftInput();
//    }
//
//    @Override
//    public void onEnterAnimationEnd(Bundle savedInstanceState) {
//        super.onEnterAnimationEnd(savedInstanceState);
//        //加载本地聊天记录
//        page = (int) ChatManager.getPages(number);
//
//
////        keyboardHeightProvider.setKeyboardHeightObserver(chatPanel);
////        keyboardHeightProvider.start();
//
//        loadRecords();
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        bottomStatusHeight = ScreenUtils.getNavigationBarHeight(context);
////        keyboardHeightProvider = new KeyboardHeightProvider(_mActivity);
//
//        recyclerView = view.findViewById(R.id.recycler_view);
//        initRecyclerView();
//
//        chatPanel = view.findViewById(R.id.chat_panel);
//        chatPanel.setOnOperationListener(this);
//
//        com.zpj.popup.util.KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
//            chatPanel.onKeyboardHeightChanged(height, 0);
//        });
//
//    }
//
//    private void initRecyclerView() {
//        tbAdapter = new ChatRecyclerAdapter(context, tblist);
//        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
//        recyclerView.setItemAnimator(new SlideInOutBottomItemAnimator(recyclerView));
//        recyclerView.setAdapter(tbAdapter);
//        tbAdapter.isPicRefresh = true;
//        tbAdapter.notifyDataSetChanged();
//        tbAdapter.setSendErrorListener(position -> {
//            ChatMessageBean tbub = tblist.get(position);
//            if (tbub.getType() == ChatRecyclerAdapter.TO_USER_IMG) {
//                sendImage(tbub.getImageLocal());
//                tblist.remove(position);
//            }
//        });
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(RecyclerView view, int scrollState) {
//                // TODO Auto-generated method stub
//                switch (scrollState) {
//                    case RecyclerView.SCROLL_STATE_IDLE:
//                        tbAdapter.handler.removeCallbacksAndMessages(null);
//                        tbAdapter.setIsGif(true);
//                        tbAdapter.isPicRefresh = false;
//                        tbAdapter.notifyDataSetChanged();
//                        break;
//                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        tbAdapter.handler.removeCallbacksAndMessages(null);
//                        tbAdapter.setIsGif(false);
//                        tbAdapter.isPicRefresh = true;
//
//                        chatPanel.hideEmojiPanel();
//
//                        hideSoftInput();
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
//    }
//
//    protected void loadRecords() {
//        isDown = true;
//        if (pagelist != null) {
//            pagelist.clear();
//        }
//        pagelist = ChatManager.loadPages(); // page, number
//        Collections.reverse(pagelist);
//        position = pagelist.size();
//        if (pagelist.size() != 0) {
//            pagelist.addAll(tblist);
//            tblist.clear();
//            tblist.addAll(pagelist);
//            if (imageList != null) {
//                imageList.clear();
//            }
//            if (imagePosition != null) {
//                imagePosition.clear();
//            }
//            int key = 0;
//            int position = 0;
//            for (ChatMessageBean cmb : tblist) {
//                if (cmb.getType() == ChatRecyclerAdapter.FROM_USER_IMG || cmb.getType() == ChatRecyclerAdapter.TO_USER_IMG) {
//                    imageList.add(cmb.getImageLocal());
//                    imagePosition.put(key, position);
//                    position++;
//                }
//                key++;
//            }
//            tbAdapter.setImageList(imageList);
//            tbAdapter.setImagePosition(imagePosition);
//            post(new Runnable() {
//                @Override
//                public void run() {
////                    pullList.refreshComplete();
//                    tbAdapter.notifyDataSetChanged();
////                    recyclerView.smoothScrollToPosition(ChatFragment.this.position - 1);
//                    isDown = false;
//                }
//            });
//            if (page == 0) {
////                pullList.refreshComplete();
////                pullList.setPullGone();
//            } else {
//                page--;
//            }
//        } else {
//            if (page == 0) {
////                pullList.refreshComplete();
////                pullList.setPullGone();
//            }
//        }
//    }
//
//    private void downLoad() {
//        if (!isDown) {
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    loadRecords();
//                }
//            }).start();
//        }
//    }
//
//    public ChatMessageBean getTbub(String username, int type,
//                                   String Content, String imageIconUrl, String imageUrl,
//                                   String imageLocal, String userVoicePath, String userVoiceUrl,
//                                   Float userVoiceTime, @ChatConst.SendState int sendState) {
//        ChatMessageBean tbub = new ChatMessageBean();
//        tbub.setUserName(username);
//        String time = returnTime();
//        tbub.setTime(time);
//        tbub.setType(type);
//        tbub.setUserContent(Content);
//        tbub.setImageIconUrl(imageIconUrl);
//        tbub.setImageUrl(imageUrl);
//        tbub.setUserVoicePath(userVoicePath);
//        tbub.setUserVoiceUrl(userVoiceUrl);
//        tbub.setUserVoiceTime(userVoiceTime);
//        tbub.setSendState(sendState);
//        tbub.setImageLocal(imageLocal);
//        tbub.save();
//
//        return tbub;
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    public static String returnTime() {
//        SimpleDateFormat sDateFormat = new SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss");
//        String date = sDateFormat.format(new java.util.Date());
//        return date;
//    }
//
//
//
//
//    private void receiveMsgText(final String content) {
//        String message = "回复：" + content;
//        ChatMessageBean tbub = new ChatMessageBean();
//        tbub.setUserName(userName);
//        String time = returnTime();
//        tbub.setUserContent(message);
//        tbub.setTime(time);
//        tbub.setType(ChatRecyclerAdapter.FROM_USER_MSG);
//        tblist.add(0, tbub);
//        tbAdapter.isPicRefresh = true;
//        tbAdapter.notifyItemInserted(0);
////            recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//        recyclerView.smoothScrollToPosition(0);
//        tbub.save();
//    }
//
//    private void receiveImageText(final String filePath) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ChatMessageBean tbub = new ChatMessageBean();
//                tbub.setUserName(userName);
//                String time = returnTime();
//                tbub.setTime(time);
//                tbub.setImageLocal(filePath);
//                tbub.setType(ChatRecyclerAdapter.FROM_USER_IMG);
//                tblist.add(tbub);
//                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
//                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
//                post(() -> {
//                    tbAdapter.isPicRefresh = true;
//                    tbAdapter.notifyItemInserted(0);
////                    recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//                    recyclerView.smoothScrollToPosition(0);
//                });
//                tbub.save();
//            }
//        }).start();
//    }
//
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
//                    tbAdapter.isPicRefresh = true;
//                    tbAdapter.notifyItemInserted(0);
////                    recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//                    recyclerView.smoothScrollToPosition(0);
//                });
//                ChatFragment.this.filePath = filePath;
//                postDelayed(() -> receiveImageText(filePath), 3000);
//                i++;
//            }
//        }).start();
//    }
//
//    @Override
//    public void sendText(String content) {
//        hideSoftInput();
//        tblist.add(0, getTbub(userName, ChatRecyclerAdapter.TO_USER_MSG, content, null, null,
//                null, null, null, 0f, ChatConst.COMPLETED));
//        tbAdapter.isPicRefresh = true;
//        tbAdapter.notifyItemInserted(0);
////        recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
//        recyclerView.smoothScrollToPosition(0);
//        postDelayed(() -> receiveMsgText(content), 1000);
//    }
//
//    @Override
//    public void onEmojiSelected(String key) {
//
//    }
//
//    @Override
//    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
//        AToast.normal("categoryName=" + categoryName + " stickerName=" + stickerName);
//        sendImage(stickerBitmapPath);
//    }
//}
