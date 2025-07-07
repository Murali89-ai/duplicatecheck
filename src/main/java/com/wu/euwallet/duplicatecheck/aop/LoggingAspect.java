package com.wu.euwallet.duplicatecheck.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("@annotation(com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation)")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Method Invoked: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "@annotation(com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation)", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("Method Executed: {} | Response: {}", joinPoint.getSignature().toShortString(), result);
    }
}
