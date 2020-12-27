package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.recyclerview.state.StateManager;

public abstract class StateSwipeBackFragment extends StateFragment {

    @Override
    protected final boolean supportSwipeBack() {
        return true;
    }
}
