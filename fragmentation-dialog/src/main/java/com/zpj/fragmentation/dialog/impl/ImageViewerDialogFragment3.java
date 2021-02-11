package com.zpj.fragmentation.dialog.impl;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.fragmentation.dialog.imagetrans.ImageLoad;
import com.zpj.fragmentation.dialog.imagetrans.MyImageLoad2;
import com.zpj.fragmentation.dialog.interfaces.OnDragChangeListener;
import com.zpj.fragmentation.dialog.widget.BlankView;
import com.zpj.fragmentation.dialog.widget.HackyViewPager;
import com.zpj.fragmentation.dialog.widget.ImageViewContainer;
import com.zpj.fragmentation.dialog.widget.PhotoViewContainer;
import com.zpj.fragmentation.dialog.widget.PlaceholderImageView2;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


public class ImageViewerDialogFragment3<T> extends BaseDialogFragment
        implements OnDragChangeListener, View.OnClickListener {

    protected static final int DEFAULT_ANIM_DURATION = 360;

//    private final BlockActionQueue actionQueue = new BlockActionQueue();

    protected FrameLayout container;
    protected PhotoViewContainer photoViewContainer;
    protected BlankView placeholderView;
    protected TextView tv_pager_indicator, tv_save;
    protected HackyViewPager pager;
    protected ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    protected final List<T> urls = new ArrayList<>();
    protected MyImageLoad2<T> loader = new MyImageLoad2<>();;
    protected OnSrcViewUpdateListener<T> srcViewUpdateListener;
    protected int position;
    protected Rect rect = null;
    protected ImageView srcView; //动画起始的View，如果为null，移动和过渡动画效果会没有，只有弹窗的缩放功能
    protected PlaceholderImageView2 snapshotView;
    protected boolean isAnimationEnd;
    protected boolean isShowPlaceholder = false; //是否显示占位白色，当图片切换为大图时，原来的地方会有一个白色块
    protected int placeholderColor = -1; //占位View的颜色
    protected int placeholderStrokeColor = -1; // 占位View的边框色
    protected int placeholderRadius = -1; // 占位View的圆角
    protected boolean isShowSaveBtn = false; //是否显示保存按钮
    protected boolean isShowIndicator = false; //是否页码指示器
    protected boolean isInfinite = false;//是否需要无限滚动
    protected View customView;
    protected int bgColor = Color.rgb(32, 36, 46);//弹窗的背景颜色，可以自定义

    private int backgroundColor = Color.TRANSPARENT;

    public interface OnSrcViewUpdateListener<T> {
        void onSrcViewUpdate(@NonNull ImageViewerDialogFragment3<T> popup, int position);
    }

    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_image_viewer;
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

//        if (srcView != null) {
//            int[] locations = new int[2];
//            this.srcView.getLocationInWindow(locations);
////            int offset;
////            if (getActivity() != null && (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
////                    & getActivity().getWindow().getAttributes().flags)
////                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
////                offset = 0;
////            } else {
////                offset = ScreenUtils.getStatusBarHeight(context);
////            }
////            locations[1] = locations[1] - offset;
//            rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
//        }

        container = findViewById(R.id.container);
        if (getCustomLayoutId() > 0) {
            customView = LayoutInflater.from(getContext()).inflate(getCustomLayoutId(), container, false);
            customView.setVisibility(View.INVISIBLE);
            customView.setAlpha(0);
            container.addView(customView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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


                if (isAnimationEnd) {
                    if (srcViewUpdateListener != null) {
                        srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment3.this, i);
                    }
                    ImageViewContainer itemView = pager.findViewWithTag(position);
                    if (itemView != null) {
                        itemView.showPlaceholder(srcView.getDrawable());
                    }
                }

//                if (callback != null) {
//                    srcView = callback.getImageView(position);
//                    srcView.setVisibility(View.INVISIBLE);
//                    int[] locations = new int[2];
//                    srcView.getLocationInWindow(locations);
//                    rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
//                }
            }
        });
        pager.setAdapter(new PhotoViewAdapter());
//        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(position);
        pager.setVisibility(View.INVISIBLE);
//        addOrUpdateSnapshot();
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

//        if (callback != null) {
//            srcView = callback.getImageView(position);
//            int[] locations = new int[2];
//            srcView.getLocationInWindow(locations);
//            rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
//            int offset = ScreenUtils.getScreenHeight(getContext()) - getRootView().getMeasuredHeight();
//            snapshotView.setTranslationX(rect.left);
//            snapshotView.setTranslationY(rect.top - offset);
//            snapshotView.setScaleType(srcView.getScaleType());
//            setWidthHeight(snapshotView, rect.width(), rect.height());
////            Log.d(TAG, "onCreate rect.width=" + rect.width() + " rect.height=" + rect.height());
//            snapshotView.setImageDrawable(srcView.getDrawable());
//        }

        if (srcViewUpdateListener != null) {
            srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment3.this, position);
        } else {
            addOrUpdateSnapshot();
        }

