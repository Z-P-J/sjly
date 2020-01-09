package com.zpj.popupmenuview.popup;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.popupmenuview.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EverywherePopup extends BasePopup<EverywherePopup> {

    private static final String TAG = "EverywherePopup";

    public interface OnItemClickListener {
        void onItemClicked(String title, int position);
    }

    public static EverywherePopup create(Context context) {
        return new EverywherePopup(context);
    }

    private final List<String> titleList = new ArrayList<>();
    private final List<Integer> iconList = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

    private EverywherePopup(Context context) {
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_everywhere_pop, null);
        LinearLayout container = view.findViewById(R.id.container);
//        int i = 0;
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            final TextView textView = (TextView) LayoutInflater.from(view.getContext())
                    .inflate(R.layout.item_popup_menu, null, false);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ViewUtil.dp2px(context, menuItemHeight));
            textView.setTag(i);
            textView.setText(title);
            if (i < iconList.size()) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(iconList.get(i), 0, 0, 0);
            }
            container.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        int position = (int) textView.getTag();
                        mOnItemClickListener.onItemClicked(textView.getText().toString(), position);
                    }
                    dismiss();
                }
            });
        }
//        for (String title : titleList) {
//
//            i++;
//        }
        setContentView(view)
                .setAnimationStyle(R.style.LeftTopPopAnim);
