package com.zpj.statemanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class StateManager extends BaseConfig<StateManager> {

    private static StateConfig DEFAULT_CONFIG;

    private final FrameLayout recyclerView;

    private final View contentView;


    private final Context context;

    private Runnable onRetry;
    private Runnable onLogin;

//    private boolean isRecyclable = false;

    public static final int STATE_CONTENT = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_EMPTY = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_LOGIN = 4;
    public static final int STATE_NO_NETWORK = 5;

    private int state = STATE_CONTENT;


    public interface Action {
        void run(final StateManager manager);
    }

    public View getStateView() {
        return recyclerView;
    }

    public static class StateConfig extends BaseConfig<StateConfig> { }

    private StateManager(View view) {
        this.contentView = view;
        this.context = view.getContext();
        loadingViewHolder = config().loadingViewHolder;
        emptyViewHolder = config().emptyViewHolder;
        errorViewHolder = config().errorViewHolder;
        loginViewHolder = config().loginViewHolder;
        noNetworkViewHolder = config().noNetworkViewHolder;

        recyclerView = new FrameLayout(context);

        recyclerView.setLayoutParams(view.getLayoutParams());
        if (view.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
            parent.addView(recyclerView);
        }
    }

    public static StateConfig config() {
        if (DEFAULT_CONFIG == null) {
            synchronized (StateManager.class) {
                if (DEFAULT_CONFIG == null) {
                    DEFAULT_CONFIG = new StateConfig();
                }
            }
        }
        return DEFAULT_CONFIG;
    }

    public static StateManager with(View view) {
        return new StateManager(view);
    }

    public StateManager onRetry(final Action action) {
        this.onRetry = new Runnable() {
            @Override
            public void run() {
                if (action != null) {
                    action.run(StateManager.this);
                }
            }
        };
        return this;
    }

    public StateManager onLogin(final Action action) {
        this.onLogin = new Runnable() {
            @Override
            public void run() {
                if (action != null) {
                    action.run(StateManager.this);
                }
            }
        };
        return this;
    }

//    public StateManager setRecyclable(boolean recyclable) {
//        this.isRecyclable = recyclable;
//        return this;
//    }

    public StateManager showContent() {
        changeState(STATE_CONTENT, null);
        return this;
    }

    public StateManager showLoading() {
        changeState(STATE_LOADING, null);
        return this;
    }

    public StateManager showEmpty() {
        changeState(STATE_EMPTY, null);
        return this;
    }

    public StateManager showError(String msg) {
        changeState(STATE_ERROR, msg);
        return this;
    }

    public StateManager showError() {
        changeState(STATE_ERROR, null);
        return this;
    }

    public StateManager showLogin() {
        changeState(STATE_LOGIN, null);
        return this;
    }

    public StateManager showNoNetwork() {
        changeState(STATE_NO_NETWORK, null);
        return this;
    }

    private void changeState(int state, String msg) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = null;
        recyclerView.removeAllViews();
        switch (state) {
            case STATE_CONTENT:
                if (contentView.getParent() instanceof ViewGroup) {
                    ViewGroup parent = (ViewGroup) contentView.getParent();
                    parent.removeView(contentView);
                    recyclerView.addView(contentView, params);
                    parent.addView(recyclerView);
                } else {
                    recyclerView.addView(contentView, params);
                }
                return;
            case STATE_LOADING:
                view = getLoadingViewHolder().onCreateView(context);
                break;
            case STATE_EMPTY:
                view = getEmptyViewHolder().onCreateView(context);
                break;
            case STATE_ERROR:
                view = getErrorViewHolder().onCreateView(context);
                if (errorViewHolder instanceof BaseViewHolder) {
                    ((BaseViewHolder) errorViewHolder).setOnRetry(onRetry);
                    ((BaseViewHolder) errorViewHolder).setMsg(msg);
                }
                break;
            case STATE_LOGIN:
                view = getLoginViewHolder().onCreateView(context);
                if (loginViewHolder instanceof BaseViewHolder) {
                    ((BaseViewHolder) loginViewHolder).setOnLogin(onLogin);
                }
                break;
            case STATE_NO_NETWORK:
                view = getNoNetworkViewHolder().onCreateView(context);
                if (noNetworkViewHolder instanceof BaseViewHolder) {
                    ((BaseViewHolder) noNetworkViewHolder).setOnRetry(onRetry);
                }
                break;
        }
        if (view != null) {
            recyclerView.addView(view, params);
        }

    }

}
