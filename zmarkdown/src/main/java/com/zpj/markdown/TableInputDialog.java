//package com.zpj.markdown;
//
//import android.app.Dialog;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TextInputLayout;
//import android.support.v4.app.DialogFragment;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.AppCompatEditText;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.TextView;
//
//import java.util.Objects;
//
///**
// * Created by wangshouheng on 2017/7/1.
// */
//public class TableInputDialog extends DialogFragment {
//
//    private OnConfirmClickListener onConfirmClickListener;
//
//    public static TableInputDialog getInstance() {
//        return new TableInputDialog();
//    }
//
//    public static TableInputDialog getInstance(OnConfirmClickListener onConfirmClickListener){
//        TableInputDialog tableInputDialog = new TableInputDialog();
//        tableInputDialog.setOnConfirmClickListener(onConfirmClickListener);
//        return tableInputDialog;
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_input_table, null, false);
//        TextView tvMdCancel = view.findViewById(R.id.tv_md_cancel);
//        TextView tvMdConfirm = view.findViewById(R.id.tv_md_confirm);
//        tvMdCancel.setTextColor(getResources().getColor(R.color.colorText));
//        tvMdConfirm.setTextColor(getResources().getColor(R.color.colorPrimary));
//        AppCompatEditText etMdRowsNumber = view.findViewById(R.id.et_md_rows_number);
//        AppCompatEditText etMdColsNumber = view.findViewById(R.id.et_md_cols_number);
//        TextInputLayout rowNumberHint = view.findViewById(R.id.rowNumberHint);
//        TextInputLayout columnNumberHint = view.findViewById(R.id.columnNumberHint);
//
//        tvMdConfirm.setOnClickListener(v -> {
//            String rowNumberStr = Objects.requireNonNull(etMdRowsNumber.getText()).toString().trim();
//            String columnNumberStr = Objects.requireNonNull(etMdColsNumber.getText()).toString().trim();
//
//            if (TextUtils.isEmpty(rowNumberStr)) rowNumberHint.setError(getString(R.string.note_table_rows_required));
//            if (TextUtils.isEmpty(columnNumberStr)) columnNumberHint.setError(getString(R.string.note_table_cols_required));
//
//            if (rowNumberHint.isErrorEnabled()) rowNumberHint.setErrorEnabled(false);
//            if (columnNumberHint.isErrorEnabled()) columnNumberHint.setErrorEnabled(false);
//
//            if (onConfirmClickListener != null) onConfirmClickListener.onConfirmClick(rowNumberStr, columnNumberStr);
//
//            dismiss();
//        });
//
//        tvMdCancel.setOnClickListener(v -> dismiss());
//
//        return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
//                .setTitle(R.string.note_table_insert)
//                .setView(view)
//                .create();
//    }
//
//    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
//        this.onConfirmClickListener = onConfirmClickListener;
//    }
//
//    public interface OnConfirmClickListener {
//        void onConfirmClick(String rows, String cols);
//    }
//}
