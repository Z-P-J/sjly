package com.sjly.zpj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sjly.zpj.fragment.CoolApkFragment;
import com.sjly.zpj.fragment.QianQianFragment;
import com.sjly.zpj.fragment.XinHaiFragment;

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
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        //tabLayout.addTab(tabLayout.newTab().setText("酷安"));
        //tabLayout.addTab(tabLayout.newTab().setText("芊芊经典"));
        //tabLayout.addTab(tabLayout.newTab().setText("心海e站"));
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            Fragment fragment = null;
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        fragment = new CoolApkFragment();
                        tabLayout.getTabAt(position).setText("酷安");
                        break;
                    case 1:
                        fragment = new QianQianFragment();
                        tabLayout.getTabAt(position).setText("芊芊经典");
                        break;
                    case 2:
                        fragment = new XinHaiFragment();
                        tabLayout.getTabAt(position).setText("心海e站");
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        tabLayout.setupWithViewPager(viewPager);

        //coolApkFragment = new CoolApkFragment();

        //fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().add(R.id.container,coolApkFragment).commit();
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
