package com.zpj.markdown;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;

import com.zpj.popup.impl.AlertPopup;

import java.util.Objects;

/**
 * Created by wangshouheng on 2017/7/1.
 */
public class TableInputDialog2 extends AlertPopup {

    private OnConfirmClickListener onConfirmClickListener;

    private AppCompatEditText etMdRowsNumber;
    private AppCompatEditText etMdColsNumber;
    TextInputLayout rowNumberHint;
    TextInputLayout columnNumberHint;

    public static TableInputDialog2 with(Context context) {
        return new TableInputDialog2(context);
    }

    private TableInputDialog2(Context context) {
        super(context);
        setConfirmButton(() -> {
            String rowNumberStr = Objects.requireNonNull(etMdRowsNumber.getText()).toString().trim();
            String columnNumberStr = Objects.requireNonNull(etMdColsNumber.getText()).toString().trim();

            if (TextUtils.isEmpty(rowNumberStr)) rowNumberHint.setError(context.getString(R.string.note_table_rows_required));
            if (TextUtils.isEmpty(columnNumberStr)) columnNumberHint.setError(context.getString(R.string.note_table_cols_required));

            if (rowNumberHint.isErrorEnabled()) rowNumberHint.setErrorEnabled(false);
            if (columnNumberHint.isErrorEnabled()) columnNumberHint.setErrorEnabled(false);

            if (onConfirmClickListener != null) onConfirmClickListener.onConfirmClick(rowNumberStr, columnNumberStr);

            dismiss();
        });

        setTitle(context.getString(R.string.note_table_insert));
        setContent(R.layout.layout_dialog_input_table);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        etMdRowsNumber = findViewById(R.id.et_md_rows_number);
        etMdColsNumber = findViewById(R.id.et_md_cols_number);
        rowNumberHint = findViewById(R.id.rowNumberHint);
        columnNumberHint = findViewById(R.id.columnNumberHint);
    }

    public TableInputDialog2 setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
        return this;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String rows, String cols);
    }
}
