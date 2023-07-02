package aopTest.springSample.blo.impl;

import aopTest.annotation.After;
import aopTest.annotation.Before;
import aopTest.annotation.Service;
import aopTest.annotation.annotationTest;
import aopTest.springSample.blo.ServiceTest;

@Service
@SuppressWarnings({"unused"})
public class ServiceTestImpl implements ServiceTest {

    @Override
    @annotationTest
    public void test() {
        System.out.println("this is bpo");
    }
}
