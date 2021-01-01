package com.zpj.shouji.market.ui.widget.selection;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.zpj.shouji.market.R;

/**
 * Created by wangyang53 on 2018/3/26.
 */

public class SelectableTextView extends TextView implements PromptPopWindow.CursorListener, OperationView.OperationItemClickListener {
    private final String TAG = SelectableTextView.class.getSimpleName();
    private Context mContext;
    private PromptPopWindow promptPopWindow;
    private SelectedTextInfo mSelectedTextInfo;
    private BackgroundColorSpan backgroundColorSpan;
    private int downX, downY;
    private Spannable orgSpannable;
    private InternalOnPreDrawListener mInternalPreOnDrawListener;

    public SelectableTextView(Context context) {
        super(context);
        init(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        backgroundColorSpan = new BackgroundColorSpan(getResources().getColor(R.color.gray_little));
        setTextIsSelectable(false);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mInternalPreOnDrawListener == null) {
                    mInternalPreOnDrawListener = new InternalOnPreDrawListener();
                    getViewTreeObserver().addOnPreDrawListener(mInternalPreOnDrawListener);
                }

                orgSpannable = new SpannableString(getSpannableText());
                selectAll();
                updateSelected();
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (promptPopWindow != null) {
                            promptPopWindow.showOperation();
                        }
                    }
                });

                return true;
            }
        });

    }

    private void updateSelected() {
        Log.d(TAG, "updateSelected");
        if (mSelectedTextInfo == null) {
            return;
        }
        final Spannable spannableText = getSpannableText();
        if (mSelectedTextInfo != null && spannableText != null) {
            showCursor();
            updateText(spannableText);
        }
    }

    private void selectAll() {
        Log.d(TAG, "selectAll");
        if (TextUtils.isEmpty(orgSpannable))
            return;
        mSelectedTextInfo = new SelectedTextInfo();
        mSelectedTextInfo.start = 0;
        mSelectedTextInfo.end = orgSpannable.length();
        mSelectedTextInfo.spannable = orgSpannable;

        Layout layout = getLayout();
        if (layout != null) {
            mSelectedTextInfo.startPosition[0] = (int) layout.getPrimaryHorizontal(mSelectedTextInfo.start);
            int startLine = layout.getLineForOffset(mSelectedTextInfo.start);
            mSelectedTextInfo.startPosition[1] = (int) layout.getLineBottom(startLine);
            mSelectedTextInfo.startLineTop = layout.getLineTop(startLine);

            int endLine = layout.getLineForOffset(mSelectedTextInfo.end);
            mSelectedTextInfo.endPosition[0] = (int) layout.getSecondaryHorizontal(mSelectedTextInfo.end);
            mSelectedTextInfo.endPosition[1] = (int) layout.getLineBottom(endLine);
            mSelectedTextInfo.endLineTop = layout.getLineTop(endLine);
        }

    }

    private void showCursor() {
        Log.d(TAG, "updateCursor");
        int x = 0, y = 0;
        int[] coors = getLocation();
        if (promptPopWindow == null) {
            promptPopWindow = new PromptPopWindow(getContext());
            promptPopWindow.setCursorTouchListener(this);
            promptPopWindow.setOperationClickListener(this);
        }
        x = (int) (coors[0] + mSelectedTextInfo.startPosition[0] + getPaddingLeft());
        y = coors[1] + mSelectedTextInfo.startPosition[1] + getPaddingTop();
        Point left = new Point(x, y);

        x = (int) (coors[0] + mSelectedTextInfo.endPosition[0] + getPaddingLeft());
        y = coors[1] + mSelectedTextInfo.endPosition[1] + getPaddingTop();
        Point right = new Point(x, y);

        Rect visibleRect = new Rect();
        getGlobalVisibleRect(visibleRect);

        visibleRect.left = visibleRect.left - CursorView.getFixWidth();
        visibleRect.right += 1;
        visibleRect.bottom += 1;
        promptPopWindow.setCursorVisible(true, !visibleRect.isEmpty() && visibleRect.contains(left.x, left.y));
        promptPopWindow.setCursorVisible(false, !visibleRect.isEmpty() && visibleRect.contains(right.x, right.y));
        promptPopWindow.updateCursor(this, left, right, mSelectedTextInfo.startLineTop + coors[1] + getPaddingTop(), visibleRect);

    }

    private void updateText(Spannable spannableText) {
        spannableText.removeSpan(backgroundColorSpan);
        CustomImageSpan[] customImageSpans = spannableText.getSpans(0, spannableText.length(), CustomImageSpan.class);
        if (mSelectedTextInfo != null) {
            spannableText.setSpan(backgroundColorSpan, mSelectedTextInfo.start, mSelectedTextInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if (customImageSpans != null && customImageSpans.length > 0) {
                for (int i = 0; i < customImageSpans.length; i++) {
                    if (spannableText.getSpanStart(customImageSpans[i]) >= mSelectedTextInfo.start && spannableText.getSpanEnd(customImageSpans[i]) <= mSelectedTextInfo.end) {
                        customImageSpans[i].setBlackLayer(true);
                    } else {
                        customImageSpans[i].setBlackLayer(false);
                    }
                }
            }
        } else {
            if (customImageSpans != null && customImageSpans.length > 0) {
                for (int i = 0; i < customImageSpans.length; i++) {
                    customImageSpans[i].setBlackLayer(false);
                }
            }
        }


        setText(spannableText);
    }

    private Spannable getSpannableText() {
        Spannable spannableText = null;
        if (!(getText() instanceof Spannable)) {
            spannableText = new SpannableString(getText());
        } else spannableText = (Spannable) getText();

        return spannableText;
    }

    private int[] getLocation() {
        int[] location = new int[2];
        getLocationInWindow(location);
//        location[0]+=getTranslationX();
//        location[1]+=getTranslationY();
        return location;
    }

    @Override
    public boolean OnCursorTouch(boolean isLeft, View view, MotionEvent event) {
        Log.d(TAG, "OnCursorTouch:" + event);
        if (mSelectedTextInfo == null)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                if (promptPopWindow != null) {
                    promptPopWindow.hideOperation();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "OnCursorTouch setOperationVisible  visible");
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (promptPopWindow != null) {
                            promptPopWindow.showOperation();
                        }
                    }
                });
                break;
            case MotionEvent.ACTION_MOVE:
