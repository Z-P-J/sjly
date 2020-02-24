package com.zpj.widget.toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.widget.toolbar.R;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.StatusBarUtils;

public abstract class BaseToolbar extends RelativeLayout implements ViewStub.OnInflateListener {

    protected boolean fillStatusBar;                      // 是否撑起状态栏, true时,标题栏浸入状态栏
    protected int titleBarColor;                          // 标题栏背景颜色
    protected int statusBarColor;                         // 状态栏颜色
    protected int statusBarMode;                          // 状态栏模式
    protected int statusBarHeight = 0;                    // 标题栏高度

    protected int titleBarHeight;                         // 标题栏高度

    protected boolean showBottomLine;                     // 是否显示底部分割线
    protected int bottomLineColor;                        // 分割线颜色
    protected float bottomShadowHeight;                   // 底部阴影高度

    protected int bottomHeight;

    protected ViewStub vsLeftContainer;
    protected ViewStub vsMiddleContainer;
    protected ViewStub vsRightContainer;

    protected View inflatedLeft;
    protected View inflatedMiddle;
    protected View inflatedRight;

    protected View viewStatusBarFill;                     // 状态栏填充视图
    private View viewBottomLine;                        // 分隔线视图
    private View viewBottomShadow;                      // 底部阴影




    public BaseToolbar(Context context) {
        this(context, null);
    }

    public BaseToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttribute(context, attrs);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        setBackgroundColor(titleBarColor);

        if (fillStatusBar) {
            transparentStatusBar(context);
        }

        // 构建分割线视图
        if (showBottomLine) {
            // 已设置显示标题栏分隔线,5.0以下机型,显示分隔线
            viewBottomLine = new View(context);
            viewBottomLine.setId(generateViewId());
            viewBottomLine.setBackgroundColor(bottomLineColor);
            bottomHeight = Math.max(1, ScreenUtils.dp2pxInt(context, 0.4f));
            LayoutParams bottomLineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bottomHeight);
//            bottomLineParams.addRule(ALIGN_PARENT_BOTTOM);
            addView(viewBottomLine, bottomLineParams);
        } else if (bottomShadowHeight != 0) {
            viewBottomShadow = new View(context);
            viewBottomShadow.setId(generateViewId());
            viewBottomShadow.setBackgroundResource(R.drawable.comm_titlebar_bottom_shadow);
            bottomHeight = ScreenUtils.dp2pxInt(context, bottomShadowHeight);
            LayoutParams bottomShadowParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bottomHeight);
