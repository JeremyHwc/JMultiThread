package com.jeremy.jmultithread.threadcoreknowledge.threadobjectclasscommonmethods;

/**
 * 两个线程交替打印0~100的奇偶数，用synchronized关键字实现
 * <p>
 * 思路：新建两个线程，一个只处理偶数，一个只处理奇数（用位运算）。用synchronized来通信
 * <p>
 * 缺点：涉及到多次对于锁的争夺，效率较低
 */
public class PrintOddEvenWithSync {
    private static final Object LOCK = new Object();
    private static final int LIMIT = 100;
    private static int count;

    public static void main(String[] args) {
        new Thread(new OddRunnable()).start();
        new Thread(new EvenRunnable()).start();
    }

    private static class OddRunnable implements Runnable {
        @Override
        public void run() {
            while (count < LIMIT) {
                synchronized (LOCK) {
                    if ((count & 1) == 1) {
                        System.out.println("奇数：" + count);
                        count++;
                    }
                }
            }
        }
    }

    private static class EvenRunnable implements Runnable {
        @Override
        public void run() {
            while (count <= LIMIT) {
                synchronized (LOCK) {
                    if ((count & 1) == 0) {
                        System.out.println("偶数：" + count);
                        count++;
                    }
                }
            }
        }
    }
}
