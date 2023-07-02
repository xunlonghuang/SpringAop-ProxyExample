package aopTest.annotation;

import java.lang.annotation.*;

/**
 * 此注解表示controller方法的匹配路由
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
    String value();
}
