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

    // 특정 JoinPoint 에서 수행될 부가기능을 정리
    @Before("controller() || service()")
    public void beforeLogic(final JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.info("====== method = {} ======", method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                log.info("parameter type = {}", arg.getClass().getSimpleName());
                log.info("parameter value = {}", arg);
            }
        }
    }

    @AfterReturning("controller() || service()")
    public void afterLogic(final JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.info("====== method = {} ======", method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                log.info("return type = {}", arg.getClass().getSimpleName());
                log.info("return value = {}", arg);
            }
        }
    }

    @AfterThrowing(pointcut = "controller()", throwing = "e")
    public void afterThrowingLogging(
            final JoinPoint joinPoint,
            final Exception e
    ) {
        log.error("!!! Occured error in request {}", joinPoint.getSignature().toShortString());
        log.error("{}", e.getMessage());
    }

    // JoinPoint로 메서드 정보 가져오기
    private Method getMethod(final JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
