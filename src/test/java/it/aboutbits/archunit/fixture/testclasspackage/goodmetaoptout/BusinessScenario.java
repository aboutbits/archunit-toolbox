package it.aboutbits.archunit.fixture.testclasspackage.goodmetaoptout;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// A project's own test stereotype, carrying the opt-out as a meta-annotation so it is declared
/// once rather than repeated on every scenario test.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ArchIgnoreNoProductionCounterpart
public @interface BusinessScenario {
}
