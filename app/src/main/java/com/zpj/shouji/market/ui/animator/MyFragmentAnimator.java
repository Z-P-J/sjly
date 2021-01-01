package com.zpj.shouji.market.ui.animator;

import android.os.Parcel;
import android.os.Parcelable;

import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.shouji.market.R;

public class MyFragmentAnimator extends FragmentAnimator implements Parcelable {

//    public MyFragmentAnimator() {
//        enter = R.anim.my_fragment_enter;
//        exit = R.anim.my_fragment_exit;
//        popEnter = R.anim.my_fragment_pop_enter;
//        popExit = R.anim.my_fragment_pop_exit;
//    }

    public MyFragmentAnimator() {
        enter = R.anim.vertical_fragment_enter;
        exit = R.anim.vertical_fragment_exit;
        popEnter = R.anim.vertical_fragment_pop_enter;
        popExit = R.anim.vertical_fragment_pop_exit;
    }

    protected MyFragmentAnimator(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyFragmentAnimator> CREATOR = new Creator<MyFragmentAnimator>() {
        @Override
        public MyFragmentAnimator createFromParcel(Parcel in) {
            return new MyFragmentAnimator(in);
        }

        @Override
        public MyFragmentAnimator[] newArray(int size) {
            return new MyFragmentAnimator[size];
        }
    };

}
