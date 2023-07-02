package aopTest.containerUtil;

import aopTest.ProxyFactory;
import aopTest.ProxyTestInvocationHandler;
import aopTest.annotation.*;
import aopTest.util.FileUtil;
import aopTest.util.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class AnalysisAnnotation {
    private List<String> classPaths;
    private List<Class> classes;
    private ClassLoader cl;
    //示例值:annotationTest -> {Class@987} "class aopTest.Advice.AdviceTest"
    private Map<String,Class> annotationMap;
    //示例值:{Class@980} "interface aopTest.springSample.blo.ServiceTest"
    // -> {Class@958} "class aopTest.springSample.blo.impl.ServiceTestImpl"
    private Map<Class,Class> serviceMap;
    //示例值:aopTest.springSample.blo.ServiceTest -> {Class@958} "class aopTest.springSample.blo.impl.ServiceTestImpl"
    private Map<String,Class> nameServiceMap;
    private Map<String,Class> urlMap;
    private Map<Class,Class> proxyMap;
    private FileUtil util;
    private List<Class> service;

    private List<Class> controller;
    private Map<String,Class> aspects;

    //示例值:/url/usb -> {ControllerTest@967}
    private Map<String,Object> url2Controller;
    //示例值:/url/usb -> {Method@971} "public void aopTest.springSample.controller.ControllerTest.testFace()"
    private Map<String,Method> url2Method;
    //示例值:/url/usb -> {Class@951} "class aopTest.springSample.controller.ControllerTest"
    private Map<String,Class> url2ControllerClass;
    private ProxyFactory proxyFactory;
    private ArrayList<Class> dto;

    public AnalysisAnnotation(List<String> classPath){
        this.classPaths = classPath;
        this.cl = AnalysisAnnotation.class.getClassLoader();
        this.init();
    }

    public AnalysisAnnotation(){
        this.cl = AnalysisAnnotation.class.getClassLoader();
        this.init();
    }

    public AnalysisAnnotation(List<String> classPath,ClassLoader cl){
        this.classPaths = classPath;
        this.cl = cl;
        this.init();
    }
    private void init(){
        this.annotationMap = new HashMap<>();
        this.serviceMap = new HashMap<>();
        this.urlMap = new HashMap<>();
        this.proxyMap = new HashMap<>();
        this.util = new FileUtil();
        this.classes = new ArrayList<>();
        this.url2Method = new HashMap<>();
        this.url2Controller = new HashMap<>();
        this.aspects = new HashMap<>();
        this.url2ControllerClass = new HashMap<>();
        this.controller = new ArrayList<>();
        this.service = new ArrayList<>();
        this.nameServiceMap = new HashMap<>();
        this.dto = new ArrayList<>();
    }

    /**
     * 分析项目路径下的所有类。
     * @param basePackage 项目包路径 在这里的值应该是springSample
     */
    public void classification(String basePackage){
        List<String> classNames = getClassNames(basePackage);
        for(String className : classNames){
            Class clazz = this.loadClass(className);
            this.classes.add(clazz);
            // 筛选出Service 实现类
            if(clazz.isAnnotationPresent(Service.class)){
//                List<Class> classes = new ArrayList<>(Arrays.asList(clazz.getInterfaces()));
//                this.service.addAll(classes);
                this.service.add(clazz);
            }
            // 筛选出增强类
            if(clazz.isAnnotationPresent(aspect.class)){
                aspect asp = (aspect) clazz.getAnnotation(aspect.class);
                String value = asp.value();
                this.annotationMap.put(value,clazz);
            }
            // 筛选出Controller类
            if(clazz.isAnnotationPresent(Controller.class)){
                this.controller.add(clazz);
            }
            //筛选出DTO类
            if(clazz.isAnnotationPresent(Data.class)){
                this.dto.add(clazz);
            }
            if(clazz.isAnnotation()){
                String annotationName = clazz.getSimpleName();
                this.aspects.put(annotationName,clazz);
            }
        }
//        Set<String> aspectNames = this.aspects.keySet();
        // 获得增强注解和增强类的映射
//        for(Class clazz : this.classes){
//            List<Annotation> annotations = new ArrayList<>(Arrays.asList(clazz.getDeclaredAnnotations()));
//            for(Annotation annotation : annotations){
//                String annotationName = annotation.getClass().getSimpleName();
//                for(String aspectName : aspectNames){
//                    if(aspectName.equals(annotationName)){
//                        //表名clazz这个类引用了aspectName这个注解。
//                        List<Class> aspectClass = this.aspects.computeIfAbsent(aspectName, k -> new ArrayList<>());
//                        aspectClass.add(clazz);
//                    }
//                }
//            }
//        }
    }

    public void analysisController(){

        for(Class controller : this.controller){
            Field[] fields = controller.getDeclaredFields();
            Method[] methods = controller.getDeclaredMethods();
            Object obj = null;
            try {
                obj = controller.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for(Method method : methods){
                if(method.isAnnotationPresent(Mapping.class)){
                    Mapping mapping = method.getAnnotation(Mapping.class);
                    String url = mapping.value();
                    this.url2Controller.put(url,obj);
                    this.url2ControllerClass.put(url,controller);
                    method.setAccessible(true);
                    this.url2Method.put(url,method);
                }
            }
            injectionField(fields,obj);
        }
    }

    public void injectionField(Field[] fields,Object obj){
        Set<Class> serviceSet = this.serviceMap.keySet();
        for(Field field : fields){
            if(field.isAnnotationPresent(Autowrited.class)){
                //此时可以断定此字段是需要自动注入的
                //所以现在需要区分需要自动注入的是DTO还是Service
                //TODO 注入变量
                boolean isDTO = false;
                Class fieldType = field.getType();
                for(Class clazz : this.dto){
                    if(fieldType.equals(clazz)){
                        try {
                            field.set(obj,clazz.newInstance());
                            isDTO = true;
                            break;
                        } catch (IllegalAccessException | InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                //如果该字段在上一个for循环中确定为是DTO字段，则不需要继续寻找对应的service实现类
                if(!isDTO){
                    for(Class clazz : serviceSet){
                        if(fieldType.equals(clazz)){
                            //TODO
                            try {
                                Class serviceImpl = this.serviceMap.get(clazz);
                                Object serviceImplObj = serviceImpl.newInstance();
                                Field[] serviceFields = serviceImpl.getFields();
                                //递归调用，使后续service都注入字段
                                injectionField(serviceFields,serviceImplObj);
                                Field.setAccessible(fields, true);
                                InvocationHandler proxyInvocationHandler = new ProxyTestInvocationHandler<>(serviceImplObj);
                                serviceImplObj = Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz}, proxyInvocationHandler);
                                field.set(obj,serviceImplObj);
                                break;
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 分析service
     */
    public void analysisService(){
//        for(Map.Entry<Class,Class> entry : this.serviceMap.entrySet()){
//            Class interfaceService = entry.getKey();
//            Class implService = entry.getValue();
//            this.serviceMap.put(interfaceService,implService);
//            Field[] fields = implService.getDeclaredFields();
//            for(Field field : fields){
//                if(field.isAnnotationPresent(Autowrited.class)){
//                    //TODO 自动注入
//                }
//            }
//        }
        for(Class clazz : this.service){
            List<Class> interfaceServices = Arrays.asList(clazz.getInterfaces());
            //将ServiceImpl与其实现的所有接口列表构成映射
            String implName = clazz.getSimpleName();
            for(Class interfaceService : interfaceServices){
                String serviceName = interfaceService.getSimpleName();
                if (implName.equals(serviceName+"Impl")){
                    this.serviceMap.put(interfaceService,clazz);
                    String name = interfaceService.getName();
                    this.nameServiceMap.put(name,clazz);
                    //找到了serviceImpl==>service的关系 跳出循环
                    break;
                }
            }
        }
    }

    /**
     * 遍历Controller类列表以及ServicesImpl类列表
     * 寻找需要自动注入的字段
     */

    public void main(String basePage){
        this.classification(basePage);
        this.analysisService();
        ProxyFactory.seteMap(annotationMap,serviceMap,aspects);
        this.analysisController();
//        this.proxyFactory = new ProxyFactory();
    }

    public static void main(String[] args){
        AnalysisAnnotation analysisAnnotation = new AnalysisAnnotation();
        analysisAnnotation.main("aopTest");
    }

    public Map<String,Class> getAspects(){
        return this.aspects;
    }

    public Map<String,Class> getAnnotationMap(){
        return this.annotationMap;
    }

    public  Map<String,Class> getNameServiceMap(){
        return this.nameServiceMap;
    }

    public ProxyFactory getProxyFactory(){
        return this.proxyFactory;
    }

    public Map<String,Method> getUrl2Method(){
        return url2Method;
    }

    public Map<String,Object> getUrl2Controller(){
        return url2Controller;
    }

    public Map<String,Class> getUrl2ControllerClass(){
        return this.url2ControllerClass;
    }

    public Map<Class,Class> getServiceMap(){
        return this.serviceMap;
    }

    public Class loadClass(String classPath){
        Class clazz = null;
        try{
            clazz = this.cl.loadClass(classPath);
        } catch (ClassNotFoundException e) {
            System.out.println(String.format("未找到%s类",classPath));
        }
        return clazz;
    }

    /**
     * 筛选连接点中的切点
     * @param methods
     * @return
     */
    public List<Method> getPointCut(List<Method> methods){
        for(Method method : methods){
            Annotation[] annotations = method.getAnnotations();
            for(Annotation annotation : annotations){
                //遍历方法上的注解
                String anname = annotation.annotationType().getSimpleName();
                if(anname.equalsIgnoreCase("Before"))
                    continue;
                else if(anname.equalsIgnoreCase("After"))
                    continue;
            }
        }
        return null;
    }

    public Method getAdvice(Class clazz,boolean needBefore){
        List<Method> methods = Util.getMethods(clazz);
        Method before = null;
        Method after = null;
        for(Method method : methods){
            Annotation[] annotations = method.getAnnotations();
            for(Annotation annotation : annotations){
                //遍历方法上的注解
                String anname = annotation.annotationType().getSimpleName();
                if(anname.equalsIgnoreCase("Before")){
                    before = method;
                    break;
                }
                else if(anname.equalsIgnoreCase("After")){
                    after = method;
                    break;
                }
            }
            if(before != null && needBefore)
                return before;
            else if(after != null && !needBefore)
                return after;
        }
        return null;
    }

    public Method getBefore(Class clazz){
        return getAdvice(clazz,true);
    }

    public Method getAfter(Class clazz){
        return getAdvice(clazz,false);
    }

//    public void analysisClass(Class clazz){
//        boolean isAnn = clazz.isAnnotationPresent(aspect.class);
//        //如果有aspect注解，说明此类是通知类，也就是增强方法类
//        if(isAnn){
//            aspect asp = (aspect) clazz.getAnnotation(aspect.class);
//            String value = asp.value();
//            this.annotationMap.put(value,clazz);
//        }
//        else{
//            //得到所有连接点。
//            List<Method> methods = Util.getAllMethod(clazz);
//        }
//    }

    /**
     * 获得包下所有类的类名
     * @param basePackage
     * @return
     */
    public List<String> getClassNames(String basePackage){
        return this.util.getClassNames(basePackage);
    }

//    public void analysisController(String basePage){
//        List<String> classNames = getClassNames(basePage);
//        //分析controller
//        for(String className : classNames){
//            Class clazz = this.loadClass(className);
//            clazz.isAnnotation();
//        }
//    }


    /**
     * 分析Service包，检索路径下所有类的路径并分析成类名。
     * 然后遍历类名集合，先确定Service、Advice
     * @param basePackage
     */
    public void analysisService(String basePackage){
        List<String> classNames = getClassNames(basePackage);
        // 分析service包下的类。 构造<ServiceInterface,ServiceInterfaceImpl>的集合
        for(String className : classNames){
            Class service = this.loadClass(className);
            boolean isInter = service.isInterface();
            //如果是不是实现类的话。跳过。
            if(!isInter){
                List<Class> classes = new ArrayList<>(Arrays.asList(service.getInterfaces()));
                for(Class clazz : classes){
                    this.serviceMap.put(clazz,service);
                }
            }
        }
    }

}
