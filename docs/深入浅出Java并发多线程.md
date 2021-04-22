# 深入浅出Java并发多线程

[toc]

## 第7章、趣解Thread类和Object类中线程相关方法

Join

作用：因为新的线程加入了我们，所以我们要等它执行完再出发。

用法：main等待thread1执行完毕，注意谁等谁。

```java
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
        },"child_thread");


        try {
            childThread.start();
//            childThread.join(1000);
            synchronized (childThread){
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
        },"child_thread");


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
```



Join源码解析

```java
/**                                                                          
 * Waits at most {@code millis} milliseconds for this thread to              
 * die. A timeout of {@code 0} means to wait forever.                        
 *                                                                           
 * <p> This implementation uses a loop of {@code this.wait} calls            
 * conditioned on {@code this.isAlive}. As a thread terminates the           
 * {@code this.notifyAll} method is invoked. It is recommended that          
 * applications not use {@code wait}, {@code notify}, or                     
 * {@code notifyAll} on {@code Thread} instances.                            
 *                                                                           
 * @param  millis                                                            
 *         the time to wait in milliseconds                                  
 *                                                                           
 * @throws  IllegalArgumentException                                         
 *          if the value of {@code millis} is negative                       
 *                                                                           
 * @throws  InterruptedException                                             
 *          if any thread has interrupted the current thread. The            
 *          <i>interrupted status</i> of the current thread is               
 *          cleared when this exception is thrown.                           
 */                                                                          
// BEGIN Android-changed: Synchronize on separate lock object not this Thread
// public final synchronized void join(long millis)                          
public final void join(long millis)                                          
throws InterruptedException {                                                
    synchronized(lock) {                                                     
    long base = System.currentTimeMillis();                                  
    long now = 0;                                                            
                                                                             
    if (millis < 0) {                                                        
        throw new IllegalArgumentException("timeout value is negative");     
    }                                                                        
                                                                             
    if (millis == 0) {                                                       
        while (isAlive()) {                                                  
            lock.wait(0);                                                    
        }                                                                    
    } else {                                                                 
        while (isAlive()) {                                                  
            long delay = millis - now;                                       
            if (delay <= 0) {                                                
                break;                                                       
            }                                                                
            lock.wait(delay);                                                
            now = System.currentTimeMillis() - base;                         
        }                                                                    
    }                                                                        
    }                                                                        
}                                                                            
// END Android-changed: Synchronize on separate lock object not this Thread. 
```

从注释可以看出，join传入参数mills大于0时，表示被join线程需要wait mills这么多秒的时间；如果传入参数等于0，则被join线程会一直处于wait状态，直到join线程终止。注意：join中的代码片段都是在被join的线程之中执行的。



疑问：

从join的源码中可以看出，join方法中没有执行了wait却没有执行notify的操作，那究竟是谁执行了这个唤醒操作呢？

![](imgs/thread_join.jpg)

如上图所示，需要深入到JVM层去研究。实际上，每个thread类的run方法执行完成以后会自动的执行notify的操作。

由此可以得出，thread.join方法的等价代码

```java
synchronized(thread){
	thread.wait();
}
```

为什么这里的同步对象必须是thread，用其他的对象不行？

在join期间，线程处于哪种线程状态？





yield方法详解

作用：释放我的CPU时间片

定位：JVM不保证遵循

yield和sleep区别：是否随时可能再次被调度



## 第10章、追寻并发的崇高理想-线程安全【工作常用】

死锁

```java
package com.basic.thread;

public class DeadLock {
    private static final Object LOCK1 = new Object();
    private static final Object LOCK2 = new Object();

    /**
     * <result>
     *     1
     *     2
     * </result>
     */
    public static void main(String[] args) {
        new Thread(new DeadLockRunnable1()).start();
        new Thread(new DeadLockRunnable2()).start();
    }

    private static class DeadLockRunnable1 implements Runnable {

        @Override
        public void run() {
            synchronized (LOCK1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("1");
                synchronized (LOCK2) {
                    System.out.println("11");
                }
            }
        }
    }

    private static class DeadLockRunnable2 implements Runnable {

        @Override
        public void run() {
            synchronized (LOCK2) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("2");
                synchronized (LOCK1) {
                    System.out.println("22");
                }
            }
        }
    }
}
```

对象发布和初始化的时候的安全问题

什么是发布

什么是逸出

