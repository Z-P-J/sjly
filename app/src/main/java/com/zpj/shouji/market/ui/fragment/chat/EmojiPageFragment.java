///*
// * Copyright (c) 2015, 张涛.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.zpj.shouji.market.ui.fragment.chat;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.FrameLayout;
//import android.widget.GridView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.model.Emojicon;
//import com.zpj.shouji.market.ui.adapter.EmojiAdapter;
//import com.zpj.shouji.market.ui.widget.ChatPanel;
//import com.zpj.shouji.market.utils.EmojiDisplayRules;
//
//import java.util.List;
//
//import me.yokeyword.fragmentation.SupportFragment;
//
///**
// * Emoji表情分类的显示
// *
// * @author kymjs (http://www.kymjs.com/) on 6/8/15.
// */
//public class EmojiPageFragment extends Fragment {
//
//    private static final int ITEM_PAGE_COUNT = 28;
//
//    private ViewPager mPagerFace;
//    private LinearLayout pagePointLayout;
//
//    private Activity aty;
//    private GridView[] allPageViews;
//    private RadioButton[] pointViews;
//    private ChatPanel.OnOperationListener listener;
//
//    private List<Emojicon> datas;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        aty = getActivity();
//        View rootView = inflater.inflate(R.layout.fragment_chat_face, null);
//        datas = EmojiDisplayRules.getAllByType();
//        initView(rootView);
//        return rootView;
//    }
//
//    protected void initView(View rootView) {
//        mPagerFace = rootView.findViewById(R.id.frag_pager_face);
//        pagePointLayout = rootView.findViewById(R.id.frag_point);
//
//        int total = datas.size();
//        int pages = total / ITEM_PAGE_COUNT
//                + (total % ITEM_PAGE_COUNT == 0 ? 0 : 1);
//
//        allPageViews = new GridView[pages];
//        pointViews = new RadioButton[pages];
//
//        for (int x = 0; x < pages; x++) {
//            int start = x * ITEM_PAGE_COUNT;
//            int end = (start + ITEM_PAGE_COUNT) > total ? total
//                    : (start + ITEM_PAGE_COUNT);
//            final List<Emojicon> itemDatas = datas.subList(start, end);
//            GridView view = new GridView(aty);
//            EmojiAdapter faceAdapter = new EmojiAdapter(view, itemDatas);
//
//            view.setNumColumns(7);
//            view.setHorizontalSpacing(1);
//            view.setVerticalSpacing(1);
//            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
//            view.setCacheColorHint(0);
//            view.setPadding(2, 0, 2, 0);
//            view.setBackgroundResource(android.R.color.transparent);
//            view.setSelector(android.R.color.transparent);
//            view.setVerticalScrollBarEnabled(false);
//            view.setGravity(Gravity.CENTER);
//            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT));
//            view.setAdapter(faceAdapter);
//
//            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        int position, long id) {
//                    if (listener != null) {
//                        Emojicon emoji = itemDatas.get(position);
//                        if (EmojiDisplayRules.isDeleteEmojicon(emoji)) {
//                            listener.selectedBackSpace(emoji);
//                        } else {
//                            listener.selectedEmoji(emoji);
//                        }
//                    }
//                }
//            });
//            allPageViews[x] = view;
//
//            RadioButton tip = new RadioButton(aty);
//            tip.setBackgroundResource(R.drawable.selector_bg_tip);
//            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
//                    8, 8);
//            layoutParams.leftMargin = 10;
//            pagePointLayout.addView(tip, layoutParams);
//            if (x == 0) {
//                tip.setChecked(true);
//            }
//            pointViews[x] = tip;
//        }
//
//        PagerAdapter facePagerAdapter = new FacePagerAdapter(allPageViews);
//        mPagerFace.setAdapter(facePagerAdapter);
//        mPagerFace.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int index) {
//                pointViews[index].setChecked(true);
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//            }
//        });
//    }
//
//    public class FacePagerAdapter extends PagerAdapter {
//        private final GridView[] gridViewList;
//
//        public FacePagerAdapter(GridView[] gridViewList) {
//            this.gridViewList = gridViewList;
//        }
//
//        @Override
//        public int getCount() {
//            return gridViewList.length;
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == arg1;
//        }
//
//        @Override
//        public int getItemPosition(Object object) {
//            return super.getItemPosition(object);
//        }
//
//        @Override
//        public void destroyItem(View arg0, int arg1, Object arg2) {
//            ((ViewPager) arg0).removeView(gridViewList[arg1]);
//        }
//
//        @Override
//        public Object instantiateItem(View arg0, int arg1) {
//            ((ViewPager) arg0).addView(gridViewList[arg1]);
//            return gridViewList[arg1];
//        }
//    }
//
//    public void setOnOperationListener(ChatPanel.OnOperationListener onOperationListener) {
//        this.listener = onOperationListener;
//    }
//}
