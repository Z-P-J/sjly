package com.zpj.markdown;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zpj.dialog.ZAlertDialog;
import com.zpj.dialog.base.IDialog;
import com.zpj.dialog.base.ZAbstractDialog;

import java.util.Objects;

/**
 * Created by wangshouheng on 2017/7/1.
 */
public class TableInputDialog2 extends ZAlertDialog implements IDialog.OnViewCreateListener {

    private OnConfirmClickListener onConfirmClickListener;

    private AppCompatEditText etMdRowsNumber;
    private AppCompatEditText etMdColsNumber;
    TextInputLayout rowNumberHint;
    TextInputLayout columnNumberHint;

    public static TableInputDialog2 with(Context context) {
        TableInputDialog2 dialog = new TableInputDialog2();
        FragmentActivity activity;
        if (context instanceof FragmentActivity) {
            activity = (FragmentActivity) context;
        } else {
            activity = ((FragmentActivity) ((ContextWrapper) context).getBaseContext());
        }
        dialog.setFragmentActivity(activity);
        return dialog;
    }

    public TableInputDialog2() {
        setScreenWidthP(0.9f);
        setOnViewCreateListener(this);
        setPositiveButton(dialog -> {
            String rowNumberStr = Objects.requireNonNull(etMdRowsNumber.getText()).toString().trim();
            String columnNumberStr = Objects.requireNonNull(etMdColsNumber.getText()).toString().trim();

            if (TextUtils.isEmpty(rowNumberStr)) rowNumberHint.setError(getString(R.string.note_table_rows_required));
            if (TextUtils.isEmpty(columnNumberStr)) columnNumberHint.setError(getString(R.string.note_table_cols_required));

            if (rowNumberHint.isErrorEnabled()) rowNumberHint.setErrorEnabled(false);
            if (columnNumberHint.isErrorEnabled()) columnNumberHint.setErrorEnabled(false);

            if (onConfirmClickListener != null) onConfirmClickListener.onConfirmClick(rowNumberStr, columnNumberStr);

            dismiss();
        });
    }

    @Override
    protected void setFragmentActivity(FragmentActivity activity) {
        super.setFragmentActivity(activity);
        setTitle(R.string.note_table_insert);
        setContentView(R.layout.layout_dialog_input_table);
    }

    public TableInputDialog2 setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
        return this;
    }

    @Override
    public void onViewCreate(IDialog dialog, View view) {
        etMdRowsNumber = view.findViewById(R.id.et_md_rows_number);
        etMdColsNumber = view.findViewById(R.id.et_md_cols_number);
        rowNumberHint = view.findViewById(R.id.rowNumberHint);
        columnNumberHint = view.findViewById(R.id.columnNumberHint);
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String rows, String cols);
    }
}
