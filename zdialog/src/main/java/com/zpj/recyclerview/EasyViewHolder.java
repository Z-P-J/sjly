package com.zpj.recyclerview;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpj.utils.ClickHelper;

public final class EasyViewHolder extends RecyclerView.ViewHolder {

    private IEasy.OnItemClickCallback clickCallback;
    private ClickHelper.OnClickListener onClickListener;
    private ClickHelper.OnLongClickListener onLongClickListener;
    private final View itemView;
    private int position;

    EasyViewHolder(@NonNull View view) {
        super(view);
        this.itemView = view;
        ClickHelper.with(itemView).setOnClickListener(new ClickHelper.OnClickListener() {
            @Override
            public void onClick(View v, float x, float y) {
                if (clickCallback != null && clickCallback.shouldIgnoreClick(itemView)) {
                    return;
                }
                if (onClickListener != null) {
                    onClickListener.onClick(v, x, y);
                }
            }
        }).setOnLongClickListener(new ClickHelper.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v, float x, float y) {
                if (clickCallback != null && clickCallback.shouldIgnoreClick(itemView)) {
                    return true;
                } else if (onLongClickListener != null) {
                    return onLongClickListener.onLongClick(v, x, y);
                }
                return false;
            }
        });
    }

    public <T extends View> T getView(@IdRes int id) {
        return itemView.findViewById(id);
    }

    public void setVisible(@IdRes int id, boolean visible) {
        View view = itemView.findViewById(id);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setInVisible(@IdRes int id) {
        View view = itemView.findViewById(id);
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public <T extends TextView> T getTextView(@IdRes int id) {
        return itemView.findViewById(id);
    }

    public <T extends ImageView> T getImageView(@IdRes int id) {
        return itemView.findViewById(id);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getHolderPosition() {
        return position;
    }

    void setItemClickCallback(IEasy.OnItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void setOnItemClickListener(final ClickHelper.OnClickListener listener) {
        onClickListener = listener;
    }

    public void setOnItemLongClickListener(final ClickHelper.OnLongClickListener listener) {
        onLongClickListener = listener;
    }

    public View getItemView() {
        return itemView;
    }

    public boolean performClick() {
        return itemView.performClick();
    }
}