//        addOrUpdateSnapshot();

        if (customView != null) customView.setVisibility(View.VISIBLE);
        if (srcView == null) {
//            photoViewContainer.setBackgroundColor(bgColor);
            pager.setVisibility(View.VISIBLE);
//            showPagerIndicator();
//            photoViewContainer.isReleasing = false;
//            doAfterShow();
//            if (customView != null)
//                customView.setAlpha(1f);
            animateShadowBg(bgColor, new UpdateListener() {
                @Override
                public void onUpdate(float value) {
                    pager.setScaleX(value);
                    pager.setScaleY(value);
                    if (customView != null) {
                        customView.setAlpha(value);
                    }
                }

                @Override
                public void onEnd() {
                    showPagerIndicator();
                    photoViewContainer.isReleasing = false;
                    doAfterShow();
                }
            });
            return;
        }
        photoViewContainer.isReleasing = true;
        snapshotView.setVisibility(View.VISIBLE);
        snapshotView.setScaleType(srcView.getScaleType());
        snapshotView.post(new Runnable() {
            @Override
            public void run() {
                ImageViewContainer itemView = pager.findViewWithTag(position);
                if (itemView != null && srcView != null) {
//                                    itemView.getPhotoView().setDrawable(srcView.getDrawable());
                    itemView.showPlaceholder(srcView.getDrawable());
                }
                TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
                        .setDuration(DEFAULT_ANIM_DURATION)
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeTransform())
                        .addTransition(new ChangeImageTransform())
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .addListener(new TransitionListenerAdapter() {
                            @Override
                            public void onTransitionEnd(@NonNull Transition transition) {
                                isAnimationEnd = true;

                                pager.setVisibility(View.VISIBLE);
//                                actionQueue.start();
                                snapshotView.setVisibility(View.INVISIBLE);
                                showPagerIndicator();
                                photoViewContainer.isReleasing = false;
                                doAfterShow();
                            }

                        }));
                snapshotView.setTranslationY(0);
                snapshotView.setTranslationX(0);
//                snapshotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                snapshotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                setWidthHeight(snapshotView, photoViewContainer.getWidth(), photoViewContainer.getHeight());

                // do shadow anim.
                animateShadowBg(bgColor, new UpdateListener() {
                    @Override
                    public void onUpdate(float value) {
                        if (customView != null) {
                            customView.setAlpha(value);
                        }
                    }

                    @Override
                    public void onEnd() {

                    }
                });
//                XPopup.getAnimationDuration()
//                if (customView != null)
//                    customView.animate().alpha(1f).setDuration(DEFAULT_ANIM_DURATION).start();
            }
        });

    }

    protected void loadNewUrl(int position, T url) {
        ImageViewContainer ivContainer = pager.findViewWithTag(position);
        ivContainer.showProgressBar();
        loader.loadImage(url, new ImageLoad.LoadCallback() {
            @Override
            public void progress(float progress) {
                ivContainer.setProgress(progress);
            }

            @Override
            public void loadFinish(Drawable drawable) {
                ivContainer.onLoadFinished();
            }
        }, ivContainer, String.valueOf(ivContainer.hashCode()));
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
            setWidthHeight(placeholderView, rect.width(), rect.height());
            placeholderView.setTranslationX(rect.left);
            int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
            placeholderView.setTranslationY(rect.top - offset);
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
            snapshotView = new PlaceholderImageView2(getContext());
            photoViewContainer.addView(snapshotView);
            snapshotView.setScaleType(srcView.getScaleType());
            int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
            snapshotView.setTranslationX(rect.left);
            snapshotView.setTranslationY(rect.top - offset);
            setWidthHeight(snapshotView, rect.width(), rect.height());
        }
        setupPlaceholder();
        snapshotView.setImageDrawable(srcView.getDrawable());
    }

    private interface UpdateListener {
        void onUpdate(float value);
        void onEnd();
    }

    private void animateShadowBg(final int endColor, UpdateListener listener) {
        int start = backgroundColor;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                photoViewContainer.setBackgroundColor((Integer) argbEvaluator.evaluate(animation.getAnimatedFraction(),
//                        start, endColor));
                float value = (float) animation.getAnimatedValue();
                backgroundColor = (Integer) argbEvaluator.evaluate(value,
                        start, endColor);
                photoViewContainer.setBackgroundColor(backgroundColor);
                if (listener != null) {
                    listener.onUpdate(value);
                    if (value == 1f) {
                        listener.onEnd();
                    }
                }
            }
        });
