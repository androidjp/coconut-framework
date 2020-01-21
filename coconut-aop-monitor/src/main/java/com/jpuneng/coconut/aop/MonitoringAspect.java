package com.jpuneng.coconut.aop;

import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jasper Wu
 * @date 4/3/2019
 **/
@Aspect
@Component
public class MonitoringAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Around("@annotation(Monitoring)")
    public Object duringMethod(ProceedingJoinPoint joinPoint) throws Exception {
        long curTimeStamp = System.currentTimeMillis();
        // 先收集我们要调的monitor，到一个array
        // 然后，再一次性call multiMonitor method
        List<AbstractPostMonitor> monitors = collectPostMonitors(joinPoint);
        return handleJoinPointWithMultiOrderedPostMonitors(joinPoint, curTimeStamp, monitors);
    }

    private List<AbstractPostMonitor> collectPostMonitors(ProceedingJoinPoint joinPoint) {
        List<AbstractPostMonitor> monitors = new ArrayList<>();
        for (Annotation annotation : ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotations()) {
            Monitoring monitoringAnnotation = null;
            monitoringAnnotation = (annotation instanceof Monitoring) ? (Monitoring) annotation : annotation.annotationType().getAnnotation(Monitoring.class);
            if (monitoringAnnotation == null) {
                continue;
            }
            AbstractPostMonitor tempMonitor = getMonitor(monitoringAnnotation);
            List<AbstractPostMonitor> tempMonitors = getMonitors(monitoringAnnotation);
            if (CollectionUtils.isNotEmpty(tempMonitors)) {
                monitors.addAll(tempMonitors);
            } else {
                monitors.add(tempMonitor);
            }
        }
        monitors = monitors.stream().distinct().collect(Collectors.toList());
        return monitors;
    }

    private AbstractPostMonitor getMonitor(Monitoring monitoringAnnotation) {
        Class<? extends AbstractPostMonitor> processorClass = monitoringAnnotation.postMonitor();
        return this.applicationContext.getBean(processorClass);
    }

    private List<AbstractPostMonitor> getMonitors(Monitoring monitoringAnnotation) {
        Class<? extends AbstractPostMonitor>[] processorClasses = monitoringAnnotation.postMonitors();
        return Arrays.stream(processorClasses)
                .map(clazz -> this.applicationContext.getBean(clazz))
                .collect(Collectors.toList());
    }


    private Object handleJoinPointWithMultiOrderedPostMonitors(ProceedingJoinPoint joinPoint, long curTimeStamp, List<AbstractPostMonitor> postMonitors) throws Exception {
        Object result = null;
        try {
            result = joinPoint.proceed();
            for (AbstractPostMonitor postMonitor : postMonitors) {
                postMonitor.onSuccess(joinPoint, result, System.currentTimeMillis() - curTimeStamp);
            }
            return result;
        } catch (Throwable throwable) {
            for (AbstractPostMonitor postMonitor : postMonitors) {
                postMonitor.onError(joinPoint, System.currentTimeMillis() - curTimeStamp, throwable);
            }
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
