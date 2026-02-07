package com.biglibon.sharedlibrary.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler that logs measured performance.
 */
@Slf4j
@Component
public class LogPerformanceMetricHandler implements PerformanceMetricHandler {

    @Override
    public void publish(PerformanceMetric metric) {
        // log, Kafka, DB publish nereye etmek istersek
        if (metric.success()) {
            log.info("[TRACK PERFORMANCE METRIC] operation={}, class={}, method={}, durationMs={}, measuredAt={}",
                    metric.operation(), metric.className(), metric.methodName(),
                    metric.durationMs(), metric.measuredAt());
            return;
        }

        log.warn("[TRACK PERFORMANCE METRIC] operation={}, class={}, method={}, durationMs={}, measuredAt={}, errorType={}",
                metric.operation(), metric.className(), metric.methodName(),
                metric.durationMs(), metric.measuredAt(), metric.errorType());
    }
}