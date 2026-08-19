package it.aboutbits.springboot.toolbox.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Stub of the spring-boot-toolbox stereotype. spring-boot-toolbox depends on archunit-toolbox, so it
/// cannot be a dependency here. Verified against the real artifact: RUNTIME retention, TYPE target.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Store {
    String value();
}
