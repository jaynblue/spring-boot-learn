package com.space.al;

import org.apache.el.util.ReflectionUtil;
import org.springframework.util.ReflectionUtils;

/**
 * @author pankui
 * @date 03/04/2018
 * <pre>
 *
 *  问题：判断一个单向链表是否有环，如果有环则找到环的入口节点。
 *
 *  <https://blog.csdn.net/piaojun_pj/article/details/5965298>
 *
 *  判断是否有环：
 *
 *      思路1：用两个指针p1,p2同时指向链表的头部，p1一次移动一步，p2一次移动两步，
 *      如果最终p1和p2重合则说明链表有环，
 *      如果p2走到空指针（链表的结尾）则说明链表无环。 时间复杂度为O (n)。
 *
 *
 *      如果最终p1和p2重合，使p2重新指向链表的头结点，然后p1和p2同时一次移动一步，
 *      当p1和p2再次重合时该节点指针就是环的入口节点指针。
 *
 * </pre>
 */
public class LinkList {


    public boolean hasCircle (ListNode head) {

        boolean isHasCircle = false;
        // 链表为空
        if (head == null) {
            return false;
        }

        //指向链表的头部
        ListNode p1 = head;

        //指向链表的头部
        ListNode p2 = head;
        //不为空执行while循环
        while (p2 != null) {

            if (p2.next == null || p2.next.next == null) {
                return false;
            }
            // p1  一次移动一步
            p1 = p1.next;

            // p2一次移动两步
            p2 = p2.next.next;

            //单链表有环
            if (p1 == p2){
                isHasCircle = true;
                break;
            }
        }
        if (isHasCircle) {
            // 查找环的入口点，
            p1 = head;
            while (p1 != p2) {
                p1 = p1.next;
                p2 = p2.next;
            }
            System.out.println(p1.data);
        }
        return isHasCircle;
    }


   /* public Node insert (int data){
        Node node = new Node(data);
       // node.next = node;
        return node;
    }
*/
    public static void main(String[] args) {

        LinkList l = new LinkList();

        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(4);

        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node2;


        //如果一个链表有环，那么用一个指针去遍历，是永远走不到头的。
        //System.out.println(node.toString());


        LinkList linkList = new LinkList();

        boolean isCircle = linkList.hasCircle(node1);

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
