package com.zpj.shouji.market.ui.widget.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.utils.ScreenUtils;

/**
 * 头像上传裁剪框
 */
public class ClipView extends View {
    private final Paint paint = new Paint();
    //画裁剪区域边框的画笔
    private final Paint borderPaint = new Paint();
    //裁剪框水平方向间距
    private float mHorizontalPadding;
    //裁剪框边框宽度
    private int clipBorderWidth;
    //裁剪圆框的半径
    private int clipRadius;
    //裁剪框矩形宽度
//    private float clipWidth;
    //裁剪框类别，（圆形、矩形），默认为圆形
    private ClipType clipType = ClipType.CIRCLE;
    private final Xfermode xfermode;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //去锯齿
        paint.setAntiAlias(true);
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(clipBorderWidth);
        borderPaint.setAntiAlias(true);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //通过Xfermode的DST_OUT来产生中间的透明裁剪区域，一定要另起一个Layer（层）
        canvas.saveLayer(0, 0, this.getWidth(), this.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //设置背景
        canvas.drawColor(Color.parseColor("#a8000000"));
        paint.setXfermode(xfermode);
        //绘制圆形裁剪框
        if (clipType == ClipType.CIRCLE) {
            //中间的透明的圆
            canvas.drawCircle(this.getWidth() / 2f, this.getHeight() / 2f, clipRadius, paint);
            //白色的圆边框
            canvas.drawCircle(this.getWidth() / 2f, this.getHeight() / 2f, clipRadius, borderPaint);
        } else if (clipType == ClipType.RECTANGLE) { //绘制矩形裁剪框
            float clipHeight = this.clipRadius * 2 / 1.8f;
            //绘制中间的矩形
            canvas.drawRect(mHorizontalPadding, this.getHeight() / 2f - clipHeight / 2f,
                    this.getWidth() - mHorizontalPadding, this.getHeight() / 2f + clipHeight / 2f, paint);
            //绘制白色的矩形边框
            canvas.drawRect(mHorizontalPadding, this.getHeight() / 2f - clipHeight / 2f,
                    this.getWidth() - mHorizontalPadding, this.getHeight() / 2f + clipHeight / 2f, borderPaint);
        }
        //出栈，恢复到之前的图层，意味着新建的图层会被删除，新建图层上的内容会被绘制到canvas (or the previous layer)
        canvas.restore();
    }

    /**
     * 获取裁剪区域的Rect
     *
     * @return
     */
    public RectF getClipRect() {
        RectF rect = new RectF();
        //宽度的一半 - 圆的半径
        rect.left = (this.getWidth() / 2f - clipRadius);
        //宽度的一半 + 圆的半径
        rect.right = (this.getWidth() / 2f + clipRadius);
        float clipHeight;
        if (clipType == ClipType.CIRCLE) {
            clipHeight = this.clipRadius;
        } else {
            clipHeight = this.clipRadius / 1.8f;
        }
        //高度的一半 - 圆的半径
        rect.top = (this.getHeight() / 2f - clipHeight);
        //高度的一半 + 圆的半径
        rect.bottom = (this.getHeight() / 2f + clipHeight);
        return rect;
    }

    /**
     * 设置裁剪框边框宽度
     *
     * @param clipBorderWidth
     */
    public void setClipBorderWidth(int clipBorderWidth) {
        this.clipBorderWidth = clipBorderWidth;
        borderPaint.setStrokeWidth(clipBorderWidth);
        invalidate();
    }

    /**
     * 设置裁剪框水平间距
     *
     * @param mHorizontalPadding
     */
    public void setmHorizontalPadding(float mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
        this.clipRadius = (int) (ScreenUtils.getScreenWidth(getContext()) - 2 * mHorizontalPadding) / 2;
//        this.clipWidth = clipRadiusWidth * 2;
    }


    /**
     * 设置裁剪框类别
     *
     * @param clipType
     */
    public void setClipType(ClipType clipType) {
        if (this.clipType != clipType) {
            this.clipType = clipType;
            invalidate();
        }
    }

    /**
     * 裁剪框类别，圆形、矩形
     */
    public enum ClipType {
        CIRCLE, RECTANGLE
    }
}
