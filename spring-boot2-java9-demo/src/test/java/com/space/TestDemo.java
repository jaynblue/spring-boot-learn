package com.space;

import com.space.service.AbstractTemplate;
import com.space.service.ConcreteTemplate;
import com.space.service.DemoService;
import com.space.service.impl.Demo2ServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author pankui
 * @date 13/04/2018
 * <pre>
 *
 * </pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDemo {

    @Autowired
    @Qualifier(value = "demo2ServiceImpl")
    private DemoService demoService;


    @Autowired
    private ConcreteTemplate concreteTemplate;

    @Resource
    private AbstractTemplate abstractTemplate;

    @Test
    public void testDemo () {
        demoService.print();
    }


    @Test
    public void testSubAbstract() {
        concreteTemplate.templateMethod();
        System.out.println("==========");
        concreteTemplate.abstractMethod();
    }

    @Test
    public void testAbstract(){
      //  AbstractTemplate abstractTemplate =
        abstractTemplate.templateMethod();
    }



}