//        XPopup.getAnimationDuration()
        animator.setDuration(DEFAULT_ANIM_DURATION)
                .setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @Override
    public void doDismissAnimation() {
        if (srcViewUpdateListener != null) {
            srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment3.this, position);
        }
        if (srcView != null) {
            ImageViewContainer current = pager.findViewWithTag(pager.getCurrentItem());
            if (current != null) {
                Matrix matrix = current.getPhotoView().getSupportMatrix();
                if (matrix != null) {
                    snapshotView.setSupportMatrix(matrix);
                }

            }
        }
        if (srcView == null) {
//            photoViewContainer.setBackgroundColor(Color.TRANSPARENT);
//            doAfterDismiss();
//            pager.setVisibility(View.INVISIBLE);
//            placeholderView.setVisibility(View.INVISIBLE);
            float startScaleX = pager.getScaleX();
            float startScaleY = pager.getScaleY();
            final float startAlpha;
            if (customView == null) {
                startAlpha = 1f;
            } else {
                startAlpha = customView.getAlpha();
            }
            animateShadowBg(Color.TRANSPARENT, new UpdateListener() {
                @Override
                public void onUpdate(float value) {
                    pager.setScaleX((1 - value) * startScaleX);
                    pager.setScaleY((1 - value) * startScaleY);
                    if (customView != null) {
                        customView.setAlpha((1 - value) * startAlpha);
                    }
                }

                @Override
                public void onEnd() {
//                    actionQueue.onDestroy();
                    doAfterDismiss();
                    pager.setVisibility(View.INVISIBLE);
                    placeholderView.setVisibility(View.INVISIBLE);
                }
            });
            return;
        }
        TransitionManager.endTransitions((ViewGroup) snapshotView.getParent());
//        ImageViewContainer current = pager.findViewWithTag(position);
//        if (current.getPlaceholder().getDrawable() instanceof GifDrawable) {
//            srcView.setImageDrawable(current.getPlaceholder().getDrawable());
//            snapshotView.setImageDrawable(current.getPlaceholder().getDrawable());
//        }
        tv_pager_indicator.setVisibility(View.INVISIBLE);
        tv_save.setVisibility(View.INVISIBLE);
        pager.setVisibility(View.INVISIBLE);
        snapshotView.setVisibility(View.VISIBLE);
        photoViewContainer.isReleasing = true;
        Log.d("ImageViewerPopup", "snapshotView.getImageMatrix()=" + snapshotView.getImageMatrix());
        TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
                .setDuration(DEFAULT_ANIM_DURATION)
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform())
                .setInterpolator(new FastOutSlowInInterpolator())
                .addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
//                        actionQueue.onDestroy();
                        doAfterDismiss();
                        ImageViewContainer current = pager.findViewWithTag(position);
                        if (current.getPlaceholder().getDrawable() instanceof GifDrawable) {
                            srcView.setImageDrawable(current.getPlaceholder().getDrawable());
                        }
                    }
                }));

        int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
        snapshotView.setTranslationY(rect.top - offset);
        snapshotView.setTranslationX(rect.left);
        snapshotView.setScaleX(1f);
        snapshotView.setScaleY(1f);
        snapshotView.setScaleType(srcView.getScaleType());
        setWidthHeight(snapshotView, rect.width(), rect.height());

        // do shadow anim.
        animateShadowBg(Color.TRANSPARENT, new UpdateListener() {
            @Override
            public void onUpdate(float value) {
                if (customView != null) {
                    customView.setAlpha(1 -  value);
                }
            }

            @Override
            public void onEnd() {

            }
        });
//        if (customView != null)
//            customView.animate()
//                    .alpha(0f)
//                    .setDuration(DEFAULT_ANIM_DURATION)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            if (customView != null) customView.setVisibility(View.INVISIBLE);
//                        }
//                    })
//                    .start();
    }

    @Override
    protected boolean onBackPressed() {
        dismiss();
        return true;
    }

