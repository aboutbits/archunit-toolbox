package org.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Stub of the blacklisted JUnit 4 annotation, so a rule test can pin a real entry of
/// BLACKLISTED_ANNOTATIONS without putting JUnit 4 on this library's classpath.
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
}
