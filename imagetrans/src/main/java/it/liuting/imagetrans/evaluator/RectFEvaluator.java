package it.liuting.imagetrans.evaluator;

/**
 * Created by liuting on 17/2/24.
 */

import android.animation.TypeEvaluator;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * This evaluator can be used to perform type interpolation between <code>Rect</code> values.
 */
public class RectFEvaluator implements TypeEvaluator<RectF> {

    /**
     * When null, a new Rect is returned on every evaluate call. When non-null,
     * mRect will be modified and returned on every evaluate.
     */
    private RectF mRect;

    /**
     * Construct a RectEvaluator that returns a new Rect on every evaluate call.
     * To avoid creating an object for each evaluate call,
     * {@link android.animation.RectEvaluator#RectEvaluator(Rect)} should be used
     * whenever possible.
     */
    public RectFEvaluator() {
    }

    /**
     * Constructs a RectEvaluator that modifies and returns <code>reuseRect</code>
     * in {@link #evaluate(float, RectF, RectF)} calls.
     * The value returned from
     * {@link #evaluate(float, RectF, RectF)} should
     * not be cached because it will change over time as the object is reused on each
     * call.
     *
     * @param reuseRect A Rect to be modified and returned by evaluate.
     */
    public RectFEvaluator(RectF reuseRect) {
        mRect = reuseRect;
    }

    /**
     * This function returns the result of linearly interpolating the start and
     * end Rect values, with <code>fraction</code> representing the proportion
     * between the start and end values. The calculation is a simple parametric
     * calculation on each of the separate components in the Rect objects
     * (left, top, right, and bottom).
     * <p>
     * <p>If {@link #RectFEvaluator(RectF)} was used to construct
     * this RectEvaluator, the object returned will be the <code>reuseRect</code>
     * passed into the constructor.</p>
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue The start Rect
     * @param endValue   The end Rect
     * @return A linear interpolation between the start and end values, given the
     * <code>fraction</code> parameter.
     */
    @Override
    public RectF evaluate(float fraction, RectF startValue, RectF endValue) {
        float left = startValue.left + (endValue.left - startValue.left) * fraction;
        float top = startValue.top + (endValue.top - startValue.top) * fraction;
        float right = startValue.right + (endValue.right - startValue.right) * fraction;
        float bottom = startValue.bottom + (endValue.bottom - startValue.bottom) * fraction;
        if (mRect == null) {
            return new RectF(left, top, right, bottom);
        } else {
            mRect.set(left, top, right, bottom);
            return mRect;
        }
    }
}
