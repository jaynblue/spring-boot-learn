package com.space.service.impl;

import com.space.service.DemoService;
import org.springframework.stereotype.Service;

/**
 * @author pankui
 * @date 13/04/2018
 * <pre>
 *
 * </pre>
 */
@Service
public class Demo2ServiceImpl extends DemoService {

    @Override
    public void print (){
        System.out.println("***********"+getClass());
    }
}
