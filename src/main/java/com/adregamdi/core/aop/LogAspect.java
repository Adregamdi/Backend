package com.adregamdi.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
@Aspect
public class LogAspect {
    @Pointcut("execution(* com.adregamdi..*Controller.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* com.adregamdi..*Service.*(..))")
    public void service() {
    }

    // 메서드 진입 로깅
    @Before("controller() || service()")
    public void beforeLogic(final JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("====== Entering: {} - Method: {} ======", className, method.getName());

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                log.info("Param[{}] type = {}, value = {}", i, args[i].getClass().getSimpleName(), args[i]);
            } else {
                log.info("Param[{}] is null", i);
            }
        }
    }

    // 메서드 종료 로깅
    @AfterReturning(pointcut = "controller() || service()", returning = "result")
    public void afterLogic(final JoinPoint joinPoint, Object result) {
        Method method = getMethod(joinPoint);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("====== Exiting: {} - Method: {} ======", className, method.getName());

        if (result != null) {
            log.info("Return type = {}, value = {}", result.getClass().getSimpleName(), result);
        } else {
            log.info("Return value is null");
        }
    }

    // 예외 로깅
    @AfterThrowing(pointcut = "controller() || service()", throwing = "e")
    public void afterThrowingLogging(final JoinPoint joinPoint, final Throwable e) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.error("!!! Exception in {}.{}", className, methodName);
        log.error("Exception type: {}", e.getClass().getName());
        log.error("Exception message: {}", e.getMessage());
        log.error("Stack trace:", e);

        // 원인 예외 로깅
        Throwable cause = e.getCause();
        if (cause != null) {
            log.error("Caused by: {} - {}", cause.getClass().getName(), cause.getMessage());
        }
    }

    private Method getMethod(final JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
