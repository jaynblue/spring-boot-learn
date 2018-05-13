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
public abstract class AbstractTemplate {

    public void templateMethod (){
        System.out.println(" template method "+getClass());
    }

    protected abstract void abstractMethod ();

    static {
        System.out.println("#### AbstractTemplate ##");
    }
}
