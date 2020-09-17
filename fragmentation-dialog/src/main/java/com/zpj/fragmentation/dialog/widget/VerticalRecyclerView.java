//package com.zpj.fragmentation.dialog.widget;
//
//import android.content.Context;
//import android.graphics.drawable.GradientDrawable;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.AttributeSet;
//
//import com.zpj.fragmentation.dialog.R;
//import com.zpj.utils.ScreenUtils;
//
///**
// * Description:
// * Create by dance, at 2018/12/12
// */
//public class VerticalRecyclerView extends RecyclerView {
//    public VerticalRecyclerView(@NonNull Context context) {
//        this(context, null);
//    }
//
//    public VerticalRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public VerticalRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        setLayoutManager(new LinearLayoutManager(context));
//    }
//
//    public void setupDivider(){
//        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setShape(GradientDrawable.RECTANGLE);
//        drawable.setColor(getResources().getColor(R.color._xpopup_list_divider));
//        drawable.setSize(10, ScreenUtils.dp2pxInt(getContext(), .4f));
//        decoration.setDrawable(drawable);
//        addItemDecoration(decoration);
//    }
//
//}
