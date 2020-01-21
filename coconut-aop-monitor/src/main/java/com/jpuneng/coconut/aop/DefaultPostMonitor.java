package com.jpuneng.coconut.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * @author Jasper Wu
 * @date 4/3/2019
 **/
@Component("default_post_processor")
@Slf4j
public class DefaultPostMonitor<T> extends AbstractPostMonitor<T> {

    @Override
    protected void onSuccess(ProceedingJoinPoint joinPoint, T firstParam, Object result, Long timeRaise) {
        super.onSuccess(joinPoint, firstParam, result, timeRaise);
    }

    @Override
    protected void onError(ProceedingJoinPoint joinPoint, T firstParam, Long timeRaise, Throwable throwable) throws Exception {
        super.onError(joinPoint, firstParam, timeRaise, throwable);
    }
}
