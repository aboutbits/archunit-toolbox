package it.aboutbits.archunit.toolbox.support;

import com.tngtech.archunit.junit.ArchIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to ignore a group of tests in the architecture check.
 * <p>
 * This annotation should be used on a @Nested test class.
 * </p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ArchIgnore(reason = "This is a @Nested test class to logically group tests with no matching production code nested class.")
public @interface ArchIgnoreGroupName {
}
