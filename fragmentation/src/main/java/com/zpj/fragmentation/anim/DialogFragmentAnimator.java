package com.zpj.fragmentation.anim;

import android.os.Parcel;
import android.os.Parcelable;

import com.zpj.fragmentation.R;

/**
 * Created by YoKeyword on 16/2/5.
 */
public class DialogFragmentAnimator extends FragmentAnimator implements Parcelable{

    public DialogFragmentAnimator() {
        enter = R.anim.dialog_fragment;
        exit = R.anim.dialog_fragment;
        popEnter = R.anim.dialog_fragment;
        popExit = R.anim.dialog_fragment;
    }

    protected DialogFragmentAnimator(Parcel in) {
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

    public static final Creator<DialogFragmentAnimator> CREATOR = new Creator<DialogFragmentAnimator>() {
        @Override
        public DialogFragmentAnimator createFromParcel(Parcel in) {
            return new DialogFragmentAnimator(in);
        }

        @Override
        public DialogFragmentAnimator[] newArray(int size) {
            return new DialogFragmentAnimator[size];
        }
    };
}
