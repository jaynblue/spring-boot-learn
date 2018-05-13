/**
 * @author pankui
 * @date 13/05/2018
 * <pre>
 *
 * </pre>
 */
module spring.boot2.data.redis.reactive.main {

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
    requires spring.data.redis;
    requires lombok;
    requires spring.data.commons;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.security.web;





    //需要导入每个包名
    opens space.pankui;
    opens space.pankui.service;
    opens space.pankui.dao;
    //opens com.space.service;
}