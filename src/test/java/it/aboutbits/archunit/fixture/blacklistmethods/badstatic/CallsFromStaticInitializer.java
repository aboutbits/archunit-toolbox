package it.aboutbits.archunit.fixture.blacklistmethods.badstatic;

import org.assertj.core.api.AbstractThrowableAssert;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CallsFromStaticInitializer {
    static final AbstractThrowableAssert<?, ? extends Throwable> ASSERTION;

    static {
        ASSERTION = assertThatThrownBy(() -> {
            throw new IllegalStateException();
        });
    }

    public Object assertion() {
        return ASSERTION;
    }
}
