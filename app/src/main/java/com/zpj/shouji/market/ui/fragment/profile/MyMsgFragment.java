package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.event.StartFragmentEvent;
import com.zpj.shouji.market.model.PrivateLetterInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.chat.ChatFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.shouji.market.utils.ThemeUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class MyMsgFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"我的私信", "提到我的", "收到的赞"};

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    public static void start() {
        StartFragmentEvent.start(new MyMsgFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_discover;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("我的消息");
        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        PrivateLetterFragment privateLetterFragment = findChildFragment(PrivateLetterFragment.class);
        if (privateLetterFragment == null) {
            privateLetterFragment = PrivateLetterFragment.newInstance();
        }
        MentionMeFragment mentionMeFragment = findChildFragment(MentionMeFragment.class);
        if (mentionMeFragment == null) {
            mentionMeFragment = MentionMeFragment.newInstance();
        }
        ReceivedGoodFragment receivedGoodFragment = findChildFragment(ReceivedGoodFragment.class);
        if (receivedGoodFragment == null) {
            receivedGoodFragment = ReceivedGoodFragment.newInstance();
        }
        fragments.add(privateLetterFragment);
        fragments.add(mentionMeFragment);
        fragments.add(receivedGoodFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());
        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ThemeUtils.initStatusBar(this);
    }

    @Override
    public void onDestroy() {
        HttpApi.updateFlagApi(UpdateFlagAction.GOOD);
        super.onDestroy();
    }

    public static class PrivateLetterFragment extends NextUrlFragment<PrivateLetterInfo> {

        public static PrivateLetterFragment newInstance() {
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, "http://tt.tljpxm.com/app/user_message_index_xml_v3.jsp");
            PrivateLetterFragment fragment = new PrivateLetterFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.item_private_letter;
        }

        @Override
        public void onDestroy() {
            HttpApi.updateFlagApi(UpdateFlagAction.PRIVATE);
            super.onDestroy();
        }

        @Override
        public void onClick(EasyViewHolder holder, View view, PrivateLetterInfo data) {
//        super.onClick(holder, view, data);
            ChatFragment.start(data.getSendId(), data.getNikeName());
        }

        @Override
        public PrivateLetterInfo createData(Element element) {
            return BeanUtils.createBean(element, PrivateLetterInfo.class);
        }

        @Override
        public void onBindViewHolder(EasyViewHolder holder, List<PrivateLetterInfo> list, int position, List<Object> payloads) {
            PrivateLetterInfo info = list.get(position);
            holder.setText(R.id.tv_name, info.getNikeName());
            holder.setText(R.id.tv_time, info.getTime());
            holder.setText(R.id.tv_content, info.getContent());
            ImageView img = holder.getView(R.id.iv_icon);
            Glide.with(img).load(info.getAvatar())
                    .apply(RequestOptions.circleCropTransform())
                    .into(img);
        }
    }

    public static class MentionMeFragment extends ThemeListFragment {

        public static MentionMeFragment newInstance() {
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, "http://tt.tljpxm.com/app/user_content_aite_xml_v2.jsp");
            MentionMeFragment fragment = new MentionMeFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onDestroy() {
            HttpApi.updateFlagApi(UpdateFlagAction.AT);
            super.onDestroy();
        }

    }

    public static class ReceivedGoodFragment extends ThemeListFragment {

        public static ReceivedGoodFragment newInstance() {
            String url = "http://tt.shouji.com.cn/app/user_content_flower_myself_xml_v2.jsp";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            ReceivedGoodFragment fragment = new ReceivedGoodFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
