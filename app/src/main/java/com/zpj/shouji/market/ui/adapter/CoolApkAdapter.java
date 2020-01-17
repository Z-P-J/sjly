package com.zpj.shouji.market.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.CoolApkItem;

import java.util.List;

public class CoolApkAdapter extends RecyclerView.Adapter<CoolApkAdapter.ViewHolder> {
    private List<CoolApkItem> coolApkItemList;
    private OnItemClickListener mItemClickListener;
    private Context context;
    private String app_site = "";
    private String app_img_site = "";
    private RequestManager requestManager;
    private String result = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int app_all_count = 0;
    private int app_update_count = 0;
    private int app_new_count = 0;
    private int app_old_count = 0;
    private OnItemClickListener onItemClickListener;


    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView app_icon;
        TextView app_title;
        TextView app_info;
        TextView app_count;
        TextView app_description;
        TextView app_type;
        TextView app_result;
        CardView app_item;
        TextView app_all;
        TextView app_update;
        TextView app_new;
        TextView app_old;

        public ViewHolder(View view){
            super(view);
            itemView = view;
            app_icon = view.findViewById(R.id.app_icon);
            app_title = view.findViewById(R.id.app_title);
            //app_title.setMovementMethod(ScrollingMovementMethod.getInstance());
            app_info = view.findViewById(R.id.app_info);
            app_count = view.findViewById(R.id.app_count);
            app_description = view.findViewById(R.id.app_description);
            //app_description.setMovementMethod(ScrollingMovementMethod.getInstance());
            app_type = view.findViewById(R.id.app_type);
            app_result = view.findViewById(R.id.app_result);
            app_item = view.findViewById(R.id.app_item);
            app_all = view.findViewById(R.id.app_all);
            app_update = view.findViewById(R.id.app_update);
            app_new = view.findViewById(R.id.app_new);
            app_old = view.findViewById(R.id.app_old);
        }
    }

    public CoolApkAdapter(List<CoolApkItem> coolApkItemList){
        this.coolApkItemList = coolApkItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        context = parent.getContext();
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coolapk_item,parent,false);

        final ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick((Integer) v.getTag());
                }
            }
        });

        requestManager = Glide.with(parent.getContext());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CoolApkAdapter.ViewHolder holder, final int position) {
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //editor = sharedPreferences.edit();
        holder.app_title.setText(coolApkItemList.get(position).getApp_title());
        holder.app_count.setText(coolApkItemList.get(position).getApp_count());
        holder.app_info.setText(coolApkItemList.get(position).getApp_info());
        holder.app_description.setText(coolApkItemList.get(position).getApp_description());
        app_site = coolApkItemList.get(position).getApp_site();
        app_img_site = coolApkItemList.get(position).getApp_img_site();
        Log.d("app__site",app_site);
        requestManager.load(app_img_site).into(holder.app_icon);
        //holder.app_result.setText(compareVersion(app_site));
        //coolApkItemList.get(position).setApp_result()
        String app_type = coolApkItemList.get(position).getApp_site().substring(1,4);
        if (app_type.equals("apk")) {
            app_type = "应用";
            holder.app_type.setBackgroundColor(Color.WHITE);
            holder.app_type.setTextColor(Color.GRAY);
        } else if (app_type.equals("gam")){
            app_type = "游戏";
            holder.app_type.setBackgroundColor(Color.GRAY);
            holder.app_type.setTextColor(Color.WHITE);
        }
        holder.app_type.setText(app_type);

        String app_result = coolApkItemList.get(position).getApp_result();
        holder.app_result.setText(app_result);
        //holder.app_result.setText(sharedPreferences.getString("app_result_" + (position + 20),""));
        if (app_result.equals("已收录")) {
            holder.app_result.setBackgroundColor(Color.GREEN);
        } else if (app_result.equals("待更新")) {
            holder.app_result.setBackgroundColor(Color.YELLOW);
        } else if (app_result.equals("未收录")){
            holder.app_result.setBackgroundColor(Color.RED);
        }
        holder.app_result.setTextColor(Color.DKGRAY);

        holder.app_item.setTag(position);

        /*
        holder.item_app_linear.onViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("position",""+position);
                Uri uri = Uri.parse("https://www.coolapk.com" + coolApkItemList.get(position).getAppSite());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(intent);


            }
        });
        */

    }

    @Override
    public int getItemCount() {
        return coolApkItemList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    private void countApp(){
        app_all_count = coolApkItemList.size();
        for (CoolApkItem coolApkItem : coolApkItemList) {
            if (coolApkItem.getApp_result().equals("待更新"))
                app_update_count++;
            else if (coolApkItem.getApp_result().equals("未收录"))
                app_new_count++;
            else  if (coolApkItem.getApp_result().equals("已收录"))
                app_old_count++;
        }
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


}
