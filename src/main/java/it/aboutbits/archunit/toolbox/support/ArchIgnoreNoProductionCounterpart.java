package it.aboutbits.archunit.toolbox.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Marks a test class that has no matching counterpart in the production code, for example a
/// scenario test named after the behaviour it describes.
///
/// Must not be meta-annotated with ArchUnit's `@ArchIgnore`: the ArchUnit JUnit engine resolves
/// meta-annotations, so that would skip every `@ArchTest` on the annotated class instead of
/// exempting it from a single rule. The rules read this annotation by its own type.
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ArchIgnoreNoProductionCounterpart {
}
