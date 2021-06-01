package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.widget.DialogHeaderLayout;
import com.zpj.utils.KeyboardObserver;

public abstract class ModifiedDialogFragment extends BottomDragDialogFragment<ModifiedDialogFragment> {

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setCornerRadiusDp(20);
        super.initView(view, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DialogHeaderLayout headerLayout = findViewById(R.id.layout_dialog_header);
        if (headerLayout != null) {
            headerLayout.setOnCloseClickListener(view1 -> dismiss());
        }
        KeyboardObserver.registerSoftInputChangedListener(_mActivity, contentView, height -> {
            contentView.setTranslationY(-height);
        });
    }

    @Override
    public void onDestroyView() {
        hideSoftInput();
        super.onDestroyView();
    }

    protected void showKeyboard(View view) {
        postDelayed(() -> showSoftInput(view), 150);
    }

}
