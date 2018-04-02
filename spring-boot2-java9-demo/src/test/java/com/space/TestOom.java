package com.space;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pankui
 * @date 01/04/2018
 * <pre>
 *
 * </pre>
 */
public class TestOom {


    @Test
    public void testOom (){
        List list = new ArrayList();
        Integer i = 0;
        while (true) {
            list.add(i);
        }
    }

    public static void main(String[] args) {


        URL url = null;
        List<ClassLoader> classLoaderList = new ArrayList<ClassLoader>();
        try {
            url = new File("/tmp").toURI().toURL();
            URL[] urls = {url};
            while (true){
                ClassLoader loader = new URLClassLoader(urls);
                classLoaderList.add(loader);
                loader.loadClass("com.space.TestOom");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static int  temp = 1;

    @Test
    public void testStaticOOm(){
        List list = new ArrayList();
        while (true) {
            temp ++;
            list.add(temp);
        }
    }
}
