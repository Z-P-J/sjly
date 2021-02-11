package com.zpj.fragmentation.queue;

import android.os.Handler;
import android.os.Looper;

import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportHelper;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The queue of perform action.
 * <p>
 * Created by YoKey on 17/12/29.
 * Modified by Z-P-J
 */
public class ActionQueue {
    private final Queue<Action> mQueue = new LinkedList<>();
    private final Handler mMainHandler;

    public ActionQueue(Handler mainHandler) {
        this.mMainHandler = mainHandler;
    }

    public void enqueue(final Action action) {
        if (isThrottleBACK(action)) return;

//        if (action.action == Action.ACTION_LOAD && mQueue.isEmpty()) { // && Thread.currentThread() == Looper.getMainLooper().getThread()
//            RxHandler.post(action::run);
//            return;
//        }

        if (action.action == Action.ACTION_LOAD && mQueue.isEmpty()
                && Thread.currentThread() == Looper.getMainLooper().getThread()) {
            action.run();
            return;
        }

        mMainHandler.post(() -> enqueueAction(action));

//        RxHandler.post(() -> enqueueAction(action));

    }

    private void enqueueAction(Action action) {
        mQueue.add(action);
        if (mQueue.size() == 1) {
            handleAction();
        }
    }

    private void handleAction() {
        if (mQueue.isEmpty()) return;

        final Action action = mQueue.peek();
        mMainHandler.postDelayed(() -> {
            action.run();
            executeNextAction(action);
        }, action.delay);
//        RxHandler.post(() -> {
//            action.run();
//            executeNextAction(action);
//        }, action.delay);
    }

    private void executeNextAction(Action action) {
        if (action.action == Action.ACTION_POP) {
            ISupportFragment top = SupportHelper.getBackStackTopFragment(action.fragmentManager);
            action.duration = top == null ? Action.DEFAULT_POP_TIME : top.getSupportDelegate().getExitAnimDuration();
        }

        mMainHandler.postDelayed(() -> {
            mQueue.poll();
            handleAction();
        }, action.duration);
//        RxHandler.post(() -> {
//            mQueue.poll();
//            handleAction();
//        }, action.duration);
    }

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
