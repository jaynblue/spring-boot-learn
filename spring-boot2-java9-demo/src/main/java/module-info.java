/**
 * @autor pankui
 * @date 11/03/2018
 * <p>
 * <pre>
 * 男儿当自强
 * </pre>
 */
module spring.boot2.java9.demo.main {

    requires java.sql;


    requires spring.core;
    requires spring.beans;
    requires spring.context;
    requires spring.aop;
    requires spring.web;
    requires spring.expression;


    requires spring.boot;
    requires spring.boot.autoconfigure;


    //需要导入每个包名
    opens com.space;
    opens com.space.controller;
}