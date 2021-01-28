package com.jeremy.jmultithread.threadcoreknowledge.threadobjectclasscommonmethods;

public class JoinExample {
    public static void main(String[] args) {
//        mainWaitOtherThreads();
//        testInterrupt();
//        testJoinThreadState();
//        testJoinEquivalence();
    }

    /**
     * This method is to describe the other way to implement the join method effect.
     *
     * <result>
     * Main thread current state: TIMED_WAITING
     * Main thread current state: RUNNABLE
     * <result/>
     */
    private static void testJoinEquivalence() {
        Thread mainThread = Thread.currentThread();

        Thread childThread = new Thread(() -> {
            try {
                // If the join method params is 1000 > 0 and the main thread state is TIMED_WAITING.
                // If the join method params is 0 and the main thread state is WAITING.
                System.out.println("Main thread current state: " + mainThread.getState());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted.");
            }
        }, "child_thread");


        try {
            childThread.start();
//            childThread.join(1000);
            synchronized (childThread) {
                childThread.wait(1000);
            }
            System.out.println("Main thread current state: " + mainThread.getState());
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted.");

        }
    }

    /**
     * When other threads join in main thread which state is WAITING(PS:It's no BLOCKED)
     *
     * <result>
     * Main thread current state: WAITING
     * Main thread current state: RUNNABLE
     * <result/>
     */
    private static void testJoinThreadState() {
        Thread mainThread = Thread.currentThread();

        Thread childThread = new Thread(() -> {
            try {
                // If the join method params is 1000 > 0 and the main thread state is TIMED_WAITING.
                // If the join method params is 0 and the main thread state is WAITING.
                System.out.println("Main thread current state: " + mainThread.getState());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted.");
            }
        }, "child_thread");


        try {
            childThread.start();
            childThread.join(1000);
            System.out.println("Main thread current state: " + mainThread.getState());
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted.");

        }
    }

    /**
     * <result>
     * Main thread is waiting for join_thread to complete.
     * join_thread begin...
     * main was interrupted.
     * Main thread completed.
     * join_thread was interrupted.
     * join_thread ending...
     * <result/>
     */
    private static void testInterrupt() {
        Thread mainThread = Thread.currentThread();

        Thread childThread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " begin...");
            try {
                mainThread.interrupt();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted.");
            }
            System.out.println(Thread.currentThread().getName() + " ending...");
        }, "join_thread");

        System.out.println("Main thread is waiting for " + childThread.getName() + " to complete.");
        try {
            childThread.start();
            childThread.join();
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted.");
            childThread.interrupt();
        }
        System.out.println("Main thread completed.");
    }


    /**
     * Test the main thread that waiting to thread1 and thread2 to complete.
     *
     * <result>
     * Main Thread mark Beginning...
     * thread1
     * thread2
     * Main Thread mark ending...
     * <result/>
     */
    private static void mainWaitOtherThreads() {
        System.out.println("Main Thread mark Beginning...");
        Thread thread1 = new Thread(() -> System.out.println(Thread.currentThread().getName()), "thread1");
        Thread thread2 = new Thread(() -> System.out.println(Thread.currentThread().getName()), "thread2");
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Main Thread mark ending... ");
    }

}
