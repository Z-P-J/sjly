//package com.zpj.shouji.market.utils;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.Target;
//import com.zpj.popup.interfaces.IImageLoader;
//import com.zpj.shouji.market.R;
//
//import java.io.File;
//
//public class PopupImageLoader implements IImageLoader<String> {
//
//    public final RequestOptions options = new RequestOptions()
//            .centerCrop()
////            .placeholder(R.drawable.bga_pp_ic_holder_light)
//            .error(R.drawable.bga_pp_ic_holder_light)
////                .override(layoutParams.width, layoutParams.height)
//            .override(Target.SIZE_ORIGINAL);
//
//    @Override
//    public void loadImage(int position, @NonNull String uri, @NonNull ImageView imageView) {
//        Glide.with(imageView)
//                .load(uri)
//                .apply(options)
//                .into(imageView);
//    }
//
//    @Override
//    public File getImageFile(@NonNull Context context, @NonNull String uri) {
//        try {
//            return Glide.with(context).downloadOnly().load(uri).submit().get();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
