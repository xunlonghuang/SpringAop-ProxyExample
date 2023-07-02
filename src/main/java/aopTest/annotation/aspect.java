package aopTest.annotation;

import java.lang.annotation.*;

/**
 * 该注解定义被修饰对象对应的注解名
 */
@Documented
//该接口作用域为类
@Target(ElementType.TYPE)
//声明接口的生命周期
@Retention(RetentionPolicy.RUNTIME)
public @interface aspect {
    String value() default "";
}