//            bottomShadowParams.addRule(ALIGN_PARENT_BOTTOM);
            addView(viewBottomShadow, bottomShadowParams);
        }

        inflateLeftContainer(vsLeftContainer);
        inflateRightContainer(vsRightContainer);
        inflateMiddleContainer(vsMiddleContainer);

        int id = NO_ID;
        if (inflatedLeft != null) {
            id = getInflatedId(vsLeftContainer, inflatedLeft);
        } else if (inflatedRight != null) {
            id = getInflatedId(vsRightContainer, inflatedRight);
        } else if (inflatedMiddle != null) {
            id = getInflatedId(vsMiddleContainer, inflatedMiddle);
        } else if (viewStatusBarFill != null) {
            id = viewStatusBarFill.getId();
        }
        if (id != NO_ID) {
            if (viewBottomLine != null) {
                LayoutParams layoutParams = (LayoutParams) viewBottomLine.getLayoutParams();
                layoutParams.addRule(BELOW, id);
            } else if (viewBottomShadow != null) {
                LayoutParams layoutParams = (LayoutParams) viewBottomShadow.getLayoutParams();
                layoutParams.addRule(BELOW, id);
            }
        }
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.z_toolbar, this, true);
        vsLeftContainer = findViewById(R.id.vs_left_container);
        vsMiddleContainer = findViewById(R.id.vs_middle_container);
        vsRightContainer = findViewById(R.id.vs_right_container);

        vsLeftContainer.setOnInflateListener(this);
        vsMiddleContainer.setOnInflateListener(this);
        vsRightContainer.setOnInflateListener(this);
    }
    public void initAttribute(final Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BaseToolbar);

        fillStatusBar = array.getBoolean(R.styleable.BaseToolbar_z_toolbar_fillStatusBar, false);
        titleBarColor = array.getColor(R.styleable.BaseToolbar_z_toolbar_titleBarColor, Color.WHITE);
        titleBarHeight = (int) array.getDimension(R.styleable.BaseToolbar_z_toolbar_titleBarHeight, ScreenUtils.dp2pxInt(context, 56));
        statusBarColor = array.getColor(R.styleable.BaseToolbar_z_toolbar_statusBarColor, Color.WHITE);
        statusBarMode = array.getInt(R.styleable.BaseToolbar_z_toolbar_statusBarMode, 0);

        showBottomLine = array.getBoolean(R.styleable.BaseToolbar_z_toolbar_showBottomLine, false);
        bottomLineColor = array.getColor(R.styleable.BaseToolbar_z_toolbar_bottomLineColor, Color.parseColor("#f8f8f8"));
        bottomShadowHeight = array.getDimension(R.styleable.BaseToolbar_z_toolbar_bottomShadowHeight, 0);
        array.recycle();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setUpImmersionTitleBar();
    }

    @Override
    public void onInflate(ViewStub stub, View inflated) {
        LayoutParams params = (LayoutParams) inflated.getLayoutParams();
        if (viewStatusBarFill != null) {
            params.addRule(BELOW, viewStatusBarFill.getId());
        }
        params.height = titleBarHeight;

        if (stub == vsLeftContainer) {
            inflatedLeft = inflated;
            params.addRule(ALIGN_PARENT_START);
        } if (stub == vsRightContainer) {
            inflatedRight = inflated;
            params.addRule(ALIGN_PARENT_END);
        } if (stub == vsMiddleContainer) {
            inflatedMiddle = inflated;
            if (inflatedLeft != null) {
                params.addRule(END_OF, getInflatedId(vsLeftContainer, inflatedLeft));
            } else {
                params.addRule(ALIGN_PARENT_START);
                inflatedMiddle.setPadding(
                        ScreenUtils.dp2pxInt(getContext(), 16),
                        inflatedMiddle.getPaddingTop(),
                        inflatedMiddle.getPaddingEnd(),
                        inflatedMiddle.getPaddingBottom()
                );
            }
            if (inflatedRight != null) {
                params.addRule(START_OF, getInflatedId(vsRightContainer, inflatedRight));
            } else {
                params.addRule(ALIGN_PARENT_END);
                inflatedMiddle.setPadding(
                        inflatedMiddle.getPaddingStart(),
                        inflatedMiddle.getPaddingTop(),
                        ScreenUtils.dp2pxInt(getContext(), 16),
                        inflatedMiddle.getPaddingBottom()
                );
            }
        }
    }

    protected int getInflatedId(ViewStub stub, View inflated) {
        if (stub.getInflatedId() == NO_ID) {
            if (inflated.getId() == NO_ID) {
                inflated.setId(generateViewId());
            }
            return inflated.getId();
        } else {
            return stub.getInflatedId();
        }
    }

    protected void transparentStatusBar(Context context) {
        boolean transparentStatusBar = StatusBarUtils.supportTransparentStatusBar();
        if (transparentStatusBar) {
            statusBarHeight = StatusBarUtils.getStatusBarHeight(context);
            viewStatusBarFill = new View(context);
            viewStatusBarFill.setId(generateViewId());
            viewStatusBarFill.setBackgroundColor(statusBarColor);
            LayoutParams statusBarParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            statusBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            addView(viewStatusBarFill, statusBarParams);
        }
    }

    private void setUpImmersionTitleBar() {
        Window window = getWindow();
        if (window == null) return;
        // 设置状态栏背景透明
        StatusBarUtils.transparentStatusBar(window);
        // 设置图标主题
        if (statusBarMode == 0) {
            StatusBarUtils.setDarkMode(window);
        } else {
            StatusBarUtils.setLightMode(window);
        }
    }

    private Window getWindow() {
        Context context = getContext();
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else if (context instanceof ContextWrapper) {
            activity = (Activity) ((ContextWrapper) context).getBaseContext();
        }
        if (activity != null) {
            return activity.getWindow();
        }
        return null;
    }










    @Override
    public void setBackgroundColor(int color) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill.setBackgroundColor(color);
        }
        titleBarColor = color;
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resource) {
        setBackgroundColor(Color.TRANSPARENT);
        super.setBackgroundResource(resource);
    }

    public void setStatusBarColor(int color) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill.setBackgroundColor(color);
        }
    }

    /**
     * 是否填充状态栏
     *
     * @param show
     */
    public void showStatusBar(boolean show) {
        if (viewStatusBarFill != null) {
            viewStatusBarFill.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void toggleStatusBarMode() {
        Window window = getWindow();
        if (window == null) return;
        StatusBarUtils.transparentStatusBar(window);
        if (statusBarMode == 0) {
            statusBarMode = 1;
            StatusBarUtils.setLightMode(window);
        } else {
            statusBarMode = 0;
            StatusBarUtils.setDarkMode(window);
        }
    }

    protected abstract void inflateLeftContainer(ViewStub viewStub);

    protected abstract void inflateMiddleContainer(ViewStub viewStub);

    protected abstract void inflateRightContainer(ViewStub viewStub);

}
