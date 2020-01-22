package io.github.androidjp.coconut.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;

/**
 * @author Jasper Wu
 * @date 4/3/2019
 **/
@Slf4j
public abstract class AbstractPostMonitor<T> {

    private T getArg0FromJoinPoint(ProceedingJoinPoint joinPoint) {
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            try {
                return (T) joinPoint.getArgs()[0];
            } catch (Exception e) {
                log.error("Occurs Exception", e);
                return null;
            }
        }
        return null;
    }

    public <A extends Annotation> A getAnnotation(ProceedingJoinPoint joinPoint, Class<A> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(annotationClass);
    }

    public void onSuccess(ProceedingJoinPoint joinPoint, Object result, Long timeRaise) {
        this.onSuccess(joinPoint, this.getArg0FromJoinPoint(joinPoint), result, timeRaise);
    }

    public void onError(ProceedingJoinPoint joinPoint, Long timeRaise, Throwable throwable) throws Exception {
        this.onError(joinPoint, this.getArg0FromJoinPoint(joinPoint), timeRaise, throwable);
    }

    protected String getTargetMethod(ProceedingJoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
    }

    protected void onSuccess(ProceedingJoinPoint joinPoint, T firstParam, Object result, Long timeRaise) {
        log.info(getTargetMethod(joinPoint) + " raise: " + timeRaise + " ms");
    }

    protected void onError(ProceedingJoinPoint joinPoint, T firstParam, Long timeRaise, Throwable throwable) throws Exception {
        log.error(getTargetMethod(joinPoint) + " raise: " + timeRaise + " ms, but occurs exception: " + ExceptionUtils.getMessage(throwable), throwable);
    }
}
