package com.jpuneng.coconut.aop;


import java.lang.annotation.*;

/**
 * @author Jasper Wu
 * @date 4/3/2019
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Monitoring {
    Class<? extends AbstractPostMonitor> postMonitor() default DefaultPostMonitor.class;

    Class<? extends AbstractPostMonitor>[] postMonitors() default {};
}
