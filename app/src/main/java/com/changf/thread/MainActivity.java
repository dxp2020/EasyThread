package com.changf.thread;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private int count = 100;
    private boolean isRunning = false;
    private ReentrantLock mLock;
    private StringBuffer sb = new StringBuffer();
    private TextView tv_container ;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            print(msg.obj.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_container = findViewById(R.id.tv_container);
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

    /*
        实现同步的方式
        a、同步函数
        b、同步代码块
        c、特殊域变量(volatile)
        d、重入锁( ReentrantLock)
    */
    private void count() {
        mLock.lock();
        if (count > 0) {
            printMessage(String.valueOf(Thread.currentThread().getName() + "--->" + count--+"\n"));
        } else {
            isRunning = false;
        }
        mLock.unlock();
    }

    private void printMessage(String message){
        Message msg = Message.obtain();
        msg.obj = message;
        handler.sendMessage(msg);
    }

    private synchronized void print(String s) {
        sb.append(s);
        tv_container.setText(sb);
    }

    public void onTest(View view){
        test1();
    }

    public void onJoin(View view){
        joinTest();
    }

    public void onJoin2(View view){
        joinTest2();
    }

    public void onJoin3(View view){
        joinTest3();
    }

    private void joinTest3() {
        isRunning=true;
        count = 100;
        sb.delete(0,sb.length());

        printMessage("暂停main线程(进入blocked状态)5毫秒，等子线程(调用join的线程)执行5毫秒之后，进入并行执行状态\n");
        SyncThread syncThread1 = new SyncThread("线程一");
        syncThread1.start();
        try {
            syncThread1.join(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printMessage("main线程执行完毕\n");
    }

    private void joinTest2() {
        isRunning=true;
        count = 100;
        sb.delete(0,sb.length());

        printMessage("在main线程中，开启两个子线程，先执行线程一，再线程二\n");

        SyncThread syncThread1 = new SyncThread("线程一");
        SyncThread syncThread2 = new SyncThread("线程二");

        syncThread1.start();
        try {
            syncThread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isRunning=true;
        count = 50;
        sb.delete(0,sb.length());
        syncThread2.start();
    }

    private void joinTest() {
        isRunning=true;
        count = 100;
        sb.delete(0,sb.length());

        printMessage("执行main线程\n");
        SyncThread syncThread1 = new SyncThread("线程一");
        syncThread1.start();
        printMessage("main线程进入等待,SyncThread开始执行\n");
        try {
            syncThread1.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printMessage("SyncThread执行完毕,继续执行main线程\n");
    }

    private void test1() {
        isRunning=true;
        count = 100;
        sb.delete(0,sb.length());

        SyncThread syncThread1 = new SyncThread("线程一");
        SyncThread syncThread2 = new SyncThread("线程二");
        SyncThread syncThread3 = new SyncThread("线程三");

        syncThread1.start();
        syncThread2.start();
        syncThread3.start();
    }

}
