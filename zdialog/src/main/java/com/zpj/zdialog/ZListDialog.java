package com.zpj.zdialog;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.IEasy;
import com.zpj.zdialog.base.IDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z-P-J
 * @date 2019/6/1 16:25
 */
public class ZListDialog<T> {

    private Context context;

    private final int layoutRes = R.layout.easy_layout_dialog_list;

    private ZDialog dialog;

    private EasyRecyclerView<T> easyRecyclerView;

    private List<T> list;

    private IEasy.OnBindViewHolderCallback<T> callback;

    private IDialog.OnDismissListener onDismissListener;

    private RecyclerView.LayoutManager layoutManager;

    private int gravity = Gravity.CENTER;

    private final List<Integer> selectPositions = new ArrayList<>();

    private boolean isShowButtons = false;

    @LayoutRes
    private int itemRes;
//    private View itemView;

    private TextView titleView;

    private IDialog.OnClickListener positiveBtnListener;
    private IDialog.OnClickListener negativeBtnListener;

    public ZListDialog(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        titleView = view.findViewById(R.id.title_view);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        easyRecyclerView = new EasyRecyclerView<>(recyclerView);
        dialog = ZDialog.with(context)
                .setContentView(view)
                .setWindowBackgroundP(0.5f)
                .setSwipeEnable(false);
    }

    public static ZListDialog with(Context context) {
        return new ZListDialog(context);
    }

    public ZListDialog<T> setPositiveButton(IDialog.OnClickListener onclickListener) {
        this.positiveBtnListener = onclickListener;
        return this;
    }

    public ZListDialog<T> setNegativeButton(IDialog.OnClickListener onclickListener) {
        this.negativeBtnListener = onclickListener;
        return this;
    }



    public ZListDialog<T> setItemRes(@LayoutRes int res) {
        this.itemRes = res;
        return this;
    }

    public ZListDialog<T> setItemList(List<T> list) {
        this.list = list;
        return this;
    }

    public ZListDialog<T> setOnBindChildView(IEasy.OnBindViewHolderCallback<T> callback) {
        this.callback = callback;
        return this;
    }

    public ZListDialog<T> setOnDismissListener(IDialog.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public ZListDialog<T> setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        return this;
    }

    public ZListDialog<T> setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public ZListDialog<T> setTitle(String title) {
        titleView.setText(title);
        return this;
    }

    public ZListDialog<T> setTitleGravity(int gravity) {
        titleView.setGravity(gravity);
        return this;
    }

    public ZListDialog<T> setTitleTextColor(int textColor) {
        titleView.setTextColor(textColor);
        return this;
    }

    public ZListDialog<T> setTitleTextSize(float textSize) {
        titleView.setTextSize(textSize);
        return this;
    }

    public ZListDialog<T> setShowButtons(boolean isShowButtons) {
        this.isShowButtons = isShowButtons;
        return this;
    }

    public void show() {
        easyRecyclerView.setData(list)
                .setItemRes(itemRes)
                .setLayoutManager(layoutManager == null ? new LinearLayoutManager(context) : layoutManager)
                .onBindViewHolder(callback)
                .build();
        dialog.setGravity(gravity)
                .setOnDismissListener(onDismissListener)
                .setOnViewCreateListener(new IDialog.OnViewCreateListener() {
                    @Override
                    public void onViewCreate(final IDialog dialog, View view) {
                        LinearLayout buttons = view.findViewById(R.id.buttons);
                        buttons.setVisibility(isShowButtons ? View.VISIBLE : View.GONE);
                        buttons.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (positiveBtnListener != null) {
                                    positiveBtnListener.onClick(dialog);
                                } else {
                                    dismiss();
                                }
                            }
                        });
                        buttons.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (negativeBtnListener != null) {
                                    negativeBtnListener.onClick(dialog);
                                } else {
                                    dismiss();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    public void addSelectPosition(int position) {
        if (!selectPositions.contains(position)) {
            selectPositions.add(position);
        }
    }

    public List<Integer> getSelectPositions() {
        return selectPositions;
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void notifyDataSetChanged() {
        easyRecyclerView.notifyDataSetChanged();
    }

    public void notifyItemChanged(int position) {
        easyRecyclerView.notifyItemChanged(position);
    }

    public EasyAdapter<T> getAdapter() {
        return easyRecyclerView.getAdapter();
    }

}
