package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.felix.atoast.library.AToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.shehuan.niv.NiceImageView;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.popup.ZPopup;
import com.zpj.popup.impl.AttachListPopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.chat.ChatFragment2;
import com.zpj.shouji.market.ui.widget.JudgeNestedScrollView;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.statelayout.StateLayout;
import com.zpj.widget.tinted.TintedImageView;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment
        implements View.OnClickListener,
        NestedScrollView.OnScrollChangeListener {

    private static final String USER_ID = "user_id";
    public static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/view_member_xml_v4.jsp?id=5636865";

    private static final String[] TAB_TITLES = {"动态", "收藏", "下载", "好友"};

    private StateLayout stateLayout;
    //    private ImageView ivBack;
//    private ImageView ivMenu;
    private ImageView ivHeader;
    private NiceImageView ivAvater;
    private NiceImageView ivToolbarAvater;
    private TextView tvFollow;
    private TintedImageView ivChat;
    private TextView tvName;
    private TextView tvToolbarName;
    private TextView tvInfo;
    private SmartRefreshLayout refreshLayout;
    private Toolbar mToolBar;
    private ViewPager mViewPager;
    private JudgeNestedScrollView scrollView;
    private View buttonBarLayout;
    private MagicIndicator magicIndicator;
//    private MagicIndicator magicIndicatorTitle;
    int toolBarPositionY = 0;
    private int mOffset = 0;
    private int mScrollY = 0;

    private final List<Fragment> fragments = new ArrayList<>();

    private String userId = "5544802";
    private boolean isMe;
    private boolean isFriend;

    private int lastState = 1;

    public static void start(String userId, boolean shouldLazyLoad) {
        ProfileFragment profileFragment = new ProfileFragment();
//        profileFragment.setShouldLazyLoad(shouldLazyLoad);
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        profileFragment.setArguments(bundle);
        StartFragmentEvent.start(profileFragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile2;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString(USER_ID);
        } else {
            userId = null;
        }
        if (TextUtils.isEmpty(userId)) {
            AToast.warning("用户不存在！");
            pop();
            return;
        }
        isMe = userId.equals(UserManager.getInstance().getUserId());

        stateLayout = view.findViewById(R.id.state_layout);
        tvFollow = view.findViewById(R.id.tv_follow);
        tvFollow.setOnClickListener(this);
        ivChat = view.findViewById(R.id.iv_chat);
        ivChat.setOnClickListener(this);
        if (isMe) {
            tvFollow.setText("编辑");
            tvFollow.setBackgroundResource(R.drawable.bg_button_round_gray);
        }

        ivHeader = view.findViewById(R.id.iv_header);
        ivAvater = view.findViewById(R.id.iv_avatar);
        ivToolbarAvater = view.findViewById(R.id.toolbar_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvToolbarName = view.findViewById(R.id.toolbar_name);
        tvInfo = view.findViewById(R.id.tv_info);
        refreshLayout = view.findViewById(R.id.layout_refresh);
        mToolBar = view.findViewById(R.id.layout_toolbar);

        mViewPager = view.findViewById(R.id.view_pager);
        scrollView = view.findViewById(R.id.scroll_view);
//        buttonBarLayout = view.findViewById(R.id.layout_button_bar);
        buttonBarLayout = toolbar.getCenterCustomView();
        magicIndicator = view.findViewById(R.id.magic_indicator);
//        magicIndicatorTitle = view.findViewById(R.id.magic_indicator_title);

        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                mToolBar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                mToolBar.setAlpha(1 - Math.min(percent, 1));
            }
        });

        mToolBar.post(this::dealWithViewPager);

        buttonBarLayout.setAlpha(0);
        mToolBar.setBackgroundColor(0);


        postDelayed(() -> stateLayout.showLoadingView(), 5);
