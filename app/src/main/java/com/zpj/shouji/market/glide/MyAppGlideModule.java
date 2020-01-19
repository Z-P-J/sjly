package com.zpj.shouji.market.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.module.AppGlideModule;
import com.zpj.shouji.market.model.InstalledAppInfo;
import com.zpj.shouji.market.glide.apk.ApkModelLoaderFactory;

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
        List<ImageHeaderParser> imageHeaderParsers = registry.getImageHeaderParsers();

        // 解决Glide加载格式不完整（丢帧、压缩等）的gif动图出错的问题
        com.spx.gifdecoder.ByteBufferGifDecoder byteBufferGifDecoder =
                new com.spx.gifdecoder.ByteBufferGifDecoder(context, imageHeaderParsers, glide.getBitmapPool(), glide.getArrayPool());
        registry.prepend(Registry.BUCKET_GIF, ByteBuffer.class, GifDrawable.class, byteBufferGifDecoder);

        registry.prepend(Registry.BUCKET_GIF,
                InputStream.class,
                GifDrawable.class, new StreamGifDecoder(imageHeaderParsers, byteBufferGifDecoder, glide.getArrayPool()));

        registry.prepend(InstalledAppInfo.class, InputStream.class, new ApkModelLoaderFactory(context));
    }
}
