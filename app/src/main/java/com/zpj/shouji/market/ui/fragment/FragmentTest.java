//package com.zpj.shouji.market.ui.fragment;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.View;
//
//import com.zpj.fragmentation.BaseFragment;
//import com.zpj.recyclerview.EasyRecyclerView;
//import com.zpj.shouji.market.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FragmentTest extends BaseFragment {
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_recycler_view;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        EasyRecyclerView<Object> recyclerView = new EasyRecyclerView<>(view.findViewById(R.id.recycler_view));
//        List<Object> list = new ArrayList<>();
//
//        recyclerView.setData(list)
//                .setItemRes(R.layout.item_test)
//                .build();
//        list.add(new Object());
//        recyclerView.notifyDataSetChanged();
//    }
//
//}
