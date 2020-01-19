package com.zpj.dialog;//package com.zpj.zdialog;
//
//import android.content.Context;
//import android.content.ContextWrapper;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.annotation.LayoutRes;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.zpj.zdialog.base.BottomSheetDialog;
//import com.zpj.zdialog.base.DialogFragment;
//import com.zpj.zdialog.base.IDialog;
//import com.zpj.zdialog.base.OutsideClickDialog;
//
//public class ZBottomSheetDialog2 extends DialogFragment implements IDialog {
//
//    private static final String FTag = "BottomSheetDialog";
////    private BottomSheetBehavior mBehavior;
//    private FragmentManager fragmentManager;
//    private FragmentActivity activity;
//    private int layoutRes;
//    private View contentView;
//
//    private OnViewCreateListener onViewCreateListener;
//
//    public static ZBottomSheetDialog2 with(Context context) {
//        ZBottomSheetDialog2 dialog = new ZBottomSheetDialog2();
//        FragmentActivity activity;
//        if (context instanceof FragmentActivity) {
//            activity = (FragmentActivity) context;
//        } else {
//            activity = ((FragmentActivity) ((ContextWrapper) context).getBaseContext());
//        }
//        dialog.setFragmentActivity(activity);
//        return dialog;
//    }
//
//    @NonNull
//    @Override
//    public OutsideClickDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        BottomSheetDialog dialog = new BottomSheetDialog(this.getContext(), this.getTheme());
//        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
//        return dialog;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view;
//        if (getLayoutRes() > 0) {
//            //调用方通过xml获取view
//            view = inflater.inflate(getLayoutRes(), container, false);
//            contentView = view;
//        } else if (getContentView() != null) {
//            view = getContentView();
//        } else {
//            view =  super.onCreateView(inflater, container, savedInstanceState);
//            contentView = view;
//        }
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        //设置默认子View布局
////        contentView = view;
//        //回调给调用者，用来设置子View及点击事件等
//        if (onViewCreateListener != null) {
//            onViewCreateListener.onViewCreate(this, view);
//        }
//    }
//
//    protected int getLayoutRes() {
//        return layoutRes;
//    }
//
//    protected View getContentView() {
//        return contentView;
//    }
//
//    /**
//     * 设置DialogView
//     *
//     * @param layoutRes 布局文件
//     * @return Builder
//     */
//    public ZBottomSheetDialog2 setContentView(@LayoutRes int layoutRes) {
//        this.layoutRes = layoutRes;
//        return this;
//    }
//
//    /**
//     * 设置DialogView
//     *
//     * @param contentView View
//     * @return Builder
//     */
//    public ZBottomSheetDialog2 setContentView(View contentView) {
//        this.contentView = contentView;
//        return this;
//    }
//
//    public ZBottomSheetDialog2 setOnViewCreateListener(OnViewCreateListener listener) {
//        this.onViewCreateListener = listener;
//        return this;
//    }
//
//    private void setFragmentActivity(FragmentActivity activity) {
//        this.activity = activity;
//    }
//
//    @Override
//    public IDialog show() {
//        if (getDialog() != null) {
//            getDialog().show();
//        } else {
//            if (fragmentManager == null) {
//                fragmentManager = activity.getSupportFragmentManager();
//            }
//            show(fragmentManager, FTag);
//        }
//        return this;
//    }
//
//    @Override
//    public <T extends View> T getView(int id) {
//        return contentView.findViewById(id);
//    }
//
//}
