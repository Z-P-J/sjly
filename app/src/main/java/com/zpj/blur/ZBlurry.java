package com.zpj.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.zpj.utils.ContextUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 参考https://github.com/goweii/Blurred
 */
public final class ZBlurry {

    private static final Float MAX_FPS = 60F;

    private IBlur sBlur;
    private static ExecutorService sExecutor;

    private long mLastFrameTime = 0L;

    private float mPercent = 0;
    private float mRadius = 0;
    private float mScale = 1;
    private boolean mAntiAlias = false;
    private boolean mKeepSize = false;
    private boolean mFitIntoViewXY = false;
    private boolean mRecycleOriginal = false;
    private float mMaxFps = MAX_FPS;

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = null;
    private ViewTreeObserver.OnDrawListener mOnDrawListener = null;

    private int mBackgroundColor = Color.TRANSPARENT;
    private int mForegroundColor = Color.TRANSPARENT;
    private Bitmap mOriginalBitmap = null;
    private View mViewFrom = null;
    private View mViewInto = null;

    private BitmapProcessor mProcessor;
    private boolean mRealTimeMode = false;

    private SnapshotInterceptor mSnapshotInterceptor = null;
    private FpsListener mFpsListener = null;
    private Listener mListener = null;
    private Callback mCallback = null;
    private Handler mCallbackHandler = null;

    private boolean isPause;

    public ZBlurry() {
        sBlur = new GaussianBlur(ContextUtils.getApplicationContext());
    }

    public static void init(Context context) {
//        if (sBlur == null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                sBlur = GaussianBlur.get(context);
//            } else {
//                sBlur = FastBlur.get();
//            }
//        }
    }

    public void recycle() {
        if (sBlur != null) {
            sBlur.recycle();
            sBlur = null;
        }
//        BitmapProcessor.get().realTimeMode(false);
        if (sExecutor != null) {
            if (!sExecutor.isShutdown()) {
                sExecutor.shutdown();
            }
            sExecutor = null;
        }
    }

    public void realTimeMode(boolean realTimeMode) {
        final IBlur iBlur = requireBlur();
        if (iBlur instanceof GaussianBlur) {
            GaussianBlur gaussianBlur = (GaussianBlur) sBlur;
            gaussianBlur.realTimeMode(realTimeMode);
        }
        if (mProcessor != null) {
            mProcessor.realTimeMode(realTimeMode);
        }
//        BitmapProcessor.get().realTimeMode(realTimeMode);
    }

    private IBlur requireBlur() {
        return Utils.requireNonNull(sBlur, "Blurred未初始化");
    }

    private static ExecutorService requireExecutor() {
        if (sExecutor == null || sExecutor.isShutdown()) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        return sExecutor;
    }

    public static ZBlurry with(Bitmap original) {
        return new ZBlurry().bitmap(original);
    }

    public static ZBlurry with(View view) {
        return new ZBlurry().view(view);
    }

    public void reset() {
        mMaxFps = MAX_FPS;
        mPercent = 0;
        mRadius = 0;
        mScale = 1;
        mKeepSize = false;
        mAntiAlias = false;
        mFitIntoViewXY = false;
        mRecycleOriginal = false;
        mOriginalBitmap = null;
        if (mViewFrom != null) {
            if (mOnPreDrawListener != null) {
                mViewFrom.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
                mOnPreDrawListener = null;
            }
            mViewFrom = null;
        }
        mViewInto = null;
        mBackgroundColor = Color.TRANSPARENT;
        mForegroundColor = Color.TRANSPARENT;
    }

    public ZBlurry view(View view) {
        reset();
        mViewFrom = view;
        return this;
    }

    public ZBlurry bitmap(Bitmap original) {
        reset();
        mOriginalBitmap = original;
        return this;
    }

    public ZBlurry suggestConfig() {
        mMaxFps = 60F;
        mPercent = 0;
        mRadius = 10;
        mScale = 8;
        mKeepSize = false;
        mAntiAlias = false;
        mFitIntoViewXY = false;
        mRecycleOriginal = false;
        return this;
    }

