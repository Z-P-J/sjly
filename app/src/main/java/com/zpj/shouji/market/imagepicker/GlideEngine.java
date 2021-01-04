/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.shouji.market.imagepicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.ImageViewDrawableTarget;
import com.zpj.shouji.market.glide.transformations.MaxSizeTransformation;

/**
 * {@link ImageEngine} implementation using Glide 4.
 */

public class GlideEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, ImageView imageView, Uri uri) {
//        Drawable drawable = context.getResources().getDrawable(R.drawable.bga_pp_ic_holder_light);
        Log.d("GlideEngine", "loadThumbnail uri=" + uri.toString());
        int placeholder = GlideRequestOptions.getPlaceholderId();
        Glide.with(context)
//                .asBitmap() // some .jpeg files are actually gif
                .load(uri)
                .apply(
                        RequestOptions.bitmapTransform(new MaxSizeTransformation())
//                        .override(resize, resize)
//                        .override(Target.SIZE_ORIGINAL)
//                                .centerCrop()
                                .placeholder(placeholder)
                                .error(placeholder)
//                        .error(drawable)
//                        .fitCenter()
                )
                .into(new ImageViewDrawableTarget(imageView));
    }

    @Override
    public void loadGifThumbnail(Context context, ImageView imageView,
                                 Uri uri) {
        int placeholder = GlideRequestOptions.getPlaceholderId();
        Glide.with(context)
                .asGif()
//                .asBitmap() // some .jpeg files are actually gif
                .load(uri)
                .apply(
                        new RequestOptions()
//                        .override(resize, resize)
//                        .override(Target.SIZE_ORIGINAL)
                                .placeholder(placeholder)
                                .error(placeholder)
//                        .fitCenter()
                )
                .into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                        imageView.setImageDrawable(resource);
                        resource.start();
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        imageView.setImageDrawable(errorDrawable);
                    }
                });
    }

    @Override
    public void loadGifImage(Context context, ImageView imageView, Uri uri) {
//        Glide.with(context)
//                .asGif()
//                .load(uri)
//                .apply(new RequestOptions()
//                        .override(Target.SIZE_ORIGINAL)
////                        .priority(Priority.HIGH)
//                        .centerCrop())
//                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}
