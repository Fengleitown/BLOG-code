package day02;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestThreadLocal {
    public static void main(String[] args) {
        test2();
    }

    // 一个线程内调用, 得到的是同一个 Connection 对象
    //        通过ThreadLocal实现线程间的资源 共享
    private static void test2() {
//        t1获取的都是一样的    t2获取的也都是一样的
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                LoggerUtils.get("t").debug("{}", Utils.getConnection());
                LoggerUtils.get("t").debug("{}", Utils.getConnection());
                LoggerUtils.get("t").debug("{}", Utils.getConnection());
            }, "t" + (i + 1)).start();
        }
    }

    // 多个线程调用, 得到的是自己的 Connection 对象
    private static void test1() {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                LoggerUtils.get("t").debug("{}", Utils.getConnection());
            }, "t" + (i + 1)).start();
        }
    }

//        通过ThreadLocal实现线程间的资源 隔离
    static class Utils {
 // static修饰的ThreadLocal对象，意味着只有一个ThreadLocal对象，一个ThreadLocal对象是怎么实现线多个connection资源程隔离的呢？
        private static final ThreadLocal<Connection> tl = new ThreadLocal<>();

        public static Connection getConnection() {
            Connection conn = tl.get(); // 到当前线程获取资源，但此时线程新建，没啥对象，所以一定是空的
            if (conn == null) {
                conn = innerGetConnection(); // 创建新的连接对象————所以每个线程来都是新建的，都是它自己的链接对象
                tl.set(conn); // 将链接资源对象存入当前线程
            }
            return conn;
        }

        private static Connection innerGetConnection() {
            try {
                return DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useSSL=false", "root", "root");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
