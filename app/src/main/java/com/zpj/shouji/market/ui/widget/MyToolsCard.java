package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihang.ShadowLayout;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.profile.MyBlacklistFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyBookingFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyCollectionFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyCommentFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyDiscoverFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyDynamicFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyFragment;
import com.zpj.shouji.market.ui.fragment.profile.MyFriendsFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import per.goweii.burred.Blurred;

public class MyToolsCard extends ShadowLayout implements View.OnClickListener {

    private DrawableTintTextView tvMyHomepage;
    private DrawableTintTextView tvMyDiscovers;
    private DrawableTintTextView tvMyComments;
    private DrawableTintTextView tvMyFriends;
    private DrawableTintTextView tvMyMessages;
    private DrawableTintTextView tvMyCollections;
    private DrawableTintTextView tvMyBookings;
    private DrawableTintTextView tvMyBlacklist;

    private FrameLayout flNotLogin;
    private TextView tvSignUp;
    private TextView tvSignIn;

    private SupportActivity activity;
    private MyFragment fragment;
    
    public MyToolsCard(@NonNull Context context) {
        this(context, null);
    }

    public MyToolsCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyToolsCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.card_my_tools, this);

        flNotLogin = findViewById(R.id.fl_not_login);
        ImageView ivBg = findViewById(R.id.iv_bg);
        GridLayout gridLayout = findViewById(R.id.grid);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
                    Bitmap bitmap = Blurred.with(MyToolsCard.this)
//                            .foregroundColor(Color.parseColor("#40ffffff"))
                            .scale(1f / 8f)
                            .radius(20)
                            .blur();
                    emitter.onNext(bitmap);
                    emitter.onComplete();
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(ivBg::setImageBitmap)
                        .subscribe();
            }
        });

        tvSignUp = findViewById(R.id.tv_sign_up);
        tvSignIn = findViewById(R.id.tv_sign_in);

        tvMyHomepage = findViewById(R.id.tv_my_homepage);
        tvMyDiscovers = findViewById(R.id.tv_my_discovers);
        tvMyComments = findViewById(R.id.tv_my_comments);
        tvMyFriends = findViewById(R.id.tv_my_friends);
        tvMyMessages = findViewById(R.id.tv_my_messages);
        tvMyCollections = findViewById(R.id.tv_my_collections);
        tvMyBookings = findViewById(R.id.tv_my_bookings);
        tvMyBlacklist = findViewById(R.id.tv_my_blacklist);

        tvSignUp.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
        tvMyHomepage.setOnClickListener(this);
        tvMyDiscovers.setOnClickListener(this);
        tvMyComments.setOnClickListener(this);
        tvMyFriends.setOnClickListener(this);
        tvMyMessages.setOnClickListener(this);
        tvMyCollections.setOnClickListener(this);
        tvMyBookings.setOnClickListener(this);
        tvMyBlacklist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_sign_up) {
            fragment.showLoginPopup(0);
            return;
        } else if (v.getId() == R.id.tv_sign_in) {
            fragment.showLoginPopup(1);
            return;
        }
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        switch (v.getId()) {
//            case R.id.tv_sign_up:
//                fragment.showLoginPopup(0);
//                break;
//            case R.id.tv_sign_in:
//                fragment.showLoginPopup(1);
//                break;
            case R.id.tv_my_homepage:
                MyDynamicFragment.start();
//                ProfileFragment.start(UserManager.getInstance().getUserId(), false);
                break;
            case R.id.tv_my_discovers:
                MyDiscoverFragment.start();
                break;
            case R.id.tv_my_comments:
                MyCommentFragment.start();
                break;
            case R.id.tv_my_friends:
                MyFriendsFragment.start(UserManager.getInstance().getUserId());
                break;
            case R.id.tv_my_messages:

                break;
            case R.id.tv_my_collections:
                MyCollectionFragment.start(UserManager.getInstance().getUserId());
                break;
            case R.id.tv_my_bookings:
                MyBookingFragment.start();
                break;
            case R.id.tv_my_blacklist:
                MyBlacklistFragment.start();
                break;
        }
    }

    public void attachActivity(SupportActivity activity) {
        this.activity = activity;
    }

    public void attachFragment(MyFragment fragment) {
        this.fragment = fragment;
    }

    public void onLogin() {
        flNotLogin.setVisibility(View.GONE);
//        removeView(flNotLogin);
//        flNotLogin = null;

        tvMyHomepage.setOnClickListener(this);
        tvMyDiscovers.setOnClickListener(this);
        tvMyComments.setOnClickListener(this);
        tvMyFriends.setOnClickListener(this);
        tvMyMessages.setOnClickListener(this);
        tvMyCollections.setOnClickListener(this);
        tvMyBookings.setOnClickListener(this);
        tvMyBlacklist.setOnClickListener(this);
    }

    public void onSignOut() {
        flNotLogin.setVisibility(View.VISIBLE);
//        removeView(flNotLogin);
//        flNotLogin = null;

        tvMyHomepage.setOnClickListener(null);
        tvMyDiscovers.setOnClickListener(null);
        tvMyComments.setOnClickListener(null);
        tvMyFriends.setOnClickListener(null);
        tvMyMessages.setOnClickListener(null);
        tvMyCollections.setOnClickListener(null);
        tvMyBookings.setOnClickListener(null);
        tvMyBlacklist.setOnClickListener(null);
    }

}
