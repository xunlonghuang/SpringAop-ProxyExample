package aopTest.annotation;

import java.lang.annotation.*;

/**
 * 该注解表示该类是Service
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
}
