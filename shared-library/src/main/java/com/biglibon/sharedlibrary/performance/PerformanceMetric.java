package com.biglibon.sharedlibrary.performance;

import java.time.Instant;

/**
 * Immutable performance metric context that can be consumed by different publishers
 * (log, kafka, etc.).
 */
public record PerformanceMetric(
        String operation,
        String className,
        String methodName,
        long durationMs,
        Instant measuredAt,
        boolean success,
        String errorType
) {
}