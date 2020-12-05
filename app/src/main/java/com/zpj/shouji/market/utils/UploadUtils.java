package com.zpj.shouji.market.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.yalantis.ucrop.CropEvent;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.callback.CropCallback;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.matisse.CaptureMode;
import com.zpj.matisse.Matisse;
import com.zpj.matisse.MimeType;
import com.zpj.matisse.engine.impl.GlideEngine;
import com.zpj.matisse.entity.Item;
import com.zpj.matisse.listener.OnSelectedListener;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.UploadImageApi;
import com.zpj.utils.ScreenUtils;

import java.io.File;
import java.util.List;

public class UploadUtils {

    public static void upload(SupportActivity activity, boolean isPickAvatar) {
        Matisse.from(activity)
                .choose(MimeType.ofImage())//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(1)//最大选择数量为9
                .spanCount(3)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .imageEngine(new GlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .capture(true, CaptureMode.All)//存储到哪里
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Item> itemList) {
                        String clipImageName = "clip_" + (System.currentTimeMillis() / 1000) + ".png";
                        File clipImage = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + File.separator + "PhotoPick/image",
                                clipImageName
                        );
                        UCrop uCrop = UCrop.of(itemList.get(0).getContentUri(), Uri.fromFile(clipImage));

                        if (isPickAvatar) {
                            uCrop.withAspectRatio(1, 1);
                            int maxSize = ScreenUtils.dp2pxInt(activity, 144);
                            uCrop.withMaxResultSize(maxSize, maxSize);
                        } else {
                            int height = ScreenUtils.getScreenHeight(activity);
                            int width = ScreenUtils.getScreenWidth(activity);
                            uCrop.withAspectRatio(height, width);
//                            uCrop.withAspectRatio(16, 9);
                            uCrop.withMaxResultSize(width / 2, (int) ((float) width * width / height) / 2);
                        }


                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.NONE);
                        options.setCompressionQuality(100);
                        options.setFreeStyleCropEnabled(true);
                        options.setCircleDimmedLayer(isPickAvatar);
                        options.setShowCropGrid(false);
                        options.setHideBottomControls(true);
                        options.setShowCropFrame(false);
                        options.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
                        options.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimary));
                        uCrop.withOptions(options);

                        CropEvent.register(uCrop, UploadImageApi::uploadCropImage);

                        uCrop.start(activity);
                    }
                })
                .start();
    }

}
