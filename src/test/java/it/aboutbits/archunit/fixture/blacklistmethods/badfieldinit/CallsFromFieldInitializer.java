package it.aboutbits.archunit.fixture.blacklistmethods.badfieldinit;

import org.assertj.core.api.AbstractThrowableAssert;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** An instance field initializer is compiled into the constructor. */
public class CallsFromFieldInitializer {
    private final AbstractThrowableAssert<?, ? extends Throwable> assertion = assertThatThrownBy(() -> {
        throw new IllegalStateException();
    });

    public Object assertion() {
        return assertion;
    }
}
