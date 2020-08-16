package com.zpj.shouji.market.ui.fragment.base;

import com.zpj.fragmentation.BaseFragment;

public abstract class ListenerFragment extends BaseFragment {

    public interface FragmentLifeCycler{
        void onFragmentStart();
        void onFragmentStop();
        void onFragmentDestroy();
    }

    private FragmentLifeCycler lifeCycler;

    public void setFragmentLifeCycler(FragmentLifeCycler lifeCycler){
        this.lifeCycler = lifeCycler;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifeCycler != null) {
            lifeCycler.onFragmentStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifeCycler != null) {
            lifeCycler.onFragmentStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifeCycler != null) {
            lifeCycler.onFragmentDestroy();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

}
