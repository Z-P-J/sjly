package it.liuting.imagetrans;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import it.liuting.imagetrans.listener.OnTransformListener;

/**
 * Created by liuting on 18/3/15.
 */
class DialogView extends FrameLayout implements OnTransformListener {

    private ImageTransBuild build;
    private InterceptViewPager viewPager;
    private ImagePagerAdapter mAdapter;
    private boolean isOpened = false;

    DialogView(Context context, ImageTransBuild build) {
        super(context);
        this.build = build;
    }

    void onCreate(DialogInterface dialogInterface) {
        viewPager = new InterceptViewPager(getContext());
        addView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mAdapter = new ImagePagerAdapter(build.imageList);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(build.clickIndex);
        View maskView = build.imageTransAdapter.onCreateView(this, viewPager, dialogInterface);
        if (maskView != null) {
            addView(maskView);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                build.nowIndex = position;
                build.imageTransAdapter.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void onDismiss(Dialog dialog) {
        ImageItemView itemView = mAdapter.getItemView(build.nowIndex);
        if (itemView != null) itemView.onDismiss();
        else dialog.dismiss();
    }


    @Override
    public void transformStart() {
        build.imageTransAdapter.onOpenTransStart();
        viewPager.setCanScroll(false);
    }

    @Override
    public void transformEnd() {
        isOpened = true;
        build.imageTransAdapter.onOpenTransEnd();
        viewPager.setCanScroll(true);
        mAdapter.loadWhenTransEnd();
    }

    class ImagePagerAdapter extends PagerAdapter {

        private List<String> mData;
        private SparseArray<ImageItemView> itemViewSparseArray;

        public ImagePagerAdapter(@NonNull List<String> data) {
            mData = data;
            itemViewSparseArray = new SparseArray<>();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageItemView view = itemViewSparseArray.get(position);
            if (view == null) {
                view = new ImageItemView(container.getContext(), build, position, mData.get(position));
                if (build.needTransOpen(position, false)) {
                    view.bindTransOpenListener(DialogView.this);
                }
                view.init(isOpened);
                itemViewSparseArray.put(position, view);
            }
            container.addView(view);
            return view;
        }

        public ImageItemView getItemView(int pos) {
            return itemViewSparseArray.get(pos);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            itemViewSparseArray.remove(position);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void loadWhenTransEnd() {
            for (int i = 0; i < itemViewSparseArray.size(); i++) {
                ImageItemView itemView = itemViewSparseArray.valueAt(i);
                if (itemView != null) itemView.loadImageWhenTransEnd();
            }
        }
    }
}
