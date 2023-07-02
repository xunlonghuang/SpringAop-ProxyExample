package aopTest.springSample.controller;

import aopTest.annotation.Autowrited;
import aopTest.annotation.Controller;
import aopTest.annotation.Mapping;
import aopTest.springSample.blo.ServiceTest;

@Controller
@SuppressWarnings({"unused"})
public class ControllerTest {

    @Autowrited
    private ServiceTest bpo;

    @Mapping("/url/usb")
    public void testFace(){
        System.out.println("this is controller");
        bpo.test();
    }
}
