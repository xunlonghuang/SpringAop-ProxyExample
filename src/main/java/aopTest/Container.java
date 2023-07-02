package aopTest;

import aopTest.annotation.Autowrited;
import aopTest.containerUtil.AnalysisAnnotation;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 本类的角色是模仿spring容器。
 * 承担的职责是：
 * 1.扫描指定的包
 * 2.根据配置路径找到指定的方法
 * 3.遍历该类每个方法的注解。根据注解动态生成代理类
 */
@SuppressWarnings({"unused"})
public class Container {
    private Map<String,Class> nameServiceMap;
    private Map<String,Class> annotationMap;
    private Map<Class,Class> serviceMap;
    private Map<String,Class> aspects;
    public Container(){
    }

    private  Object getObject(Class clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }



    private Object getProxy(Object t, Class clazz){
//        try{
//            List<Field> needInjection = needInjuection(clazz);
//            if(needInjection.size() == 0){
//                ProxyFactory proxy = new ProxyFactory(t);
//                ProxyFactory.seteMap(annotationMap,serviceMap,aspects);
//                return Proxy.newProxyInstance(t.getClass().getClassLoader(),t.getClass().getInterfaces(),proxy);
//            }
//            for(Field field : needInjection){
//                Type type = field.getType();
//                String typeName = type.getTypeName();
//                Class serviceClass = this.nameServiceMap.get(typeName);
//                Object injectionValue = getProxy(getObject(serviceClass),field.getType());
//                field.setAccessible(true);
//                field.set(t,injectionValue);
//            }
//        } catch (IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }
//        return t;


        ProxyFactory proxy = new ProxyFactory(t);
        ProxyFactory.seteMap(annotationMap,serviceMap,aspects);
        return Proxy.newProxyInstance(t.getClass().getClassLoader(),t.getClass().getInterfaces(),proxy);
    }

    private List<Field> needInjuection(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        List<Field> needInjuection = new ArrayList<>();
        for(Field field : fields){
            if(field.isAnnotationPresent(Autowrited.class))
                needInjuection.add(field);
        }
        return needInjuection;
    }

    public void main(){
        AnalysisAnnotation util = new AnalysisAnnotation();
        util.main("aopTest");
        ProxyFactory proxyFactory = util.getProxyFactory();
        Map<String, Method> url2Method = util.getUrl2Method();
        Map<String, Object> url2Contorller = util.getUrl2Controller();
        Map<String, Class> url2ControllerClass = util.getUrl2ControllerClass();
        this.nameServiceMap = util.getNameServiceMap();
        this.serviceMap = util.getServiceMap();
        this.annotationMap = util.getAnnotationMap();
        this.aspects = util.getAspects();
//        for(Map.Entry<String,Object> entry : url2Contorller.entrySet()){
//            String url = entry.getKey();
//            Object controller = entry.getValue();
//            Class cc = url2ControllerClass.get(url);
//            controller = getProxy(controller,cc);
//            entry.setValue(controller);
//            entry.setValue(controller);
//        }
        // 模拟前端发送请求。
        Scanner sc = new Scanner(System.in);
        String input = "";
        while (!input.equalsIgnoreCase("exit")){
            try{
                System.out.println("请输入url");
                if(sc.hasNext()){
                    input = sc.nextLine();
                    Object obj = url2Contorller.get(input);
                    if(obj == null){
                        System.out.println("404 not found");
                    }
                    else{
                        Method method = url2Method.get(input);
                        method.setAccessible(true);
                        method.invoke(obj);
                    }
                }
           } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Bye");
    }

}
