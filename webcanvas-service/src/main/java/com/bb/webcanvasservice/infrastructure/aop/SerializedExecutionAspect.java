package com.bb.webcanvasservice.infrastructure.aop;

import com.bb.webcanvasservice.common.annotation.Sequential;
import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.concurrent.SequentialTaskExecutor;
import com.bb.webcanvasservice.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 동시 접근 제어 및 순차 처리 보장이 필요한 메소드를 처리하는 Aspect
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SerializedExecutionAspect {

    private final SequentialTaskExecutor taskExecutor;

    @Around("@annotation(sequential)")
    public Object aroundSerialized(ProceedingJoinPoint joinPoint,
                                   Sequential sequential) throws Throwable {


        // 리턴 타입 확인
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> returnType = method.getReturnType();

        /**
         * 리턴이 없는 경우
         */
        if (Void.TYPE.equals(returnType)) {
            taskExecutor.execute(sequential.key(), () -> {
                try {
                    joinPoint.proceed();
                }
                catch(BusinessException be) {
                    throw be;
                }
                catch(Throwable t) {
                    log.error(t.getMessage());
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            });

            return null;
        }

        /**
         * 리턴이 있는 경우
         */
        CompletableFuture<Object> future = taskExecutor.execute(sequential.key(), () -> {
            try {
                return CompletableFuture.completedFuture(joinPoint.proceed());
            } catch (Throwable t) {
                CompletableFuture<Object> failed = new CompletableFuture<>();
                failed.completeExceptionally(t);
                return failed;
            }
        });

        // timeout 처리
        if (sequential.timeoutMillis() > 0) {
            return future.get(sequential.timeoutMillis(), TimeUnit.MILLISECONDS);
        }
        else {
            return future.get();
        }
    }
}
