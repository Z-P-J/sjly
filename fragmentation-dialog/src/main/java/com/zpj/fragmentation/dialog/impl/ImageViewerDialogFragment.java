package com.zpj.fragmentation.dialog.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.ChangeTransform;
import android.support.transition.Transition;
import android.support.transition.TransitionListenerAdapter;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.popup.R;
import com.zpj.popup.XPopup;
import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.core.BasePopup;
import com.zpj.popup.enums.PopupStatus;
import com.zpj.popup.interfaces.IImageLoader;
import com.zpj.popup.interfaces.OnDragChangeListener;
import com.zpj.popup.photoview.PhotoView;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.popup.widget.BlankView;
import com.zpj.popup.widget.HackyViewPager;
import com.zpj.popup.widget.PhotoViewContainer;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Description: 大图预览的弹窗，使用Transition实现
 * Create by lxj, at 2019/1/22
 */
public class ImageViewerDialogFragment<T> extends BaseDialogFragment
        implements OnDragChangeListener, View.OnClickListener {

    protected FrameLayout container;
    protected PhotoViewContainer photoViewContainer;
    protected BlankView placeholderView;
    protected TextView tv_pager_indicator, tv_save;
    protected HackyViewPager pager;
    protected ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    protected final List<T> urls = new ArrayList<>();
    protected IImageLoader<T> imageLoader;
    protected OnSrcViewUpdateListener<T> srcViewUpdateListener;
    protected int position;
    protected Rect rect = null;
    protected ImageView srcView; //动画起始的View，如果为null，移动和过渡动画效果会没有，只有弹窗的缩放功能
    protected PhotoView snapshotView;
    protected boolean isShowPlaceholder = false; //是否显示占位白色，当图片切换为大图时，原来的地方会有一个白色块
    protected int placeholderColor = -1; //占位View的颜色
    protected int placeholderStrokeColor = -1; // 占位View的边框色
    protected int placeholderRadius = -1; // 占位View的圆角
    protected boolean isShowSaveBtn = true; //是否显示保存按钮
    protected boolean isShowIndicator = true; //是否页码指示器
    protected boolean isInfinite = false;//是否需要无限滚动
    protected View customView;
    protected int bgColor = Color.rgb(32, 36, 46);//弹窗的背景颜色，可以自定义

    public PopupStatus popupStatus = PopupStatus.Dismiss;

    public interface OnSrcViewUpdateListener<T> {
        void onSrcViewUpdate(@NonNull ImageViewerDialogFragment<T> popup, int position);
    }

    @Override
    protected final int getImplLayoutId() {
        return R.layout._xpopup_image_viewer_popup_view;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected PopupAnimator getShadowAnimator(FrameLayout flContainer) {
        return null;
    }

    protected int getCustomLayoutId() {
        return 0;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        if (srcView != null) {
            int[] locations = new int[2];
            this.srcView.getLocationInWindow(locations);
//            int offset;
//            if (getActivity() != null && (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    & getActivity().getWindow().getAttributes().flags)
//                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
//                offset = 0;
//            } else {
//                offset = ScreenUtils.getStatusBarHeight(context);
//            }
//            locations[1] = locations[1] - offset;
            rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
        }

        container = findViewById(R.id.container);
        if (getCustomLayoutId() > 0) {
            customView = LayoutInflater.from(getContext()).inflate(getCustomLayoutId(), container, false);
            customView.setVisibility(View.INVISIBLE);
            customView.setAlpha(0);
            container.addView(customView);
        }


        tv_pager_indicator = findViewById(R.id.tv_pager_indicator);
        tv_save = findViewById(R.id.tv_save);
        placeholderView = findViewById(R.id.placeholderView);
        photoViewContainer = findViewById(R.id.photoViewContainer);
        photoViewContainer.setOnDragChangeListener(this);
        photoViewContainer.setFocusableInTouchMode(true);
        photoViewContainer.setFocusable(true);
        photoViewContainer.setClickable(true);
        pager = findViewById(R.id.pager);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                position = i;
                showPagerIndicator();
                //更新srcView
                if (srcViewUpdateListener != null) {
                    srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment.this, i);
                }
            }
        });
        pager.setAdapter(new PhotoViewAdapter());
//        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(position);
        pager.setVisibility(View.INVISIBLE);
        addOrUpdateSnapshot();
