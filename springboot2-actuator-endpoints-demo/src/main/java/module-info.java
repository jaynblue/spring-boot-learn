


/**
 * @author pankui
 * @date 15/05/2018
 * <pre>
 *
 * </pre>
 */
module springboot.actuator.endpoints.demo.main {

    requires java.sql;


    requires spring.core;
    requires spring.beans;
    requires spring.context;
    requires spring.aop;
    requires spring.web;
    requires spring.expression;


    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.boot.actuator;
    requires de.codecentric.spring.boot.admin.server;
    requires spring.boot.starter.security;
    requires spring.security.config;
    requires spring.security.web;



    //需要导入每个包名
    opens space.pankui;
    opens space.pankui.endpoint;
    opens space.pankui.controller;
    opens space.pankui.conf;
}





