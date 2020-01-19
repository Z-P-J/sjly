package com.zpj.dialog;

import android.animation.Animator;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.zpj.utils.AnimHelper;
import com.zpj.dialog.base.BottomSheetDialog;
import com.zpj.dialog.base.IDialog;
import com.zpj.dialog.base.OutsideClickDialog;
import com.zpj.dialog.base.ZAbstractDialog;

public class ZBottomSheetDialog extends ZAbstractDialog<ZBottomSheetDialog> implements IDialog {

    public static ZBottomSheetDialog with(Context context) {
        ZBottomSheetDialog dialog = new ZBottomSheetDialog();
        FragmentActivity activity;
        if (context instanceof FragmentActivity) {
            activity = (FragmentActivity) context;
        } else {
            activity = ((FragmentActivity) ((ContextWrapper) context).getBaseContext());
        }
        dialog.setFragmentActivity(activity);
        return dialog;
    }

    public ZBottomSheetDialog() {
        setAnimatorCreateListener(new OnAnimatorCreateListener() {
            @Override
            public Animator createInAnimator(View view) {
                return AnimHelper.createBottomInAnim(view);
            }

            @Override
            public Animator createOutAnimator(View view) {
                return AnimHelper.createBottomOutAnim(view);
            }
        });
        setSwipeEnable(false);
    }

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
