package com.jeremy.jmultithread.threadcoreknowledge.threadobjectclasscommonmethods;

/**
 * 两个线程交替打印出0~100的奇偶数，通过Notify和Wait来实现
 */
public class PrintOddEvenWithWaitNotify {
    private static final Object LOCK = new Object();
    private static final int LIMIT = 100;
    private static int sCount;

    public static void main(String[] args) {
        new Thread(new TurningRunnable()).start();
        new Thread(new TurningRunnable()).start();
    }

    private static class TurningRunnable implements Runnable {

        @Override
        public void run() {
            while (sCount <= LIMIT) {
                synchronized (LOCK) {
                    System.out.println(Thread.currentThread().getName()
                            + ": " + sCount);
                    sCount++;
                    LOCK.notify();
                    // sCount已经超过100以后，就不要再让该线程处于等待状态，而是结束任务
                    if (sCount <= LIMIT) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
}
