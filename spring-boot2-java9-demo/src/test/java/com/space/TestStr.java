package com.space;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.StringConcatFactory;

/**
 * @author pankui
 * @date 01/04/2018
 * <pre>
 *
 * </pre>
 */
public class TestStr {
    /**
     * java8 查看字节码 字符串拼接使用StringBuilder
     *
     * java9 查看字节码 字符串拼接使用的是 StringConcatFactory 的 makeConcatWithConstants
     *
     * javap -c  TestStr.class 查看字节码（.class 去掉）
     *
     * */

    @Test
    public void testStr () {
        String s = "a" + "b" +1+ "df";
        s += "f";

        System.out.println(s);
    }

    /**
     * 查看字节码 java8 循环会new 多个StringBuilder
     *
     *  而java9 使用 StringConcatFactory 不会多new 对象
     *
     *
     * */
    @Test
    public void testStrFor() {
        String a = "a";
        for (int i = 0 ;i < 100 ; i ++) {
            a += "df";
        }

        System.out.println(a);
    }


    /**
     *
     *
     *
     *
     show DATABASES ;

     USE website;

     SHOW TABLES ;


     INSERT INTO website.temp(str) VALUES ("我们啊的啊是的额个啊");

     SELECT *
     FROM website.temp;

     select str,length(str),char_length(str)  from website.temp;

     结论: mysql varchar(n) n 是字符。所以
     varchar(n)  n是多少就能存多少个汉字或者多少非中文字符串.

     比如varchar(10) 就可以存十个汉字，或者 对于非中文字符串，可以插入包含10个字符以及小于10个字符的字符串.


     *
     * */
    @Test
    public void testStrLen () throws UnsupportedEncodingException {
        String str = "我们啊的啊是的额个啊";
        byte[] bytes = str.getBytes("UTF-8");
        System.out.println(str.length());//6
        System.out.println(bytes.length);//10


        String str1 = "aaaaaaasdf";
        byte[] bytes1 = str1.getBytes("UTF-8");
        System.out.println(str1.length());//6
        System.out.println(bytes1.length);//10
    }
}
