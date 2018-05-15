/**
 * @author pankui
 * @date 15/05/2018
 * <pre>
 *
 * </pre>
 */
module springboot2.actuator.endpoints.demo.main {

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
    requires spring.jcl;
   // requires commons.logging;
    requires org.apache.logging.log4j;

    //requires javax.annotation;



    //需要导入每个包名
    opens space.pankui;
    opens space.pankui.endpoint;
    opens space.pankui.controller;
}