//        stateLayout.showLoadingView();
        getMemberInfo();
    }

    @Override
    public void toolbarLeftImageButton(@NonNull ImageButton imageButton) {
        super.toolbarLeftImageButton(imageButton);
        imageButton.setOnClickListener(v -> pop());
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        super.toolbarRightImageButton(imageButton);
        imageButton.setOnClickListener(v -> {
            AttachListPopup<String> popup = ZPopup.attachList(context);
            popup.addItem("分享主页");
            if (!isMe) {
                popup.addItem("加入黑名单");
                popup.addItem("举报Ta");
            }
            popup.setOnSelectListener((position, title) -> {
                        switch (position) {
                            case 0:
                                WebFragment.shareHomepage(userId);
                                break;
                            case 1:
                                HttpApi.addBlacklistApi(userId);
                                break;
                            case 2:
                                AToast.warning("TODO");
                                break;
                        }
                    })
                    .show(imageButton);
        });
    }

    private void getMemberInfo() {
        HttpApi.getMemberInfoApi(userId)
                .onSuccess(element -> {
                    Log.d("onGetUserItem", "element=" + element);
                    isFriend = "1".equals(element.selectFirst("isfriend").text());
                    if (isFriend) {
                        tvFollow.setText("已关注");
                    } else {
                        ivChat.setVisibility(View.GONE);
                    }
                    String memberBackground = element.selectFirst("memberbackground").text();
                    if (!TextUtils.isEmpty(memberBackground)) {
                        Glide.with(context).load(memberBackground)
                                .apply(new RequestOptions()
                                        .error(R.drawable.bg_member_default)
                                        .placeholder(R.drawable.bg_member_default)
                                )
                                .into(ivHeader);
                    }
                    String url = element.selectFirst("memberavatar").text();
                    RequestOptions options = new RequestOptions()
                            .error(R.drawable.ic_user_head)
                            .placeholder(R.drawable.ic_user_head);
                    Glide.with(context)
                            .load(url)
                            .apply(options)
                            .into(ivAvater);
                    Glide.with(context)
                            .load(url)
                            .apply(options)
                            .into(ivToolbarAvater);

                    String nickName = element.selectFirst("nickname").text();
                    tvName.setText(nickName);
                    tvToolbarName.setText(nickName);
                    tvInfo.setText(element.selectFirst("membersignature").text());

                    postOnEnterAnimationEnd(() -> {
                        stateLayout.showContentView();
                        scrollView.setOnScrollChangeListener(ProfileFragment.this);
                        initViewPager();
                    });
                })
                .onError(throwable -> {
                    pop();
                    AToast.error(throwable.getMessage());
                })
                .subscribe();
    }

    private void initViewPager() {
        MyDynamicFragment dynamicFragment = findChildFragment(MyDynamicFragment.class);
        if (dynamicFragment == null) {
            dynamicFragment = MyDynamicFragment.newInstance(userId, false);
        }
        fragments.add(dynamicFragment);
        MyCollectionFragment collectionFragment = findChildFragment(MyCollectionFragment.class);
        if (collectionFragment == null) {
            collectionFragment = MyCollectionFragment.newInstance(userId, false);
        }
        fragments.add(collectionFragment);
        UserDownloadedFragment userDownloadedFragment = findChildFragment(UserDownloadedFragment.class);
        if (userDownloadedFragment == null) {
            userDownloadedFragment = UserDownloadedFragment.newInstance(userId);
        }
        fragments.add(userDownloadedFragment);

        MyFriendsFragment friendsFragment = findChildFragment(MyFriendsFragment.class);
        if (friendsFragment == null) {
            friendsFragment = MyFriendsFragment.newInstance(userId, false);
        }
        fragments.add(friendsFragment);
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(fragments.size());

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, mViewPager, TAB_TITLES, true);

