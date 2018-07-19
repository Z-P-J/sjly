package com.sjly.zpj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sjly.zpj.fragment.CoolApkFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private long firstTime=0;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private CoolApkFragment coolApkFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        coolApkFragment = new CoolApkFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container,coolApkFragment).commit();
        //getCoolApkHtml();

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis()-firstTime>2000){
            Toast.makeText(this, "再次点击退出！", Toast.LENGTH_SHORT).show();
            firstTime=System.currentTimeMillis();
        }else {
            finish();
        }
    }


    /*
    private  void getCoolApkHtml(){

        new Thread(){
            String htmlContent = "";
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.coolapk.com/apk/update?p=1")
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                            .timeout(3000)
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                    htmlContent = doc.body().toString();

                    Elements elements = doc.select("div.app_list_left").select("a");
                    for (int i = 0; i < 20; i++) {
                        int j = elements.get(i).select("p.list_app_info").text().indexOf("M");
                        String str = elements.get(i).select("p.list_app_info").text().substring(0,j+1);
                        editor.putString("app_site_"+i, elements.get(i).attr("href"));
                        editor.putString("app_img_site_"+i, elements.get(i).attr("src"));
                        editor.putString("app_title_"+i, elements.get(i).select("p.list_app_title").text());
                        editor.putString("app_info_"+i, elements.get(i).select("p.list_app_info").text());
                        editor.putString("app_count_"+i, elements.get(i).select("span.list_app_count").text());
                        editor.putString("app_description_"+i, elements.get(i).select("p.list_app_description").text());
                        Log.d("appppp", str);
                    }
                    editor.apply();

                    //Toast.makeText(MainActivity.this, elements.get(0).text(), Toast.LENGTH_SHORT).show();


                }catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.d("CoolApkHtml:", htmlContent);

                //getAppDetail(htmlContent);
            }

        }.start();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container,coolApkFragment).commit();

        //Toast.makeText(, "getCoolApkHtml", Toast.LENGTH_SHORT).show();
    }

    private void getAppDetail(final String htmlContent){
        new Thread(){
            @Override
            public void run() {

                try {
                    Toast.makeText(MainActivity.this, htmlContent, Toast.LENGTH_SHORT).show();
                    Elements elements = Jsoup.parse(htmlContent).select("app_list_left").select("a");
                    Toast.makeText(MainActivity.this, elements.get(0).text(), Toast.LENGTH_SHORT).show();
                    //Log.d("app0:",elements.get(0).text());
                }catch (Exception e) {
                    e.printStackTrace();
                }


                //for(int i = 0; i < 20; i++){
                //    String str = elements.get(i).select()
                //}

            }
        }.start();
    }
    */
}
