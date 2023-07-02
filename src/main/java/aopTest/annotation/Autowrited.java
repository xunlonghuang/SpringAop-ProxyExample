package aopTest.annotation;

import java.lang.annotation.*;

/**
 * 此注解标识的字段将会被自动填充
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowrited {
}
