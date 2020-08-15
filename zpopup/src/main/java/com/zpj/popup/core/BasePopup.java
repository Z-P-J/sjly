package com.zpj.popup.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zpj.popup.XPopup;
import com.zpj.popup.animator.EmptyAnimator;
import com.zpj.popup.animator.PopupAnimator;
import com.zpj.popup.animator.ScaleAlphaAnimator;
import com.zpj.popup.animator.ScrollScaleAnimator;
import com.zpj.popup.animator.ShadowBgAnimator;
import com.zpj.popup.animator.TranslateAlphaAnimator;
import com.zpj.popup.animator.TranslateAnimator;
import com.zpj.popup.enums.PopupStatus;
import com.zpj.popup.impl.FullScreenPopup;
import com.zpj.popup.interfaces.OnBackPressedListener;
import com.zpj.popup.interfaces.OnDismissListener;
import com.zpj.popup.interfaces.OnHideListener;
import com.zpj.popup.interfaces.OnShowListener;
import com.zpj.popup.util.ActivityUtils;
import com.zpj.popup.util.KeyboardUtils;
import com.zpj.popup.util.XPopupUtils;
import com.zpj.popup.util.navbar.NavigationBarObserver;
import com.zpj.popup.util.navbar.OnNavigationBarListener;

import java.util.ArrayList;
import java.util.Stack;

import static com.zpj.popup.enums.PopupAnimation.NoAnimation;

/**
 * Description: 弹窗基类
 * Create by lxj, at 2018/12/7
 */
public abstract class BasePopup<T extends BasePopup> extends FrameLayout implements OnNavigationBarListener {
    private static Stack<BasePopup> stack = new Stack<>(); //静态存储所有弹窗对象
    public PopupInfo popupInfo = new PopupInfo();
    protected final Context context;
    protected PopupAnimator popupContentAnimator;
    protected ShadowBgAnimator shadowBgAnimator;
    private int touchSlop;
    public PopupStatus popupStatus = PopupStatus.Dismiss;
    private boolean isCreated = false;
    private OnDismissListener onDismissListener;
    private OnBackPressedListener onBackPressedListener;
    private OnHideListener onHideListener;
    private OnShowListener onShowListener;

    private Drawable background;


    public BasePopup(@NonNull Context context) {
        super(context);
        this.context = context;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        shadowBgAnimator = new ShadowBgAnimator(this);
        //  添加Popup窗体内容View
        View contentView = LayoutInflater.from(context).inflate(getPopupLayoutId(), this, false);
        // 事先隐藏，等测量完毕恢复，避免View影子跳动现象。
        contentView.setAlpha(0);
        addView(contentView);
    }

    public BasePopup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BasePopup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 执行初始化
     */
    protected void init() {
        if (popupStatus == PopupStatus.Showing) return;
        popupStatus = PopupStatus.Showing;
        NavigationBarObserver.getInstance().register(getContext());
        NavigationBarObserver.getInstance().addOnNavigationBarListener(this);

        //1. 初始化Popup
        if (!isCreated) {
            initPopupContent();
        }
        //apply size dynamic
        if (!(this instanceof FullScreenPopup) && !(this instanceof ImageViewerPopup)) {
            XPopupUtils.setWidthHeight(getTargetSizeView(),
                    (getMaxWidth() != 0 && getPopupWidth() > getMaxWidth()) ? getMaxWidth() : getPopupWidth(),
                    (getMaxHeight() != 0 && getPopupHeight() > getMaxHeight()) ? getMaxHeight() : getPopupHeight()
            );
        }
        Log.d("initPopupContent", "getPopupImplView=" + getPopupImplView() + " background=" + background);
        if (getPopupImplView() != null && background != null) {
            getPopupImplView().setBackground(background);
        }
        if (!isCreated) {
            isCreated = true;
            onCreate();
            if (popupInfo.xPopupCallback != null) popupInfo.xPopupCallback.onCreated();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                // 如果有导航栏，则不能覆盖导航栏，判断各种屏幕方向
                applySize(false);
                getPopupContentView().setAlpha(1f);

                //2. 收集动画执行器
                collectAnimator();

                if (popupInfo.xPopupCallback != null) popupInfo.xPopupCallback.beforeShow();

                //3. 执行动画
                doShowAnimation();

                doAfterShow();

                //目前全屏弹窗快速弹出输入法有问题，暂时用这个方案
                if (!(BasePopup.this instanceof FullScreenPopup))
                    focusAndProcessBackPress();
            }
        }, 50);

    }

    protected boolean hasMoveUp = false;
    private void collectAnimator(){
        if(popupContentAnimator==null){
            // 优先使用自定义的动画器
            if (popupInfo.customAnimator != null) {
                popupContentAnimator = popupInfo.customAnimator;
                popupContentAnimator.targetView = getPopupContentView();
            } else {
                // 根据PopupInfo的popupAnimation字段来生成对应的动画执行器，如果popupAnimation字段为null，则返回null
                popupContentAnimator = genAnimatorByPopupType();
                if (popupContentAnimator == null) {
                    popupContentAnimator = getPopupAnimator();
                }
            }

            //3. 初始化动画执行器
            shadowBgAnimator.initAnimator();
            if (popupContentAnimator != null) {
                popupContentAnimator.initAnimator();
            }
        }
    }

    protected T self() {
        return (T) this;
    }

