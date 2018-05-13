/**
 * @autor pankui
 * @date 11/03/2018
 * <p>
 * <pre>
 * 男儿当自强
 * </pre>
 */
module spring.boot2.java9.webflux {

    requires java.sql;


    requires spring.core;
    requires spring.beans;
    requires spring.context;
    requires spring.aop;
    requires spring.web;
    requires spring.expression;


    requires spring.boot;
    requires spring.boot.autoconfigure;


    requires reactor.core;
    requires spring.webflux;


    //需要导入每个包名
    opens space.pankui;
    //opens com.space.service;
}