package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.UserInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.UserListFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.toast.ZToast;
import com.zpj.utils.Callback;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class UserPickerFragment extends BaseSwipeBackFragment {

    private static final String[] TAB_TITLES = {"乐园小编", "我关注的", "我的粉丝", "搜索用户"};
    private static final int MAX_COUNT = 5;

    private static List<UserInfo> selectedUserList;

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    private Callback<List<UserInfo>> callback;
//
//    public static void start(Callback<List<UserInfo>> callback) {
//        UserPickerFragment fragment = new UserPickerFragment();
//        fragment.callback = callback;
//        StartFragmentEvent.start(fragment);
//    }

    public static void start(Callback<String> callback) {
        UserPickerFragment fragment = new UserPickerFragment();
        fragment.callback = userInfoList -> {
            StringBuilder content = new StringBuilder();
            for (UserInfo userInfo : userInfoList) {
                content.append("@");
                content.append(userInfo.getNickName());
                content.append(" ");
            }
            callback.onCallback(content.toString());
        };
        start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_picker;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedUserList = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        callback.onCallback(new ArrayList<>(selectedUserList));
        selectedUserList.clear();
        selectedUserList = null;
        super.onDestroy();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (callback == null) {
            pop();
            return;
        }
        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);

        setToolbarTitle("选择乐友");
        postOnEnterAnimationEnd(this::initViewPager);
    }

    @Override
    public void toolbarRightTextView(@NonNull TextView view) {
        super.toolbarRightTextView(view);
        view.setOnClickListener(v -> pop());
    }

    private void initViewPager() {
        if  (selectedUserList == null) {
            selectedUserList = new ArrayList<>();
        }
        List<UserPickerChildFragment> fragments = new ArrayList<>();

        EditorPickerFragment editorPickerFragment = findChildFragment(EditorPickerFragment.class);
        if (editorPickerFragment == null) {
            editorPickerFragment = EditorPickerFragment.newInstance();
        }

        FollowerPickerFragment followersFragment = findChildFragment(FollowerPickerFragment.class);
        if (followersFragment == null) {
            followersFragment = FollowerPickerFragment.newInstance();
        }
        FansPickerFragment fansFragment = findChildFragment(FansPickerFragment.class);
        if (fansFragment == null) {
            fansFragment = FansPickerFragment.newInstance();
        }

        OthersPickerFragment othersPickerFragment = findChildFragment(OthersPickerFragment.class);
        if (othersPickerFragment == null) {
            othersPickerFragment = OthersPickerFragment.newInstance();
        }

        fragments.add(editorPickerFragment);
        fragments.add(followersFragment);
        fragments.add(fansFragment);
        fragments.add(othersPickerFragment);

        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);
    }

    public static class UserPickerChildFragment extends UserListFragment implements IEasy.OnSelectChangeListener<UserInfo> {

        @Override
        protected void buildRecyclerLayout(EasyRecyclerLayout<UserInfo> recyclerLayout) {
            super.buildRecyclerLayout(recyclerLayout);
            recyclerLayout.setMaxSelectCount(MAX_COUNT - selectedUserList.size());
            recyclerLayout.setOnSelectChangeListener(this);
        }

        @Override
        public void onSupportVisible() {
            super.onSupportVisible();
            Log.d("UserPickerChildFragment", "onSupportVisible");
            if (recyclerLayout != null) {
                recyclerLayout.setMaxSelectCount(MAX_COUNT - selectedUserList.size() + recyclerLayout.getSelectedCount());

                recyclerLayout.clearSelectedPosition();
                for (int i = 0; i < data.size(); i++) {
                    if (selectedUserList.contains(data.get(i))) {
                        recyclerLayout.addSelectedPosition(i);
                    }
                }
                if (recyclerLayout.getAdapter() != null) {
                    recyclerLayout.notifyVisibleItemChanged("easy_refresh_check_box");
                }
            }
        }

        @Override
        public void onSupportInvisible() {
            super.onSupportInvisible();
            Log.d("UserPickerChildFragment", "onSupportInvisible");
            if (recyclerLayout != null) {
                for (UserInfo userInfo : recyclerLayout.getSelectedItem()) {
                    if (!selectedUserList.contains(userInfo)) {
                        selectedUserList.add(userInfo);
                    }
                }
            }
        }

        @Override
        public void onClick(EasyViewHolder holder, View view, UserInfo data) {

        }

        @Override
        public void onSuccess(Document doc) throws Exception {
            super.onSuccess(doc);

            recyclerLayout.clearSelectedPosition();
            for (int i = 0; i < data.size(); i++) {
                if (selectedUserList.contains(data.get(i))) {
                    recyclerLayout.addSelectedPosition(i);
                }
            }

            if (!recyclerLayout.isSelectMode()) {
                recyclerLayout.enterSelectMode();
            }
        }


        @Override
        public void onSelectModeChange(boolean selectMode) {

        }

        @Override
        public void onSelectChange(List<UserInfo> list, int position, boolean isChecked) {
            UserInfo userInfo = list.get(position);
            if (isChecked) {
                selectedUserList.add(userInfo);
            } else {
                selectedUserList.remove(userInfo);
            }
        }

        @Override
        public void onSelectAll() {

        }

        @Override
        public void onUnSelectAll() {

        }

        @Override
        public void onSelectOverMax(int maxSelectCount) {
            ZToast.warning("最多只能选择" + selectedUserList.size() + "位用户");
        }


    }

    public static class EditorPickerFragment extends UserPickerChildFragment {

        public static EditorPickerFragment newInstance() {
            String url = "http://tt.tljpxm.com/app/xb_list_xml.jsp?from=aite";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            EditorPickerFragment fragment = new EditorPickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.item_user;
        }

    }

    public static class FollowerPickerFragment extends UserPickerChildFragment {

        public static FollowerPickerFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_friend_list_xml.jsp";
//            String url = "http://tt.tljpxm.com/app/view_member_friend_xml.jsp?mmid=" + UserManager.getInstance().getUserId();
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            FollowerPickerFragment fragment = new FollowerPickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class FansPickerFragment extends UserPickerChildFragment {
        public static FansPickerFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_fensi_list_xml.jsp";
//            String url = "http://tt.tljpxm.com/app/view_member_fensi_xml.jsp?mmid=" + UserManager.getInstance().getUserId();
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            FansPickerFragment fragment = new FansPickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class OthersPickerFragment extends UserPickerChildFragment {
        public static OthersPickerFragment newInstance() {
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, "");
            OthersPickerFragment fragment = new OthersPickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }



}