//        setContentView(R.layout.layout_everywhere_pop)
//                .setAnimationStyle(R.style.LeftTopPopAnim);
    }

    @Override
    protected void initViews(View view, EverywherePopup basePopup) {

    }

    public EverywherePopup addItem(String itemTitle) {
        titleList.add(itemTitle);
        return this;
    }

    public EverywherePopup addItems(String... itemTitles) {
        titleList.addAll(Arrays.asList(itemTitles));
        return this;
    }

    public EverywherePopup addItems(List<String> itemTitles) {
        titleList.addAll(itemTitles);
        return this;
    }

    public EverywherePopup addIcon(@DrawableRes int itemTitle) {
        iconList.add(itemTitle);
        return this;
    }

    public EverywherePopup addIcons(@DrawableRes Integer... itemTitles) {
        iconList.addAll(Arrays.asList(itemTitles));
        return this;
    }

    public EverywherePopup addIcons(List<Integer> itemTitles) {
        iconList.addAll(itemTitles);
        return this;
    }

    public EverywherePopup setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    public EverywherePopup show(View parent) {
        apply();
        int[] location = new int[2];
        parent.getLocationInWindow(location);
        int touchX = location[0] + parent.getWidth() / 2;
        int touchY = location[1] + parent.getHeight() / 2;
//        showEverywhere(parent, location[0] + parent.getWidth() / 2, location[1] + parent.getHeight() / 2);
        int screenHeight = ScreenUtil.getScreenHeight(parent.getContext());
        int screenWidth = ScreenUtil.getScreenWidth(parent.getContext());
        int width = getWidth();
        int height = getHeight();
        int offsetX = touchX;
        int offsetY = touchY;

        boolean showLeft = touchX > screenWidth / 2;
        boolean showTop = touchY > screenHeight / 2;
        if (showTop && touchY + height < screenHeight) {
            showTop = false;
        }
        if (showLeft) {
            if (showTop) {
                Log.d(TAG, "右下");
                getPopupWindow().setAnimationStyle(R.style.RightBottomPopAnim);
                offsetX = location[0] + parent.getWidth() - width;
                offsetY = location[1] + parent.getHeight() - height;
            } else {
                Log.d(TAG, "右上");
                getPopupWindow().setAnimationStyle(R.style.RightTopPopAnim);
                offsetX = location[0] + parent.getWidth() - width;
                offsetY = location[1];
            }
        } else {
            if (showTop) {
                Log.d(TAG, "左下");
                getPopupWindow().setAnimationStyle(R.style.LeftBottomPopAnim);
                offsetX = location[0];
                offsetY = location[1] + parent.getHeight() - height;
            } else {
                Log.d(TAG, "左上");
                getPopupWindow().setAnimationStyle(R.style.LeftTopPopAnim);
                offsetX = location[0];
                offsetY = location[1];
            }
        }
        showAtLocation(parent, Gravity.NO_GRAVITY, offsetX, offsetY);
        return this;
    }

    public EverywherePopup showEverywhere(View parent, float touchX, float touchY) {
        showEverywhere(parent, (int) touchX, (int) touchY);
        return this;
    }

    /**
     * 自适应触摸点 弹出
     *
     * @param parent
     * @param touchX
     * @param touchY
     * @return
     */
    public EverywherePopup showEverywhere(View parent, int touchX, int touchY) {
        int screenHeight = ScreenUtil.getScreenHeight(parent.getContext());
        int screenWidth = ScreenUtil.getScreenWidth(parent.getContext());
        int width = getWidth();
        int height = getHeight();
        int offsetX = touchX;
        int offsetY = touchY;
//        Log.d(TAG, "touchX=" + touchX + " touchY=" + touchY
//                + " screenWidth=" + screenWidth + " screenHeight=" + screenHeight
//                + " width=" + getWidth() + " height=" + getHeight());
//        Log.d(TAG, "touchX < getWidth()=" + (touchX < getWidth()));
//        Log.d(TAG, "screenHeight - touchY < getHeight()=" + (screenHeight - touchY < getHeight()));
//        Log.d(TAG, "touchX + getWidth() > screenWidth=" + (touchX + getWidth() > screenWidth));
//        Log.d(TAG, "touchY + getHeight() > screenHeight=" + (touchY + getHeight() > screenHeight));
//        Log.d(TAG, "touchX + getWidth() > screenWidth=" + (touchX + getWidth() > screenWidth));

        boolean showLeft = touchX > screenWidth / 2;
        boolean showTop = touchY > screenHeight / 2;
//        Log.d(TAG, "showLeft=" + showLeft + " showTop=" + showTop);
        if (showTop && touchY + height < screenHeight) {
            showTop = false;
        }
//        Log.d(TAG, "showLeft=" + showLeft + " showTop=" + showTop);
        if (showLeft) {
            if (showTop) {
                Log.d(TAG, "右下");
                getPopupWindow().setAnimationStyle(R.style.RightBottomPopAnim);
                offsetX = (touchX - width);
                offsetY = touchY - height;
            } else {
                Log.d(TAG, "右上");
                getPopupWindow().setAnimationStyle(R.style.RightTopPopAnim);
                offsetX = (touchX - width);
            }
        } else {
            if (showTop) {
                Log.d(TAG, "左下");
                getPopupWindow().setAnimationStyle(R.style.LeftBottomPopAnim);
                offsetY = touchY - height;
            } else {
                Log.d(TAG, "左上");
                getPopupWindow().setAnimationStyle(R.style.LeftTopPopAnim);
            }
        }


//        if (touchX + getWidth() < screenWidth && screenHeight - touchY < getHeight()) {
//            //左下弹出动画
//            Log.d(TAG, "左下");
//            getPopupWindow().setAnimationStyle(R.style.LeftBottomPopAnim);
//            offsetY = touchY - getHeight();
//        } else if (touchX + getWidth() > screenWidth && touchY + getHeight() > screenHeight) {
//            //右下弹出动画
//            Log.d(TAG, "右下");
//            getPopupWindow().setAnimationStyle(R.style.RightBottomPopAnim);
//            offsetX = (touchX - getWidth());
//            offsetY = touchY - getHeight();
//        } else if (touchX + getWidth() > screenWidth) {
//            Log.d(TAG, "右上");
//            getPopupWindow().setAnimationStyle(R.style.RightTopPopAnim);
//            offsetX = (touchX - getWidth());
//        } else {
//            Log.d(TAG, "左上");
//            getPopupWindow().setAnimationStyle(R.style.LeftTopPopAnim);
//        }
        Log.d(TAG, "offsetX=" + offsetX + " offsetY=" + offsetY);
        showAtLocation(parent, Gravity.NO_GRAVITY, offsetX, offsetY);
        return this;
    }
}
