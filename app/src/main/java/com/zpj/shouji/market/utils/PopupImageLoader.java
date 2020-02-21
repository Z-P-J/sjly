package com.zpj.shouji.market.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.MyRequestOptions;

import java.io.File;

public class PopupImageLoader implements XPopupImageLoader {

    public final RequestOptions options = new RequestOptions()
            .centerCrop()
//            .placeholder(R.drawable.bga_pp_ic_holder_light)
            .error(R.drawable.bga_pp_ic_holder_light)
//                .override(layoutParams.width, layoutParams.height)
            .override(Target.SIZE_ORIGINAL);

    @Override
    public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
        Glide.with(imageView)
                .load(uri)
                .apply(options)
                .into(imageView);
    }

    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
