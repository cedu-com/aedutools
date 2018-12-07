package net.chinaedu.aedu.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MartinKent
 * @time 2018/1/22
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface SaveState {
    String value() default "";
}
