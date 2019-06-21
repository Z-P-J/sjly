package com.next.easynavigation.constant;

import com.daimajia.androidanimations.library.Techniques;

public enum Anim {

    DropOut(Techniques.DropOut),
    Landing(Techniques.Landing),
    TakingOff(Techniques.TakingOff),

    Flash(Techniques.Flash),
    Pulse(Techniques.Pulse),
    RubberBand(Techniques.RubberBand),
    Shake(Techniques.Shake),
    Swing(Techniques.Swing),
    Wobble(Techniques.Wobble),
    Bounce(Techniques.Bounce),
    Tada(Techniques.Tada),
    StandUp(Techniques.StandUp),
    Wave(Techniques.Wave),

    Hinge(Techniques.Hinge),
    RollIn(Techniques.RollIn),
    RollOut(Techniques.RollOut),

    BounceIn(Techniques.BounceIn),
    BounceInDown(Techniques.BounceInDown),
    BounceInLeft(Techniques.BounceInLeft),
    BounceInRight(Techniques.BounceInRight),
    BounceInUp(Techniques.BounceInUp),

    FadeIn(Techniques.FadeIn),
    FadeInUp(Techniques.FadeInUp),
    FadeInDown(Techniques.FadeInDown),
    FadeInLeft(Techniques.FadeInLeft),
    FadeInRight(Techniques.FadeInRight),

    FadeOut(Techniques.FadeOut),
    FadeOutDown(Techniques.FadeOutDown),
    FadeOutLeft(Techniques.FadeOutLeft),
    FadeOutRight(Techniques.FadeOutRight),
    FadeOutUp(Techniques.FadeOutUp),

    FlipInX(Techniques.FlipInX),
    FlipOutX(Techniques.FlipOutX),
    FlipInY(Techniques.FlipInY),
    FlipOutY(Techniques.FlipOutY),
    RotateIn(Techniques.RotateIn),
    RotateInDownLeft(Techniques.RotateInDownLeft),
    RotateInDownRight(Techniques.RotateInDownRight),
    RotateInUpLeft(Techniques.RotateInUpLeft),
    RotateInUpRight(Techniques.RotateInUpRight),

    RotateOut(Techniques.RotateOut),
    RotateOutDownLeft(Techniques.RotateOutDownLeft),
    RotateOutDownRight(Techniques.RotateOutDownRight),
    RotateOutUpLeft(Techniques.RotateOutUpLeft),
    RotateOutUpRight(Techniques.RotateOutUpRight),

    SlideInLeft(Techniques.SlideInLeft),
    SlideInRight(Techniques.SlideInRight),
    SlideInUp(Techniques.SlideInUp),
    SlideInDown(Techniques.SlideInDown),

    SlideOutLeft(Techniques.SlideOutLeft),
    SlideOutRight(Techniques.SlideOutRight),
    SlideOutUp(Techniques.SlideOutUp),
    SlideOutDown(Techniques.SlideOutDown),

    ZoomIn(Techniques.ZoomIn),
    ZoomInDown(Techniques.ZoomInDown),
    ZoomInLeft(Techniques.ZoomInLeft),
    ZoomInRight(Techniques.ZoomInRight),
    ZoomInUp(Techniques.ZoomInUp),

    ZoomOut(Techniques.ZoomOut),
    ZoomOutDown(Techniques.ZoomOutDown),
    ZoomOutLeft(Techniques.ZoomOutLeft),
    ZoomOutRight(Techniques.ZoomOutRight),
    ZoomOutUp(Techniques.ZoomOutUp);



    private Techniques yoyo;

    private Anim(Techniques anim) {
        yoyo = anim;
    }

    public Techniques getYoyo() {
        try {
            return yoyo;
        } catch (Exception e) {
            throw new Error("Can not init Yoyo");
        }
    }
}