//                mOperateWindow.dismiss();
                int newX = (int) event.getX();
                int newY = (int) event.getY();
                int verticalOffset;
                int horizontalOffset;

                //设置抖动阈值
                int lineHeight = getLineHeight();
                if (lineHeight > 0) {
                    if (lineHeight > Math.abs(newX - downX) && lineHeight > Math.abs(newY - downY)) {
                        return true;
                    }
                }

                if (isLeft) {
                    verticalOffset = mSelectedTextInfo.startPosition[1] + (newY - downY);
                    horizontalOffset = mSelectedTextInfo.startPosition[0] + (newX - downX);
                } else {
                    verticalOffset = mSelectedTextInfo.endPosition[1] + (newY - downY);
                    horizontalOffset = mSelectedTextInfo.endPosition[0] + (newX - downX);
                }
                Log.d(TAG, "OnCursorTouch verticalOffset:" + verticalOffset + "  horizontalOffset:" + horizontalOffset);
                Layout layout = getLayout();
                if (layout == null)
                    return true;
                int line = layout.getLineForVertical(verticalOffset);
                int index = layout.getOffsetForHorizontal(line, horizontalOffset);
                Log.d(TAG, "OnCursorTouch line:" + line + "  index:" + index);

                if (isLeft) {
                    if (index == mSelectedTextInfo.start)
                        return true;
                    if (index <= mSelectedTextInfo.end) {
                        mSelectedTextInfo.start = index;
                        mSelectedTextInfo.startPosition[0] = (int) (layout.getPrimaryHorizontal(index));
                        mSelectedTextInfo.startPosition[1] = layout.getLineBottom(line);
                        mSelectedTextInfo.spannable = (Spannable) orgSpannable.subSequence(index, mSelectedTextInfo.end);
                        mSelectedTextInfo.startLineTop = layout.getLineTop(line);
                    }

                } else {
                    if (index == mSelectedTextInfo.end)
                        return true;
                    if (index >= mSelectedTextInfo.start) {
                        mSelectedTextInfo.end = index;
                        mSelectedTextInfo.endPosition[0] = (int) (layout.getSecondaryHorizontal(index));
                        mSelectedTextInfo.endPosition[1] = layout.getLineBottom(line);
                        mSelectedTextInfo.spannable = (Spannable) orgSpannable.subSequence(mSelectedTextInfo.start, index);
                        mSelectedTextInfo.endLineTop = layout.getLineTop(line);
                    }

                }

                updateSelected();
                break;
        }
        return true;
    }

    @Override
    public boolean onPopLayoutTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            int[] location = getLocation();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int verticalOffset = y - getPaddingTop() - location[1];
            int horizontalOffset = x - getPaddingLeft() - location[0];
            final int adjust_range = 20;//允许的触碰误差

            if (verticalOffset < -adjust_range || horizontalOffset < -adjust_range || verticalOffset > getHeight() + adjust_range || horizontalOffset > getWidth() + adjust_range) {
                promptPopWindow.dismiss();
                return true;
            }

            Log.d(TAG, "onPopLayoutTouch verticalOffset:" + verticalOffset + "   horizontalOffset:" + horizontalOffset);
            Layout layout = getLayout();
            if (layout != null) {
                int line = layout.getLineForVertical(verticalOffset);
                int index = layout.getOffsetForHorizontal(line, horizontalOffset);
                Log.d(TAG, "onPopLayoutTouch line:" + line + "  index:" + index);
                if (index <= mSelectedTextInfo.start || index >= mSelectedTextInfo.end)
                    promptPopWindow.dismiss();
            }

        }
        return true;
    }

    @Override
    public void onCursorDismiss() {
        reset();
    }

    public void reset() {
        mSelectedTextInfo = null;
        Spannable spannable = getSpannableText();
        updateText(spannable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (promptPopWindow != null && promptPopWindow.isShowing()) {
            promptPopWindow.dismiss();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    @Override
    public void onOperationClick(OperationItem item) {
        Log.d(TAG, "onOperationClick item:" + item);
        if (item.action == OperationItem.ACTION_SELECT_ALL) {
            selectAll();
            updateSelected();
            post(new Runnable() {
                @Override
                public void run() {
                    if (promptPopWindow != null) {
                        promptPopWindow.showOperation();
                    }
                }
            });
        } else if (item.action == OperationItem.ACTION_COPY) {
            Toast.makeText(getContext(), mSelectedTextInfo.spannable, Toast.LENGTH_SHORT).show();
            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(mSelectedTextInfo.spannable);
            promptPopWindow.dismiss();
            reset();
        } else {
            promptPopWindow.dismiss();
            reset();
        }


    }

    class InternalOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

        @Override
        public boolean onPreDraw() {
            if (promptPopWindow != null && promptPopWindow.isShowing()) {
                updateSelected();
            }
            return true;
        }
    }

}
