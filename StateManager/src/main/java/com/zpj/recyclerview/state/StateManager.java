package com.zpj.recyclerview.state;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class StateManager extends BaseConfig<StateManager> {

    private static StateConfig DEFAULT_CONFIG;

    private final StatusAdapter adapter;
    private final StateRecyclerView recyclerView;

    public interface Action {
        void run(final StateManager manager);
    }

    public View getStateView() {
        return recyclerView;
    }

    public static class StateConfig extends BaseConfig<StateConfig> { }

    private StateManager(View view) {

        loadingViewHolder = config().loadingViewHolder;
        emptyViewHolder = config().emptyViewHolder;
        errorViewHolder = config().errorViewHolder;
        loginViewHolder = config().loginViewHolder;
        noNetworkViewHolder = config().noNetworkViewHolder;

        Context context = view.getContext();


        adapter = new StatusAdapter(view, this);
        recyclerView = new StateRecyclerView(context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
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
        this.adapter.onRetry = new Runnable() {
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
        this.adapter.onLogin = new Runnable() {
            @Override
            public void run() {
                if (action != null) {
                    action.run(StateManager.this);
                }
            }
        };
        return this;
    }

    public StateManager setRecyclable(boolean recyclable) {
        adapter.setRecyclable(recyclable);
        return this;
    }

    public StateManager showContent() {
        changeState(StatusAdapter.STATE_CONTENT, null);
        return this;
    }

    public StateManager showLoading() {
        changeState(StatusAdapter.STATE_LOADING, null);
        return this;
    }

    public StateManager showEmpty() {
        changeState(StatusAdapter.STATE_EMPTY, null);
        return this;
    }

    public StateManager showError(String msg) {
        changeState(StatusAdapter.STATE_ERROR, msg);
        return this;
    }

    public StateManager showError() {
        changeState(StatusAdapter.STATE_ERROR, null);
        return this;
    }

    public StateManager showLogin() {
        changeState(StatusAdapter.STATE_LOGIN, null);
        return this;
    }

    public StateManager showNoNetwork() {
        changeState(StatusAdapter.STATE_NO_NETWORK, null);
        return this;
    }

    private void changeState(int state, String msg) {
        if (adapter.getState() == state) {
            return;
        }

        if (state == StatusAdapter.STATE_CONTENT) {
            adapter.state = StatusAdapter.STATE_CONTENT;
            adapter.notifyDataSetChanged();
        } else {
            adapter.changeState(state, msg);
        }
        if (!adapter.isRecyclable) {
            recyclerView.getRecycledViewPool().clear();
        }

    }

    private static class StateRecyclerView extends RecyclerView {

        public StateRecyclerView(@NonNull Context context) {
            this(context, null);
        }

        public StateRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public StateRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent e) {
            return false;
        }

    }

    private static class StatusAdapter extends RecyclerView.Adapter<StateViewHolder> {

        private final Map<Integer, IViewHolder> viewHolderMap = new HashMap<>(3, 1);
        private final Context context;
        private final View contentView;
        private final BaseConfig<StateManager> config;

        private Runnable onRetry;
        private Runnable onLogin;

        private boolean isRecyclable = false;

        public static final int STATE_CONTENT = 0;
        public static final int STATE_LOADING = 1;
        public static final int STATE_EMPTY = 2;
        public static final int STATE_ERROR = 3;
        public static final int STATE_LOGIN = 4;
        public static final int STATE_NO_NETWORK = 5;

        private int state = STATE_CONTENT;

        private String msg;

        StatusAdapter(View view, BaseConfig<StateManager> config) {
            this.contentView = view;
            this.context = view.getContext();
            this.config = config;
        }

        @NonNull
        @Override
        public StateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if (viewType == STATE_CONTENT) {
                return new StateViewHolder(contentView, isRecyclable);
            }
            final IViewHolder holder = viewHolderMap.get(viewType);
            if (holder instanceof BaseViewHolder) {
                ((BaseViewHolder) holder).setOnLogin(onLogin);
                ((BaseViewHolder) holder).setOnRetry(onRetry);
                ((BaseViewHolder) holder).setMsg(msg);
            }

            final View view;
            if (holder.getView() == null) {
                view = holder.onCreateView(context);
                if (!isRecyclable) {
                    view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {
                            onViewDetachedFromWindow(v);
                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            holder.onDestroyView();
                            view.removeOnAttachStateChangeListener(this);
                        }
                    });
                }
            } else {
                view = holder.getView();
            }
            return new StateViewHolder(view, isRecyclable);
        }

        @Override
        public void onBindViewHolder(@NonNull StateViewHolder viewHolder, int i) {
            IViewHolder holder = viewHolderMap.get(getItemViewType(i));
            if (holder != null) {
                if (holder instanceof CustomizedViewHolder) {
                    CustomizedViewHolder baseHolder = ((CustomizedViewHolder) holder);
                    baseHolder.setMsg(msg);
                    if (baseHolder.container != null) {
                        baseHolder.container.removeAllViews();
                    }
                }
                holder.onViewCreated(viewHolder.itemView);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return state;
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public int getState() {
            return state;
        }

        public void setRecyclable(boolean recyclable) {
            isRecyclable = recyclable;
        }

        public void changeState(final int state, String msg) {
            this.msg = msg;
            Log.d("changeState1", "state=" + state + " containsKey=" + viewHolderMap.containsKey(state) + " view=" + viewHolderMap.get(state));
            if (!viewHolderMap.containsKey(state) || viewHolderMap.get(state) == null) {
                IViewHolder holder;
                switch (state) {
                    case STATE_LOADING:
                        holder = config.getLoadingViewHolder();
                        break;
                    case STATE_EMPTY:
                        holder = config.getEmptyViewHolder();
                        break;
                    case STATE_ERROR:
                        holder = config.getErrorViewHolder();
                        break;
                    case STATE_LOGIN:
                        holder = config.getLoginViewHolder();
                        break;
                    case STATE_NO_NETWORK:
                        holder = config.getNoNetworkViewHolder();
                        break;
                    case STATE_CONTENT:
                    default:
                        return;
                }

                viewHolderMap.put(state, holder);
            }
            this.state = state;
            notifyDataSetChanged();
        }

    }

    private static class StateViewHolder extends RecyclerView.ViewHolder {

        public StateViewHolder(@NonNull View itemView, boolean isRecyclable) {
            super(itemView);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            itemView.setLayoutParams(params);
            setIsRecyclable(isRecyclable);
        }
    }

}
