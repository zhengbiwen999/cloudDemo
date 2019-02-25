package com.zbw.test.forkjoin;
public class Node_sf {
//    public Node_sf mergeLinkList(Node head1, Node_sf head2) {
//
//        if (head1 == null && head2 == null) {  //如果两个链表都为空
//            return null;
//        }
//        if (head1 == null) {
//            return head2;
//        }
//        if (head2 == null) {
//            return head1;
//        }
//
//        Node head; //新链表的头结点
//        Node current;  //current结点指向新链表
//
//        // 一开始，我们让current结点指向head1和head2中较小的数据，得到head结点
//        if (head1.data < head2.data) {
//            head = head1;
//            current = head1;
//            head1 = head1.next;
//        } else {
//            head = head2;
//            current = head2;
//            head2 = head2.next;
//        }
//
//        while (head1 != null && head2 != null) {
//            if (head1.data < head2.data) {
//                current.next = head1;  //新链表中，current指针的下一个结点对应较小的那个数据
//                current = current.next; //current指针下移
//                head1 = head1.next;
//            } else {
//                current.next = head2;
//                current = current.next;
//                head2 = head2.next;
//            }
//        }
//
//        //合并剩余的元素
//        if (head1 != null) { //说明链表2遍历完了，是空的
//            current.next = head1;
//        }
//
//        if (head2 != null) { //说明链表1遍历完了，是空的
//            current.next = head2;
//        }
//
//        return head;
//    }
    public static void main(String[] args) throws InterruptedException {
//        String data = getData();
//        System.out.printf(data);
        m1();
        m2();
    }
    public static String getData() {
        for (int i = 0; i <= 5; i++) {
            if (i == 3) {
                return i + "";
            }
        }
        return "null";
    }
    private static String i = "1";
    private static String j = "2";
    private static void m1() throws InterruptedException {
//        new Thread(() -> {
            synchronized (i) {
                Thread.sleep(1000);
                synchronized (j) {
                    System.out.printf("死锁啦222");
                }
            }
//        }).start();
    }
    private static void m2() throws InterruptedException {
        synchronized (j) {
            Thread.sleep(1000);
            synchronized (i) {
                System.out.printf("死锁啦111");
            }
        }
    }
}
