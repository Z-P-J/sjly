package com.zpj.zdialog;

import android.animation.Animator;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.zpj.utils.AnimHelper;
import com.zpj.zdialog.base.BottomSheetBehavior;
import com.zpj.zdialog.base.BottomSheetDialog;
import com.zpj.zdialog.base.DialogFragment;
import com.zpj.zdialog.base.IDialog;
import com.zpj.zdialog.base.OutsideClickDialog;

public class ZBottomSheetDialog extends ZDialog implements IDialog {

//    public static ZBottomSheetDialog with(Context context) {
//        ZBottomSheetDialog dialog = new ZBottomSheetDialog();
//        FragmentActivity activity;
//        if (context instanceof FragmentActivity) {
//            activity = (FragmentActivity) context;
//        } else {
//            activity = ((FragmentActivity) ((ContextWrapper) context).getBaseContext());
//        }
//        dialog.setFragmentActivity(activity);
//        dialog.setAnimatorCreateListener(new OnAnimatorCreateListener() {
//            @Override
//            public Animator createInAnimator(View view) {
//                return AnimHelper.createBottomInAnim(view);
//            }
//
//            @Override
//            public Animator createOutAnimator(View view) {
//                return AnimHelper.createBottomOutAnim(view);
//            }
//        });
//        dialog.setSwipeEnable(false);
//        return dialog;
//    }

    @NonNull
    @Override
    public OutsideClickDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(this.getContext(), this.getTheme());
        return dialog;
    }

    @Override
    protected void onDialogStart() {

    }
}
