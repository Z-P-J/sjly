package com.zpj.markdown;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build.VERSION_CODES;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: BaseSoftInputLayout, v 0.1 2018/11/26 22:36 shouh Exp$
 */
public abstract class BaseSoftInputLayout extends FrameLayout {

    /**
     * The flag, the container will automatically be showed and hidden
     * when the keyboard state changed.
     */
    protected final static int FLAG_AUTOMATIC = 0x00;

    /**
     * The flag, only show the soft input layout, the container will be displayed.
     */
    protected final static int SHOW_SOFT_INPUT_FLAG_ONLY = 0x02;

    /**
     * The flag, hide the soft input layout only, the container won't be hidden.
     */
    protected final static int HIDE_SOFT_INPUT_FLAG_ONLY = 0x03;

    /**
     * Field is the keyboard displaying.
     */
    private boolean keyboardShowing;

    /**
     * Navigation bar height
     */
    private int navigationBarHeight = -1;

    /**
     * The height of keyboard.
     */
    protected int keyboardHeight;

    /**
     * If you want to add a control panel to the container, add it the layout and set the inner
     * panel height using {@link #setOverHeight(int)}. The control panel will be managed by the
     * base soft input layout automatically.
     */
    private int overHeight;

    /**
     * Decor view, or current view
     */
    private View rootView;

    /**
     * The keyboard state change listener.
     */
    private List<OnKeyboardStateChangeListener> onKeyboardStateChangeListeners;

    private View frame;
    private View container;
    private EditText editText;

    protected int stateFlag;
    protected int lastHitBottom;
    protected int lastCoverHeight;
    protected int hiddenHeight;

    public BaseSoftInputLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public BaseSoftInputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public BaseSoftInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public BaseSoftInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Initialize view, for example get attributes from xml, etc. The child can implement this method
     * to add custom behaviors.
     *
     * @param context the context
     * @param attrs the attribute set
     * @param defStyleAttr default style attributes
     * @param defStyleRes default style resources
     */
    protected abstract void doInitView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes);

    /**
     * Get the layout frame view.
     *
     * @return the frame view
     */
    protected abstract View getFrame();

    /**
     * Get the container view.
     *
     * @return the container view
     */
    protected abstract View getContainer();

    /**
     * Method for child to implement to get the associated EditText.
     *
     * @return the associated EditText
     */
    protected abstract EditText getEditText();

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        /* Config the keyboard state change listeners. */
        this.onKeyboardStateChangeListeners = new LinkedList<>();
