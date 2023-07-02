package aopTest.annotation;

import java.lang.annotation.*;

/**
 * 该注解定义增强方法的生效节点
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
}
