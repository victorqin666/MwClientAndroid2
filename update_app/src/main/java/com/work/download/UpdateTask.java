package com.work.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class UpdateTask implements Runnable {

    private static final int STATE_NEW = 0x01;
    private static final int STATE_RUNNING = 0x02;
    private static final int STATE_FINISH = 0x04;
    private static final int STATE_CANCELLED = 0x08;

    private static final int MSG_TASK_DONE = 0x01;
    private static InternalHandler sHandler = null;

    static {
        sHandler = new InternalHandler(Looper.getMainLooper());
    }

    private Thread mCurrentThread;
    private AtomicInteger mState = new AtomicInteger(STATE_NEW);

    /**
     * A worker will execute this method in a background thread
     */
    public abstract void doInBackground();

    /**
     * will be called after doInBackground();
     */
    public abstract void onFinish(boolean canceled);

    /**
     * When the Task is Cancelled.
     */
    protected void onCancel() {
    }

    /**
     * Restart the task, just set the state to {@link #STATE_NEW}
     */
    public void restart() {
        mState.set(STATE_NEW);
    }

    @Override
    public void run() {
        if (!mState.compareAndSet(STATE_NEW, STATE_RUNNING)) {
            return;
        }
        mCurrentThread = Thread.currentThread();
        doInBackground();
        sHandler.obtainMessage(MSG_TASK_DONE, this).sendToTarget();
    }

    /**
     * check whether this work is canceled.
     */
    public boolean isCancelled() {
        return mState.get() == STATE_CANCELLED;
    }

    /**
     * check whether this work has done
     *
     * @return
     */
    @SuppressWarnings({"unused"})
    public boolean isDone() {
        return mState.get() == STATE_FINISH;
    }

    void cancel() {
        if (mState.get() < STATE_FINISH) {
            if (mState.get() == STATE_RUNNING && null != mCurrentThread) {
                try {
                    mCurrentThread.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mState.set(STATE_CANCELLED);
            onCancel();
        }
    }

    private static class InternalHandler extends Handler {

        InternalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateTask work = (UpdateTask) msg.obj;
            switch (msg.what) {
                case MSG_TASK_DONE:
                    boolean isCanceled = work.isCancelled();
                    work.mState.set(STATE_FINISH);
                    work.onFinish(isCanceled);
                    break;
                default:
                    break;
            }
        }
    }

    public static void post(Runnable r) {
        sHandler.post(r);
    }

    public static void postDelay(Runnable r, long delayMillis) {
        sHandler.postDelayed(r, delayMillis);
    }
}