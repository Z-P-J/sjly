//package com.zpj.popup.imagetrans;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Build;
//import android.view.KeyEvent;
//import android.view.View;
//
//import com.zpj.popup.ZPopup;
//import com.zpj.popup.animator.EmptyAnimator;
//import com.zpj.popup.imagetrans.listener.ProgressViewGet;
//import com.zpj.popup.imagetrans.listener.SourceImageViewGet;
//import com.zpj.popup.impl.FullScreenPopup;
//import com.zpj.popup.interfaces.OnBackPressedListener;
//
//import java.util.List;
//
//import it.liuting.imagetrans.listener.ProgressViewGet;
//import it.liuting.imagetrans.listener.SourceImageViewGet;
//
///**
// * Created by liuting on 17/5/27.
// */
//
//public class ImageTrans implements DialogInterface.OnShowListener,
//        DialogInterface.OnKeyListener, DialogInterface {
//    private FullScreenPopup mDialog;
//    private ImageTransBuild build;
//    private DialogView dialogView;
//    private Context mContext;
//
//    public static ImageTrans with(Context context) {
//        return new ImageTrans(context);
//    }
//
//    ImageTrans(Context context) {
//        this.mContext = context;
//        build = new ImageTransBuild();
//    }
//
//    public ImageTrans setNowIndex(int index) {
//        build.clickIndex = index;
//        build.nowIndex = index;
//        return this;
//    }
//
//    public ImageTrans setImageList(List<Object> imageList) {
//        build.imageList = imageList;
//        return this;
//    }
//
//    public ImageTrans setSourceImageView(SourceImageViewGet sourceImageView) {
//        build.sourceImageViewGet = sourceImageView;
//        return this;
//    }
//
//    public ImageTrans setAdapter(ImageTransAdapter adapter) {
//        build.imageTransAdapter = adapter;
//        return this;
//    }
//
//    public ImageTrans setImageLoad(ImageLoad imageLoad) {
//        build.imageLoad = imageLoad;
//        return this;
//    }
//
//    public ImageTrans setScaleType(ScaleType scaleType) {
//        build.scaleType = scaleType;
//        return this;
//    }
//
//    public ImageTrans setConfig(ITConfig itConfig) {
//        build.itConfig = itConfig;
//        return this;
//    }
//
//    public ImageTrans setProgressBar(ProgressViewGet progressViewGet) {
//        build.progressViewGet = progressViewGet;
//        return this;
//    }
//
//    private View createView() {
//        dialogView = new DialogView(mContext, build);
//        return dialogView;
//    }
//
//    private int getDialogStyle() {
//        int dialogStyle;
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            dialogStyle = android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
//        } else {
//            dialogStyle = android.R.style.Theme_Translucent_NoTitleBar;
//        }
//        return dialogStyle;
//    }
//
//    public void show() {
//        build.checkParam();
//        mDialog = ZPopup.fullScreen(mContext);
//        mDialog.setPopupAnimator(new EmptyAnimator());
//        mDialog.setContentView(createView())
//                .setOnDismissListener(new com.zpj.popup.interfaces.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        dialogView.onDismiss(mDialog);
//                    }
//                });
//        mDialog.setOnBackPressedListener(new OnBackPressedListener() {
//            @Override
//            public boolean onBackPressed() {
//                dialogView.onDismiss(mDialog);
//                return true;
//            }
//        });
//
////        mDialog = new AlertDialog.Builder(mContext, getDialogStyle())
////                .setView(createView())
////                .create();
//        build.dialog = mDialog;
////        mDialog.setOnShowListener(this);
////        mDialog.setOnKeyListener(this);
////        mDialog.setOnKeyListener(new View.OnKeyListener() {
////            @Override
////            public boolean onKey(View v, int keyCode, KeyEvent event) {
////                return false;
////            }
////        });
//        dialogView.onCreate(mDialog);
//        mDialog.show();
//    }
//
//    @Override
//    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK &&
//                event.getAction() == KeyEvent.ACTION_UP &&
//                !event.isCanceled()) {
//            dialogView.onDismiss(mDialog);
//            return true;
//        }
//        return false;
//    }
//
//
//    @Override
//    public void onShow(DialogInterface dialog) {
////        dialogView.onCreate(this);
//    }
//
//    @Override
//    public void cancel() {
//        dismiss();
//    }
//
//    @Override
//    public void dismiss() {
//        dialogView.onDismiss(mDialog);
//    }
//
//}