//    @Override
//    public void dismiss() {
//        if (srcViewUpdateListener != null) {
//            srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment3.this, position);
//        }
//        if (srcView != null) {
//            //snapshotView拥有当前pager中photoView的样子(matrix)
////            PhotoView current = (PhotoView) pager.getChildAt(pager.getCurrentItem());
////            XPhotoView current = pager.findViewWithTag(pager.getCurrentItem());
////            if (current != null) {
////                Matrix matrix = new Matrix();
////                current.getSuppMatrix(matrix);
////                snapshotView.setSuppMatrix(matrix);
////            }
//            ImageViewContainer current = pager.findViewWithTag(pager.getCurrentItem());
//            if (current != null) {
//                Matrix matrix = current.getPhotoView().getSupportMatrix();
//                if (matrix != null) {
//                    snapshotView.setSupportMatrix(matrix);
//                }
//
//            }
////            setSrcView(srcView, position);
//        }
//        doDismissAnimation();
//        pop();
//        getSupportDelegate().pop();
//    }

    public ImageViewerDialogFragment3<T> setImageUrls(List<T> urls) {
        this.urls.addAll(urls);
        return this;
    }

    public ImageViewerDialogFragment3<T> setSrcViewUpdateListener(OnSrcViewUpdateListener<T> srcViewUpdateListener) {
        this.srcViewUpdateListener = srcViewUpdateListener;
        return this;
    }

    /**
     * 是否显示白色占位区块
     *
     * @param isShow
     * @return
     */
    public ImageViewerDialogFragment3<T> isShowPlaceholder(boolean isShow) {
        this.isShowPlaceholder = isShow;
        return this;
    }

    /**
     * 是否显示页码指示器
     *
     * @param isShow
     * @return
     */
    public ImageViewerDialogFragment3<T> isShowIndicator(boolean isShow) {
        this.isShowIndicator = isShow;
        return this;
    }

    /**
     * 是否显示保存按钮
     *
     * @param isShowSaveBtn
     * @return
     */
    public ImageViewerDialogFragment3<T> isShowSaveButton(boolean isShowSaveBtn) {
        this.isShowSaveBtn = isShowSaveBtn;
        return this;
    }

    public ImageViewerDialogFragment3<T> isInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
        return this;
    }

    public ImageViewerDialogFragment3<T> setPlaceholderColor(int color) {
        this.placeholderColor = color;
        return this;
    }

    public ImageViewerDialogFragment3<T> setPlaceholderRadius(int radius) {
        this.placeholderRadius = radius;
        return this;
    }

    public ImageViewerDialogFragment3<T> setPlaceholderStrokeColor(int strokeColor) {
        this.placeholderStrokeColor = strokeColor;
        return this;
    }

    /**
     * 设置单个使用的源View。单个使用的情况下，无需设置url集合和SrcViewUpdateListener
     *
     * @param srcView
     * @return
     */
    public ImageViewerDialogFragment3<T> setSingleSrcView(ImageView srcView, T url) {
        urls.clear();
        urls.add(url);
        setSrcView(srcView, 0);
        return this;
    }

    public ImageViewerDialogFragment3<T> setSrcView(ImageView srcView, int position) {
        this.srcView = srcView;
        this.position = position;
        if (srcView != null) {
            int[] locations = new int[2];
            this.srcView.getLocationInWindow(locations);
//            int offset;
//            if (getActivity() != null && (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    & getActivity().getWindow().getAttributes().flags)
//                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
//                offset = 0;
//            } else {
//                offset = ScreenUtils.getStatusBarHeight(srcView.getContext());
//            }
//            locations[1] = locations[1] - offset;
            rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
            Log.d("ImageViewerDialog", "rect=" + rect);
        }
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
        backgroundColor = (int) argbEvaluator.evaluate(fraction * .8f, bgColor, Color.TRANSPARENT);
        photoViewContainer.setBackgroundColor(backgroundColor);
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
            final ImageViewContainer ivContainer = new ImageViewContainer(container.getContext());
            ivContainer.setTag(position);
            // call LoadImageListener
            ivContainer.showProgressBar();
//            actionQueue.post(new Runnable() {
//                @Override
//                public void run() {
//                    loader.loadImage(urls.get(position), new ImageLoad.LoadCallback() {
//                        @Override
//                        public void progress(float progress) {
//                            ivContainer.setProgress(progress);
//                        }
//
//                        @Override
//                        public void loadFinish(Drawable drawable) {
//                            ivContainer.onLoadFinished();
//                        }
//                    }, ivContainer.getPhotoView(), String.valueOf(ivContainer.hashCode()));
//                }
//            });
            loader.loadImage(urls.get(position), new ImageLoad.LoadCallback() {
                @Override
                public void progress(float progress) {
                    ivContainer.setProgress(progress);
                }

                @Override
                public void loadFinish(Drawable drawable) {
                    ivContainer.onLoadFinished();
                }
            }, ivContainer, String.valueOf(ivContainer.hashCode()));
//            loader.loadImage(urls.get(position), new ImageLoad.LoadCallback() {
//                @Override
//                public void progress(float progress) {
//
//                }
//
//                @Override
//                public void loadFinish(Drawable drawable) {
//
//                }
//            }, photoView, String.valueOf(view.hashCode()));

            container.addView(ivContainer);
//            ivContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                }
//            });
            return ivContainer;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    protected void doAfterShow() {

    }

    protected void doAfterDismiss() {

    }

    public static void setWidthHeight(View target, int width, int height) {
        if (width <= 0 && height <= 0) return;
        ViewGroup.LayoutParams params = target.getLayoutParams();
        if (width > 0) params.width = width;
        if (height > 0) params.height = height;
        target.setLayoutParams(params);
    }


}
