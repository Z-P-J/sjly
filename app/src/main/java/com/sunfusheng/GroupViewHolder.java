package com.sunfusheng;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author sunfusheng on 2018/2/1.
 */
public class GroupViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;

    public GroupViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();
    }

    public <T extends View> T get(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = this.itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public GroupViewHolder setText(int viewId, String text) {
        TextView view = get(viewId);
        if (view != null) {
            view.setText(text);
        }
        return this;
    }

    public GroupViewHolder setText(int viewId, CharSequence text) {
        TextView view = get(viewId);
        if (view != null) {
            view.setText(text);
        }
        return this;
    }

    public GroupViewHolder setText(int viewId, int resId) {
        TextView view = get(viewId);
        if (view != null && resId != 0) {
            view.setText(resId);
        }
        return this;
    }

    public GroupViewHolder setTextColor(int viewId, int color) {
        TextView view = get(viewId);
        if (view != null) {
            view.setTextColor(color);
        }
        return this;
    }

    public GroupViewHolder setTextSize(int viewId, int size) {
        TextView view = get(viewId);
        if (view != null) {
            view.setTextSize(size);
        }
        return this;
    }

    public GroupViewHolder setImageResource(int viewId, int resId) {
        ImageView view = get(viewId);
        if (view != null && resId != 0) {
            view.setImageResource(resId);
        }
        return this;
    }

    public GroupViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = get(viewId);
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
        return this;
    }


    public GroupViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = get(viewId);
        if (view != null) {
            view.setImageDrawable(drawable);
        }
        return this;
    }


    public GroupViewHolder setBackgroundColor(int viewId, int color) {
        View view = get(viewId);
        if (view != null) {
            view.setBackgroundColor(color);
        }
        return this;
    }

    public GroupViewHolder setBackgroundResource(int viewId, int resId) {
        View view = get(viewId);
        if (view != null && resId != 0) {
            view.setBackgroundResource(resId);
        }
        return this;
    }

    public GroupViewHolder setVisible(int viewId, boolean visible) {
        View view = get(viewId);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public GroupViewHolder setVisibility(int viewId, int visibility) {
        View view = get(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
        return this;
    }
}
