package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.GlideApp;

import java.util.List;

public class NineGridImageAdapter extends NineGridImageViewAdapter<String> {

    @Override
    protected void onDisplayImage(Context context, ImageView imageView, String s) {
        if (s.toLowerCase().endsWith(".gif")) {
            Log.d("PopupImageLoader", "gif");
            GlideApp.with(context).asGif().load(s)
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.bga_pp_ic_holder_light).error(R.drawable.bga_pp_ic_holder_light).override(Target.SIZE_ORIGINAL)).into(imageView);
        } else {
            Log.d("PopupImageLoader", "png");
            Glide.with(context).load(s).apply(new RequestOptions().centerCrop().placeholder(R.drawable.bga_pp_ic_holder_light).error(R.drawable.bga_pp_ic_holder_light).override(Target.SIZE_ORIGINAL).dontAnimate()).into(imageView);
        }
    }

    @Override
    protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
        super.onItemImageClick(context, imageView, index, list);
    }
}
