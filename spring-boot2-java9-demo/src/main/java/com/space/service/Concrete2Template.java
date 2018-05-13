package com.space.service;

import org.springframework.stereotype.Service;

/**
 * @author pankui
 * @date 13/04/2018
 * <pre>
 *
 * </pre>
 */
@Service
public class Concrete2Template extends AbstractTemplate {


    static {
        System.out.println(" ~~~~~ Concrete2Template  concreteTemplate  ****** ");
    }

    @Override
    public void abstractMethod() {
        System.out.println("￥￥￥￥abstractMethod "+getClass());
    }

    @Override
    public void templateMethod() {
        ///重写父类的方法
        System.out.println(" !!! sub class  templateMethod "+getClass());
    }
}
