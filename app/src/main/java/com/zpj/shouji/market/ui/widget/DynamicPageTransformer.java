//package com.zpj.shouji.market.ui.widget;
//
//import android.graphics.Rect;
//import android.support.v4.view.ViewPager;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.List;
//
//public class DynamicPageTransformer implements ViewPager.PageTransformer {
//
//    private ViewPager mPager;
//    private List<Integer> mHeightList;
//    private IndexMultiImageAdapter mAdapter;
//    private int pagerWidth;
//
//    public DynamicPageTransformer(ViewPager viewPager, List<Integer> heightList, int pagerWidth) {
//        mPager = viewPager;
//        mHeightList = heightList;
//        setDefaultHeight();
//        this.pagerWidth = pagerWidth;
//        mAdapter = (IndexMultiImageAdapter) mPager.getAdapter();
//    }
//
//    public void setDefaultHeight() {
//        ViewGroup.LayoutParams params = mPager.getLayoutParams();
//        if (mHeightList != null && mHeightList.size() > 0)
//            params.height = mHeightList.get(0);
//        mPager.setLayoutParams(params);
//    }
//
//    private int getHeight(int position) {
//        return position >= 0 && position < mHeightList.size() ? mHeightList.get(position) : 0;
//    }
//
//    float lastPosition = -1;
//    int base = 0;
//    final float valve = 0.5f;
//    final int LEFT_SLIDING = -1;
//    final int RIGHT_SLIDING = 1;
//    final int IDLE = 0;
//    int sliding_state = IDLE;
//
//    @Override
//    public void transformPage(View page, float position) {
//        if (position >= -1 && position <= 0) {
//            int curr = mPager.getCurrentItem();
//            int gap = 0;
//
//            if (getHeight(curr) > 0) {
//                if (lastPosition != 0 && lastPosition != -1 && Math.abs(lastPosition - position) < 0.5f) {
//
//                    if (lastPosition > position) { //左滑 <- 右移
//                        if (sliding_state == RIGHT_SLIDING)
//                            base = base - 1;
//                        if (getHeight(base + 1) != 0 && getHeight(base) != 0)
//                            gap = getHeight(base + 1) - getHeight(base);
//                        sliding_state = LEFT_SLIDING;
//                    } else if (lastPosition < position) {//右滑 -> 左移
//                        if (sliding_state == LEFT_SLIDING)
//                            base = base + 1;
//                        if (getHeight(base - 1) != 0 && getHeight(base) != 0)
//                            gap = getHeight(base - 1) - getHeight(base);
//                        sliding_state = RIGHT_SLIDING;
//                    }
//                }
//            }
//
//            ViewGroup.LayoutParams params = mPager.getLayoutParams();
//
//            if (position == 0 || position == -1 || Math.abs(lastPosition - position) > valve) {
//                base = curr;
//                params.height = getHeight(base);
//                sliding_state = IDLE;
//
//            } else if (gap != 0) {
//                if (Math.abs(lastPosition - position) < valve) {
//                    if (lastPosition < position) {
//                        params.height = (int) ((1 + position) * gap + getHeight(base));
//                    } else if (lastPosition > position) {
//                        params.height = (int) ((-position) * gap + getHeight(base));
//
//                    } else
//                        lastPosition = 0;
//                }
//            }
//
//            lastPosition = position;
//            mPager.setLayoutParams(params);
//
//            if (mPager.getCurrentItem() >= 0 && mPager.getCurrentItem() < mHeightList.size() && mPager.getCurrentItem() < mAdapter.mViewArray.size()) {
//                TagShowLayout itemView = mAdapter.mViewArray.get(mPager.getCurrentItem());
//                Rect rect = new Rect(0, 0, pagerWidth, mHeightList.get(mPager.getCurrentItem()));
//                itemView.setBoundary(rect);
//            }
//        }
//
//    }
//
//
//}
