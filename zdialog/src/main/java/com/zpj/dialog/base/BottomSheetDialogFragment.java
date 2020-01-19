package com.zpj.dialog.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.zpj.dialog.R;

public class BottomSheetDialogFragment extends DialogFragment {
    public BottomSheetDialogFragment() {
    }

    @Override
    @NonNull
    public OutsideClickDialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(this.getContext(), this.getTheme());
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
