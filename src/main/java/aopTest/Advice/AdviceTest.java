package aopTest.Advice;

import aopTest.annotation.After;
import aopTest.annotation.Before;
import aopTest.annotation.aspect;

@aspect("annotationTest")
public class AdviceTest {

    @Before
    private void before(){
        System.out.println("this is before method");
    }

    @After
    private void after(){
        System.out.println("this is after method");
    }

}