    public ZBlurry backgroundColor(int color) {
        mBackgroundColor = color;
        return this;
    }

    public ZBlurry foregroundColor(int color) {
        mForegroundColor = color;
        return this;
    }

    public ZBlurry percent(float percent) {
        this.mPercent = percent;
        return this;
    }

    public ZBlurry radius(float radius) {
        this.mRadius = radius;
        return this;
    }

    public ZBlurry scale(float scale) {
        this.mScale = scale;
        return this;
    }

    public ZBlurry maxFps(float maxFps) {
        this.mMaxFps = maxFps;
        return this;
    }

    public ZBlurry keepSize(boolean keepSize) {
        this.mKeepSize = keepSize;
        return this;
    }

    public ZBlurry fitIntoViewXY(boolean fit) {
        this.mFitIntoViewXY = fit;
        return this;
    }

    public ZBlurry antiAlias(boolean antiAlias) {
        this.mAntiAlias = antiAlias;
        return this;
    }

    public ZBlurry recycleOriginal(boolean recycleOriginal) {
        this.mRecycleOriginal = recycleOriginal;
        return this;
    }

    public ZBlurry snapshotInterceptor(SnapshotInterceptor interceptor) {
        this.mSnapshotInterceptor = interceptor;
        return this;
    }

    public ZBlurry fpsListener(FpsListener listener) {
        this.mFpsListener = listener;
        return this;
    }

    public ZBlurry listener(Listener listener) {
        this.mListener = listener;
        return this;
    }

    public Bitmap blur() {
        if (mViewFrom == null && mOriginalBitmap == null) {
            throw new NullPointerException("待模糊View和Bitmap不能同时为空");
        }
        if (mListener != null) mListener.begin();
        float scale = mScale <= 0 ? 1 : mScale;
        float radius = mPercent <= 0 ? mRadius : Math.min(
                mViewFrom != null ? mViewFrom.getWidth() : mOriginalBitmap.getWidth(),
                mViewFrom != null ? mViewFrom.getHeight() : mOriginalBitmap.getHeight()
        ) * mPercent;
        final Bitmap blurredBitmap;
        if (mViewFrom == null) {
            blurredBitmap = requireBlur().process(getProcessor(), mOriginalBitmap, radius, scale, mKeepSize, mRecycleOriginal);
        } else {
            if (radius > 25) {
                scale = scale / (radius / 25);
                radius = 25;
            }
            final SnapshotInterceptor snapshotInterceptor = checkSnapshotInterceptor();
            Bitmap bitmap = snapshotInterceptor.snapshot(getProcessor(), mViewFrom, mBackgroundColor, mForegroundColor, scale, mAntiAlias);
            blurredBitmap = requireBlur().process(getProcessor(), bitmap, radius, 1, mKeepSize, mRecycleOriginal);
        }
        if (mListener != null) mListener.end();
        return blurredBitmap;
    }

//    public void blur(final Callback callback) {
//        Utils.requireNonNull(callback, "Callback不能为空");
//        mCallback = callback;
//        mCallbackHandler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                mCallbackHandler = null;
//                mCallback.down((Bitmap) msg.obj);
//            }
//        };
//        requireExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap bitmap = blur();
//                Message msg = mCallbackHandler.obtainMessage();
//                msg.obj = bitmap;
//                mCallbackHandler.sendMessage(msg);
//            }
//        });
//    }

