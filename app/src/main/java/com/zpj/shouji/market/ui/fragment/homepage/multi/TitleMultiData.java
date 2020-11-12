//package com.zpj.shouji.market.ui.fragment.homepage.multi;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.recyclerview.MultiAdapter;
//import com.zpj.recyclerview.MultiData;
//import com.zpj.shouji.market.R;
//
//import java.util.List;
//
//public class TitleMultiData extends MultiData<String> {
//
//    private final String title;
//    private final View.OnClickListener listener;
//
//    public TitleMultiData(String title, View.OnClickListener listener) {
//        this.title = title;
//        this.listener = listener;
//    }
//
//    @Override
//    public int getSpanCount(int viewType) {
//        return 4;
//    }
//
//    @Override
//    public int getLayoutId(int viewType) {
//        return R.layout.item_header_title;
//    }
//
//    @Override
//    public boolean loadData(RecyclerView recyclerView, MultiAdapter adapter) {
//        list.add(title);
//        adapter.notifyDataSetChanged();
////        recyclerView.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
////            }
////        }, 100);
//        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//        return true;
//    }
//
//    @Override
//    public void onBindViewHolder(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
//        holder.setText(R.id.tv_title, list.get(position));
//        holder.setVisible(R.id.tv_more, listener != null);
//        holder.setOnClickListener(R.id.tv_more, listener);
//    }
//
//}
