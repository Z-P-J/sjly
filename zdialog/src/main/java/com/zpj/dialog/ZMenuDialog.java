package com.zpj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.utils.ScreenUtil;
import com.zpj.dialog.base.IDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZMenuDialog {

    private static final String TAG = "ZMenuDialog";

    public interface OnItemClickListener {
        void onItemClicked(String title, int position);
    }

    private List<String> titleList = new ArrayList<>();
    private final ZDialog dialog;
    private final Context context;
    private int locationX = 0;
    private int locationY = 0;
    private int menuItemHeight = 42;
    private int maxMenuHeight = 0;
//    private int paddingStart;
//    private int paddingTop;
//    private int paddingEnd;
//    private int paddingBottom;
    private OnItemClickListener mOnItemClickListener;

    private ZMenuDialog(Context context) {
        this.context = context;
        dialog = ZDialog.with(context)
                .setSwipeEnable(false)
                .setContentView(R.layout.easy_dialog_list)
                .setWindowBackgroundP(0f);
    }

    public static ZMenuDialog with(Context context) {
        ZMenuDialog dialog = new ZMenuDialog(context);

        return dialog;
    }

    public ZMenuDialog addItems(String... itemTitles) {
        titleList.addAll(Arrays.asList(itemTitles));
        return this;
    }

    public ZMenuDialog addItems(List<String> itemTitles) {
        titleList.addAll(itemTitles);
        return this;
    }

    public ZMenuDialog addItem(String itemTitle) {
        titleList.add(itemTitle);
        return this;
    }

    public ZMenuDialog setItemHeight(int itemHeight) {
        this.menuItemHeight = itemHeight;
        return this;
    }

    public ZMenuDialog setMaxMenuHeight(int maxMenuHeight) {
        if (maxMenuHeight < 36) {
            maxMenuHeight = 36;
        }
        this.maxMenuHeight = ScreenUtil.dp2pxInt(context, maxMenuHeight);
        return this;
    }

    public ZMenuDialog setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    public void show(float x, float y) {
        locationX = (int) x;
        locationY = (int) y;
        dialog.setOnDialogStartListener(new IDialog.OnDialogStartListener() {
            @Override
            public void onStart() {
                Dialog d = dialog.getDialog();
                if (d != null) {
                    Window window = d.getWindow();
                    if (window != null) {
                        initWindow(window);
                    }
                }
            }
        }).setOnViewCreateListener(new IDialog.OnViewCreateListener() {
            @Override
            public void onViewCreate(final IDialog dialog, View view) {
                LinearLayout containerLayout = view.findViewById(R.id.container);
                int i = 0;
                for (String title : titleList) {
                    final TextView textView = (TextView) LayoutInflater.from(view.getContext())
                            .inflate(R.layout.easy_item_popup_list, null, false);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2pxInt(context, menuItemHeight));
//                    textView.setLayoutParams(params);
                    textView.setTag(i);
//                    final ViewTreeObserver observer = textView.getViewTreeObserver();
//                    observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                        @Override
//                        public boolean onPreDraw() {
//                            if (observer.isAlive()) {
//                                observer.removeOnPreDrawListener(this);
//                            }
//                            childHeight = textView.getMeasuredHeight();
//                            Log.d(TAG, "position=" + textView.getTag() + "  height=" + childHeight);
//                            ZDialog zDialog = ZMenuDialog.this.dialog;
//                            Window window = zDialog.getDialog().getWindow();
//                            WindowManager.LayoutParams lp = window.getAttributes();
//
//                            if (locationX > ScreenUtil.getScreenWidth(zDialog.getActivity()) / 2) {
//                                window.setGravity(Gravity.END | Gravity.TOP);
//                                lp.x = ScreenUtil.getScreenWidth(zDialog.getActivity()) - locationX;
//                            } else {
//                                window.setGravity(Gravity.START | Gravity.TOP);
//                                lp.x = locationX;
//                            }
//
//                            int windowHeight = childHeight * titleList.size();
//                            if (locationY + windowHeight > ScreenUtil.getScreenHeight(zDialog.getActivity())) {
//                                locationY -= windowHeight;
//                            }
//                            lp.y = locationY;
//                            lp.dimAmount = 0.0f;
//                            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                            window.setAttributes(lp);
//                            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            return true;
//                        }
//                    });
                    Log.d(TAG, "  getMeasuredHeight=" + textView.getMeasuredHeight());
                    textView.setText(title);
                    containerLayout.addView(textView, params);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnItemClickListener != null) {
                                int position = (int) textView.getTag();
                                mOnItemClickListener.onItemClicked(textView.getText().toString(), position);
                            }
                            dialog.dismiss();
                        }
                    });
                    i++;
                }
            }
        }).show();
    }


    private void initWindow(Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();

        if (locationX > ScreenUtil.getScreenWidth(dialog.getActivity()) / 2) {
            window.setGravity(Gravity.END | Gravity.TOP);
            lp.x = ScreenUtil.getScreenWidth(dialog.getActivity()) - locationX;
        } else {
            window.setGravity(Gravity.START | Gravity.TOP);
            lp.x = locationX;
        }

        int windowHeight = ScreenUtil.dp2pxInt(dialog.getContext(), 42) * titleList.size();
        if (maxMenuHeight > 0 && windowHeight > maxMenuHeight) {
            windowHeight = maxMenuHeight;
            lp.height = windowHeight;
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (locationY + windowHeight > ScreenUtil.getScreenHeight(dialog.getActivity())) {
            locationY -= windowHeight;
            locationY += resources.getDimensionPixelSize(resourceId);
        } else {
            locationY -= resources.getDimensionPixelSize(resourceId);
        }

//        if (resourceId > 0) {
//             lp.y = locationY - resources.getDimensionPixelSize(resourceId);
//        } else {
//            lp.y = locationY;
//        }
        lp.y = locationY;
        lp.dimAmount = 0.0f;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        if (windowHeight == maxMenuHeight) {
//            lp.height = windowHeight;
//        } else {
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        }
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.d(TAG, "getContentView().getMeasuredHeight()=" + dialog.getContentView().getMeasuredHeight());
    }

}
