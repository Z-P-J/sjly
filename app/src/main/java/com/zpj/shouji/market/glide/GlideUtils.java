package com.zpj.shouji.market.glide;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.zpj.shouji.market.R;

public class GlideUtils {

    public static final RequestOptions REQUEST_OPTIONS = new RequestOptions()
            .placeholder(R.drawable.bga_pp_ic_holder_light)
            .error(R.drawable.bga_pp_ic_holder_light)
            .override(Target.SIZE_ORIGINAL)
            .centerCrop();

    public static final DrawableCrossFadeFactory DRAWABLE_CROSS_FADE_FACTORY = new DrawableCrossFadeFactory
            .Builder(500)
            .setCrossFadeEnabled(true)
            .build();

    public static final DrawableTransitionOptions DRAWABLE_TRANSITION_OPTIONS = DrawableTransitionOptions.with(DRAWABLE_CROSS_FADE_FACTORY);

    public static final DrawableTransitionOptions DRAWABLE_TRANSITION_NONE = DrawableTransitionOptions.with(new DrawableCrossFadeFactory
            .Builder()
            .setCrossFadeEnabled(false)
            .build());

    private GlideUtils() {

    }

    public static void load() {

    }

}
