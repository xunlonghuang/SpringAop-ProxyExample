package aopTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyTestInvocationHandler<T> implements InvocationHandler {

    private T target;
    public ProxyTestInvocationHandler(T target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method implMethod = target.getClass().getDeclaredMethod(method.getName());
        List<Class> adviceClasses = ProxyFactory.getAdviceClass(implMethod);
        method.setAccessible(true);
        Map<Object,Method> afterMethod = new HashMap<>();
        for(Class adviceClass : adviceClasses){
            Object advice = adviceClass.newInstance();
            Map<String,Method> methodMap = ProxyFactory.getMethodMap(adviceClass);
            Method before = methodMap.get("Before");
            if(before != null){
                before.setAccessible(true);
                before.invoke(advice, args);
            }
            Method after = methodMap.get("After");
            afterMethod.put(advice,after);
        }
        method.invoke(target,args);
        for(Map.Entry<Object,Method> entry : afterMethod.entrySet()){
            Object advice = entry.getKey();
            Method after = entry.getValue();
            if(after != null){
                after.setAccessible(true);
                after.invoke(advice,args);
            }
        }
        return null;
    }
}
