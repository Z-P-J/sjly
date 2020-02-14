//package com.zpj.shouji.market.ui.fragment.chat;
//
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.ViewTreeObserver;
//
//import com.zpj.shouji.market.database.ChatManager;
//import com.zpj.shouji.market.model.ChatMessageBean;
//import com.zpj.shouji.market.ui.animator.SlideInOutBottomItemAnimator;
//import com.zpj.shouji.market.ui.widget.PullToRefreshRecyclerView;
//import com.zpj.shouji.market.ui.widget.WrapContentLinearLayoutManager;
//import com.zpj.shouji.market.utils.KeyBoardUtils;
//
//import java.lang.ref.WeakReference;
//
//public class RecyclerViewChatActivity extends BaseActivity {
//    private PullToRefreshRecyclerView myList;
//    private ChatRecyclerAdapter tbAdapter;
//    private SendMessageHandler sendMessageHandler;
//    private WrapContentLinearLayoutManager wcLinearLayoutManger;
//
//    @Override
//    protected void findView() {
//        super.findView();
//        pullList.setSlideView(new PullToRefreshRecyclerView(this));
//        myList = (PullToRefreshRecyclerView) pullList.returnMylist();
//    }
//
//    @Override
//    protected void onDestroy() {
//        tblist.clear();
//        tbAdapter.notifyDataSetChanged();
//        myList.setAdapter(null);
//        sendMessageHandler.removeCallbacksAndMessages(null);
//        super.onDestroy();
//    }
//
//    @Override
//    protected void init() {
//        setTitle("RecyclerView");
//        tbAdapter = new ChatRecyclerAdapter(this, tblist);
//        wcLinearLayoutManger = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        myList.setLayoutManager(wcLinearLayoutManger);
//        myList.setItemAnimator(new SlideInOutBottomItemAnimator(myList));
//        myList.setAdapter(tbAdapter);
//        sendMessageHandler = new SendMessageHandler(this);
//        tbAdapter.isPicRefresh = true;
//        tbAdapter.notifyDataSetChanged();
//        tbAdapter.setSendErrorListener(new ChatRecyclerAdapter.SendErrorListener() {
//
//            @Override
//            public void onClick(int position) {
//                // TODO Auto-generated method stub
//                ChatMessageBean tbub = tblist.get(position);
//                if (tbub.getType() == ChatRecyclerAdapter.TO_USER_IMG) {
//                    sendImage(tbub.getImageLocal());
//                    tblist.remove(position);
//                }
//            }
//
//        });
//        myList.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
//                        reset();
//                        KeyBoardUtils.hideKeyBoard(RecyclerViewChatActivity.this,
//                                etEditor);
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
//        controlKeyboardLayout(activityRootView, pullList);
//        super.init();
//    }
//
//    /**
//     * @param root             最外层布局
//     * @param needToScrollView 要滚动的布局,就是说在键盘弹出的时候,你需要试图滚动上去的View,在键盘隐藏的时候,他又会滚动到原来的位置的布局
//     */
//    private void controlKeyboardLayout(final View root, final View needToScrollView) {
//        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            private Rect r = new Rect();
//
//            @Override
//            public void onGlobalLayout() {
//                //获取当前界面可视部分
//                RecyclerViewChatActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
//                //获取屏幕的高度
//                int screenHeight = RecyclerViewChatActivity.this.getWindow().getDecorView().getRootView().getHeight();
//                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
//                int heightDifference = screenHeight - r.bottom;
//                int recyclerHeight = 0;
//                if (wcLinearLayoutManger != null) {
//                    recyclerHeight = wcLinearLayoutManger.getRecyclerHeight();
//                }
//                if (heightDifference == 0 || heightDifference == bottomStatusHeight) {
//                    needToScrollView.scrollTo(0, 0);
//                } else {
//                    if (heightDifference < recyclerHeight) {
//                        int contentHeight = wcLinearLayoutManger == null ? 0 : wcLinearLayoutManger.getHeight();
//                        if (recyclerHeight < contentHeight) {
//                            listSlideHeight = heightDifference - (contentHeight - recyclerHeight);
//                            needToScrollView.scrollTo(0, listSlideHeight);
//                        } else {
//                            listSlideHeight = heightDifference;
//                            needToScrollView.scrollTo(0, listSlideHeight);
//                        }
//                    } else {
//                        listSlideHeight = 0;
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void loadRecords() {
//        isDown = true;
//        if (pagelist != null) {
//            pagelist.clear();
//        }
//        pagelist = ChatManager.loadPages(page, number);
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
//            sendMessageHandler.sendEmptyMessage(PULL_TO_REFRESH_DOWN);
//            if (page == 0) {
//                pullList.refreshComplete();
//                pullList.setPullGone();
//            } else {
//                page--;
//            }
//        } else {
//            if (page == 0) {
//                pullList.refreshComplete();
//                pullList.setPullGone();
//            }
//        }
//    }
//
//    static class SendMessageHandler extends Handler {
//        WeakReference<RecyclerViewChatActivity> mActivity;
//
//        SendMessageHandler(RecyclerViewChatActivity activity) {
//            mActivity = new WeakReference<RecyclerViewChatActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            RecyclerViewChatActivity theActivity = mActivity.get();
//            if (theActivity != null) {
//                switch (msg.what) {
//                    case REFRESH:
//                        theActivity.tbAdapter.isPicRefresh = true;
//                        theActivity.tbAdapter.notifyDataSetChanged();
//                        int position = theActivity.tbAdapter.getItemCount() - 1 < 0 ? 0 : theActivity.tbAdapter.getItemCount() - 1;
//                        theActivity.myList.smoothScrollToPosition(position);
//                        break;
//                    case SEND_OK:
//                        theActivity.etEditor.setText("");
//                        theActivity.tbAdapter.isPicRefresh = true;
//                        theActivity.tbAdapter.notifyItemInserted(theActivity.tblist
//                                .size() - 1);
//                        theActivity.myList.smoothScrollToPosition(theActivity.tbAdapter.getItemCount() - 1);
//                        break;
//                    case RECERIVE_OK:
//                        theActivity.tbAdapter.isPicRefresh = true;
//                        theActivity.tbAdapter.notifyItemInserted(theActivity.tblist
//                                .size() - 1);
//                        theActivity.myList.smoothScrollToPosition(theActivity.tbAdapter.getItemCount() - 1);
//                        break;
//                    case PULL_TO_REFRESH_DOWN:
//                        theActivity.pullList.refreshComplete();
//                        theActivity.tbAdapter.notifyDataSetChanged();
//                        theActivity.myList.smoothScrollToPosition(theActivity.position - 1);
//                        theActivity.isDown = false;
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//
//    }
//
//    /**
//     * 发送文字
//     */
//    @Override
//    protected void sendMessage() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String content = etEditor.getText().toString();
//                tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_MSG, content, null, null,
//                        null, null, null, 0f, ChatConst.COMPLETED));
//                sendMessageHandler.sendEmptyMessage(SEND_OK);
//                RecyclerViewChatActivity.this.content = content;
//                receriveHandler.sendEmptyMessageDelayed(0, 1000);
//            }
//        }).start();
//    }
//
//    /**
//     * 接收文字
//     */
//    String content = "";
//
//    private void receriveMsgText(final String content) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String message = "回复：" + content;
//                ChatMessageBean tbub = new ChatMessageBean();
//                tbub.setUserName(userName);
//                String time = returnTime();
//                tbub.setUserContent(message);
//                tbub.setTime(time);
//                tbub.setType(ChatRecyclerAdapter.FROM_USER_MSG);
//                tblist.add(tbub);
//                sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
//                tbub.save();
//            }
//        }).start();
//    }
//
//    /**
//     * 发送图片
//     */
//    int i = 0;
//
//    @Override
//    protected void sendImage(final String filePath) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (i == 0) {
//                    tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
//                            0f, ChatConst.SENDING));
//                } else if (i == 1) {
//                    tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
//                            0f, ChatConst.SENDERROR));
//                } else if (i == 2) {
//                    tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
//                            0f, ChatConst.COMPLETED));
//                    i = -1;
//                }
//                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
//                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
//                sendMessageHandler.sendEmptyMessage(SEND_OK);
//                RecyclerViewChatActivity.this.filePath = filePath;
//                receriveHandler.sendEmptyMessageDelayed(1, 3000);
//                i++;
//            }
//        }).start();
//    }
//
//    /**
//     * 接收图片
//     */
//    String filePath = "";
//
//    private void receriveImageText(final String filePath) {
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
//                sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
//                tbub.save();
//            }
//        }).start();
//    }
//
//    /**
//     * 为了模拟接收延迟
//     */
//    private Handler receriveHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    receriveMsgText(content);
//                    break;
//                case 1:
//                    receriveImageText(filePath);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//}
