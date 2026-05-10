package com.finance.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.finance.controller..*.*(..))")
    public void controllerPointcut() {}

    @Pointcut("execution(* com.finance.service..*.*(..))")
    public void servicePointcut() {}

    @Around("controllerPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("[请求] {}.{} 参数: {}", className, methodName, Arrays.toString(args));
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[响应] {}.{} 耗时: {}ms", className, methodName, costTime);
            return result;
        } catch (Exception e) {
            log.error("[异常] {}.{} 错误: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }

    @Around("servicePointcut()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("[业务异常] {}.{} 错误: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
