/*
 Copyright 2011, 2012 Chris Banes.
 <p>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p>
 http://www.apache.org/licenses/LICENSE-2.0
 <p>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.zpj.fragmentation.dialog.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class PlaceholderImageView extends AppCompatImageView {

    private final PlaceholderAttacher attacher;

    public PlaceholderImageView(Context context) {
        this(context, null);
    }

    public PlaceholderImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PlaceholderImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        attacher = new PlaceholderAttacher(this);
        super.setScaleType(ScaleType.MATRIX);
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        attacher.setScaleType(scaleType);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        attacher.update();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        attacher.update();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        attacher.update();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }


}
