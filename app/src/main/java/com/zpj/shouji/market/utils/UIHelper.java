package com.zpj.shouji.market.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.market.R;
import com.wang.avi.AVLoadingIndicatorView;

public class UIHelper {
    private static Dialog mLoadingDialog;
    public static void showDialogForLoading(Context context, String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
        TextView loadingText = view.findViewById(R.id.id_tv_loading_dialog_text);
        AVLoadingIndicatorView avLoadingIndicatorView = view.findViewById(R.id.AVLoadingIndicatorView);
        loadingText.setText(msg);
        mLoadingDialog = new Dialog(context, R.style.loading_dialog_style);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        mLoadingDialog.show();
        avLoadingIndicatorView.smoothToShow();
        mLoadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mLoadingDialog.hide();
                    return true;
                }
                return false;
            }
        });
    }

    public static void HideDilog(){
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.hide();
        }
    }
}
