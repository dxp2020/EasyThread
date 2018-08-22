package com.changf.thread;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private int count = 100;
    private boolean isRunning = false;
    private ReentrantLock mLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLock = new ReentrantLock();
    }

    /**
     * 继承Thread方式
     */
    private class SyncThread extends Thread {

        SyncThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (isRunning) {
                count();
            }
        }
    }

    private  void count() {
        mLock.lock();
        if (count > 0) {
            Log.e(TAG, Thread.currentThread().getName() + "--->" + count--);
        } else {
            isRunning = false;
        }
        mLock.unlock();
    }

    public void onTest(View view){
        test1();
    }

    private void test1() {
        isRunning=true;
        count = 100;
        SyncThread syncThread1 = new SyncThread("线程一");
        SyncThread syncThread2 = new SyncThread("线程二");
        SyncThread syncThread3 = new SyncThread("线程三");

        syncThread1.start();
        syncThread2.start();
        syncThread3.start();
    }

}
