package it.aboutbits.archunit.fixture.securitytested.badmetagroup;

import it.aboutbits.archunit.toolbox.support.ArchIgnoreGroupName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ArchIgnoreGroupName
public @interface TestGroup {
}
