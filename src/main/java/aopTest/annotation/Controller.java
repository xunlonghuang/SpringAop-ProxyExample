package aopTest.annotation;

import java.lang.annotation.*;

/**
 * 此注解表示被注解类为controller
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
