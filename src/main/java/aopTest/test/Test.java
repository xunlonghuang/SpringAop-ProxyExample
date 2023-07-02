package aopTest.test;

import aopTest.annotation.aspect;
import aopTest.util.FileUtil;
import aopTest.util.Util;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public int publicVarible;
    public Test(){
        publicVarible = 1;
    }
    private void annotationTest(){
        ClassLoader cl = Test.class.getClassLoader();
        try{
            Class adviceTest = cl.loadClass("aopTest.Advice.AdviceTest");
            Annotation[] annotations = adviceTest.getAnnotations();
            for(Annotation annotation : annotations){
                System.out.println(annotation.toString());
                System.out.println(annotation.annotationType().getSimpleName());
                aspect asp = (aspect) annotation;
                System.out.println(asp.value());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void swap(ArrayList<Integer> list){
        list.remove(1);
    }

    public static void main(String[] args){
//        Test test = new Test();
//        test.fileProcessTest();
//        FileUtil fileUtil = new FileUtil("aopTest");
//        test.getClass().getMethods()[0].getName();
        Test test = new Test();
        System.out.println("111");

    }
}