//    public T setPopupCallback(XPopupCallback callback) {
//        popupInfo.xPopupCallback = callback;
//        return self();
//    }

    @Override
    public void onNavigationBarChange(boolean show) {
        if(!show){
            applyFull();
        }else {
            applySize(true);
        }
    }
    protected void applyFull(){
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = 0;
        params.leftMargin = 0;
        params.bottomMargin = 0;
        params.rightMargin = 0;
        setLayoutParams(params);
    }
    protected void applySize(boolean isShowNavBar){
        LayoutParams params = (LayoutParams) getLayoutParams();
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        boolean isNavBarShown = isShowNavBar || XPopupUtils.isNavBarVisible(getContext());
        if (rotation == 0) {
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.bottomMargin = isNavBarShown ? XPopupUtils.getNavBarHeight() : 0;
        } else if (rotation == 1) {
            params.bottomMargin = 0;
            params.rightMargin = isNavBarShown ? XPopupUtils.getNavBarHeight() : 0;
            params.leftMargin = 0;
        } else if (rotation == 3) {
            params.bottomMargin = 0;
            params.leftMargin = 0;
            params.rightMargin = isNavBarShown ? XPopupUtils.getNavBarHeight() : 0;
        }
        setLayoutParams(params);
    }

    public T setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return self();
    }

    public T setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
        return self();
    }

    public T setPopupBackground(Drawable background) {
        this.background = background;
        return self();
    }

    public T setPopupBackground(int resid) {
        this.background = context.getResources().getDrawable(resid);
        return self();
    }

    public T show() {
        if (getParent() != null) return self();
        final Activity activity = ActivityUtils.getActivity(context);
//        final Activity activity = (Activity) getContext();
        popupInfo.decorView = (ViewGroup) activity.getWindow().getDecorView();
        KeyboardUtils.registerSoftInputChangedListener(activity, this, height -> {
            if (height == 0) { // 说明对话框隐藏
                XPopupUtils.moveDown(BasePopup.this);
                hasMoveUp = false;
            } else {
                //when show keyboard, move up
                XPopupUtils.moveUpToKeyboard(height, BasePopup.this);
                hasMoveUp = true;
            }
        });
        // 1. add PopupView to its decorView after measured.
        popupInfo.decorView.post(new Runnable() {
            @Override
            public void run() {
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(BasePopup.this);
                }
                popupInfo.decorView.addView(BasePopup.this, new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));

                //2. do init，game start.
                init();
            }
        });
        return self();
    }

    protected void doAfterShow() {
        removeCallbacks(doAfterShowTask);
        postDelayed(doAfterShowTask, getAnimationDuration());
    }

    private Runnable doAfterShowTask = new Runnable() {
        @Override
        public void run() {
            popupStatus = PopupStatus.Show;
            onShow();
            if (BasePopup.this instanceof FullScreenPopup) focusAndProcessBackPress();
            if (popupInfo != null && popupInfo.xPopupCallback != null)
                popupInfo.xPopupCallback.onShow();
            if (XPopupUtils.getDecorViewInvisibleHeight(ActivityUtils.getActivity(context)) > 0 && !hasMoveUp) {
                XPopupUtils.moveUpToKeyboard(XPopupUtils.getDecorViewInvisibleHeight(ActivityUtils.getActivity(context)), BasePopup.this);
            }
        }
    };

    private ShowSoftInputTask showSoftInputTask;

    public void focusAndProcessBackPress() {
        if (popupInfo.isRequestFocus) {
            setFocusableInTouchMode(true);
            requestFocus();
            if (!stack.contains(this)) stack.push(this);
        }
        // 此处焦点可能被内容的EditText抢走，也需要给EditText也设置返回按下监听
        setOnKeyListener(new BackPressListener());
        if(!popupInfo.autoFocusEditText) showSoftInput(this);

        //let all EditText can process back pressed.
        ArrayList<EditText> list = new ArrayList<>();
        XPopupUtils.findAllEditText(list, (ViewGroup) getPopupContentView());
        for (int i = 0; i < list.size(); i++) {
            final EditText et = list.get(i);
            et.setOnKeyListener(new BackPressListener());
            if (i == 0 && popupInfo.autoFocusEditText) {
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                showSoftInput(et);
            }
        }
    }

    protected void showSoftInput(View focusView){
        if (popupInfo.autoOpenSoftInput) {
            if (showSoftInputTask == null) {
                showSoftInputTask = new ShowSoftInputTask(focusView);
            } else {
                removeCallbacks(showSoftInputTask);
            }
            postDelayed(showSoftInputTask, 10);
        }
    }

    protected void dismissOrHideSoftInput() {
        if (KeyboardUtils.sDecorViewInvisibleHeightPre == 0)
            dismiss();
        else
            KeyboardUtils.hideSoftInput(BasePopup.this);
    }

    class ShowSoftInputTask implements Runnable {
        View focusView;
        boolean isDone = false;

        public ShowSoftInputTask(View focusView) {
            this.focusView = focusView;
        }

        @Override
        public void run() {
            if (focusView != null && !isDone) {
                isDone = true;
                KeyboardUtils.showSoftInput(focusView);
            }
        }
    }

    public class BackPressListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (popupInfo.isDismissOnBackPressed && !onBackPressed()) {
                    dismissOrHideSoftInput();
                }
                return popupInfo.handleBackPressedEvent;
            }
            return false;
        }
    }

    /**
     * 根据PopupInfo的popupAnimation字段来生成对应的内置的动画执行器
     */
    protected PopupAnimator genAnimatorByPopupType() {
        if (popupInfo == null || popupInfo.popupAnimation == null) return null;
        switch (popupInfo.popupAnimation) {
            case ScaleAlphaFromCenter:
            case ScaleAlphaFromLeftTop:
            case ScaleAlphaFromRightTop:
            case ScaleAlphaFromLeftBottom:
            case ScaleAlphaFromRightBottom:
                return new ScaleAlphaAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case TranslateAlphaFromLeft:
            case TranslateAlphaFromTop:
            case TranslateAlphaFromRight:
            case TranslateAlphaFromBottom:
                return new TranslateAlphaAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case TranslateFromLeft:
            case TranslateFromTop:
            case TranslateFromRight:
            case TranslateFromBottom:
                return new TranslateAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case ScrollAlphaFromLeft:
            case ScrollAlphaFromLeftTop:
            case ScrollAlphaFromTop:
            case ScrollAlphaFromRightTop:
            case ScrollAlphaFromRight:
            case ScrollAlphaFromRightBottom:
            case ScrollAlphaFromBottom:
            case ScrollAlphaFromLeftBottom:
                return new ScrollScaleAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case NoAnimation:
                return new EmptyAnimator();
        }
        return null;
    }

    protected abstract int getPopupLayoutId();

    /**
     * 如果你自己继承BasePopupView来做，这个不用实现
     *
     * @return
     */
    protected int getImplLayoutId() {
        return -1;
    }

    /**
     * 获取PopupAnimator，用于每种类型的PopupView自定义自己的动画器
     *
     * @return
     */
    protected PopupAnimator getPopupAnimator() {
        return null;
    }

    /**
     * 请使用onCreate，主要给弹窗内部用，不要去重写。
     */
    protected void initPopupContent() {
    }

    /**
     * do init.
     */
    protected void onCreate() {
    }

    /**
     * 执行显示动画：动画由2部分组成，一个是背景渐变动画，一个是Content的动画；
     * 背景动画由父类实现，Content由子类实现
     */
    protected void doShowAnimation() {
        if (popupInfo.hasShadowBg) {
            shadowBgAnimator.isZeroDuration = (popupInfo.popupAnimation == NoAnimation);
            shadowBgAnimator.animateShow();
        }
        if (popupContentAnimator != null)
            popupContentAnimator.animateShow();
    }

    /**
     * 执行消失动画：动画由2部分组成，一个是背景渐变动画，一个是Content的动画；
     * 背景动画由父类实现，Content由子类实现
     */
    protected void doDismissAnimation() {
        if (popupInfo.hasShadowBg) {
            shadowBgAnimator.animateDismiss();
        }
        if (popupContentAnimator != null)
            popupContentAnimator.animateDismiss();
    }

    /**
     * 获取内容View，本质上PopupView显示的内容都在这个View内部。
     * 而且我们对PopupView执行的动画，也是对它执行的动画
     *
     * @return
     */
    public View getPopupContentView() {
        return getChildAt(0);
    }

    public View getPopupImplView() {
        return ((ViewGroup) getPopupContentView()).getChildAt(0);
    }

    public int getAnimationDuration() {
        return popupInfo.popupAnimation == NoAnimation ? 1 : XPopup.getAnimationDuration();
    }

    /**
     * 弹窗的最大宽度，一般用来限制布局宽度为wrap或者match时的最大宽度
     *
     * @return
     */
    protected int getMaxWidth() {
        return 0;
    }

    /**
     * 弹窗的最大高度，一般用来限制布局高度为wrap或者match时的最大宽度
     *
     * @return
     */
    protected int getMaxHeight() {
        return popupInfo.maxHeight;
    }

    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    protected int getPopupWidth() {
        return 0;
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    protected int getPopupHeight() {
        return 0;
    }

    protected View getTargetSizeView() {
        return getPopupContentView();
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (popupStatus == PopupStatus.Dismissing) return;
        popupStatus = PopupStatus.Dismissing;
        if (popupInfo.autoOpenSoftInput) KeyboardUtils.hideSoftInput(this);
        clearFocus();
        doDismissAnimation();
        doAfterDismiss();
    }

    public void hide() {
        if (popupStatus == PopupStatus.Hiding) return;
        popupStatus = PopupStatus.Hiding;
        if (popupInfo.autoOpenSoftInput) KeyboardUtils.hideSoftInput(this);
        clearFocus();
        doDismissAnimation();
        doAfterDismiss();
    }

    @Override
    public void clearFocus() {
        super.clearFocus();
//        if (!stack.isEmpty()) stack.pop();
        stack.remove(this);
        if (popupInfo != null && popupInfo.isRequestFocus) {
            if (!stack.isEmpty()) {
                stack.get(stack.size() - 1).focusAndProcessBackPress();
            } else {
                // 让根布局拿焦点，避免布局内RecyclerView类似布局获取焦点导致布局滚动
                View needFocusView = ActivityUtils.getActivity(context).findViewById(android.R.id.content);
                needFocusView.setFocusable(true);
                needFocusView.setFocusableInTouchMode(true);
            }
        }
    }

    public void delayDismiss(long delay) {
        if (delay < 0) delay = 0;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delay);
    }

    public void delayDismissWith(long delay, Runnable runnable) {
        this.dismissWithRunnable = runnable;
        delayDismiss(delay);
    }

    protected void doAfterDismiss() {
        if (popupInfo.autoOpenSoftInput) KeyboardUtils.hideSoftInput(this);
        removeCallbacks(doAfterDismissTask);
        postDelayed(doAfterDismissTask, getAnimationDuration());
    }

    private Runnable doAfterDismissTask = new Runnable() {
        @Override
        public void run() {
            if (popupStatus == PopupStatus.Dismissing) {
                onDismiss();
                if (popupInfo != null && popupInfo.xPopupCallback != null) {
                    popupInfo.xPopupCallback.onDismiss();
                }
                popupStatus = PopupStatus.Dismiss;
            } else if (popupStatus == PopupStatus.Hiding) {
                onHide();
                if (popupInfo != null && popupInfo.xPopupCallback != null) {
                    popupInfo.xPopupCallback.onHide();
                }
                popupStatus = PopupStatus.Hide;
            }

            if (dismissWithRunnable != null) {
                dismissWithRunnable.run();
                dismissWithRunnable = null;//no cache, avoid some bad edge effect.
            }
//            if (popupStatus == PopupStatus.Dismissing) {
//                popupStatus = PopupStatus.Dismiss;
//            } else if (popupStatus == PopupStatus.Hiding) {
//                popupStatus = PopupStatus.Hide;
//            }

            NavigationBarObserver.getInstance().removeOnNavigationBarListener(BasePopup.this);

//            if (!stack.isEmpty()) stack.pop();
//            if (popupInfo != null && popupInfo.isRequestFocus) {
//                if (!stack.isEmpty()) {
//                    stack.get(stack.size() - 1).focusAndProcessBackPress();
//                } else {
//                    // 让根布局拿焦点，避免布局内RecyclerView类似布局获取焦点导致布局滚动
//                    View needFocusView = ActivityUtils.getActivity(context).findViewById(android.R.id.content);
//                    needFocusView.setFocusable(true);
//                    needFocusView.setFocusableInTouchMode(true);
//                }
//            }

            // 移除弹窗，GameOver
            if (popupInfo.decorView != null) {
                popupInfo.decorView.removeView(BasePopup.this);
                KeyboardUtils.removeLayoutChangeListener(popupInfo.decorView, BasePopup.this);
            }
        }
    };

    Runnable dismissWithRunnable;

    public void dismissWith(Runnable runnable) {
        this.dismissWithRunnable = runnable;
        dismiss();
    }

    public boolean isShow() {
        return !isDismiss() && !isHide();
    }

    public boolean isDismiss() {
        return popupStatus == PopupStatus.Dismiss;
    }

    public boolean isHide() {
        return popupStatus == PopupStatus.Hide;
    }

    public void toggle() {
        if (isShow()) {
            dismiss();
        } else {
            show();
        }
    }

    protected boolean onBackPressed() {
        if (onBackPressedListener != null) {
            return onBackPressedListener.onBackPressed();
        }
        return false;
    }

    /**
     * 消失动画执行完毕后执行
     */
    protected void onDismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    protected void onHide() {
        if (onHideListener != null) {
            onHideListener.onHide();
        }
    }

    /**
     * 显示动画执行完毕后执行
     */
    protected void onShow() {
        if (onShowListener != null) {
            onShowListener.onShow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stack.clear();
        removeCallbacks(doAfterShowTask);
        removeCallbacks(doAfterDismissTask);
        KeyboardUtils.removeLayoutChangeListener(popupInfo.decorView, BasePopup.this);
        if (showSoftInputTask != null) removeCallbacks(showSoftInputTask);
        if (popupStatus == PopupStatus.Dismissing) {
            popupStatus = PopupStatus.Dismiss;
        } else if (popupStatus == PopupStatus.Hiding) {
            popupStatus = PopupStatus.Hide;
        }
        showSoftInputTask = null;
        hasMoveUp = false;
    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果自己接触到了点击，并且不在PopupContentView范围内点击，则进行判断是否是点击事件,如果是，则dismiss
        if (popupInfo.handleTouchOutsideEvent) {
            Rect rect = new Rect();
            getPopupContentView().getGlobalVisibleRect(rect);
            if (!XPopupUtils.isInRect(event.getX(), event.getY(), rect)) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float dx = event.getX() - x;
                        float dy = event.getY() - y;
                        float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                        if (distance < touchSlop && popupInfo.isDismissOnTouchOutside) {
                            dismiss();
                        }
                        x = 0;
                        y = 0;
                        break;
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public T setCancelable(boolean cancelable) {
        popupInfo.isDismissOnTouchOutside = cancelable;
        popupInfo.isDismissOnBackPressed = cancelable;
        return self();
    }

    public T setCanceledOnTouchOutside(boolean cancelable) {
        popupInfo.isDismissOnTouchOutside = cancelable;
        return self();
    }


}
