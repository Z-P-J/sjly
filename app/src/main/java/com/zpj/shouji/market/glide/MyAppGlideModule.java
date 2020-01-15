package com.zpj.shouji.market.glide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        Log.d("MyAppGlideModule", "registerComponents");
        List<ImageHeaderParser> imageHeaderParsers = registry.getImageHeaderParsers();

        com.spx.gifdecoder.ByteBufferGifDecoder byteBufferGifDecoder =
                new com.spx.gifdecoder.ByteBufferGifDecoder(context, imageHeaderParsers, glide.getBitmapPool(), glide.getArrayPool());
        registry.prepend(Registry.BUCKET_GIF, ByteBuffer.class, GifDrawable.class, byteBufferGifDecoder);

        registry.prepend(Registry.BUCKET_GIF,
                InputStream.class,
                GifDrawable.class,  new StreamGifDecoder(imageHeaderParsers, byteBufferGifDecoder, glide.getArrayPool()));
    }
}
