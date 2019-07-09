package com.zpj.sjly.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.zpj.sjly.R;
import com.zpj.sjly.constant.Key;
import com.zpj.sjly.ui.fragment.UserFragment;

public class ProfileActivity extends AppCompatActivity {

    public static void startActivity(Context context, String userId) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(Key.USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        String userId = getIntent().getStringExtra(Key.USER_ID);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, UserFragment.newInstance(userId, false)).commit();
    }

    @Override
    protected void onDestroy() {
        AToast.normal("onDestroy");
        super.onDestroy();
        finish();
    }
}