- 方法返回一个private对象（private的本意是不让外部访问）

  ```java
  package com.basic.thread;
  
  import java.util.HashMap;
  import java.util.Map;
  
  public class ImplicitEscape {
      private Map<String,String> states;
  
      public ImplicitEscape() {
          states = new HashMap<>();
          states.put("1","周一");
          states.put("2","周二");
          states.put("3","周三");
      }
  
      public Map<String,String> getStates(){
          return states;
      }
  
      /**
       * <result>
       *     周一
       *     null
       * </result>
       */
      public static void main(String[] args) {
          ImplicitEscape implicitEscape = new ImplicitEscape();
          Map<String, String> states = implicitEscape.getStates();
          System.out.println(states.get("1"));
          states.remove("1");
          System.out.println(states.get("1"));
      }
  }
  ```

- 还未完成初始化（构造函数没完全执行完毕）就把对象提供给外界，比如：

  - 在构造函数中未初始化完毕就this赋值

    ```java
    package com.basic.thread;
    
    /**
     * 初始化未完毕，就this赋值
     */
    public class InitNotFinished {
        private static Point point;
    
        public static void main(String[] args) {
            new PointMaker().start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (point != null) {
                System.out.println(point.toString());
            }
        }
    
    
        private static class Point {
            private final int x;
            private final int y;
    
            public Point(int x, int y) throws InterruptedException {
                this.x = x;
                InitNotFinished.point = this;
                Thread.sleep(100);
                this.y = y;
            }
    
            @Override
            public String toString() {
                return "Point{" +
                        "x=" + x +
                        ", y=" + y +
                        '}';
            }
        }
    
        private static class PointMaker extends Thread {
            @Override
            public void run() {
                try {
                    new Point(1, 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    ```

  - 隐式逸出 --- 注册监听事件

    代码忽略

  - 构造函数中运行线程

    ```java
    package com.basic.thread;
    
    import java.util.HashMap;
    import java.util.Map;
    
    public class ImplicitEscape_NewThread {
        private Map<String,String> states;
    
        public ImplicitEscape_NewThread() {
            new Thread(){
                @Override
                public void run() {
                    states = new HashMap<>();
                    states.put("1","周一");
                    states.put("2","周二");
                    states.put("3","周三");
                }
            }.start();
        }
    
        public Map<String,String> getStates(){
            return states;
        }
    
        /**
         * <result>
         *     周一
         *     null
         * </result>
         */
        public static void main(String[] args) {
            ImplicitEscape_NewThread implicitEscape = new ImplicitEscape_NewThread();
            Map<String, String> states = implicitEscape.getStates();
            System.out.println(states.get("1"));
            states.remove("1");
            System.out.println(states.get("1"));
        }
    }
    ```

如何解决逸出的问题

- 返回“副本”；
- 工厂模式



需要考虑线程安全的场景

- 访问共享的变量或资源，会有并发风险，比如对象的属性、静态变量、共享缓存、数据库等；
- 所有依赖时序的操作，即使每一步操作都是线程安全的，还是存在并发问题；
- 不同的数据之间存在捆绑关系的时候；
- 我们使用其他类的时候，如果对方没有申明自己是线程安全的，那么大概率会存在并发问题。



为什么多线程会带来性能问题？

- 调度：上下文切换

  - 什么是上下文

    上下文切换可以认为是内核（操作系统的核心）在CPU上对于进程（包括线程）进行以下的活动，

    - 挂起一个线程，将这个线程在CPU中的状态（上下文）存储在内存中的某处；
    - 在内存中检索下一个线程的上下文并将其在CPU中的寄存器中恢复；
    - 跳转到程序计数器所指向的位置（即跳转到线程被中断时的代码行），以恢复该线程。

    

    当可运行的线程数超过了CPU核心数时，为了使每个线程都有机会运行，就需要进行上下文切换。

  - 缓存开销

    经过上下文切换，对原有线程的缓存就失效了，这个时候CPU就需要重新进行缓存

  - 何时会导致密集的上下文切换

    频繁竞争锁、IO、频繁的线程阻塞

- 协作：内存同步

  Java内存模型 -- 为了数据的正确性，同步手段往往会使用禁止编译器优化、使CPU内的缓存失效。

  指令重排序，让缓存能利用的机会更多；

  JVM会对锁进行优化，比如发现有些锁是没有必要的，把锁自动删除



常见面试问题

- 你知道有哪些线程不安全的情况？
- 平时哪些情况下需要额外注意线程安全问题？
- 什么是多线程的上下文切换？



