package aopTest.annotation;

import java.lang.annotation.*;

/**
 * 该注解与aspect注解联合定义切点
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface annotationTest {
}
