package com.jeremy.jmultithread.threadcoreknowledge.threadobjectclasscommonmethods;

import androidx.annotation.NonNull;

public class Wait {
    private static Object object = new Object();

    public static void main(String[] args) throws InterruptedException {
        object.notify();
        synchronized (object) {
            object.wait();
        }
//        new Thread1("# 01").start();
//        new Thread1("# 02").start();
//        Thread.sleep(200);
//        new Thread2().start();
    }

    static class Thread1 extends Thread {
        public Thread1(@NonNull String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + " 开始执行！");
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 结束执行！");
            }
        }
    }

    static class Thread2 extends Thread {
        @Override
        public void run() {
            synchronized (object) {
                object.notifyAll();
                System.out.println(Thread.currentThread().getName() + " 调用了notify！");
            }
        }
    }
}
