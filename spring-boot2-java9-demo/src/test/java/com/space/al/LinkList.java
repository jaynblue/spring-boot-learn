package com.space.al;

import org.apache.el.util.ReflectionUtil;
import org.springframework.util.ReflectionUtils;

/**
 * @author pankui
 * @date 03/04/2018
 * <pre>
 *
 * </pre>
 */
public class LinkList {


    public ListNode hasCircle (ListNode head) {

        // 链表为空
        if (head == null) {
            return null;
        }

        // 只有头结点
        if (head.next == null) {
            return null;
        }
        // next 表示从头结点开始每次往后走一步的指针
        ListNode node = head;
        // 表示从头结点开始每次往后走两步的指针
        ListNode nextNext = head.next;
        //不为空执行while循环
        while (nextNext != null) {
            //单链表有环
            if (node == nextNext){
                return node;
            }
            node = node.next;
            nextNext = nextNext.next.next;

        }
        return null;
    }


   /* public Node insert (int data){
        Node node = new Node(data);
       // node.next = node;
        return node;
    }
*/
    public static void main(String[] args) {

        LinkList l = new LinkList();

        ListNode node = new ListNode(1);
        ListNode node1 = new ListNode(2);
        ListNode node2 = new ListNode(3);

        node.next = node1;
        node1.next = node2;
        node2.next = node;


        System.out.println(node.toString());


        LinkList linkList = new LinkList();

        ListNode isCircle = linkList.hasCircle(node);

        System.out.println(isCircle);


    }

}

class ListNode{

    public int data;

    public ListNode next;

    public ListNode (int data){
        this.data = data;
    }

    @Override
    public String toString (){
        return "->" + data;
    }

}
