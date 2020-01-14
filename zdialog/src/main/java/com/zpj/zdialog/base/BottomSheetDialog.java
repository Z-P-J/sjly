package com.zpj.zdialog.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.zpj.zdialog.R;

public class BottomSheetDialog extends OutsideClickDialog {
    private BottomSheetBehavior<FrameLayout> behavior;
    boolean cancelable;
    private boolean canceledOnTouchOutside;
    private boolean canceledOnTouchOutsideSet;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    class NamelessClass_1 extends BottomSheetBehavior.BottomSheetCallback {
        NamelessClass_1() {
        }

        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == 5) {
                BottomSheetDialog.this.cancel();
            }

        }

        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    }

    public BottomSheetDialog(@NonNull Context context) {
        this(context, 0);
    }

    public BottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, getThemeResId(context, theme));
        this.cancelable = true;
        this.canceledOnTouchOutside = true;
        this.bottomSheetCallback = new NamelessClass_1();
    }

    protected BottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.cancelable = true;
        this.canceledOnTouchOutside = true;

        this.bottomSheetCallback = new NamelessClass_1();
        this.cancelable = cancelable;
    }

    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(this.wrapInBottomSheet(layoutResId, null, null));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                window.clearFlags(67108864);
                window.addFlags(-2147483648);
            }

            window.setLayout(-1, -1);
        }

    }

    public void setContentView(View view) {
        super.setContentView(this.wrapInBottomSheet(0, view, null));
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(this.wrapInBottomSheet(0, view, params));
    }

    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (this.cancelable != cancelable) {
            this.cancelable = cancelable;
            if (this.behavior != null) {
                this.behavior.setHideable(cancelable);
            }
        }

    }

    protected void onStart() {
        super.onStart();
        if (this.behavior != null && this.behavior.getState() == 5) {
            this.behavior.setState(4);
        }

    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !this.cancelable) {
            this.cancelable = true;
        }

        this.canceledOnTouchOutside = cancel;
        this.canceledOnTouchOutsideSet = true;
    }

    private View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        FrameLayout container = (FrameLayout)View.inflate(this.getContext(), R.layout.z_bottom_sheet_dialog, null);
        CoordinatorLayout coordinator = container.findViewById(R.id.coordinator);
        if (layoutResId != 0 && view == null) {
            view = this.getLayoutInflater().inflate(layoutResId, coordinator, false);
        }

        FrameLayout bottomSheet = coordinator.findViewById(R.id.design_bottom_sheet);
        this.behavior = BottomSheetBehavior.from(bottomSheet);
        this.behavior.setBottomSheetCallback(this.bottomSheetCallback);
        this.behavior.setHideable(this.cancelable);
        if (params == null) {
            bottomSheet.addView(view);
        } else {
            bottomSheet.addView(view, params);
        }

        coordinator.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (onTouchOutsideListener != null) {
                    onTouchOutsideListener.onTouchOutside();
                }
//                if (BottomSheetDialog.this.cancelable && BottomSheetDialog.this.isShowing() && BottomSheetDialog.this.shouldWindowCloseOnTouchOutside()) {
//                    BottomSheetDialog.this.dismiss();
//                }
            }
        });
        ViewCompat.setAccessibilityDelegate(bottomSheet, new AccessibilityDelegateCompat() {
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                if (BottomSheetDialog.this.cancelable) {
                    info.addAction(1048576);
                    info.setDismissable(true);
                } else {
                    info.setDismissable(false);
                }

            }

            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == 1048576 && BottomSheetDialog.this.cancelable) {
                    BottomSheetDialog.this.cancel();
                    return true;
                } else {
                    return super.performAccessibilityAction(host, action, args);
                }
            }
        });
        bottomSheet.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return true;
            }
        });
        return container;
    }

    boolean shouldWindowCloseOnTouchOutside() {
        if (!this.canceledOnTouchOutsideSet) {
            TypedArray a = this.getContext().obtainStyledAttributes(new int[]{16843611});
            this.canceledOnTouchOutside = a.getBoolean(0, true);
            a.recycle();
            this.canceledOnTouchOutsideSet = true;
        }

        return this.canceledOnTouchOutside;
    }

    private static int getThemeResId(Context context, int themeId) {
        if (themeId == 0) {
//            TypedValue outValue = new TypedValue();
//            if (context.getTheme().resolveAttribute(R.attr.bottomSheetDialogTheme, outValue, true)) {
//                themeId = outValue.resourceId;
//            } else {
//                themeId = R.style.Theme_Design_Light_BottomSheetDialog;
//            }
            themeId = R.style.Z_Light_BottomSheetDialog;
        }

        return themeId;
    }
}

