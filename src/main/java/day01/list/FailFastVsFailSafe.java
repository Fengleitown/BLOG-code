package day01.list;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class FailFastVsFailSafe {
    // fail-fast 一旦发现遍历的同时其它人来修改，则立刻抛异常
    // fail-safe 发现遍历的同时其它人来修改，应当能有应对策略，例如牺牲一致性(数据不是最新的)来让整个遍历运行完成，新加的元素在
//    循环中打印不出来，但是循环完成之后可以打印出来。

    private static void failFast() {
        ArrayList<Student> list = new ArrayList<>();
        list.add(new Student("A"));
        list.add(new Student("B"));
        list.add(new Student("C"));
        list.add(new Student("D"));
        for (Student student : list) {
//            增强for循环里面有迭代器Iterator，checkForComodification()方法里判断，modCount (修改list次数) != expectedModCount (循环开始时修改list次数)则抛异常
            System.out.println(student);
        }
        System.out.println(list);
    }

    private static void failSafe() {
        CopyOnWriteArrayList<Student> list = new CopyOnWriteArrayList<>();
        list.add(new Student("A"));
        list.add(new Student("B"));
        list.add(new Student("C"));
        list.add(new Student("D"));
        for (Student student : list) {
            System.out.println(student);
        }
        System.out.println(list);
    }

    static class Student {
        String name;

        public Student(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        failSafe();
    }
}
