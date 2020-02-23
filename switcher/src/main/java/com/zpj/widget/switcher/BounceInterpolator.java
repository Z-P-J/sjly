package com.zpj.widget.switcher;

import android.view.animation.Interpolator;

class BounceInterpolator implements Interpolator {

    private final double amplitude;
    private final double frequency;

    BounceInterpolator(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (-1 * Math.pow(Math.E, -input / amplitude) * Math.cos(frequency * input) + 1);
    }
}