    public void blur(final Callback callback) {
        Utils.requireNonNull(callback, "Callback不能为空");
        mCallback = callback;

        float scale = mScale <= 0 ? 1 : mScale;
        float radius = mPercent <= 0 ? mRadius : Math.min(
                mViewFrom != null ? mViewFrom.getWidth() : mOriginalBitmap.getWidth(),
                mViewFrom != null ? mViewFrom.getHeight() : mOriginalBitmap.getHeight()
        ) * mPercent;
        final Bitmap blurredBitmap;
        if (mViewFrom == null) {
            blurredBitmap = mOriginalBitmap;
//            blurredBitmap = requireBlur().process(getProcessor(), mOriginalBitmap, radius, scale, mKeepSize, mRecycleOriginal);
        } else {
            if (radius > 25) {
                scale = scale / (radius / 25);
                radius = 25;
            }
            final SnapshotInterceptor snapshotInterceptor = checkSnapshotInterceptor();
            blurredBitmap = snapshotInterceptor.snapshot(getProcessor(), mViewFrom, mBackgroundColor, mForegroundColor, scale, mAntiAlias);
//            blurredBitmap = requireBlur().process(getProcessor(), bitmap, radius, 1, mKeepSize, mRecycleOriginal);
            scale = 1;
        }


        final float finalRadius = radius;
        final float finalScale = scale;

        Observable.create(
                new ObservableOnSubscribe<Bitmap>() {
                    @Override
                    public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                        Bitmap blur = requireBlur().process(getProcessor(), blurredBitmap, finalRadius, finalScale, mKeepSize, mRecycleOriginal);
                        emitter.onNext(blur);
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        callback.down(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    /**
     * 用于实现实时高斯模糊
     * viewFrom : 通过with()/view()传入的view
     * viewInto : 该处的ImageView
     * 将对viewFrom进行截图模糊处理，并对遮盖区域裁剪后设置到viewInto
     * viewFrom和viewInto不能有包含关系，及viewInto不能是viewFrom的子控件
     */
    public void blur(final ImageView into) {
        Utils.requireNonNull(mViewFrom, "实时高斯模糊时待模糊View不能为空");
        Utils.requireNonNull(into, "ImageView不能为空");
        mViewInto = into;
        mRealTimeMode = true;
        mProcessor = new BitmapProcessor();
        mProcessor.realTimeMode(true);
        if (mOnPreDrawListener == null) {
            mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (mViewInto == null) return true;
                    long currFrameTime = System.currentTimeMillis();
                    final float fps = 1000F / (currFrameTime - mLastFrameTime);
                    if (fps > mMaxFps) return true;
                    mLastFrameTime = currFrameTime;
                    if (mFpsListener != null) mFpsListener.currFps(fps);
                    realTimeMode(true);
                    keepSize(false);
                    recycleOriginal(true);
                    Bitmap blur = blur();
//                    Bitmap clip = BitmapProcessor.get().clip(blur, mViewFrom, mViewInto, mFitIntoViewXY, mAntiAlias);
                    Bitmap clip = getProcessor().clip(blur, mViewFrom, into, mFitIntoViewXY, mAntiAlias);
                    blur.recycle();
                    into.setImageBitmap(clip);
                    return true;
                }
            };
            mViewFrom.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        }
    }

    public void pauseBlur() {
        isPause = true;
        if (mViewFrom != null) {
            mViewFrom.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
        }
    }

    public void startBlur() {
        isPause = false;
        if (mViewFrom != null) {
            mViewFrom.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        }
    }

    public ZBlurry blur(final View into, final Callback callback) {
        Utils.requireNonNull(mViewFrom, "实时高斯模糊时待模糊View不能为空");
        Utils.requireNonNull(into, "ImageView不能为空");
        mViewInto = into;
        mRealTimeMode = true;
        mProcessor = new BitmapProcessor();
        mProcessor.realTimeMode(true);
        if (mOnPreDrawListener == null) {
            mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (mViewInto == null || isPause) return true;
//                    Log.d("OnPreDrawListener", "onPreDraw--start");
                    long currFrameTime = System.currentTimeMillis();
                    final float fps = 1000F / (currFrameTime - mLastFrameTime);
//                    Log.d("OnPreDrawListener", "onPreDraw--fps=" + fps);
                    if (fps > mMaxFps) return true;
//                    if (fps > 30) return true;
//                    mViewFrom.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
                    pauseBlur();
                    mLastFrameTime = currFrameTime;
                    if (mFpsListener != null) mFpsListener.currFps(fps);
                    realTimeMode(true);
                    keepSize(false);
                    recycleOriginal(true);

                    float scale = mScale <= 0 ? 1 : mScale;
                    float radius = mPercent <= 0 ? mRadius : Math.min(mViewFrom.getWidth(), mViewFrom.getHeight()) * mPercent;
                    if (radius > 25) {
                        scale = scale / (radius / 25);
                        radius = 25;
                    }
                    final SnapshotInterceptor snapshotInterceptor = checkSnapshotInterceptor();
                    final Bitmap blurredBitmap = snapshotInterceptor.snapshot(getProcessor(), mViewFrom, mBackgroundColor, mForegroundColor, scale, mAntiAlias);
                    final float finalRadius = radius;

//                    Log.d("OnPreDrawListener", "onPreDraw--Observable.create");
                    Observable.create(
                            new ObservableOnSubscribe<Bitmap>() {
                                @Override
                                public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
//                                    Log.d("OnPreDrawListener", "onPreDraw--subscribe");
                                    Bitmap blur = requireBlur().process(getProcessor(), blurredBitmap, finalRadius, 1, mKeepSize, mRecycleOriginal);
                                    Bitmap clip = getProcessor().clip(blur, mViewFrom, mViewInto, mFitIntoViewXY, mAntiAlias);
                                    blur.recycle();
                                    emitter.onNext(clip);
                                    emitter.onComplete();
                                }
                            })
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Bitmap>() {
                                @Override
                                public void onSubscribe(Disposable d) {
//                                    Log.d("OnPreDrawListener", "onPreDraw--doOnSubscribe");
                                }

                                @Override
                                public void onNext(Bitmap bitmap) {
//                                    Log.d("OnPreDrawListener", "onPreDraw--doOnNext bitmap=" + bitmap);

                                    callback.down(bitmap);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    mLastFrameTime = System.currentTimeMillis();
//                                    mViewFrom.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
                                    startBlur();
//                                    Log.d("OnPreDrawListener", "onPreDraw--doOnError");
                                }

                                @Override
                                public void onComplete() {
                                    mLastFrameTime = System.currentTimeMillis();
//                                    Log.d("OnPreDrawListener", "onPreDraw--doOnComplete");
//                                    mViewFrom.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
                                    startBlur();
                                }
                            });

//                    blur(new Callback() {
//                        @Override
//                        public void down(Bitmap bitmap) {
//                            Bitmap clip = getProcessor().clip(bitmap, mViewFrom, mViewInto, mFitIntoViewXY, mAntiAlias);
//                            bitmap.recycle();
//                            callback.down(clip);
//                        }
//                    });

//                    Bitmap blur = blur();
////                    Bitmap clip = BitmapProcessor.get().clip(blur, mViewFrom, mViewInto, mFitIntoViewXY, mAntiAlias);
//                    Bitmap clip = getProcessor().clip(blur, mViewFrom, mViewInto, mFitIntoViewXY, mAntiAlias);
//                    blur.recycle();
//                    callback.down(clip);



//                    mViewInto.setBackground(new BitmapDrawable(mViewInto.getResources(), clip));
//                    Log.d("OnPreDrawListener", "onPreDraw--return");
                    return true;
                }
            };
            mViewFrom.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        }
        return this;
    }

    public BitmapProcessor getProcessor() {
        if (mProcessor == null) {
            mProcessor = new BitmapProcessor();
            mProcessor.realTimeMode(mRealTimeMode);
        }
        return mProcessor;
    }

    private SnapshotInterceptor checkSnapshotInterceptor() {
        if (mSnapshotInterceptor == null) {
            mSnapshotInterceptor = new DefaultSnapshotInterceptor();
        }
        return mSnapshotInterceptor;
    }

    public interface SnapshotInterceptor {
        Bitmap snapshot(BitmapProcessor processor,
                        View from,
                        int backgroundColor,
                        int foregroundColor,
                        float scale,
                        boolean antiAlias);
    }

    public interface Callback {
        void down(Bitmap bitmap);
    }

    public interface Listener {
        void begin();

        void end();
    }

    public interface FpsListener {
        void currFps(float fps);
    }
}
