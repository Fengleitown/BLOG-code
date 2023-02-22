package day02;


import jdk.internal.misc.Unsafe;

// --add-opens java.base/jdk.internal.misc=ALL-UNNAMED
public class SyncVsCas {
    static final Unsafe U = Unsafe.getUnsafe();
    static final long BALANCE = U.objectFieldOffset(Account.class, "balance");

    static class Account {
        volatile int balance = 10;
    }

    private static void showResult(Account account, Thread t1, Thread t2) {
        try {
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            LoggerUtils.get().debug("{}", account.balance);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sync(Account account) {
        Thread t1 = new Thread(() -> {
            synchronized (account) {
                int old = account.balance;
                int n = old - 5;
                account.balance = n;
            }
        },"t1");

        Thread t2 = new Thread(() -> {
            synchronized (account) {
                int o = account.balance;
                int n = o + 5;
                account.balance = n;
            }
        },"t2");

        showResult(account, t1, t2);
    }

    public static void cas(Account account) {
        Thread t1 = new Thread(() -> {
            while (true) {
                int o = account.balance;
                int n = o - 5;
                if (U.compareAndSetInt(account, BALANCE, o, n)) {
                    break;
                }
            }
        },"t1");

        Thread t2 = new Thread(() -> {
            while (true) {
                int o = account.balance;
                int n = o + 5;
                if (U.compareAndSetInt(account, BALANCE, o, n)) {
                    break;
                }
            }
        },"t2");

        showResult(account, t1, t2);
    }

    private static void basicCas(Account account) {
        while (true) {
            int o = account.balance;
            int n = o + 5;
            if(U.compareAndSetInt(account, BALANCE, o, n)){
                break;
            }
        }
        System.out.println(account.balance);
    }

    public static void main(String[] args) {
        Account account = new Account();
        cas(account);
//        Account account = new Account();
//        while (true) {
//            int old = account.balance;
//            int news = old + 5;
//            if (U.compareAndSetInt(account, BALANCE, old, news)) {
//                break;
//            }
//        }
//
//        System.out.println(account.balance);
// 这个方法就是大名鼎鼎的CAS。
//      原理 本来就是 10 的基础上加 5，现在防止多线程情况下静态变量被篡改或者是覆盖新值，就引用了这个方法.
//       拿到静态变量balance的值，与old多比较，相同则说明没有其他线程来改变静态变量的值。则我可以修改成功。
//        如果拿到静态变量balance的值!=old，则compareAndSetInt()就修改失败了。



    }


}
