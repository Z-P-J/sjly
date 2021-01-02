package com.zpj.shouji.market.utils;

import com.zpj.fragmentation.SupportActivity;
import com.zpj.shouji.market.imagepicker.ImagePicker;

public class UploadUtils {

    public static void upload(SupportActivity activity, boolean isPickAvatar) {
        ImagePicker.with()
                .maxSelectable(1)
                .isCrop(true, isPickAvatar)
                .start();
//        ImagePicker.from(activity)
//                .choose(MimeType.ofImage())//照片视频全部显示MimeType.allOf()
//                .countable(true)//true:选中后显示数字;false:选中后显示对号
//                .maxSelectable(1)//最大选择数量为9
//                .spanCount(3)
//                .isCrop(true, isPickAvatar)
//                .thumbnailScale(0.85f)//缩放比例
//                .imageEngine(new GlideEngine())//图片加载方式，Glide4需要自定义实现
////                .setOnSelectedListener(new ImagePicker.OnSelectedListener() {
////                    @Override
////                    public void onSelected(@NonNull List<Item> itemList) {
////                        CropImageFragment.start(itemList.get(0), isPickAvatar);
////
//////                        String clipImageName = "clip_" + (System.currentTimeMillis() / 1000) + ".png";
//////                        File clipImage = new File(
//////                                Environment.getExternalStorageDirectory().getAbsolutePath()
//////                                        + File.separator + "PhotoPick/image",
//////                                clipImageName
//////                        );
//////                        UCrop uCrop = UCrop.of(itemList.get(0).getContentUri(), Uri.fromFile(clipImage));
//////
//////                        if (isPickAvatar) {
//////                            uCrop.withAspectRatio(1, 1);
//////                            int maxSize = ScreenUtils.dp2pxInt(activity, 144);
//////                            uCrop.withMaxResultSize(maxSize, maxSize);
//////                        } else {
//////                            int height = ScreenUtils.getScreenHeight(activity);
//////                            int width = ScreenUtils.getScreenWidth(activity);
//////                            uCrop.withAspectRatio(height, width);
////////                            uCrop.withAspectRatio(16, 9);
//////                            uCrop.withMaxResultSize(width / 2, (int) ((float) width * width / height) / 2);
//////                        }
//////
//////
//////                        UCrop.Options options = new UCrop.Options();
//////                        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//////                        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.NONE);
//////                        options.setCompressionQuality(100);
//////                        options.setFreeStyleCropEnabled(true);
//////                        options.setCircleDimmedLayer(isPickAvatar);
//////                        options.setShowCropGrid(false);
//////                        options.setHideBottomControls(true);
//////                        options.setShowCropFrame(false);
//////                        options.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
//////                        options.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimary));
//////                        uCrop.withOptions(options);
//////
//////                        CropEvent.register(uCrop, UploadImageApi::uploadCropImage);
//////
//////                        uCrop.start(activity);
////                    }
////                })
//                .start();
    }

}
