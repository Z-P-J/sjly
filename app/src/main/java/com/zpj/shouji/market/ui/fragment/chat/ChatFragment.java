package com.zpj.shouji.market.ui.fragment.chat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.felix.atoast.library.AToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.database.ChatManager;
import com.zpj.shouji.market.model.ChatMessageBean;
import com.zpj.shouji.market.ui.animator.SlideInOutBottomItemAnimator;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.widget.PullToRefreshLayout;
import com.zpj.shouji.market.ui.widget.PullToRefreshRecyclerView;
import com.zpj.shouji.market.ui.widget.WrapContentLinearLayoutManager;
import com.zpj.shouji.market.utils.ExpandGridView;
import com.zpj.shouji.market.utils.FileSaveUtil;
import com.zpj.shouji.market.utils.KeyBoardUtils;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.shouji.market.utils.SmileUtils;
import com.zpj.utils.KeyboardHeightProvider;
import com.zpj.utils.ScreenUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends BaseFragment implements KeyboardHeightProvider.KeyboardHeightObserver {

//    public PullToRefreshLayout pullList;
    public boolean isDown = false;
    public int position; //加载滚动刷新位置
    public int bottomStatusHeight = 0;




    public EditText etEditor;
    public ImageView ivEmoji;
    public ImageView ivImage;
    public ImageView ivApp;
    public ImageView ivSend;
    public RelativeLayout rlEmojiPanel;
    public ViewPager vpEmoji;

    private RecyclerView recyclerView;
    private ChatRecyclerAdapter tbAdapter;

    public String userName = "test";//聊天对象昵称
    public String[] item = {"你好!", "我正忙着呢,等等", "有啥事吗？", "有时间聊聊吗", "再见！"};
    public List<ChatMessageBean> tblist = new ArrayList<ChatMessageBean>();
    private List<String> reslist;
    public int page = 0;
    public int number = 10;
    public List<ChatMessageBean> pagelist = new ArrayList<ChatMessageBean>();
    public ArrayList<String> imageList = new ArrayList<String>();//adapter图片数据
    public HashMap<Integer, Integer> imagePosition = new HashMap<Integer, Integer>();//图片下标位置



    String filePath = "";
    int i = 0;
    String content = "";

    private KeyboardHeightProvider keyboardHeightProvider;
    private boolean isKeyboardShowing;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public void onDestroy() {
        tblist.clear();
        tbAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(null);
        keyboardHeightProvider.close();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        //加载本地聊天记录
        page = (int) ChatManager.getPages(number);
        loadRecords();


        keyboardHeightProvider.setKeyboardHeightObserver(this);
        keyboardHeightProvider.start();

        showSoftInput(etEditor);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        bottomStatusHeight = ScreenUtil.getNavigationBarHeight(context);
        keyboardHeightProvider = new KeyboardHeightProvider(_mActivity);

//        pullList = view.findViewById(R.id.pull_to_refresh);
//        pullList.setSlideView(new PullToRefreshRecyclerView(context));
//        pullList.setpulltorefreshNotifier(this::downLoad);
        recyclerView = view.findViewById(R.id.recycler_view);
        initRecyclerView();


        etEditor = view.findViewById(R.id.et_editor);
        ivImage = view.findViewById(R.id.iv_image);
        ivEmoji = view.findViewById(R.id.iv_emoji);
        ivApp = view.findViewById(R.id.iv_app);
        ivSend = view.findViewById(R.id.iv_send);



        rlEmojiPanel = view.findViewById(R.id.rl_emoji_panel);
        vpEmoji = view.findViewById(R.id.vp_emoji);
        // 表情list
        reslist = getExpressionRes(40);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        vpEmoji.setAdapter(new ExpressionPagerAdapter(views));


        ivEmoji.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                rlEmojiPanel.setVisibility(View.VISIBLE);
                hideSoftInput();
            } else if (rlEmojiPanel.getVisibility() == View.GONE){
                rlEmojiPanel.setVisibility(View.VISIBLE);
            } else {
                rlEmojiPanel.setVisibility(View.GONE);
            }
        });
        ivSend.setOnClickListener(v -> sendMessage());
        ivImage.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                hideSoftInput();
            }
            rlEmojiPanel.setVisibility(View.GONE);
            AToast.normal("图片");
        });
        ivApp.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                hideSoftInput();
            }
            rlEmojiPanel.setVisibility(View.GONE);
            AToast.normal("app");
        });
    }

    private void initRecyclerView() {
        tbAdapter = new ChatRecyclerAdapter(context, tblist);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new SlideInOutBottomItemAnimator(recyclerView));
        recyclerView.setAdapter(tbAdapter);
        tbAdapter.isPicRefresh = true;
        tbAdapter.notifyDataSetChanged();
        tbAdapter.setSendErrorListener(position -> {
            ChatMessageBean tbub = tblist.get(position);
            if (tbub.getType() == ChatRecyclerAdapter.TO_USER_IMG) {
                sendImage(tbub.getImageLocal());
                tblist.remove(position);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                // TODO Auto-generated method stub
                switch (scrollState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(true);
                        tbAdapter.isPicRefresh = false;
                        tbAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(false);
                        tbAdapter.isPicRefresh = true;

                        rlEmojiPanel.setVisibility(View.GONE);

                        KeyBoardUtils.hideKeyBoard(context,
                                etEditor);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    protected void loadRecords() {
        isDown = true;
        if (pagelist != null) {
            pagelist.clear();
        }
        pagelist = ChatManager.loadPages(); // page, number
        position = pagelist.size();
        if (pagelist.size() != 0) {
            pagelist.addAll(tblist);
            tblist.clear();
            tblist.addAll(pagelist);
            if (imageList != null) {
                imageList.clear();
            }
            if (imagePosition != null) {
                imagePosition.clear();
            }
            int key = 0;
            int position = 0;
            for (ChatMessageBean cmb : tblist) {
                if (cmb.getType() == ChatRecyclerAdapter.FROM_USER_IMG || cmb.getType() == ChatRecyclerAdapter.TO_USER_IMG) {
                    imageList.add(cmb.getImageLocal());
                    imagePosition.put(key, position);
                    position++;
                }
                key++;
            }
            tbAdapter.setImageList(imageList);
            tbAdapter.setImagePosition(imagePosition);
            post(new Runnable() {
                @Override
                public void run() {
//                    pullList.refreshComplete();
                    tbAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(ChatFragment.this.position - 1);
                    isDown = false;
                }
            });
            if (page == 0) {
//                pullList.refreshComplete();
//                pullList.setPullGone();
            } else {
                page--;
            }
        } else {
            if (page == 0) {
//                pullList.refreshComplete();
//                pullList.setPullGone();
            }
        }
    }

    private void downLoad() {
        if (!isDown) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    loadRecords();
                }
            }).start();
        }
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(context, R.layout.layout_expression_gridview, null);
        ExpandGridView gv = view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(context,
                1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (filename != "delete_expression") { // 不是删除键，显示表情
                        // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                        @SuppressWarnings("rawtypes")
                        Class clz = Class
                                .forName("com.maxi.chatdemo.utils.SmileUtils");
                        Field field = clz.getField(filename);
                        String oriContent = etEditor.getText()
                                .toString();
                        int index = Math.max(
                                etEditor.getSelectionStart(), 0);
                        StringBuilder sBuilder = new StringBuilder(oriContent);
                        Spannable insertEmotion = SmileUtils.getSmiledText(
                                context,
                                (String) field.get(null));
                        sBuilder.insert(index, insertEmotion);
                        etEditor.setText(sBuilder.toString());
                        etEditor.setSelection(index
                                + insertEmotion.length());
                    } else { // 删除文字或者表情
                        if (!TextUtils.isEmpty(etEditor.getText())) {

                            int selectionStart = etEditor
                                    .getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = etEditor.getText()
                                        .toString();
                                String tempStr = body.substring(0,
                                        selectionStart);
                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                if (i != -1) {
                                    CharSequence cs = tempStr.substring(i,
                                            selectionStart);
                                    if (SmileUtils.containsKey(cs.toString()))
                                        etEditor.getEditableText()
                                                .delete(i, selectionStart);
                                    else
                                        etEditor.getEditableText()
                                                .delete(selectionStart - 1,
                                                        selectionStart);
                                } else {
                                    etEditor.getEditableText().delete(
                                            selectionStart - 1, selectionStart);
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "f" + x;
            reslist.add(filename);
        }
        return reslist;

    }

    private void showDialog(final String path) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // // TODO Auto-generated method stub
                try {
                    String GalPicPath = getSavePicPath();
                    Bitmap bitmap = PictureUtil.compressSizeImage(path);
                    boolean isSave = FileSaveUtil.saveBitmap(
                            PictureUtil.reviewPicRotate(bitmap, GalPicPath),
                            GalPicPath);
                    File file = new File(GalPicPath);
                    if (file.exists() && isSave) {
                        sendImage(GalPicPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private String getSavePicPath() {
        final String dir = FileSaveUtil.SD_CARD_PATH + "image_data/";
        try {
            FileSaveUtil.createSDDirectory(dir);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String fileName = System.currentTimeMillis() + ".png";
        return dir + fileName;
    }

    public ChatMessageBean getTbub(String username, int type,
                                   String Content, String imageIconUrl, String imageUrl,
                                   String imageLocal, String userVoicePath, String userVoiceUrl,
                                   Float userVoiceTime, @ChatConst.SendState int sendState) {
        ChatMessageBean tbub = new ChatMessageBean();
        tbub.setUserName(username);
        String time = returnTime();
        tbub.setTime(time);
        tbub.setType(type);
        tbub.setUserContent(Content);
        tbub.setImageIconUrl(imageIconUrl);
        tbub.setImageUrl(imageUrl);
        tbub.setUserVoicePath(userVoicePath);
        tbub.setUserVoiceUrl(userVoiceUrl);
        tbub.setUserVoiceTime(userVoiceTime);
        tbub.setSendState(sendState);
        tbub.setImageLocal(imageLocal);
        tbub.save();

        return tbub;
    }

//    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
//
//        @Override
//        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            if (keyCode == KeyEvent.KEYCODE_ENTER
//                    && event.getAction() == KeyEvent.ACTION_DOWN) {
//                sendMessage();
//                return true;
//            }
//            return false;
//        }
//    };

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }




    protected void sendMessage() {
        hideSoftInput();
        String content = etEditor.getText().toString();
        tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_MSG, content, null, null,
                null, null, null, 0f, ChatConst.COMPLETED));
        etEditor.setText("");
        tbAdapter.isPicRefresh = true;
        tbAdapter.notifyItemInserted(tblist
                .size() - 1);
        recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);

        ChatFragment.this.content = content;
        postDelay(() -> receiveMsgText(content), 1000);
    }

    private void receiveMsgText(final String content) {
        String message = "回复：" + content;
        ChatMessageBean tbub = new ChatMessageBean();
        tbub.setUserName(userName);
        String time = returnTime();
        tbub.setUserContent(message);
        tbub.setTime(time);
        tbub.setType(ChatRecyclerAdapter.FROM_USER_MSG);
        tblist.add(tbub);
        post(() -> {
            tbAdapter.isPicRefresh = true;
            tbAdapter.notifyItemInserted(tblist
                    .size() - 1);
            recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
        });
        tbub.save();
    }

    private void receiveImageText(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatMessageBean tbub = new ChatMessageBean();
                tbub.setUserName(userName);
                String time = returnTime();
                tbub.setTime(time);
                tbub.setImageLocal(filePath);
                tbub.setType(ChatRecyclerAdapter.FROM_USER_IMG);
                tblist.add(tbub);
                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                post(() -> {
                    tbAdapter.isPicRefresh = true;
                    tbAdapter.notifyItemInserted(tblist
                            .size() - 1);
                    recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
                });
                tbub.save();
            }
        }).start();
    }

    protected void sendImage(final String filePath) {
        // TODO 压缩图片 Bitmap bitmap = PictureUtil.compressSizeImage(path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                            0f, ChatConst.SENDING));
                } else if (i == 1) {
                    tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                            0f, ChatConst.SENDERROR));
                } else if (i == 2) {
                    tblist.add(getTbub(userName, ChatRecyclerAdapter.TO_USER_IMG, null, null, null, filePath, null, null,
                            0f, ChatConst.COMPLETED));
                    i = -1;
                }
                imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                post(() -> {
                    etEditor.setText("");
                    tbAdapter.isPicRefresh = true;
                    tbAdapter.notifyItemInserted(tblist
                            .size() - 1);
                    recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1);
                });
                ChatFragment.this.filePath = filePath;
                postDelay(() -> receiveImageText(filePath), 3000);
                i++;
            }
        }).start();
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        isKeyboardShowing = height > 0;
        if (height != 0) {
            rlEmojiPanel.setVisibility(View.INVISIBLE);
            rlEmojiPanel.getLayoutParams().height = height;
            rlEmojiPanel.requestLayout();
        } else {
            if (rlEmojiPanel.getVisibility() != View.VISIBLE) {
                rlEmojiPanel.setVisibility(View.GONE);
            }
        }
        if (isKeyboardShowing) {
            postDelay(() -> recyclerView.smoothScrollToPosition(tbAdapter.getItemCount() - 1), 1);
        }
    }
}