//        if (isInfinite) pager.setOffscreenPageLimit(urls.size() / 2);
        if (!isShowIndicator) tv_pager_indicator.setVisibility(View.GONE);
        if (!isShowSaveBtn) {
            tv_save.setVisibility(View.GONE);
        } else {
            tv_save.setOnClickListener(this);
        }
    }

    @Override
    public void doShowAnimation() {
        if (customView != null) customView.setVisibility(View.VISIBLE);
        if (srcView == null || popupStatus == PopupStatus.Hide) {
            photoViewContainer.setBackgroundColor(bgColor);
            pager.setVisibility(View.VISIBLE);
            showPagerIndicator();
            photoViewContainer.isReleasing = false;
            doAfterShow();
            if (customView != null)
                customView.setAlpha(1f);
            return;
        }
        photoViewContainer.isReleasing = true;
        snapshotView.setVisibility(View.VISIBLE);
        snapshotView.post(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
                        .setDuration(XPopup.getAnimationDuration())
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeTransform())
                        .addTransition(new ChangeImageTransform())
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .addListener(new TransitionListenerAdapter() {
                            @Override
                            public void onTransitionEnd(@NonNull Transition transition) {
                                pager.setVisibility(View.VISIBLE);
                                snapshotView.setVisibility(View.INVISIBLE);
                                showPagerIndicator();
                                photoViewContainer.isReleasing = false;
                                doAfterShow();
                            }
                        }));
                snapshotView.setTranslationY(0);
                snapshotView.setTranslationX(0);
                snapshotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                XPopupUtils.setWidthHeight(snapshotView, photoViewContainer.getWidth(), photoViewContainer.getHeight());

                // do shadow anim.
                animateShadowBg(bgColor);
                if (customView != null)
                    customView.animate().alpha(1f).setDuration(XPopup.getAnimationDuration()).start();
            }
        });

    }

    private void setupPlaceholder() {
        placeholderView.setVisibility(isShowPlaceholder ? View.VISIBLE : View.INVISIBLE);
        if (isShowPlaceholder) {
            if (placeholderColor != -1) {
                placeholderView.color = placeholderColor;
            }
            if (placeholderRadius != -1) {
                placeholderView.radius = placeholderRadius;
            }
            if (placeholderStrokeColor != -1) {
                placeholderView.strokeColor = placeholderStrokeColor;
            }
            XPopupUtils.setWidthHeight(placeholderView, rect.width(), rect.height());
            placeholderView.setTranslationX(rect.left);
            placeholderView.setTranslationY(rect.top);
            placeholderView.invalidate();
        }
    }

    private void showPagerIndicator() {
        if (urls.size() > 1) {
            int posi = isInfinite ? position % urls.size() : position;
            tv_pager_indicator.setText((posi + 1) + "/" + urls.size());
        }
        if (isShowSaveBtn) tv_save.setVisibility(View.VISIBLE);
    }

    private void addOrUpdateSnapshot() {
        if (srcView == null) return;
        if (snapshotView == null) {
            snapshotView = new PhotoView(getContext());
            photoViewContainer.addView(snapshotView);
            snapshotView.setScaleType(srcView.getScaleType());
            snapshotView.setTranslationX(rect.left);
            snapshotView.setTranslationY(rect.top);
            XPopupUtils.setWidthHeight(snapshotView, rect.width(), rect.height());
        }
        setupPlaceholder();
        snapshotView.setImageDrawable(srcView.getDrawable());
    }

    private void animateShadowBg(final int endColor) {
        final int start = ((ColorDrawable) photoViewContainer.getBackground()).getColor();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                photoViewContainer.setBackgroundColor((Integer) argbEvaluator.evaluate(animation.getAnimatedFraction(),
                        start, endColor));
            }
        });
        animator.setDuration(XPopup.getAnimationDuration())
                .setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @Override
    public void doDismissAnimation() {
        if (srcView == null) {
            photoViewContainer.setBackgroundColor(Color.TRANSPARENT);
            doAfterDismiss();
            pager.setVisibility(View.INVISIBLE);
            placeholderView.setVisibility(View.INVISIBLE);
            return;
        }
        tv_pager_indicator.setVisibility(View.INVISIBLE);
        tv_save.setVisibility(View.INVISIBLE);
        pager.setVisibility(View.INVISIBLE);
        snapshotView.setVisibility(View.VISIBLE);
        photoViewContainer.isReleasing = true;
        Log.d("ImageViewerPopup", "snapshotView.getParent()=" + snapshotView.getParent());
        TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
                .setDuration(XPopup.getAnimationDuration())
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform())
                .setInterpolator(new FastOutSlowInInterpolator())
                .addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
                        doAfterDismiss();
                        pager.setVisibility(View.INVISIBLE);
                        snapshotView.setVisibility(View.VISIBLE);
                        pager.setScaleX(1f);
                        pager.setScaleY(1f);
                        snapshotView.setScaleX(1f);
                        snapshotView.setScaleY(1f);
                        placeholderView.setVisibility(View.INVISIBLE);
                    }
                }));

        snapshotView.setTranslationY(rect.top);
        snapshotView.setTranslationX(rect.left);
        snapshotView.setScaleX(1f);
        snapshotView.setScaleY(1f);
        snapshotView.setScaleType(srcView.getScaleType());
        XPopupUtils.setWidthHeight(snapshotView, rect.width(), rect.height());

        // do shadow anim.
        animateShadowBg(Color.TRANSPARENT);
        if (customView != null)
            customView.animate().alpha(0f).setDuration(XPopup.getAnimationDuration())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (customView != null) customView.setVisibility(View.INVISIBLE);
                        }
                    })
                    .start();
    }

    @Override
    public void dismiss() {
        if (popupStatus != PopupStatus.Show) return;
        popupStatus = PopupStatus.Dismissing;
        if (srcView != null) {
            //snapshotView拥有当前pager中photoView的样子(matrix)
//            PhotoView current = (PhotoView) pager.getChildAt(pager.getCurrentItem());
            PhotoView current = pager.findViewWithTag(pager.getCurrentItem());
            if (current != null) {
                Matrix matrix = new Matrix();
                current.getSuppMatrix(matrix);
                snapshotView.setSuppMatrix(matrix);
            }
        }
        doDismissAnimation();
        getSupportDelegate().pop();
    }

    public ImageViewerDialogFragment<T> setImageUrls(List<T> urls) {
        this.urls.addAll(urls);
        return this;
    }

    public ImageViewerDialogFragment<T> setSrcViewUpdateListener(OnSrcViewUpdateListener<T> srcViewUpdateListener) {
        this.srcViewUpdateListener = srcViewUpdateListener;
        return this;
    }

    public ImageViewerDialogFragment<T> setImageLoader(IImageLoader<T> imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }

    /**
     * 是否显示白色占位区块
     *
     * @param isShow
     * @return
     */
    public ImageViewerDialogFragment<T> isShowPlaceholder(boolean isShow) {
        this.isShowPlaceholder = isShow;
        return this;
    }

    /**
     * 是否显示页码指示器
     *
     * @param isShow
     * @return
     */
    public ImageViewerDialogFragment<T> isShowIndicator(boolean isShow) {
        this.isShowIndicator = isShow;
        return this;
    }

    /**
     * 是否显示保存按钮
     *
     * @param isShowSaveBtn
     * @return
     */
    public ImageViewerDialogFragment<T> isShowSaveButton(boolean isShowSaveBtn) {
        this.isShowSaveBtn = isShowSaveBtn;
        return this;
    }

    public ImageViewerDialogFragment<T> isInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
        return this;
    }

    public ImageViewerDialogFragment<T> setPlaceholderColor(int color) {
        this.placeholderColor = color;
        return this;
    }

    public ImageViewerDialogFragment<T> setPlaceholderRadius(int radius) {
        this.placeholderRadius = radius;
        return this;
    }

    public ImageViewerDialogFragment<T> setPlaceholderStrokeColor(int strokeColor) {
        this.placeholderStrokeColor = strokeColor;
        return this;
    }

    /**
     * 设置单个使用的源View。单个使用的情况下，无需设置url集合和SrcViewUpdateListener
     *
     * @param srcView
     * @return
     */
    public ImageViewerDialogFragment<T> setSingleSrcView(ImageView srcView, T url) {
        urls.clear();
        urls.add(url);
        setSrcView(srcView, 0);
        return this;
    }

    public ImageViewerDialogFragment<T> setSrcView(ImageView srcView, int position) {
        this.srcView = srcView;
        this.position = position;
//        if (srcView != null) {
//            int[] locations = new int[2];
//            this.srcView.getLocationInWindow(locations);
//            int offset;
//            if (getActivity() != null && (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    & getActivity().getWindow().getAttributes().flags)
//                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
//                offset = 0;
//            } else {
//                offset = ScreenUtils.getStatusBarHeight(context);
//            }
//            locations[1] = locations[1] - offset;
//            rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
//        }
        return this;
    }

    public void updateSrcView(ImageView srcView) {
        setSrcView(srcView, position);
        addOrUpdateSnapshot();
    }

    public void updateSrcView(ImageView srcView, int position) {
        if (this.position != position) {
            return;
        }
        setSrcView(srcView, position);
        addOrUpdateSnapshot();
    }

    @Override
    public void onRelease() {
        dismiss();
    }

    @Override
    public void onDragChange(int dy, float scale, float fraction) {
        tv_pager_indicator.setAlpha(1 - fraction);
        if (customView != null) customView.setAlpha(1 - fraction);
        if (isShowSaveBtn) tv_save.setAlpha(1 - fraction);
        photoViewContainer.setBackgroundColor((Integer) argbEvaluator.evaluate(fraction * .8f, bgColor, Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        if (v == tv_save) save();
    }

    /**
     * 保存图片到相册，会自动检查是否有保存权限
     */
    protected void save() {
        //check permission
        XPermission.create(getContext(), PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        //save bitmap to album.
                        XPopupUtils.saveBmpToAlbum(getContext(), imageLoader, urls.get(isInfinite ? position % urls.size() : position));
                    }

                    @Override
                    public void onDenied() {
                        Toast.makeText(getContext(), "没有保存权限，保存功能无法使用！", Toast.LENGTH_SHORT).show();
                    }
                }).request();
    }

    public class PhotoViewAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return isInfinite ? Integer.MAX_VALUE / 2 : urls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return o == view;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());
            photoView.setTag(position);
            // call LoadImageListener
            if (imageLoader != null)
                imageLoader.loadImage(position, urls.get(isInfinite ? position % urls.size() : position), photoView);

            container.addView(photoView);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return photoView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    protected void doAfterShow() {
        popupStatus = PopupStatus.Show;
    }

    protected void doAfterDismiss() {
        popupStatus = PopupStatus.Dismiss;
    }


}
