package com.zpj.shouji.market.ui.widget.selection;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.shouji.market.R;

/**
 * Created by wangyang53 on 2018/4/9.
 */

public class SimpleSelectableTextView extends TextView {
    private PopupWindow popupWindow;

    public SimpleSelectableTextView(Context context) {
        super(context);
        init();
    }

    public SimpleSelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleSelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTextIsSelectable(false);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showOperationPopWindow();
                return true;
            }
        });
    }

    private void showOperationPopWindow() {
        popupWindow = new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout contentView = new LinearLayout(getContext());
        contentView.setOrientation(LinearLayout.VERTICAL);

        LinearLayout ll_list = new LinearLayout(getContext());
        ll_list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_list.setOrientation(LinearLayout.HORIZONTAL);
        ll_list.setBackgroundResource(R.drawable.bg_operation);
        ll_list.setPadding(20, 10, 20, 10);
        TextView tv_copy = new TextView(getContext());
        tv_copy.setText("复制全部");
        tv_copy.setTextColor(Color.WHITE);
        tv_copy.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_list.addView(tv_copy);

        contentView.addView(ll_list);

        tv_copy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                onCopy();
            }
        });

        View arrow = new View(getContext());
        arrow.setBackgroundResource(R.drawable.triangle_down);
        LinearLayout.LayoutParams arrowLp = new LinearLayout.LayoutParams(17, 17);
        arrow.setLayoutParams(arrowLp);
        contentView.addView(arrow);

        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        popupWindow.setContentView(contentView);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);

        contentView.measure(0, 0);
        int height = popupWindow.getContentView().getMeasuredHeight();
        WindowManager manager = ((Activity) getContext()).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int screenHeight = outMetrics.heightPixels;
        int[] loc = new int[2];
//        getLocationInWindow(loc);
        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        loc[0] = rect.left;
        loc[1] = rect.top;

        int visibleHeight = rect.bottom - rect.top;

        int x = loc[0];
        if (x < 0)
            x = 20;
        int y = loc[1] - 20 - height;
        boolean isDown = true;
        if (y < screenHeight / 5) {
            y = loc[1] + 20 + visibleHeight;
            isDown = false;
        }
        if (y > screenHeight / 5 * 4) {
            if (loc[1] - 20 - height < screenHeight / 5) {
                isDown = false;
                y = loc[1] + 20;
            } else {
                y = loc[1] - 20 - height;
                isDown = true;
            }
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(17, 17);
        lp.setMargins(20, 0, 0, 0);
        if (isDown) {
            arrow.setBackgroundResource(R.drawable.triangle_down);
            contentView.removeView(arrow);
            contentView.addView(arrow, lp);
        } else {
            arrow.setBackgroundResource(R.drawable.triangle_up);
            contentView.removeView(arrow);
            contentView.addView(arrow, 0, lp);
        }

        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, x, y);

    }


    protected void onCopy() {
        Toast.makeText(getContext(), getText(), Toast.LENGTH_SHORT).show();
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(getText());
    }


}
