package day02;

import static day02.LoggerUtils.get;
import static day02.LoggerUtils.main;

public class WaitVsSleep {
    static final Object LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        sleeping();
    }

    private static void illegalWait() throws InterruptedException {
//        LOCK.wait();//没获得锁之前调用有问题，获得锁之后就没问题了
        synchronized (LOCK){
            LOCK.wait();
        }
    }

    private static void waiting() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (LOCK) {
                try {
                    LoggerUtils.get("t").debug("waiting...");
                    LOCK.wait(5000L);//拿到锁之后马上又会释放掉锁
                } catch (InterruptedException e) {
                    LoggerUtils.get("t").debug("interrupted...");
                    e.printStackTrace();
                }
            }
        }, "t1");
        t1.start();

        Thread.sleep(100);
        synchronized (LOCK) {//紧接着隔0.1S 主线程就会拿到锁
            main.debug("other...");
        }

    }

    private static void sleeping() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (LOCK) {
                try {
                    get("t").debug("sleeping...");
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    get("t").debug("interrupted...");
                    e.printStackTrace();
                }
            }
        }, "t1");
        t1.start();

        Thread.sleep(100);
        t1.interrupt();
        synchronized (LOCK) {
            main.debug("other...");
        }
    }
}