//        this.onKeyboardStateChangeListeners.add(new OnKeyboardStateChangeListener() {
//            @Override
//            public void onShown(int height) {
//                if (stateFlag == FLAG_AUTOMATIC) {
//                    showContainer();
//                }
//                stateFlag = FLAG_AUTOMATIC;
//            }
//
//            @Override
//            public void onHidden(int height) {
//                if (stateFlag == FLAG_AUTOMATIC) {
//                    hideContainer();
//                }
//                stateFlag = FLAG_AUTOMATIC;
//            }
//        });

        /* Initialize view */
        doInitView(context, attrs, defStyleAttr, defStyleRes);

        /* Get the views from the child implementation. */
        frame = getFrame();
        container = getContainer();
        editText = getEditText();

        /* Get the root view. */
        if (context instanceof Activity) {
            rootView = ((Activity) context).getWindow().getDecorView();
        } else {
            rootView = this;
        }

        /* Add layout change observer to the root view. */
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                detectKeyboardState();
            }
        });
    }

    /**
     * Detect the keyboard state when the global layout state changed.
     */
    private void detectKeyboardState() {
        /* Get the visible size of root view. */
        Rect visibleRect = new Rect();
        rootView.getWindowVisibleDisplayFrame(visibleRect);

        /* Get size of root view. */
        Rect hitRect = new Rect();
        rootView.getHitRect(hitRect);

        /* Get the cover height, we will use to judge keyboard state. */
        int coverHeight = hitRect.bottom - visibleRect.bottom;

        /* Fix show/hide navigation bar for MeiZu */
        if (lastCoverHeight == coverHeight && lastHitBottom == hitRect.bottom) {
            return;
        }

        /* Keep last values */
        lastHitBottom = hitRect.bottom;
        int deltaCoverHeight = coverHeight - lastCoverHeight;
        lastCoverHeight = coverHeight;

        if (coverHeight > navigationBarHeight) {
            /* Fix show/hide navigation bar for HuaWei */
            if ((deltaCoverHeight == navigationBarHeight
                    || deltaCoverHeight == -navigationBarHeight) && keyboardShowing) {
                hiddenHeight += deltaCoverHeight;
            }
            int shownHeight =   - hiddenHeight;

            /* Calculate the height of the container. */
            if (keyboardHeight != shownHeight) {
                keyboardHeight = shownHeight;
                container.getLayoutParams().height = shownHeight + overHeight;
                container.requestLayout();
            }

            /* Keyboard state callback */
            if (!onKeyboardStateChangeListeners.isEmpty()) {
                for (OnKeyboardStateChangeListener listener : onKeyboardStateChangeListeners) {
                    listener.onShown(hiddenHeight);
                }
            }

            keyboardShowing = true;

            refreshFrameLayout(visibleRect.bottom + shownHeight);
        } else {
            /* Fix show/hide navigation bar for HuaWei */
            if ((deltaCoverHeight == navigationBarHeight
                    || deltaCoverHeight == -navigationBarHeight) && !keyboardShowing) {
                hiddenHeight += deltaCoverHeight;
            }

            if (coverHeight != hiddenHeight) {
                hiddenHeight = coverHeight;
            }

            /* Keyboard state callback */
            if (!onKeyboardStateChangeListeners.isEmpty()) {
                for (OnKeyboardStateChangeListener listener : onKeyboardStateChangeListeners) {
                    listener.onHidden(hiddenHeight);
                }
            }

            keyboardShowing = false;

            refreshFrameLayout(visibleRect.bottom);
        }
    }

    /**
     * Refresh the layout of {@link #frame}. This method is used to config the height of the frame,
     * that is, the height of the parent of the whole view.
     *
     * @param bottom the bottom
     */
    private void refreshFrameLayout(int bottom) {
        /* Get the focusable size of frame.. */
        Rect rect = new Rect();
        frame.getHitRect(rect);

        /* Get the coordinate x, y of the frame */
        int[] location = new int[2];
        frame.getLocationInWindow(location);
        int height = bottom - rect.top - location[1];

        /* Refresh the height of the frame */
        if (height != frame.getLayoutParams().height) {
            frame.getLayoutParams().height = height;
            frame.requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (navigationBarHeight == -1) {
            frame.getLayoutParams().height = getMeasuredHeight();
            navigationBarHeight = getNavigationBarHeight(getContext());
        }
    }

    static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
            if (id > 0) {
                navigationBarHeight = rs.getDimensionPixelSize(id);
            }
        } catch (Exception e) {
            // default 0
        }
        return navigationBarHeight;
    }

    /**
     * Show the soft input layout.
     */
    public void showSoftInput() {
        if(editText == null) return;
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * Hide the soft input layout.
     */
    public void hideSoftInput() {
        if(editText == null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Show soft input layout but don't show the container.
     */
    public void showSoftInputOnly() {
        stateFlag = SHOW_SOFT_INPUT_FLAG_ONLY;
        showSoftInput();
    }

    /**
     * Hide the soft input layout, but keep the container
     */
    public void hideSoftInputOnly() {
        stateFlag = HIDE_SOFT_INPUT_FLAG_ONLY;
        hideSoftInput();
    }

    /**
     * Display the container.
     */
    public void showContainer() {
        if (container != null) {
            container.setVisibility(VISIBLE);
        }
    }

    /**
     * Hide the container.
     */
    public void hideContainer() {
        if (container != null) {
            container.setVisibility(GONE);
        }
    }

    /**
     * Set the height above the keyboard. The layout on the area will be managed by the container.
     *
     * @param overHeight the over height
     */
    public void setOverHeight(int overHeight) {
        this.overHeight = overHeight;
    }

    public int getOverHeight() {
        return overHeight;
    }

    /**
     * Is the keyboard displaying
     *
     * @return true->displaying
     */
    public boolean isKeyboardShowing() {
        return keyboardShowing;
    }

    /**
     * Get the keyboard height, the value is valid only when the keyboard is shown
     *
     * @return the height of keyboard
     */
    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    /**
     * Add the keyboard state change listener to the list.
     *
     * @param onKeyboardStateChangeListener the keyboard state change listener
     */
    public void addOnKeyboardStateChangeListener(OnKeyboardStateChangeListener onKeyboardStateChangeListener) {
        this.onKeyboardStateChangeListeners.add(onKeyboardStateChangeListener);
    }

    public interface OnKeyboardStateChangeListener {
        /**
         * Called when the keyboard is showing
         *
         * @param height the height of keyboard
         */
        void onShown(int height);

        /**
         * Called when the keyboard is hidden
         *
         * @param height the height of hidden height
         */
        void onHidden(int height);
    }
}
