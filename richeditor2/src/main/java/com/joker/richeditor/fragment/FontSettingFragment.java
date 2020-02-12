package com.joker.richeditor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joker.richeditor.R;
import com.joker.richeditor.adapter.FontSettingAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Font Setting Fragment
 * Created by even.wu on 9/8/17.
 */

public class FontSettingFragment extends Fragment {
    public static final String TYPE = "type";

    public static final int TYPE_SIZE = 0;
    public static final int TYPE_LINE_HEIGHT = 1;
    public static final int TYPE_FONT_FAMILY = 2;

    private List<String> fontFamilyList =
            Arrays.asList("Arial", "Arial Black", "Comic Sans MS", "Courier New", "Helvetica Neue",
                    "Helvetica", "Impact", "Lucida Grande", "Tahoma", "Times New Roman", "Verdana");

    private List<String> fontSizeList =
            Arrays.asList("12", "14", "16", "18", "20", "22", "24", "26", "28", "36");

    private List<String> fontLineHeightList =
            Arrays.asList("1.0", "1.2", "1.4", "1.6", "1.8", "2.0", "3.0");

    private RecyclerView rvContainer;
    private FontSettingAdapter mAdapter;
    private OnResultListener mOnResultListener;
    private List<String> dataSourceList = fontSizeList;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_font_setting, null);
        int type = getArguments().getInt(TYPE);
        if (type == TYPE_SIZE) {
            dataSourceList = fontSizeList;
        } else if (type == TYPE_LINE_HEIGHT) {
            dataSourceList = fontLineHeightList;
        } else if (type == TYPE_FONT_FAMILY) {
            dataSourceList = fontFamilyList;
        }
        rvContainer = (RecyclerView) rootView.findViewById(R.id.rv_container);
        initRecyclerView();
        return rootView;
    }

    private void initRecyclerView() {
        rvContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new FontSettingAdapter(dataSourceList);
        mAdapter.setOnItemClickListener(new FontSettingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FontSettingAdapter adapter, View view, int position) {
                if (mOnResultListener != null) {
                    mOnResultListener.onResult(dataSourceList.get(position));
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction()
                            .remove(FontSettingFragment.this)
                            .show(fm.findFragmentByTag(EditorMenuFragment.class.getName()))
                            .commit();
                }
            }
        });
        rvContainer.setAdapter(mAdapter);
    }

    interface OnResultListener {
        void onResult(String result);
    }

    public void setOnResultListener(OnResultListener mOnResultListener) {
        this.mOnResultListener = mOnResultListener;
    }
}
