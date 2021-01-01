package com.zpj.fragmentation.queue;

import android.os.Looper;
import android.util.Log;

import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportHelper;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The queue of perform action.
 * <p>
 * Created by YoKey on 17/12/29.
 * Modified by Z-P-J
 */
public class BlockActionQueue {

    private static final String TAG = "BlockActionQueue";

    private final Queue<Action> mQueue = new LinkedList<>();
    private final AtomicBoolean start = new AtomicBoolean(false);

    public boolean isStart() {
        return start.get();
    }

    public void start() {
        start.set(true);
        handleAction();
    }

    public void stop() {
        start.set(false);
    }

    public void onDestroy() {
        start.set(false);
        this.mQueue.clear();
    }

    public void post(final Runnable runnable) {
        enqueue(new Action() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    public void postDelayed(final Runnable runnable, long delay) {
        Action action = new Action() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        action.delay = delay;
        enqueue(action);
    }

    public void enqueue(final Action action) {
        if (isThrottleBACK(action)) return;

        if (start.get() && action.action == Action.ACTION_LOAD && mQueue.isEmpty()
                && Thread.currentThread() == Looper.getMainLooper().getThread()) {
            action.run();
            return;
        }
        RxHandler.post(() -> {
//            enqueueAction(action);
            mQueue.add(action);
            Log.d(TAG, "size=" + mQueue.size());
            if (mQueue.size() == 1) {
                handleAction();
            }
        });
    }

//    private void enqueueAction(Action action) {
//        mQueue.add(action);
//        Log.d(TAG, "size=" + mQueue.size());
//        if (mQueue.size() == 1) {
//            handleAction();
//        }
//    }

    private void handleAction() {
        if (!start.get() || mQueue.isEmpty()) return;
        Log.d(TAG, "handleAction");

        final Action action = mQueue.peek();
        RxHandler.post(() -> {
            action.run();
//            executeNextAction(action);
            if (action.action == Action.ACTION_POP) {
                ISupportFragment top = SupportHelper.getBackStackTopFragment(action.fragmentManager);
                action.duration = top == null ? Action.DEFAULT_POP_TIME : top.getSupportDelegate().getExitAnimDuration();
            }
            mQueue.poll();
            handleAction();
        }, action.delay);
    }

//    private void executeNextAction(Action action) {
//        if (action.action == Action.ACTION_POP) {
//            ISupportFragment top = SupportHelper.getBackStackTopFragment(action.fragmentManager);
//            action.duration = top == null ? Action.DEFAULT_POP_TIME : top.getSupportDelegate().getExitAnimDuration();
//        }
//        RxHandler.post(() -> {
//            mQueue.poll();
//            handleAction();
//        }, action.duration);
//    }

    private boolean isThrottleBACK(Action action) {
        if (action.action == Action.ACTION_BACK) {
            Action head = mQueue.peek();
            if (head != null && head.action == Action.ACTION_POP) {
                return true;
            }
        }
        return false;
    }
}
