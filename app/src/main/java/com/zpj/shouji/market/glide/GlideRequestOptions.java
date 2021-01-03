package com.zpj.shouji.market.glide;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class GlideRequestOptions {

    private static RequestOptions DEFAULT_ICON_OPTION;


    private RequestOptions options;
    private List<BitmapTransformation> transformationList;

    private GlideRequestOptions() {
        options = new RequestOptions();
        transformationList = new ArrayList<>();
    }

    public static RequestOptions getDefaultIconOption() {
//        if (DEFAULT_ICON_OPTION == null) {
//            DEFAULT_ICON_OPTION = GlideRequestOptions.with()
//                    .centerCrop()
//                    .roundedCorners(10)
//                    .get();
//        }
//        return DEFAULT_ICON_OPTION.clone();
        return GlideRequestOptions.with()
                .centerCrop()
                .roundedCorners(10)
                .get()
                .error(R.drawable.ic_apk)
                .placeholder(R.drawable.ic_apk);
//                .error(R.mipmap.ic_launcher)
//                .placeholder(R.mipmap.ic_launcher);
    }

    public static RequestOptions getImageOption() {
        int placeholder = getPlaceholderId();
        return new RequestOptions()
                .error(placeholder)
                .placeholder(placeholder)
                .override(Target.SIZE_ORIGINAL)
                .centerCrop();
    }

    public static int getPlaceholderId() {
        return AppConfig.isNightMode() ? R.drawable.ic_placeholder_image_dark : R.drawable.ic_placeholder_image_light;
    }

    public static GlideRequestOptions with() {
        return new GlideRequestOptions();
    }

    public GlideRequestOptions addTransformation(BitmapTransformation transformation) {
        transformationList.add(transformation);
        return this;
    }

    public GlideRequestOptions centerCrop() {
        transformationList.add(new CenterCrop());
        return this;
    }

    public GlideRequestOptions roundedCorners(int dp) {
        transformationList.add(new RoundedCorners(ScreenUtils.dp2pxInt(ContextUtils.getApplicationContext(), dp)));
        return this;
    }

    public GlideRequestOptions skipMemoryCache(boolean flag) {
        options = options.skipMemoryCache(flag);
        return this;
    }

    public RequestOptions get() {
        options = options.transform(transformationList.toArray(new BitmapTransformation[0]));
        return options;
    }

}
