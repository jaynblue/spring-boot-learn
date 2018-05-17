/**
 * @author pankui
 * @date 16/05/2018
 * <pre>
 *
 * </pre>
 */
module spring.boot2.micrometer.demo.main {

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
    requires micrometer.core;
    requires micrometer.registry.jmx;
    requires tomcat.embed.core;


    //需要导入每个包名
    opens space.pankui;
    opens space.pankui.micrometer;
    opens space.pankui.controller;


}