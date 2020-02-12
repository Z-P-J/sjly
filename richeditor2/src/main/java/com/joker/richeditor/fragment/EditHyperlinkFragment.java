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
 * Edit Hyperlink Activity
 * Created by even.wu on 10/8/17.
 */

public class EditHyperlinkFragment extends Fragment implements View.OnClickListener {
    private EditText etAddress;
    private EditText etDisplayText;
    private OnHyperlinkListener mOnHyperlinkListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_hyperlink, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        etAddress = (EditText) view.findViewById(R.id.et_address);
        etDisplayText = (EditText) view.findViewById(R.id.et_display_text);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    public void setOnHyperlinkListener(OnHyperlinkListener mOnHyperlinkListener) {
        this.mOnHyperlinkListener = mOnHyperlinkListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            removeThisFragment();
        } else if (i == R.id.btn_ok) {
            if (mOnHyperlinkListener != null) {
                mOnHyperlinkListener.onHyperlinkOK(etAddress.getText().toString(), etDisplayText.getText().toString());
                removeThisFragment();
            }
        }
    }

    private void removeThisFragment() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    public interface OnHyperlinkListener {
        void onHyperlinkOK(String address, String text);
    }
}
