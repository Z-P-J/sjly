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
package com.zpj.shouji.market.imagepicker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.imagepicker.SelectionManager;
import com.zpj.shouji.market.imagepicker.entity.Item;

public class MediaGrid extends FrameLayout implements View.OnClickListener {

    private ImageView mThumbnail;
    private CheckView mCheckView;
    private ImageView mGifTag;

    private Item mMedia;
    private PreBindInfo mPreBindInfo;
    private OnMediaGridClickListener mListener;

    public MediaGrid(Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_image_grid_content, this, true);

        mThumbnail = findViewById(R.id.media_thumbnail);
        mCheckView = findViewById(R.id.check_view);
        mGifTag = findViewById(R.id.gif);

        mThumbnail.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
    }

    public ImageView getThumbnail() {
        return mThumbnail;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v == mThumbnail) {
                mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            } else if (v == mCheckView) {
                mListener.onCheckViewClicked(mCheckView, mMedia, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindMedia(Item item) {
        mMedia = item;
        setGifTag();
        initCheckView();
        setImage();
    }

    public Item getMedia() {
        return mMedia;
    }

    private void setGifTag() {
        mGifTag.setVisibility(mMedia.isGif() ? View.VISIBLE : View.GONE);
    }

    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        if (mMedia.isGif()) {
            SelectionManager.getInstance().imageEngine.loadGifThumbnail(getContext(), mThumbnail, mMedia.getContentUri());
        } else {
            SelectionManager.getInstance().imageEngine.loadThumbnail(getContext(), mThumbnail, mMedia.getContentUri());
        }
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    public void removeOnMediaGridClickListener() {
        mListener = null;
    }

    public interface OnMediaGridClickListener {

        void onThumbnailClicked(ImageView thumbnail, Item item, EasyViewHolder holder);

        void onCheckViewClicked(CheckView checkView, Item item, EasyViewHolder holder);
    }

    public static class PreBindInfo {
        boolean mCheckViewCountable;
        EasyViewHolder mViewHolder;

        public PreBindInfo(boolean checkViewCountable,
                           EasyViewHolder viewHolder) {
            mCheckViewCountable = checkViewCountable;
            mViewHolder = viewHolder;
        }
    }

}
