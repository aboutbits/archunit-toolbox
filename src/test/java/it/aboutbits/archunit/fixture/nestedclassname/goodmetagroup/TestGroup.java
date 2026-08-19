package it.aboutbits.archunit.fixture.nestedclassname.goodmetagroup;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreGroupName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// A project's own marker for a purely organisational @Nested class, carrying the opt-out as a
/// meta-annotation.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ArchIgnoreGroupName
public @interface TestGroup {
}
