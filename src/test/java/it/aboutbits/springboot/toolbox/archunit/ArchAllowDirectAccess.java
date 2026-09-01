package it.aboutbits.springboot.toolbox.archunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Stub of the spring-boot-toolbox opt-out annotation. Verified against the real artifact: RUNTIME
/// retention, targets TYPE, FIELD and RECORD_COMPONENT.
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ArchAllowDirectAccess {
    String reason();
}