//        CommonNavigator navigator = new CommonNavigator(getContext());
//        navigator.setAdjustMode(true);
//        navigator.setScrollPivotX(0.65f);
//        navigator.setAdapter(new CommonNavigatorAdapter() {
//            @Override
//            public int getCount() {
//                return TAB_TITLES.length;
//            }
//
//            @Override
//            public IPagerTitleView getTitleView(Context context, int index) {
//                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
//                titleView.setNormalColor(getResources().getColor(R.color.color_text_major));
//                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
//                titleView.setTextSize(14);
//                titleView.setText(TAB_TITLES[index]);
//                titleView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mViewPager.setCurrentItem(index);
//                    }
//                });
//                return titleView;
//            }
//
//            @Override
//            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
//                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
//                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
//                int color = getResources().getColor(R.color.colorPrimary);
//                indicator.setColors(color, color);
//                return indicator;
//            }
//        });
//        magicIndicator.setNavigator(navigator);
//        ViewPagerHelper.bind(magicIndicator, mViewPager);

//        dealWithViewPager();
    }

    private void dealWithViewPager() {
        toolBarPositionY = mToolBar.getHeight();
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = ScreenUtils.getScreenHeight(context) - toolBarPositionY - magicIndicator.getHeight() + 1;
        mViewPager.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        if (v == ivChat) {
            ChatFragment2.start(userId, tvName.getText().toString());
        } else if (v == tvFollow) {
            if (isMe) {
                AToast.normal("编辑");
            } else if (isFriend) {
                ZPopup.alert(context)
                        .setTitle("取消关注")
                        .setContent("确定取消关注该用户？")
                        .setConfirmButton(popup -> HttpApi.deleteFriendApi(userId)
                                .onSuccess(data -> {
                                    Log.d("deleteFriendApi", "data=" + data);
                                    String result = data.selectFirst("result").text();
                                    if ("success".equals(result)) {
                                        AToast.success("取消关注成功");
                                        tvFollow.setText("关注");
                                        ivChat.setVisibility(View.GONE);
                                        isFriend = false;
                                    } else {
                                        AToast.error(data.selectFirst("info").text());
                                    }
                                })
                                .onError(throwable -> AToast.error(throwable.getMessage()))
                                .subscribe())
                        .show();
            } else {
                HttpApi.addFriendApi(userId)
                        .onSuccess(data -> {
                            Log.d("addFriendApi", "data=" + data);
                            String result = data.selectFirst("result").text();
                            if ("success".equals(result)) {
                                AToast.success("关注成功");
                                tvFollow.setText("已关注");
                                ivChat.setVisibility(View.VISIBLE);
                                isFriend = true;
                            } else {
                                AToast.error(data.selectFirst("info").text());
                            }
                        })
                        .onError(throwable -> AToast.error(throwable.getMessage()))
                        .subscribe();
            }
        }
    }


    private int lastScrollY = 0;
//    private int h = DensityUtil.dp2px(100);
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        Log.d("onScrollChange", "scrollX=" + scrollX + " scrollY=" + scrollY + " oldScrollX=" + oldScrollX + " oldScrollY=" + oldScrollY);
        int[] location = new int[2];
        magicIndicator.getLocationOnScreen(location);
        int yPosition = location[1];
        if (yPosition < toolBarPositionY) {
            scrollView.setNeedScroll(false);
        } else {
            scrollView.setNeedScroll(true);
        }

        int h = ScreenUtils.dp2pxInt(context, 100);
        if (lastScrollY < h) {
            scrollY = Math.min(h, scrollY);
            mScrollY = Math.min(scrollY, h);
            buttonBarLayout.setAlpha(1f * mScrollY / h);
            mToolBar.setBackgroundColor(((255 * mScrollY / h) << 24) | (ContextCompat.getColor(context, R.color.colorPrimary) & 0x00ffffff));
            ivHeader.setTranslationY(mOffset - mScrollY);
        }
//                if (scrollY == 0) {
//                    ivBack.setImageResource(R.drawable.ic_back);
//                    ivMenu.setImageResource(R.drawable.ic_more_vert_grey_24dp);
//                } else {
//                    ivBack.setImageResource(R.drawable.ic_back);
//                    ivMenu.setImageResource(R.drawable.ic_more_vert_grey_24dp);
//                }

        lastScrollY = scrollY;
    }
}
