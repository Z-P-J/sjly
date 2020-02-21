package com.zpj.shouji.market.glide;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.zpj.shouji.market.R;

public class MyRequestOptions {

    public static final RequestOptions DEFAULT_OPTIONS = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.bga_pp_ic_holder_light)
            .error(R.drawable.bga_pp_ic_holder_light)
//                .override(layoutParams.width, layoutParams.height)
            .override(Target.SIZE_ORIGINAL);

}
