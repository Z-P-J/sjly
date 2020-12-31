//package com.zpj.fragmentation.dialog.imagetrans;
//
//import android.animation.ArgbEvaluator;
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.transition.ChangeBounds;
//import android.support.transition.ChangeImageTransform;
//import android.support.transition.ChangeTransform;
//import android.support.transition.Transition;
//import android.support.transition.TransitionListenerAdapter;
//import android.support.transition.TransitionManager;
//import android.support.transition.TransitionSet;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewCompat;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.animation.FastOutSlowInInterpolator;
//import android.support.v4.widget.ViewDragHelper;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.LinearInterpolator;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.phoenix.xphotoview.XPhotoView;
//import com.zpj.fragmentation.dialog.imagetrans.listener.OnTransformListener;
//import com.zpj.fragmentation.dialog.impl.FullScreenDialogFragment;
//import com.zpj.fragmentation.dialog.photoview.PhotoView;
//import com.zpj.fragmentation.queue.BlockActionQueue;
//import com.zpj.utils.ScreenUtils;
//
//import java.util.List;
//
///**
// * Created by liuting on 18/3/15.
// */
//public class DialogView2<T> extends FrameLayout implements OnTransformListener {
//
//    private static final String TAG = "DialogView2";
//
//    public interface OnGetImageViewCallback {
//        ImageView getImageView(int pos);
//    }
//
//    private final BlockActionQueue actionQueue = new BlockActionQueue();
//
//    private ImageTransBuild<T> build;
//    private final InterceptViewPager viewPager;
//    private ImagePagerAdapter<T> mAdapter;
//    private boolean isOpened = false;
//
//    protected PhotoView snapshotView;
//
//    private final MyImageLoad2<T> loader;
//
//    public OnGetImageViewCallback callback;
//    protected Rect rect = null;
//    private ImageView currentImageView;
//    private int backgroundColor = Color.TRANSPARENT;
//    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
//
//
//    public static void setWidthHeight(View target, int width, int height) {
//        ViewGroup.LayoutParams params = target.getLayoutParams();
//        params.width = width;
//        params.height = height;
//    }
//
//    public DialogView2(@NonNull Context context) {
//        this(context, null);
//    }
//
//    public DialogView2(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public DialogView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        viewPager = new InterceptViewPager(context);
//        snapshotView = new PhotoView(context);
//        loader = new MyImageLoad2<>();
//        setBackgroundColor(Color.TRANSPARENT);
//    }
//
//    public void onCreate(ImageTransBuild<T> build, FullScreenDialogFragment dialogInterface) {
//        this.build = build;
//        if (this.build.nowIndex < 0) {
//            this.build.nowIndex = 0;
//        }
//
//        addView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        viewPager.setVisibility(View.INVISIBLE);
//
//        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        addView(snapshotView, params);
//        snapshotView.setVisibility(View.VISIBLE);
//        if (callback != null) {
//            currentImageView = callback.getImageView(build.nowIndex);
//            int[] locations = new int[2];
//            currentImageView.getLocationInWindow(locations);
//            rect = new Rect(locations[0], locations[1], locations[0] + currentImageView.getWidth(), locations[1] + currentImageView.getHeight());
//            int offset = ScreenUtils.getScreenHeight(getContext()) - getRootView().getMeasuredHeight();
//            snapshotView.setTranslationX(rect.left);
//            snapshotView.setTranslationY(rect.top - offset);
//            snapshotView.setScaleType(currentImageView.getScaleType());
//            setWidthHeight(snapshotView, rect.width(), rect.height());
//            Log.d(TAG, "onCreate rect.width=" + rect.width() + " rect.height=" + rect.height());
//            snapshotView.setImageDrawable(currentImageView.getDrawable());
//        }
////        snapshotView.post(new Runnable() {
////            @Override
////            public void run() {
//////                XPopup.getAnimationDuration()
////
////            }
////        });
//
//        snapshotView.post(new Runnable() {
//            @Override
//            public void run() {
//                TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
//                        .setDuration(360)
//                        .addTransition(new ChangeBounds())
//                        .addTransition(new ChangeTransform())
//                        .addTransition(new ChangeImageTransform())
//                        .setInterpolator(new FastOutSlowInInterpolator())
//                        .addListener(new TransitionListenerAdapter() {
//                            @Override
//                            public void onTransitionEnd(@NonNull Transition transition) {
//                                XPhotoView itemView = viewPager.findViewWithTag(build.nowIndex);
//                                if (itemView != null && currentImageView != null) {
//                                    itemView.setDrawable(currentImageView.getDrawable());
//                                }
//                                viewPager.setVisibility(View.VISIBLE);
////                                snapshotView.setVisibility(GONE);
////                                snapshotView.setDrawable(null);
//                                actionQueue.start();
//                            }
//
//                            @Override
//                            public void onTransitionStart(@NonNull Transition transition) {
//                                super.onTransitionStart(transition);
//
//                            }
//                        }));
//                snapshotView.setTranslationY(0);
//                snapshotView.setTranslationX(0);
//                snapshotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                Log.d(TAG, "onCreate getWidth=" + getWidth() + " getHeight=" + getHeight());
//                setWidthHeight(snapshotView, getWidth(), getHeight());
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (currentImageView != null) {
//                            currentImageView.setVisibility(INVISIBLE);
//                        }
//                    }
//                }, 180);
//            }
//        });
//
//        // do shadow anim.
//        animateShadowBg(Color.rgb(32, 36, 46));
//
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Toast.makeText(getContext(), "position=" + position, Toast.LENGTH_SHORT).show();
//                build.nowIndex = position;
//                build.imageTransAdapter.onPageSelected(position);
//
//                if (callback != null) {
//                    if (currentImageView != null) {
//                        currentImageView.setVisibility(VISIBLE);
//                    }
//                    currentImageView = callback.getImageView(build.nowIndex);
//                    currentImageView.setVisibility(INVISIBLE);
//                    int[] locations = new int[2];
//                    currentImageView.getLocationInWindow(locations);
//                    rect = new Rect(locations[0], locations[1], locations[0] + currentImageView.getWidth(), locations[1] + currentImageView.getHeight());
//                }
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        mAdapter = new ImagePagerAdapter<>(build.imageList);
//        viewPager.setAdapter(mAdapter);
//        viewPager.setOffscreenPageLimit(3);
//        viewPager.setCurrentItem(build.clickIndex);
//        View maskView = build.imageTransAdapter.onCreateView(this, viewPager, dialogInterface);
//        if (maskView != null) {
//            addView(maskView);
//        }
//
//    }
//
//    public ImagePagerAdapter<T> getAdapter() {
//        return mAdapter;
//    }
//
//    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
//        viewPager.addOnPageChangeListener(listener);
//    }
//
//    public void loadNewUrl(T url) {
//        int now = build.nowIndex;
//        build.imageList.set(now, url);
//        XPhotoView itemView = viewPager.findViewWithTag(now);
////        loader.loadImage(url, new ImageLoad.LoadCallback() {
////            @Override
////            public void progress(float progress) {
////
////            }
////
////            @Override
////            public void loadFinish(Drawable drawable) {
////
////            }
////        }, itemView, String.valueOf(itemView.hashCode()));
//    }
//
//    public int getCurrentItem() {
//        return viewPager.getCurrentItem();
//    }
//
//    public InterceptViewPager getViewPager() {
//        return viewPager;
//    }
//
//    public void onDismiss(FullScreenDialogFragment dialog) {
//        TransitionManager.endTransitions((ViewGroup) snapshotView.getParent());
//        viewPager.setVisibility(View.INVISIBLE);
//        snapshotView.setVisibility(View.VISIBLE);
//        snapshotView.setImageDrawable(currentImageView.getDrawable());
//        XPhotoView itemView = viewPager.findViewWithTag(build.nowIndex);
//        itemView.onDismiss(snapshotView);
////        snapshotView.setImageMatrix(itemView.getImageMatrix());
//
//
////        removeView(viewPager);
//        if (rect == null || currentImageView == null) {
//            return;
//        }
//        TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
//                .setDuration(360)
//                .addTransition(new ChangeBounds())
//                .addTransition(new ChangeTransform())
//                .addTransition(new ChangeImageTransform())
//                .setInterpolator(new FastOutSlowInInterpolator())
//                .addListener(new TransitionListenerAdapter() {
//                    @Override
//                    public void onTransitionEnd(@NonNull Transition transition) {
//
//                    }
//                }));
//
//
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (currentImageView != null) {
//                    currentImageView.setVisibility(VISIBLE);
//                }
//            }
//        }, 260);
//
//        int offset = ScreenUtils.getScreenHeight(getContext()) - getRootView().getMeasuredHeight();
//        snapshotView.setTranslationY(rect.top - offset);
//        snapshotView.setTranslationX(rect.left);
//        snapshotView.setScaleX(1f);
//        snapshotView.setScaleY(1f);
//        snapshotView.setScaleType(currentImageView.getScaleType());
//
//
//        setWidthHeight(snapshotView, rect.width(), rect.height());
//
//        // do shadow anim.
//        animateShadowBg(Color.TRANSPARENT);
//    }
//
//
//    @Override
//    public void transformStart() {
//        build.imageTransAdapter.onOpenTransStart();
//        viewPager.setCanScroll(false);
//    }
//
//    @Override
//    public void transformEnd() {
//        isOpened = true;
//        build.imageTransAdapter.onOpenTransEnd();
//        viewPager.setCanScroll(true);
//        for (int i = 0; i < build.imageList.size(); i++) {
//            View view = viewPager.findViewWithTag(i);
//            if (view instanceof XPhotoView) {
////                build.imageLoad.loadImage(build.imageList.get(i), new ImageLoad.LoadCallback() {
////                    @Override
////                    public void progress(float progress) {
////
////                    }
////
////                    @Override
////                    public void loadFinish(Drawable drawable) {
////
////                    }
////                }, view, );
//            }
//        }
//    }
//
//    @Override
//    public void onTransform(float ratio) {
//        build.imageTransAdapter.onTransform(ratio);
//    }
//
//
//    private void animateShadowBg(final int endColor) {
//        int start = backgroundColor;
//        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                backgroundColor = (Integer) argbEvaluator.evaluate(animation.getAnimatedFraction(),
//                        start, endColor);
//                setBackgroundColor(backgroundColor);
//            }
//        });
//        animator.setDuration(360)
//                .setInterpolator(new LinearInterpolator());
//        animator.start();
//    }
//
//    public boolean isOpened() {
//        return isOpened;
//    }
//
//    class ImagePagerAdapter<S> extends PagerAdapter {
//
//        private final List<S> mData;
//
//        public ImagePagerAdapter(@NonNull List<S> data) {
//            mData = data;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            XPhotoView view = new XPhotoView(container.getContext());
//            view.setTag(position);
//            actionQueue.post(new Runnable() {
//                @Override
//                public void run() {
////                    loader.loadImage(build.imageList.get(position), new ImageLoad.LoadCallback() {
////                        @Override
////                        public void progress(float progress) {
////
////                        }
////
////                        @Override
////                        public void loadFinish(Drawable drawable) {
////
////                        }
////                    }, view, String.valueOf(view.hashCode()));
//                }
//            });
//            container.addView(view);
//            return view;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public int getCount() {
//            return mData.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//    }
//}
