package com.zpj.shouji.market.ui.widget.selection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpj.shouji.market.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangyang53 on 2018/3/28.
 */

public class OperationView extends LinearLayout {
    private final String TAG = OperationView.class.getSimpleName();
    private Context mContext;
    private LinearLayout ll_list;
    private View arrow;
    private List<OperationItem> operationList = new ArrayList<>();
    private OperationItemClickListener listener;
    private int screenHeight, screenWidth;
    private final int MIN_MARGIN_LEFT_RIGHT = 20;
    private final int MIN_MARGIN_TOP = CursorView.getFixHeight();

    public OperationView(Context context) {
        super(context);
        init(context);
    }

    public OperationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OperationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
        OperationItem item1 = new OperationItem();
        item1.action = OperationItem.ACTION_COPY;
        item1.name = "复制";

        OperationItem item2 = new OperationItem();
        item2.action = OperationItem.ACTION_SELECT_ALL;
        item2.name = "全选";

        OperationItem item3 = new OperationItem();
        item3.action = OperationItem.ACTION_CANCEL;
        item3.name = "取消";

        operationList.add(item1);
        operationList.add(item2);
        operationList.add(item3);

        setBackgroundColor(Color.TRANSPARENT);

//        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        ll_list = new LinearLayout(context);
        ll_list.setOrientation(LinearLayout.HORIZONTAL);
        ll_list.setBackgroundResource(R.drawable.bg_operation);
        ll_list.setPadding(20, 10, 20, 10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_list.setLayoutParams(lp);
        for (int i = 0; i < operationList.size(); i++) {
            final OperationItem item = operationList.get(i);
            TextView textView = new TextView(context);
            MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(Color.WHITE);
            textView.setText(item.name);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onOperationClick(item);
                    }
                }
            });
            ll_list.addView(textView);
            if (i != operationList.size() - 1) {
                View divider = new View(context);
                divider.setBackgroundColor(Color.WHITE);
                LayoutParams mlp = new LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
                mlp.setMargins(20, 0, 20, 0);
                divider.setLayoutParams(mlp);
                ll_list.addView(divider);
            }
        }


        arrow = new View(context);
        arrow.setBackgroundResource(R.drawable.triangle_down);
        RelativeLayout.LayoutParams arrowLp = new RelativeLayout.LayoutParams(17, 17);
        arrow.setLayoutParams(arrowLp);

        addView(ll_list);
        addView(arrow);

        WindowManager manager = ((Activity) mContext).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
    }


    public void setOperationClickListener(OperationItemClickListener listener) {
        this.listener = listener;
    }

    public interface OperationItemClickListener {
        void onOperationClick(OperationItem item);
    }

    public void update(Point left, Point right) {
        Log.d(TAG, "update:" + left + "  " + right);
        int centerArrowX = (left.x + right.x) / 2;
        if (centerArrowX < left.x)
            centerArrowX = left.x;
        int x, y;
        x = centerArrowX - getWidth() / 2;
        if (x + getWidth() > screenWidth - MIN_MARGIN_LEFT_RIGHT) {
            x = screenWidth - MIN_MARGIN_LEFT_RIGHT - getWidth();
        } else if (x < MIN_MARGIN_LEFT_RIGHT) {
            x = MIN_MARGIN_LEFT_RIGHT;
        }

        boolean down = true;
        y = left.y - MIN_MARGIN_TOP - getHeight();
        if (y < screenHeight / 5) {
            y = right.y + MIN_MARGIN_TOP;
            down = false;
        }
        if (y > screenHeight / 5 * 4) {
            int tmp = left.y - MIN_MARGIN_TOP - getHeight();
            if (tmp < screenHeight / 5) {
                y = (left.y + right.y) / 2;
                down = false;
            } else {
                y = tmp;
                down = true;
            }
        }

        setX(x);
        setY(y);
        setArrow(down, (int) (centerArrowX - getX()));
    }

    public void setArrow(boolean down, int x) {
        if (down) {
            arrow.setBackgroundResource(R.drawable.triangle_down);
            removeView(arrow);
            addView(arrow);
        } else {
            arrow.setBackgroundResource(R.drawable.triangle_up);
            removeView(arrow);
            addView(arrow, 0);
        }

        arrow.setX(x);
        invalidate();
    }
}
