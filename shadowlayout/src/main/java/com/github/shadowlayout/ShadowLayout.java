package com.github.shadowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

/***
 *   created by zhongruiAndroid on 2019/6/20
 */
public class ShadowLayout extends FrameLayout {
    private Paint shadowPaint;
    private Paint solidPaint;
    private Path scopePath = new Path();
    private Path bgPath = new Path();

    private float shadowRange;
    private float shadowRangeOffset;
    private int shadowColor;

    private float shadowOffsetLeft;
    private float shadowOffsetTop;
    private float shadowOffsetRight;
    private float shadowOffsetBottom;

    private float shadowTopLeftRadius;
    private float shadowTopRightRadius;
    private float shadowBottomRightRadius;
    private float shadowBottomLeftRadius;

    private int solidColor;

    private BlurMaskFilter.Blur blurMaskFilter = BlurMaskFilter.Blur.OUTER;
    private float[] viewRadius;
    private RectF pathRect;
    private RectF bgRect;

    public ShadowLayout(Context context) {
        super(context);
        init(null);
    }
    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(AttributeSet attrs) {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
        shadowRange =   typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowRange, dp2px(14));
        shadowRangeOffset =   typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowRangeOffset,dp2px(5));
        shadowColor = typedArray.getColor(R.styleable.ShadowLayout_shadowlayout_shadowColor, Color.parseColor("#b9cccccc"));

        shadowOffsetLeft    =  typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowOffsetLeft, 0);

        shadowOffsetTop     =   typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowOffsetTop, 0);
        shadowOffsetRight   =  typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowOffsetRight, 0);
        shadowOffsetBottom  =  typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowOffsetBottom, 0);

        float shadowRadius =   typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowRadius, 0);
        shadowTopLeftRadius =   typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowTopLeftRadius, shadowRadius);
        shadowTopRightRadius =   typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowTopRightRadius, shadowRadius);
        shadowBottomRightRadius =   typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowBottomRightRadius, shadowRadius);
        shadowBottomLeftRadius =  typedArray.getDimension(R.styleable.ShadowLayout_shadowlayout_shadowBottomLeftRadius, shadowRadius);
        solidColor = typedArray.getColor(R.styleable.ShadowLayout_shadowlayout_solidColor,Color.WHITE);

        typedArray.recycle();

        setPadding((int)shadowRange,(int)shadowRange,(int)shadowRange,(int)shadowRange);

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColor(shadowColor);
        if (shadowRange <= shadowRangeOffset) {
            shadowRange = shadowRangeOffset;
        }

        setMask();
        setSolidBgPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        changeRange( );
    }

    private void setSolidBgPaint(){
        if (solidPaint == null) {
            solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        solidPaint.setColor(solidColor);
    }

    private void setMask(){
        shadowPaint.setMaskFilter(new BlurMaskFilter(shadowRange-shadowRangeOffset, blurMaskFilter));
    }



    private void changeRange( ) {
//        int leftRange =  shadowRange;
//        int topRange =  shadowRange;
//        int rightRange =  shadowRange;
//        int bottomRange =  shadowRange;
        if (scopePath != null&&scopePath.isEmpty()==false) {
            scopePath.reset();
        }
        if (bgPath != null&&bgPath.isEmpty()==false) {
            bgPath.reset();
        }

        pathRect = new RectF(
                shadowRange+shadowOffsetLeft,
                shadowRange+shadowOffsetTop,
                getWidth() - shadowRange-shadowOffsetRight,
                getHeight() - shadowRange-shadowOffsetBottom);

        viewRadius = new float[]{shadowTopLeftRadius, shadowTopLeftRadius,
                shadowTopRightRadius, shadowTopRightRadius,
                shadowBottomRightRadius, shadowBottomRightRadius,
                shadowBottomLeftRadius, shadowBottomLeftRadius};

        scopePath.addRoundRect(pathRect, viewRadius, Path.Direction.CW);


        bgRect = new RectF(
                shadowRange ,
                shadowRange ,
                getWidth() - shadowRange ,
                getHeight() - shadowRange );


        bgPath.addRoundRect(bgRect, viewRadius, Path.Direction.CW);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(scopePath, shadowPaint);

        canvas.drawPath(bgPath,solidPaint);

    }


    public int dp2px(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dp + 0.5f);
    }

    public float getShadowRange() {
        return shadowRange;
    }

    public ShadowLayout setShadowRange(float shadowRange) {
        if(shadowRange>shadowRangeOffset&&this.shadowRange!=shadowRange){
            this.shadowRange = shadowRange;
            setMask();
            changeRange();
//            invalidate();
            setPadding((int)shadowRange,(int)shadowRange,(int)shadowRange,(int)shadowRange);
        }

        return this;
    }

    public float getShadowRangeOffset() {
        return shadowRangeOffset;
    }

    public ShadowLayout setShadowRangeOffset(float shadowRangeOffset) {
        if(shadowRangeOffset>0&&this.shadowRangeOffset!=shadowRangeOffset){
            this.shadowRangeOffset = shadowRangeOffset;
            setMask();
            invalidate();
        }
        return this;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public ShadowLayout setShadowColor(@ColorInt int shadowColor) {
        if(this.shadowColor!=shadowColor){
            this.shadowColor = shadowColor;
            shadowPaint.setColor(shadowColor);
            invalidate();
        }
        return this;
    }

    public float getShadowOffsetLeft() {
        return shadowOffsetLeft;
    }

    public ShadowLayout setShadowOffsetLeft(float shadowOffsetLeft) {
        if(shadowOffsetLeft>=0&&this.shadowOffsetLeft != shadowOffsetLeft){
            this.shadowOffsetLeft = shadowOffsetLeft;
            changeRange();
            invalidate();
        }
        return this;
    }

    public float getShadowOffsetTop() {
        return shadowOffsetTop;
    }

    public ShadowLayout setShadowOffsetTop(float shadowOffsetTop) {
        if(shadowOffsetTop>=0&&this.shadowOffsetTop != shadowOffsetTop){
            this.shadowOffsetTop = shadowOffsetTop;
            changeRange();
            invalidate();
        }
        return this;
    }

    public float getShadowOffsetRight() {
        return shadowOffsetRight;
    }

    public ShadowLayout setShadowOffsetRight(float shadowOffsetRight) {
        if(shadowOffsetRight>=0&&this.shadowOffsetRight!= shadowOffsetRight){
            this.shadowOffsetRight = shadowOffsetRight;
            changeRange();
            invalidate();
        }
        return this;
    }

    public float getShadowOffsetBottom() {
        return shadowOffsetBottom;
    }

    public ShadowLayout setShadowOffsetBottom(float shadowOffsetBottom) {
        if(shadowOffsetBottom>=0&&this.shadowOffsetBottom != shadowOffsetBottom){
            this.shadowOffsetBottom = shadowOffsetBottom;
            changeRange();
            invalidate();
        }
        return this;
    }

    public ShadowLayout setShadowRadius(float shadowTopLeftRadius,float shadowTopRightRadius,float shadowBottomRightRadius,float shadowBottomLeftRadius) {
        boolean flag=false;
        if(shadowTopLeftRadius>=0&&this.shadowTopLeftRadius !=shadowTopLeftRadius){
            this.shadowTopLeftRadius = shadowTopLeftRadius;
            flag=true;
        }
        if(shadowTopRightRadius>=0&&this.shadowTopRightRadius !=shadowTopRightRadius){
            this.shadowTopRightRadius = shadowTopRightRadius;
            flag=true;
        }
        if(shadowBottomRightRadius>=0&&this.shadowBottomRightRadius !=shadowBottomRightRadius){
            this.shadowBottomRightRadius = shadowBottomRightRadius;
            flag=true;
        }
        if(shadowBottomLeftRadius>=0&&this.shadowBottomLeftRadius !=shadowBottomLeftRadius){
            this.shadowBottomLeftRadius = shadowBottomLeftRadius;
            flag=true;
        }
        if(flag){
            changeRange();
            invalidate();
        }
        return this;
    }
    public ShadowLayout setShadowRadius(float shadowRadius) {
        if(shadowRadius>=0){
            boolean flag=false;
            if(this.shadowTopLeftRadius !=shadowRadius){
                this.shadowTopLeftRadius = shadowRadius;
                flag=true;
            }
            if(this.shadowTopRightRadius !=shadowRadius){
                this.shadowTopRightRadius = shadowRadius;
                flag=true;
            }
            if(this.shadowBottomRightRadius !=shadowRadius){
                this.shadowBottomRightRadius = shadowRadius;
                flag=true;
            }
            if(this.shadowBottomLeftRadius !=shadowRadius){
                this.shadowBottomLeftRadius = shadowRadius;
                flag=true;
            }
            if(flag){
                changeRange();
                invalidate();
            }
        }
        return this;
    }

    public float getShadowTopLeftRadius() {
        return shadowTopLeftRadius;
    }

    public ShadowLayout setShadowTopLeftRadius(float shadowTopLeftRadius) {
        if(shadowTopLeftRadius >=0&&this.shadowTopLeftRadius != shadowTopLeftRadius){
            this.shadowTopLeftRadius = shadowTopLeftRadius;
            changeRange();
            invalidate();
        }
        return this;
    }

    public float getShadowTopRightRadius() {
        return shadowTopRightRadius;
    }

    public ShadowLayout setShadowTopRightRadius(float shadowTopRightRadius) {
        if(shadowTopRightRadius >=0&&this.shadowTopRightRadius != shadowTopRightRadius){
            this.shadowTopRightRadius = shadowTopRightRadius;
            changeRange();
            invalidate();
        }
        return this;
    }

    public float getShadowBottomRightRadius() {
        return shadowBottomRightRadius;
    }

    public ShadowLayout setShadowBottomRightRadius(float shadowBottomRightRadius) {
        if(shadowBottomRightRadius >=0&&this.shadowBottomRightRadius != shadowBottomRightRadius){
            this.shadowBottomRightRadius = shadowBottomRightRadius;
            changeRange();
            invalidate();
        }
        return this;
    }

    public float getShadowBottomLeftRadius() {
        return shadowBottomLeftRadius;
    }

    public ShadowLayout setShadowBottomLeftRadius(float shadowBottomLeftRadius) {
        if(shadowBottomLeftRadius >=0&&this.shadowBottomLeftRadius != shadowBottomLeftRadius){
            this.shadowBottomLeftRadius = shadowBottomLeftRadius;
            changeRange();
            invalidate();
        }
        return this;
    }

    public int getSolidColor() {
        return solidColor;
    }

    public ShadowLayout setSolidColor(@ColorInt int solidBgColor) {
        if(this.solidColor!= solidBgColor){
            this.solidColor= solidBgColor;
            setSolidBgPaint();
            invalidate();
        }
        return this;
    }


}
