package com.biglibon.sharedlibrary.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Aspect that collects execution time for methods annotated with {@link TrackPerformanceMetric}.
 */
@Aspect
@Component
public class PerformanceMetricAspect {

    private final PerformanceMetricHandler metricPublisher;

    public PerformanceMetricAspect(PerformanceMetricHandler metricPublisher) {
        this.metricPublisher = metricPublisher;
    }

    @Around("@annotation(trackPerformanceMetric)")
    public Object measure(ProceedingJoinPoint joinPoint, TrackPerformanceMetric trackPerformanceMetric) throws Throwable {
        long startNanos = System.nanoTime();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String operation = trackPerformanceMetric.value().isBlank()
                ? className + "." + methodName
                : trackPerformanceMetric.value();
        Instant measuredAt = Instant.now();

        try {
            Object result = joinPoint.proceed();
            publish(operation, className, methodName, startNanos, measuredAt, true, null);
            return result;
        } catch (Throwable throwable) {
            publish(operation, className, methodName, startNanos, measuredAt, false, throwable.getClass().getSimpleName());
            throw throwable;
        }
    }

    private void publish(String operation,
                         String className,
                         String methodName,
                         long startNanos,
                         Instant measuredAt,
                         boolean success,
                         String errorType) {

        long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
        metricPublisher.publish(new PerformanceMetric(
                operation,
                className,
                methodName,
                durationMs,
                measuredAt,
                success,
                errorType
        ));
    }
}