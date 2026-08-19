package it.aboutbits.archunit.toolbox.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a &#64;Nested test class that only groups tests logically and therefore has no matching
 * nested class in the production code.
 * <p>
 * Must not be meta-annotated with ArchUnit's &#64;ArchIgnore: the ArchUnit JUnit engine resolves
 * meta-annotations, so that would skip every &#64;ArchTest on the annotated class instead of
 * exempting it from a single rule. The rules read this annotation by its own type.
 * </p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ArchIgnoreGroupName {
}
