package com.sjly.zpj.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjly.zpj.R;

public class XinHaiFragment extends BaseFragment{


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xinhai_fragment,null);
        return view;
    }

    @Override
    public void lazyLoadData() {

    }
}
