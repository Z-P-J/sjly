package com.sjly.zpj.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sjly.zpj.R;
import com.sjly.zpj.fragment.CoolApkItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Iterator;
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
            app_icon = (ImageView)view.findViewById(R.id.app_icon);
            app_title = (TextView) view.findViewById(R.id.app_title);
            //app_title.setMovementMethod(ScrollingMovementMethod.getInstance());
            app_info = (TextView)view.findViewById(R.id.app_info);
            app_count = (TextView)view.findViewById(R.id.app_count);
            app_description = (TextView)view.findViewById(R.id.app_description);
            //app_description.setMovementMethod(ScrollingMovementMethod.getInstance());
            app_type = (TextView)view.findViewById(R.id.app_type);
            app_result = (TextView)view.findViewById(R.id.app_result);
            app_item = (CardView)view.findViewById(R.id.app_item);
            app_all = (TextView)view.findViewById(R.id.app_all);
            app_update = (TextView)view.findViewById(R.id.app_update);
            app_new = (TextView)view.findViewById(R.id.app_new);
            app_old = (TextView)view.findViewById(R.id.app_old);
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
        holder.app_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("position",""+position);
                Uri uri = Uri.parse("https://www.coolapk.com" + coolApkItemList.get(position).getApp_site());
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

    /*
    private String compareVersion(final String app_site){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String id = "";
                String url = "https://www.coolapk.com" + app_site;
                final String packageName;
                if (app_site.startsWith("/apk/")) {
                    packageName = app_site.substring(5);
                }else {
                    packageName = app_site.substring(6);
                }
                Document doc;
                Elements elements;
                try {
                    doc = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .timeout(2000)
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true).get();

                    elements = doc.select("span.list_app_info");
                    String versionName2 = elements.text();
                    //Log.d("version",elements.text());

                    doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/app_search_quick_xml.jsp?s=" + packageName)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    elements = doc.select("package");
                    Log.d("elementSize","" + elements.size());
                    if (elements.size() > 0) {
                        Log.d("sjlypackageName",packageName + "\t" + elements.get(0).text());
                        id = doc.select("id").get(0).text();
                        Log.d("ididididid11111111",id);
                    } else {
                        Log.d("sjlypackageName",packageName + "\t无");
                        Log.d("ididididid11111111","无");
                    }

                    if(elements.size() > 0 && elements.get(0).text().equals(packageName)) {
                        doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/soft_show.jsp?id=" + id)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                                .ignoreHttpErrors(true)
                                .ignoreContentType(true)
                                .get();
                        if (doc.select("versionname").size() == 0) {
                            doc = Jsoup.connect("http://tt.shouji.com.cn/androidv3/game_show.jsp?id=" + id)
                                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                                    .ignoreHttpErrors(true)
                                    .ignoreContentType(true)
                                    .get();
                        }

                        Log.d("sjlyversionname",doc.select("versionname").get(0).text());
                        String versionName1 = doc.select("versionname").get(0).text();
                        if(compareVersion(versionName1,versionName2)){
                            result = "已收录";
                            Log.d("result","已收录"+"\t"+versionName1+"\t"+versionName2);
                        }else {
                            result = "待更新";
                            Log.d("result","待更新"+"\t"+versionName1+"\t"+versionName2);
                        }
                    } else {
                        result = "未收录";
                        Log.d("result","未收录"+"\t无\t"+versionName2);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return result;
    }

    private boolean compareVersion(String version1, String version2){
        if (version1.equals(version2)){
            return true;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        int minLen = Math.min(version1Array.length,version2Array.length);
        int diff = 0;
        while (index < minLen && (diff = Integer.parseInt(version1Array[index])- Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0){
            for (int i = index; i < version1Array.length; i++){
                if (Integer.parseInt(version1Array[1]) > 0)
                    return true;
            }
            for (int i = index; i < version2Array.length; i++){
                if (Integer.parseInt(version2Array[1]) > 0)
                    return false;
            }
            return true;
        }else {
            return diff > 0;
        }
    }
    */

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
