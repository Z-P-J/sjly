package com.joker.richeditor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.joker.richeditor.R;


/**
 * Edit Table Fragment
 * Created by even.wu on 10/8/17.
 */

public class EditTableFragment extends Fragment implements View.OnClickListener {
    private EditText etRows;
    private EditText etCols;
    private OnTableListener mOnTableListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_table, null);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        etRows = (EditText) view.findViewById(R.id.et_rows);
        etCols = (EditText) view.findViewById(R.id.et_cols);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    public void setOnTableListener(OnTableListener mOnTableListener) {
        this.mOnTableListener = mOnTableListener;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.iv_back) {
            removeThisFragment();
        } else if (id == R.id.btn_ok) {
            if (mOnTableListener != null) {
                mOnTableListener.onTableOK(Integer.valueOf(etRows.getText().toString()), Integer.valueOf(etCols.getText().toString()));
                removeThisFragment();
            }
        }
    }

    private void removeThisFragment() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    public interface OnTableListener {
        void onTableOK(int rows, int cols);
    }
}
