package com.zpj.sjly;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.zpj.sjly.fragment.AppChinaFragment;
import com.zpj.sjly.fragment.CoolApkFragment;
import com.zpj.sjly.fragment.QianQianFragment;
import com.zpj.sjly.fragment.XinHaiFragment;

public class MainActivity extends AppCompatActivity {

    private long firstTime=0;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout)findViewById(R.id.tabs);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
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
                        tabLayout.getTabAt(position).setText("芊芊精典");
                        break;
                    case 2:
                        fragment = new XinHaiFragment();
                        tabLayout.getTabAt(position).setText("心海e站");
                        break;
                    case 3:
                        fragment = new AppChinaFragment();
                        tabLayout.getTabAt(position).setText("应用汇");
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 4;
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
}
