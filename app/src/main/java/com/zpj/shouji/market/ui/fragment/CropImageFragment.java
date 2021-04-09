package com.zpj.shouji.market.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.imagepicker.entity.Item;
import com.zpj.shouji.market.ui.fragment.base.SkinFragment;
import com.zpj.shouji.market.ui.widget.crop.ClipView;
import com.zpj.shouji.market.ui.widget.crop.ClipViewLayout;
import com.zpj.shouji.market.utils.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class CropImageFragment extends SkinFragment implements View.OnClickListener {

    private ClipViewLayout clipViewLayout;
    private boolean isCropAvatar;
    private Item item;

//    public static void start(Item item, boolean isCropAvatar) {
//        start(newInstance(item, isCropAvatar));
//    }

    public static CropImageFragment newInstance(Item item, boolean isCropAvatar) {
        Bundle args = new Bundle();
        args.putParcelable(Keys.INFO, item);
        args.putBoolean(Keys.TYPE, isCropAvatar);
        CropImageFragment fragment = new CropImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_crop_image;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "图片裁剪";
    }

    @Override
    protected void initStatusBar() {
        lightStatusBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = getArguments().getParcelable(Keys.INFO);
            isCropAvatar = getArguments().getBoolean(Keys.TYPE);
        } else {
            pop();
        }
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        clipViewLayout = (ClipViewLayout) findViewById(R.id.clipViewLayout1);
        TextView btnCancel = (TextView) findViewById(R.id.btn_cancel);
        TextView btnOk = (TextView) findViewById(R.id.bt_ok);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);


        clipViewLayout.setVisibility(View.VISIBLE);
        clipViewLayout.setClipType(isCropAvatar ? ClipView.ClipType.CIRCLE : ClipView.ClipType.RECTANGLE);

    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        //设置图片资源
        clipViewLayout.initSrcPic(item.getFile(context));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                pop();
                break;
            case R.id.bt_ok:
                generateUriAndReturn();
                break;
        }
    }


    /**
     * 生成Uri并且通过setResult返回给打开的activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图
        Bitmap zoomedCropBitmap = clipViewLayout.clip();
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        File folder = new File(context.getExternalCacheDir(), "crop_img");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File cacheFile = new File(folder, "cropped_" + System.currentTimeMillis() + ".png");
        Uri mSaveUri = Uri.fromFile(cacheFile);
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = context.getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            EventBus.sendCropEvent(cacheFile, isCropAvatar);
            pop();
        }
    }


}
