package com.biglibon.sharedlibrary.performance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for performance tracking.
 *
 * <p>The annotation is intentionally small so that new optional attributes can be
 * introduced later without changing usage in services.</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackPerformanceMetric {

    /**
     * Optional operation name override. If empty, the default is ClassName.methodName.
     */
    String value() default "";
}